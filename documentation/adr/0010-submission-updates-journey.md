## Submission updates journey

- Author: Danny Betts
- Status: DRAFT
- Approved by: Dan Ashworth

## Overview
When a resource submission is provided to the NSTA by an operator there may be incorrect or incomplete data on that submission.
The NSTA will review this and may decide that the submission needs updating before being approved for the system of record.
ReMI should provide a mechanism to request updates from operators and for the operators to create draft updates and submit those.

## Solution
A new `SubmissionUpdate` entity will be created with the following model:

| id  | submissionId | submissionDetailId | requestedTimestamp | requestedByWuaId | reason                   | resolved | resolvedTimestamp | resolvedByWuaID |
|-----|--------------|--------------------|--------------------|------------------|--------------------------|----------|-------------------|-----------------|
| 1   | 100          | 123                | [timestamp]        | 1000             | There is incorrect info  | true     | [timestamp]       | 1234            |
| 2   | 101          | 456                | [timestamp]        | 1000             | Some other reason        |          |                   |                 |

From the review screen for a submission detail, regulator users will be able to request an update for it. 
This will take the NSTA user to a screen to enter an update request reason and submit the request. 
This will populate the `SubmissionUpdate` entity, other than the resolved columns.
An email will also be sent to the operator submitter users to inform them an update has been requested.

On either submission or deletion of the update the resolved columns will be populated. Only one update request will be allowed at a time
for a `Submission` so we can simply resolve any unresolved `SubmissionUpdate` entries for that given `Submission`. 
An "Update requested" flag will also appear on the work area entries for those which have an unresolved update.

On entry to the submission from the work area the operator will have a button to start the update. The operator will be taken
to a "Start now" screen which display the update request reason as entered by the NSTA requester. On starting the update the
previous submission details will be copied forward onto a new `DRAFT` submission detail for the same `Submission`,
with the version number being increased by 1. The operator is then taken to the task list to complete the submission or delete it.

Once the update is resolved the NSTA review screen will now show the latest submission details for that updated `Submission`.

While an update is being drafted the approval, withdraw and request update actions will be unavailable. The security rules will need to be updated
for those pages to prevent access during updates.