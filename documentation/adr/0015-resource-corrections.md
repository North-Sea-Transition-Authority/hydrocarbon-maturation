## Overview
Exploration users will need to be able to correct data for prospects and discoveries in the instances where incorrect
data was previously entered, or missing data is being inserted into the system of record. There are four types of corrections:
- Remove an incorrect submission
- Change of approval date of a submission
- Correct the information provided on a submission
- Add a missing historical update to a resource

Each on of the correction types will require a different technical solution.

## Correction types - technical approach
All types will require the submission details table to be audited to keep track of who is changing the submissions during corrections.
This can be done by using the Spring Envers auditing functionality.

The corrections process will be found via the system of record screen for a given resource.

### Remove an incorrect submission
When removing an incorrect submission the user will be asked to search for a list of approved submissions following the 
same logic as the system of record view. (i.e. the latest approved version of submissions). On starting the correction
all submission details under the submission will be marked as deleted so they no longer appear in the system of record.
The following rules are in place for removals:
- on removing a post drill discovery submission the link to the prospect that created it is removed
- on removing the create submission for a prospect or discovery that resource is removed from any prospect/discovery group it is in 
a submission cannot be deleted if it has a draft correction open for it

### Change of approval date of a submission
When changing the approval date of a submission the user will be asked to search for a list of approved submissions following the
same logic as the system of record view. The latest approved version of the submissions will be updated to the 
entered date (set to midnight for the time). The following rules are in places for changes to approval dates:
- any approved submission for that resource can be selected for to update the date on
- must provide the new approved date for the submission which
  - cannot be before the creation date of the resource (unless it is the create submission)
  - cannot be on the same date as another submission for the same resource
  - cannot be in the future
  - cannot be on or after the date of any relinquishment submission (unless it is the relinquishment submission)
  - cannot be on or after the date of any dry well submission (unless it is a relinquishment or dry well submission)
  - for a prospect must be prior to the date of any discovery submission on that prospect
- on apply the approved date to the system of record the names displayed for all organisations are updated to reflect the 
name that was active on the new approval date
  - if no organisation name was present on that date then the current name for the organisation is displayed

### Correct the information provided on a submission
On correcting the information provided on a submission the user will be asked to search for a list of approved submissions following the
same logic as the system of record view. A new version of the submission will be created and the information
from the latest version will be copied forward. (This is very similar to the update process).
The following rules are in place for these corrections:
- any information relevant to that submission type can be updated in the same way as a new draft submission
- the draft correction is not shown in the system of record
- on submitting the correction
  - it will replace the current view of information for that submission
  - the history of data prior to the correction is retained but does not need to be visible to the user

### Add a missing historical update to a resource
On adding a missing historical update to a resource a new submission is created and the initial data for the inserted 
submission is copied from the latest approved submission prior to the date provided for the submission being inserted. 
Only prospect and discovery updates can be created via this correction process.
The following rules are in place for these corrections:
- must provide the approval date of the submission (set to midnight for the time)
  - cannot be before the creation date of the resource
  - cannot be on the same date as another submission for the same resource
  - cannot be in the future
  - cannot be on or after the date of any relinquishment or dry well submission
  - for a prospect must be prior to the date of any discovery submission on that prospect
