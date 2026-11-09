# Investigation: Creating a Discovery from a Relinquished Prospect

> **Status: Proposed.** The NSTA has asked that a post-drill discovery
> (`CREATE_DISCOVERY_FROM_PROSPECT`) can be created against a prospect that has an
> accepted prospect relinquishment (`RELINQUISHMENT_PROSPECT`).

## Problem

At the moment, a discovery can't be created from a relinquished prospect. Only the prospect
picker blocks it. The server doesn't check.

The "Advise of a post-drill discovery" page
(`discovery/PostDrillDiscoveryController.java:61-71`) fills its search selector from
`ResourceRestController.searchProspects(..., CREATE_DISCOVERY_FROM_PROSPECT, ...)`, which calls
`SubmissionQueryService.getLatestProspectsByNameForSubmissionType`
(`energyresource/SubmissionQueryService.java:74`). That query leaves out a prospect when any of
these are true:

1. It has a non-withdrawn submission in `DRAFT` or `SUBMITTED` (any type).
2. It has a non-withdrawn `ACCEPTED` submission whose type is in `submissionTypesToFilterBy`.
3. It is already linked to a discovery through `resource_links`.
4. It is inactive (archived), or the industry user isn't a submitter for its organisation.

`submissionTypesToFilterBy` is built like this:

```java
var submissionTypesToFilterBy = new ArrayList<String>();
submissionTypesToFilterBy.add(SubmissionType.RELINQUISHMENT_PROSPECT.name());

if (submissionType != SubmissionType.RELINQUISHMENT_PROSPECT) {
  submissionTypesToFilterBy.add(SubmissionType.POST_DRILL_DRY_HOLE.name());
}
```

So an accepted relinquishment rules a prospect out of every prospect journey, discovery
creation included.

On POST, `StartDiscoveryFormValidator` only checks that a prospect was selected.
`SubmissionCreateService.createNewDiscoveryFromProspect` then accepts any resource-detail ID it
is given. No business rule is enforced on the server.

The page guidance in `templates/remi/discovery/newDiscoveryFromProspect.ftl:19` also tells users:

> Only prospects that exist on an extant licence and have not been relinquished are shown.

## Recommendation

### Step 1: Stop filtering out relinquished prospects for discovery creation

Build the filter for each submission type, so that relinquishment only excludes a prospect from
the journeys that should still be blocked:

```java
var submissionTypesToFilterBy = switch (submissionType) {
  // A relinquished prospect may still be progressed to a discovery; a dry hole may not
  case CREATE_DISCOVERY_FROM_PROSPECT -> List.of(SubmissionType.POST_DRILL_DRY_HOLE.name());
  // A dry-hole prospect may still be relinquished
  case RELINQUISHMENT_PROSPECT -> List.of(SubmissionType.RELINQUISHMENT_PROSPECT.name());
  default -> List.of(
      SubmissionType.RELINQUISHMENT_PROSPECT.name(),
      SubmissionType.POST_DRILL_DRY_HOLE.name());
};
```

Callers `PROSPECT_UPDATE`, `POST_DRILL_DRY_HOLE` and `RELINQUISHMENT_PROSPECT` behave as before.
The in-flight rule (1) and the already-linked rule (3) stay as they are. This means:

- A prospect with a **draft or submitted** relinquishment still can't be picked. The
  relinquishment must be accepted, or withdrawn, first (Resolved Question 1).
- A prospect that is both dry-holed and relinquished stays excluded because of the dry hole
  (Resolved Question 2).
- Inactive prospects, and prospects outside an industry user's submitting organisations, stay
  excluded (Resolved Questions 3 and 4).

### Step 2: Close the server-side validation gap

Since the eligibility rule is changing anyway, `StartDiscoveryFormValidator` (and ideally the
relinquishment, update and dry-hole start validators too) should check that the selected
prospect is in the eligible set, not just that something was selected. The simplest approach is
to pull the `WHERE` conditions in `getLatestProspectsByNameForSubmissionType` into a shared
`isProspectEligibleForSubmissionType(resourceDetailId, submissionType, user)` query, so the
picker and the validator can't drift apart.

### Step 3: Stop copying the relinquishment date into the discovery

`createNewDiscoveryFromProspect` copies the sections of the prospect's **latest** submission
detail into the new discovery submission. For a relinquished prospect, that is the
`RELINQUISHMENT_PROSPECT` detail.

`GeneralDetailsDuplicationService.preventCopyingForward`
(`submissiondetails/generaldetails/GeneralDetailsDuplicationService.java:83`) already clears the
information source (set to `RELINQUISHMENT` by `setRelinquishmentInformationSource`) and the
licence-application fields when a discovery is created from a prospect. It does **not** clear
`relinquishmentDate`, so the new discovery's `general_details` row would keep the prospect's
relinquishment date.

The date is hidden in the UI today, because `GeneralDetailSummaryService.getDateOfRelinquishmentDisplay`
and `generaldetailsSummaryCard.ftl` only show it for relinquishment submission types, and the
`general_details` reporting columns don't include it. Even so, it's misleading data, and it would
come back if the discovery were later relinquished and the form pre-filled from it. Clear it when
creating a discovery from a prospect:

```java
if (!newSubmissionDetail.hasSameSubmissionType(oldSubmissionDetail)
    && (isUpdateSubmissionType || isDiscoveryFromProspect)) {
  preventCopyingForward(newGeneralDetail);
}
if (isDiscoveryFromProspect) {
  newGeneralDetail.setRelinquishmentDate(null);
}
```

(Alternatively, clear it in `preventCopyingForward`. Prospect and discovery updates already
re-enter it through the relinquishment journey, so that is safe too.)

The other sections copied from a relinquishment detail are the same as a prospect update. The
task list (`OpportunityTaskListSectionService`) and validators treat `RELINQUISHMENT_PROSPECT` the
same as `PROSPECT_UPDATE`, so the discovery starts from the prospect's last accepted technical
data, as it does now.

### Step 4: Update the page guidance

Change `newDiscoveryFromProspect.ftl:19` so it no longer says relinquished prospects are
excluded. For example: "Only prospects that are not linked to an existing discovery, have not
been declared a dry hole, and have no submission in progress are shown." The page heading and
label also say "What prospect is being updated?", which reads oddly for this journey. This is a
good time to fix it.

## Knock-on Effects

### Prospect disappears from the system of record and reporting as soon as a draft exists

The `resource_links` row is created when the draft discovery is **created**
(`SubmissionCreateService.java:180`), not when it's accepted. From that point:

- **SoR search:** `ResourceQueryCommonService` (around line 244) hides prospects linked to a
  discovery.
- **Reporting view:** `R__1.0_latest_submission_detail_reporting_view.sql:31` leaves out
  prospects that have a `resource_links` row. Any NSTA reporting that counts relinquished
  prospects will see that number drop as discoveries are raised against them, including from
  drafts that may later be deleted.
- **Future prospect journeys:** rule (3) hides the prospect from update, dry-hole and
  relinquishment pickers.

This already happens for non-relinquished prospects, so nothing new is needed in code. The NSTA
has confirmed the reporting drop-off is acceptable (Resolved Question 5).

### Discovery history includes the prospect's relinquishment

`SubmissionHistoryQueryService` joins through `resource_links`, so the discovery's record
timeline shows all the prospect's submissions, including the accepted **Prospect relinquishment**
followed by **Post-drill discovery**. That is accurate, but it's a new combination in the UI. We
need to confirm the NSTA is happy with it, or decide whether the relinquishment entry needs
explaining.

### Comparison baseline for the draft discovery

While the discovery is in draft, `SubmissionDetailService.getLatestCreateDiscoveryFromProspectSubmissionDetail`
uses `getLatestApprovedProspectSubmissionForResource`, which already includes
`RELINQUISHMENT_PROSPECT`. Review and "changes since last submission" comparisons will run
against the relinquishment detail. No change is needed.

### Corrections

- `CorrectionService.canRemoveSubmissions` blocks removing submissions from a prospect while it
  is linked to a discovery. After this change, a relinquishment accepted in error can't be removed
  until the discovery is removed. `CorrectionCreateService.deleteSubmission` deletes the link
  when a `CREATE_DISCOVERY_FROM_PROSPECT` submission is removed, so the route is: remove the
  discovery, then remove the relinquishment.
- `CorrectionFormValidator.checkDiscoveryAfterLatestProspect` (line 590) already counts
  `RELINQUISHMENT_PROSPECT` as a prospect submission. The discovery's accepted date can't be
  corrected to before the relinquishment's accepted date. That is consistent, but the NSTA may
  want to confirm it, for example where the well was drilled before relinquishment but reported
  after it.
- The prospect correction rules ("before the relinquishment date of the prospect", "before the
  initial discovery date of the resource") still hold together. A historic update added to the
  prospect must be before both.

### Regulator-managed information

The relinquishment report upload (`RegulatorManagedInformationController`, driven by
`isLatestSubmissionForResourceRelinquishment`) belongs to the **prospect** resource and is not
copied to the discovery. The new discovery gets its own regulator-managed information with
`ResponsibleTeamType.EXPLORATION`, as now. No change is needed.

### Who can raise the discovery

For industry users, the picker only shows prospects whose `RESOURCE_DETAILS.ORGANISATION_ID` is
an organisation they submit for. After relinquishment the operator may no longer hold the
acreage, and the party that made the discovery may be a different organisation that can't see
the prospect. Regulator users see every prospect. The NSTA has confirmed that industry users can
raise these discoveries only where the organisation still matches (Resolved Question 4). Where it
doesn't match, the industry user won't see the prospect, and a regulator user would need to raise
the discovery.

### Unaffected areas

These were checked and have no relinquishment-specific logic on this path:
`epmqmessage`, `externalapi`, `workarea`, `undevelopeddiscoveries` (works from the discovery),
`ResourceArchiveService`, `SecuritySubmissionTypes` and `SubmissionType` (no new type or
permission is needed).

## Tests

- `SubmissionQueryServiceIntegrationTest`: add
  `getLatestProspectsByNameForSubmissionType_resourceHasAcceptedRelinquishment_createDiscoveryFromProspect_assertReturned`,
  and keep the existing `..._resourceHasAcceptedRelinquishment_assertNotReturned` passing for the
  other types. Add a case where a draft relinquishment still excludes the prospect, and one where
  a dry hole plus relinquishment still excludes it.
- `GeneralDetailsDuplicationService` test: the relinquishment date is cleared for
  `CREATE_DISCOVERY_FROM_PROSPECT`.
- `StartDiscoveryFormValidator` / `PostDrillDiscoveryController` tests: an ineligible prospect ID
  is rejected (if Step 2 is taken).

## Resolved Questions

1. **In-flight relinquishments:** a discovery is **not** allowed while a relinquishment is in
   draft or submitted. The existing in-flight rule (1) stays as it is.
2. **Dry-hole prospects:** an accepted `POST_DRILL_DRY_HOLE` still blocks a discovery being
   created from the prospect.
3. **Archived prospects:** only active prospects are included. The `RESOURCES.ACTIVE` condition
   stays as it is.
4. **Submitting organisation:** industry users can create a discovery from a relinquished
   prospect, but only where the prospect's organisation is one they submit for. The existing
   `RESOURCE_DETAILS.ORGANISATION_ID` condition stays as it is. Regulator users can still pick any
   eligible prospect.
5. **Reporting:** it's acceptable for relinquished prospects to drop out of the reporting view and
   SoR search once a discovery (including a draft) is raised against them.

None of these answers changes the recommendation. Steps 1–4 only remove the relinquishment
exclusion for `CREATE_DISCOVERY_FROM_PROSPECT`, and leave every other eligibility rule as it is.

## Affected Files

- `energyresource/SubmissionQueryService.java`: per-type relinquishment filter
- `discovery/StartDiscoveryFormValidator.java`: server-side eligibility check (Step 2)
- `submissiondetails/generaldetails/GeneralDetailsDuplicationService.java`: clear
  `relinquishmentDate`
- `templates/remi/discovery/newDiscoveryFromProspect.ftl`: guidance text and label
- `src/test/.../energyresource/SubmissionQueryServiceIntegrationTest.java` and related unit tests
