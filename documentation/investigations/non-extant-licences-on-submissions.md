# Investigation: Allowing Non-Extant Licences on Submissions

> **Status: Proposed.** The NSTA has asked to be able to add non-extant licences (for example
> expired, surrendered or revoked licences) to the prospect and discovery submissions it raises,
> and still be able to submit them. Every place that shows a licence reference should also show
> its status.

## Problem

Today a submission can only be submitted with a non-extant licence on it if the submission is a
**correction**.

### The selector already offers non-extant licences

The General details page (`submissiondetails/generaldetails/GeneralDetailsController.java`) gets
its licence search selector from `LicenceRestController.searchLiveLicences`. Despite the name, this
calls `LicenceQueryService.getLiveLicencesBy`, which returns licences of **every** status. The
results show only the licence reference (`LicenceDto.getSelectionText()`), so there is no way to
tell an extant licence from a non-extant one when picking.

Once added, the list marks any non-extant licence "This licence is not extant", because
`LicenceDto.isValid()` returns `LicenceStatus.EXTANT.equals(licenceStatus)`
(`templates/remi/submissiondetails/generalDetails.ftl:42`). That message doesn't say what the
status actually is.

### Validation then rejects it

`GeneralDetailsFormValidator` calls `validateExtantLicences` in both modes:

- **Partial** (save and come back later): whenever any licences are selected.
- **Full** (save and complete, and the task list's submittable check): whenever any licences are
  selected.

Both calls are wrapped in `BooleanUtils.isNotTrue(submissionDetail.isCorrection())`.
`validateExtantLicences` looks up the selected licences and rejects the field with "You must only
select extant licences" if any of them fails `LicenceQueryService.isLicenceStatusValid`
(`== EXTANT`).

`GeneralDetailSubmissionService.isSectionSubmittable` runs the same validator with
`ValidationType.FULL`, so the section can never be completed and the submission can never be
submitted. Even the draft can't be saved with a non-extant licence on it.

Field submissions skip licence validation in General details (`validateFull` only calls
`validateLicences` when `!submissionDetail.isFieldSubmission()`), and fields are out of scope for
this change (Resolved Question 2).

### Licence references are shown without a status

Licence references are displayed in two ways, and neither includes the status:

- **General details summary card:** `GeneralDetailSummaryService.getLicenceDisplay` returns the
  stored `licence_reference` values from the `licences` table, and `generaldetailsSummaryCard.ftl`
  lists them as plain text.
- **Everything else:** `SubmissionDetailDisplayService.getLicencesForDisplay` and
  `getLicencesForDisplayFromMap` look licences up in the Energy Portal (so the status is already
  loaded), then map them to `LicenceDto::licenceRef`. They are used by:
  - the review page header (`ReviewSubmissionService:193`, `reviewSubmission.ftl:54`)
  - the system of record record page header (`ViewResourceRecordService:87`,
    `viewResourceRecord.ftl:93`)
  - system of record search results (`SearchResourceRecordsService:446`)
  - work area items (`WorkAreaItemService:228`)

### Licences are no longer optional for regulators

- `77b41511` (#626, #628, Jan 2024) allowed non-extant licences on corrections. It also made
  licences optional for regulator submissions by only requiring one when
  `SubmitterType.INDUSTRY`.
- `538e43d7` (May 2026) refactored the licence validation and **removed the `SubmitterType`
  condition**. Licences are required for regulator submissions again, but the template still
  shows the field as optional (`optionalLicences = isRegulatorSubmission`).

The NSTA has confirmed that regulators should be able to submit with no licences (Resolved
Question 4), so the validator is wrong and the template is right.

The same refactor also removed the `!isRelinquishment` exemption from partial validation, so
relinquishments now get the extant check too. The NSTA has confirmed this is correct for industry
relinquishments (Resolved Question 8).

### Prospect picker guidance is wrong

`updateProspect.ftl:21`, `postDrillDryHole.ftl:19` and `newDiscoveryFromProspect.ftl:19` tell
users that only prospects on an extant licence are shown. The pickers
(`SubmissionQueryService.getLatestProspectsByNameForSubmissionType`) don't filter by licence at
all. This is unrelated to the change itself, but it is being fixed at the same time (Resolved
Question 12).

## Recommendation

### Step 1: Skip the extant check for regulator submissions

`SubmissionDetail.getSubmitterType()` is already set to `REGULATOR` when a regulator or area-team
user creates a submission (`SubmissionDetailPersistenceService.createInitialSubmissionDetail`), and
for every correction. Pull the existing correction exemption into a single helper and extend it:

```java
private boolean shouldValidateLicenceStatus(SubmissionDetail submissionDetail) {
  // Regulator submissions and corrections may record licences that are no longer extant
  return BooleanUtils.isNotTrue(submissionDetail.isCorrection())
      && !SubmitterType.REGULATOR.equals(submissionDetail.getSubmitterType());
}
```

Use it in both the `validatePartial` and the `validateLicences` call sites. Industry submissions,
including industry relinquishments, keep the extant check (Resolved Questions 1 and 8).

Industry update submissions requested through review (`createUpdateSubmissionDetail`) are always
`SubmitterType.INDUSTRY`. They copy the licences forward from the previous accepted version, so if
that was a regulator submission with a non-extant licence, the operator must remove it before they
can submit (Resolved Question 3). An industry relinquishment that inherits a lapsed licence works
the same way. The existing "You must only select extant licences" error covers both, and Step 2
means the operator can see which licence is the problem.

### Step 2: Show the status in brackets wherever a licence reference is shown

Every licence reference is displayed as `<reference> (<status>)`, for example `P1234 (Extant)` or
`P5678 (Expired)`, for all users (Resolved Questions 5 and 9).

#### 2a: Status display names

Map the known statuses to display names, and anything else to `Unknown` (Resolved Question 11):

| `LicenceStatus` | Display |
|---|---|
| `EXTANT` | Extant |
| `EXPIRED` | Expired |
| `SURRENDERED` | Surrendered |
| `REVOKED` | Revoked |
| anything else, or `null` | Unknown |

```java
public enum LicenceStatusDisplay {
  ...
  public static String of(LicenceStatus licenceStatus) {
    if (licenceStatus == null) {
      return UNKNOWN.displayName;
    }
    return switch (licenceStatus) {
      case EXTANT -> "Extant";
      case EXPIRED -> "Expired";
      case SURRENDERED -> "Surrendered";
      case REVOKED -> "Revoked";
      default -> "Unknown";
    };
  }
}
```

`LicenceStatus` is generated from the Energy Portal API schema, so the `default` branch is needed
in case the API adds values. Add a single formatter on `LicenceDto` and use it everywhere:

```java
public String getDisplayName() {
  return "%s (%s)".formatted(licenceRef, LicenceStatusDisplay.of(licenceStatus));
}
```

#### 2b: Search selector

Make what `LicenceRestController.searchLiveLicences` returns depend on who is searching (Resolved
Question 7):

- **Regulator and area-team users** find licences of every status.
- **Industry users** only find extant licences.

Both see the status in brackets, because `LicenceDto.getSelectionText()` now returns
`getDisplayName()`. Inject `User` into the endpoint (as
`OrganisationUnitRestController.searchOrganisationUnits` already does):

```java
@GetMapping("/licence")
public RestSearchResult searchLiveLicences(@RequestParam("term") String searchReference, User user) {
  // Regulators may record non-extant licences; industry users may only pick extant ones
  var licences = internalTeamService.isRegulatorOrAreaTeamUser(user)
      ? licenceQueryService.getLiveLicencesBy(searchReference)
      : licenceQueryService.getLicencesByReferenceAndStatus(searchReference, LicenceStatus.EXTANT);
  return searchSelectorService.search(searchReference, licences);
}
```

The `ReverseRouter` calls that build the endpoint URL pass `null` for the new `User` argument.

`SearchSelectorService.search` keeps results whose selection text contains the search term
(case-insensitive). The Energy Portal search has already filtered by reference, so adding
` (Extant)` doesn't change the results: `P12` still matches `P1234 (Extant)`, and typing a status
such as `expired` returns nothing, because the API finds no licence with that reference.

This endpoint is used in three places, and the change applies to all of them:

| Page | Who uses it | Effect |
|---|---|---|
| General details (`GeneralDetailsController:115`) | Industry and regulators | Regulators see all licences. Industry can no longer find non-extant licences (today they can find them, but validation rejects them) |
| System of record search filter (`SearchProspectAndDiscoveryRecordsController:110`) | Industry and regulators | Regulators can filter by any licence. Industry can only filter by extant licences (see Knock-on Effects) |
| Field regulator-managed information (`FieldRegulatorManagedInformationController:207`) | Area teams only (`@HasAnyRole`) | Always the regulator behaviour |

`searchExtantLicences`, used by `NewFieldController`, is also built on `LicenceDto`, so the new
field page shows `P1234 (Extant)` too. Its filtering is unchanged because fields are out of scope.

The search decides by the **user**, but the validator in Step 1 decides by the submission's
`SubmitterType`. These agree in practice, because General details is `@IsFromResponsibleTeam`, so
regulator submissions are only edited by regulators and industry submissions are only edited by
industry users.

#### 2c: Preselected licences

Search selectors show a preselected value using a separate lookup, not the search endpoint. Use
`getDisplayName()` in those too:

- `SearchResourceRecordsService.getPreselectedLicence` (saved system of record filter, Resolved
  Question 10): change `licence.licenceRef()` to `licence.getDisplayName()`. This is the only
  licence preselection. `LicenceApplicationQueryService` preselects licence *applications*, which
  have no licence status.

#### 2d: General details form (added licences)

Show each added licence as `P1234 (Expired)` in the add-to-list, and only flag it as an error when
it's actually invalid for that submission (Resolved Question 9):

- `GeneralDetailsFormService.getLicenceViews` returns a view per licence, with the status in the
  name (`LicenceDto.getName()` returns `getDisplayName()`).
- `isValid()` is `true` for regulator submissions and corrections, and `EXTANT`-only for industry
  submissions. Wrap `LicenceDto` in a view record that takes this flag, rather than changing
  `LicenceDto.isValid()`, which other add-to-list callers rely on.
- Keep `invalidItemText="This licence is not extant"`. Industry users now see the status in the
  name alongside it, for example `P5678 (Expired)` with "This licence is not extant".

#### 2e: General details summary card

`GeneralDetailSummaryService.getLicenceDisplay` should look up statuses with
`LicenceQueryService.getLicenceMapByIds` (one API call for all licences on the detail) and display
`getDisplayName()`. If a licence can't be found in the Energy Portal, show the stored reference
with `(Unknown)`. The summary card is shared by the task list review, check your answers and the
system of record tabs, so this one change covers all of them.

#### 2f: Headers, search results and work area

Change `SubmissionDetailDisplayService.getLicencesForDisplay` and `getLicencesForDisplayFromMap`
to map to `LicenceDto::getDisplayName` instead of `LicenceDto::licenceRef`. Both already load the
`LicenceDto`, so this needs no extra API calls. It covers:

- review page header (Resolved Question 9)
- system of record record page header (Resolved Question 9)
- system of record search results
- work area items

The last two weren't in the original request, but they use the same helper. The NSTA has confirmed
the status should show there as well, for industry users too (Resolved Question 9).

### Step 3: Make licences optional for regulator submissions again

Put the `SubmitterType` condition back in `validateLicences`:

```java
if (CollectionUtils.isEmpty(generalDetailsForm.getLicences())
    && !isRelinquishment
    && SubmitterType.INDUSTRY.equals(submissionDetail.getSubmitterType())) {
  errors.rejectValue(LICENCE_SELECT, LICENCE_SELECT + ".required",
      "You must select at least one licence");
}
```

The template already shows the field as optional for regulators, so no template change is needed.
Restore the regulator tests that `538e43d7` removed from `GeneralDetailsFormValidatorTest`.

### Step 4: Correct the prospect picker guidance

Remove the extant-licence claim and describe what the pickers actually filter on (Resolved
Question 12). The pickers exclude prospects that have a submission in progress, that are linked to
a discovery, or that are archived, as well as the type-specific exclusions below.

| Template | Current text | New text |
|---|---|---|
| `prospect/updateProspect.ftl:21` | Only prospects that exist on an extant licence are shown. Prospects that have already had a post-drill or relinquishment submission can no longer be updated here. | Prospects that have already had a post-drill or relinquishment submission, that have a submission in progress, or that are linked to a discovery can no longer be updated here. |
| `prospect/postDrillDryHole.ftl:19` | Only prospects that exists on an extant licence and have not been relinquished are shown. | Only prospects that have not been relinquished or declared a dry hole, have no submission in progress and are not linked to a discovery are shown. |
| `discovery/newDiscoveryFromProspect.ftl:19` | Only prospects that exist on an extant licence and have not been relinquished are shown. | Only prospects that have not been relinquished or declared a dry hole, have no submission in progress and are not linked to a discovery are shown. |

The relinquished-prospect investigation (`discovery-from-relinquished-prospect.md`, Step 4) also
rewrites `newDiscoveryFromProspect.ftl:19`, to drop "have not been relinquished". Whichever change
lands second should use that wording: "Only prospects that are not linked to an existing
discovery, have not been declared a dry hole, and have no submission in progress are shown."

For industry users the pickers also only show prospects belonging to an organisation they submit
for. This is already covered by `guidance.newSubmissionGuidanceText`, which each page includes
straight after this paragraph, so it isn't repeated here.

## Knock-on Effects

Large parts of the app treat "has an extant licence" as meaning "is licensed". A resource whose
licences are **all** non-extant (or that has no licences) behaves as unlicensed in each of these
places. The NSTA has confirmed this is acceptable (Resolved Question 6).

### System of record visibility for industry users

`ViewResourceRecordService.isResourceAssociatedWithAnExtantLicence` and
`SearchResourceRecordsService.filterResultsForUser` only let industry users see a prospect or
discovery record if it is on at least one extant licence. Regulator users see everything. A
resource with only non-extant licences is visible to the NSTA but hidden from the operator.

### Undeveloped discoveries

`UndevelopedDiscoveriesOverviewService` (lines 82 and 330) works out `isLicensed` from extant
licences only. Discoveries with only non-extant licences show as **unlicensed** in the overview and
in resource-group rollups.

### System of record licence filter for industry users

Today industry users can pick any licence in the system of record search filter. After Step 2b
they can only pick extant ones. Industry users can already only see records on at least one extant
licence, so in most cases this changes nothing. The one thing lost: an industry user can no longer
filter by a non-extant licence on a resource that also has an extant licence. A non-extant licence
already saved in an industry user's session filter still displays (with its status, after Step 2c),
because preselection looks the licence up by ID rather than through the search.

### Status shown to industry users

Because the status appears wherever a reference is shown, industry users will see `(Expired)` and
similar against licences in the work area, search results and summaries. These are statuses from
the Energy Portal, not new information, but operators will see them for the first time. The NSTA
has confirmed this is intended (Resolved Question 9).

### Other extant-only checks

`GeneralDetailsPersistenceService.isOnAnExtantLicence` / `LicencePersistenceService.hasAnExtantLicence`
have no callers outside the persistence layer, so they have no effect today.

### Unaffected areas

These have no licence-status logic and show no licence references: `externalapi` and
`epmqmessage`. Fields (`FieldFormValidator`, `FieldRegulatorManagedInformationSummaryService`) keep
their extant-only rules, though any field licence shown through `LicenceDto.getDisplayName()` gets
the status too.

## Tests

- `GeneralDetailsFormValidatorTest`:
  - regulator submission with a non-extant licence passes partial and full validation
  - industry submission (including an update following a regulator submission, and an industry
    relinquishment) still fails with `licenceSelect.notValid`
  - regulator submission with no licences passes full validation; industry submission still fails
    with `licenceSelect.required`
  - correction behaviour unchanged
- `GeneralDetailSubmissionService` test: regulator submission with a non-extant licence, or none,
  is submittable.
- `LicenceStatusDisplay` test: each known status maps to its display name; `null` and an
  unrecognised value map to `Unknown`.
- `LicenceDto` test: `getDisplayName()`, `getSelectionText()` and `getName()` return
  `P1234 (Extant)`.
- `LicenceRestControllerTest`: a regulator user gets licences of every status; an industry user
  only gets extant licences; both see the status in brackets; a partial reference still matches.
- `SearchProspectAndDiscoveryRecordsControllerTest`, `FieldRegulatorManagedInformationControllerTest`
  and `GeneralDetailsControllerTest`: update the expected `licenceEndpoint` model attribute if the
  route changes.
- `GeneralDetailsControllerTest`: added-licence views show the status, and are valid for regulator
  submissions and invalid (when non-extant) for industry ones.
- `GeneralDetailSummaryServiceTest`: every licence shows its status; a licence missing from the
  Energy Portal shows `(Unknown)`.
- `SubmissionDetailDisplayServiceTest`: both display methods return references with statuses.
- `SearchResourceRecordsServiceTest`: the preselected licence includes the status. Update the
  expected licence text in search result and work area tests (`WorkAreaItemServiceTest`,
  `ReviewSubmissionServiceTest`, `ViewResourceRecordServiceTest`).

## Resolved Questions

1. **Who:** NSTA (regulator) submissions only. Industry submissions keep the extant check.
2. **Which resources:** prospects and discoveries only. Fields are out of scope.
3. **Updates after a regulator submission:** industry updates must remove any non-extant licence
   before they can be submitted.
4. **Licence optional for regulators:** regulator submissions can be submitted with no licences.
5. **Status display:** the licence status is shown in the search selector, on the General details
   form and in the summaries, rather than only "This licence is not extant".
6. **Visibility:** it's acceptable that operators can't see resources in the system of record, and
   that undeveloped discoveries show as unlicensed, when none of a resource's licences are extant.
7. **Licence search:** everywhere the licence search is used (General details, the system of
   record search filter and field regulator-managed information), regulators find every licence,
   and industry users only find extant licences.
8. **Relinquishments:** the extant check applies to industry relinquishments too. An industry
   relinquishment with a lapsed licence must have it removed before it can be saved.
9. **Status format:** the status is shown in brackets after the reference, for example
   `P1234 (Expired)`, wherever a licence reference is shown, including the review and system of
   record page headers. Extant licences show `(Extant)` too, and statuses are shown to industry
   users as well as regulators, including in the work area and system of record search results. On the General details form this is the recommended approach: no error for
   regulator submissions, and the status in the name for everyone.
10. **Saved filter:** a preselected licence in the system of record search filter shows its
    status.
11. **Statuses:** `EXTANT`, `EXPIRED`, `SURRENDERED` and `REVOKED` get display names; anything
    else is shown as `Unknown`.
12. **Prospect picker guidance:** the incorrect extant-licence wording is replaced.

## Affected Files

- `submissiondetails/generaldetails/GeneralDetailsFormValidator.java`: regulator exemption
  (Step 1), licences optional for regulators (Step 3)
- `energyportal/licence/application/LicenceStatusDisplay.java` (new) and `LicenceDto.java`: status
  display names and `getDisplayName()` (Step 2a)
- `energyportal/licence/application/LicenceRestController.java`: results that depend on the user
  (Step 2b)
- `systemofrecord/SearchProspectAndDiscoveryRecordsController.java` and
  `fieldregulatormanagedinformation/FieldRegulatorManagedInformationController.java`: only the
  `ReverseRouter` call for the endpoint changes (Step 2b)
- `systemofrecord/SearchResourceRecordsService.java`: preselected licence (Step 2c)
- `submissiondetails/generaldetails/GeneralDetailsController.java`,
  `GeneralDetailsFormService.java` and a new added-licence view record: status and validity
  (Step 2d)
- `submissiondetails/generaldetails/GeneralDetailSummaryService.java`: summary card (Step 2e)
- `submissiondetails/SubmissionDetailDisplayService.java`: headers, search results and work area
  (Step 2f)
- `templates/remi/prospect/updateProspect.ftl`, `templates/remi/prospect/postDrillDryHole.ftl` and
  `templates/remi/discovery/newDiscoveryFromProspect.ftl`: guidance wording (Step 4)
- `src/test/...`: the tests listed above
