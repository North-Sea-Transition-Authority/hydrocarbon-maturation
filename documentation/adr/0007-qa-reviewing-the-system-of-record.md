# QA reviewing a resource submission

## Problem statement

We have a requirement for users to be able to QA review in detail a hydrocarbon resource submission. They will have access to this screen from the work area.

Industry users should only have visibility of submissions if they are the exploration team role and the responsible team for the resource is the exploration team.  
Regulator users should have visibility of submissions they are authorised to access (being a member of the responsible team with Exploration role)

The user should be able to view a summary of key information about the submission including
-   the resource name (from submission being reviewed)
-   the quad/block centre of interest (from submission being reviewed)
-   the responsible team (from central resource data)
-   the responsible organisation (from submission being reviewed)
-   the licences (from submission being reviewed)

The user should be able to see a history of approved or submitted submissions for the resource via a submissions drop down menu.
For each submission in the history, the user should be able to select and then view the full summary of the submission. 
By default, the submission being reviewed will be selected and shown.

The user should be able to see the regulator managed information for the resource:
- prospect group (if applicable)
- discovery group (if applicable)
- common details (relinquishment report if exists)

Regulator users will be able to carry out various QA actions, such as approving or withdrawing the submission, or requesting updates.

## Solution

### Security

We will already have much of the functionality to fulfil the security rules for this screen as thy are largely the same as the rules for the view resource record screen outlined in [0006-viewing-the-system-of-record.md](0006-viewing-the-system-of-record.md).
Where the rules differ, is that the review screen requires that the regulator user is in the exploration team role and the responsible team for the resource is the exploration team, however the rules for industry users is exactly the same. We will extract the security rules for the industry users and make it reusable for both cases. 

### Summary of resource

As the summary at the top of the page contains key information that is almost identical to the key information outlined in [0006-viewing-the-system-of-record.md](0006-viewing-the-system-of-record.md).

The only difference being that the review screen needs to show the submission type. We will adapt the existing query service to also return this information. We will also need to adapt the underlying Jooq query to be driven by the submission detail rather than by the resource. 

### Submissions drop down menu

This functionality is very similar to the functionality outlined in [0006-viewing-the-system-of-record.md](0006-viewing-the-system-of-record.md).
The only difference being that we also need to show the submitted submissions as well as approved submissions. Therefore, we will adapt the service that so that it can conditionally also include submitted submissions.

The format for submitted submissions also differs slightly. The format in the drop-down menu for the two states will be:
- not approved: "(#) [submission type] (Pending approval)"
- approved: "(#) [submission type] (Approved: dd Month yyyy)"

### Full summary of submission

We will fully reuse the summary of submission functionality outlined in [0006-viewing-the-system-of-record.md](0006-viewing-the-system-of-record.md).

### QA Actions

Actions to approve/withdraw submission or request an update will be shown under the key information. These will only be available to regulator users.

The actions available will be:
- Approve submission (primary action)
- Request update
- Withdraw submission

When the user clicks on any of these actions, they will either be taken to a new screen (starting a journey that will return them back to the review) or a sidebar will open where they can complete action.

The approach we will take is still yet to be determined.

### Regulator managed information

Regulator managed information will be displayed in a card above the summary of submission. It will include the geographical location and prospect/discovery group information.

### Access from work area

The work area already shows applications that are in a submitted state. So no changes need to made to make them visible.

#### Access control
As well as implementing the security rules at the screen level, we will also need to make the necessary changes to the work area query service so that only submissions a user can review are shown for submitted submissions.

#### Sorting
For industry users, draft submissions will be shown before submitted submissions.
For regulators, the sorting strategy is still to be determined. For now, we will presume it to be the same as for industry users.

### Notifications

When a submission is ready to be reviewed, an email will be sent to all relevant regulator users (user that have the Exploration role and be a member of the responsible team).

Emails will also be sent out when a regulator user completes any of the actions outlined in the QA Actions section.