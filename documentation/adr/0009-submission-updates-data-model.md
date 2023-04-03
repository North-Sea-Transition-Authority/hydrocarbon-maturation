# Creating submission updates - data model
- Author: Danny Betts
- Status: DRAFT
- Approved by:

## Requirements

This document compares the pros and cons of two approaches to the data model required to allow updates to submissions.
Submission updates will allow users to submit a new version of a submission if any data is deemed to be incorrect or inaccurate.
The ReMI data model should support updates while allowing for maintainability when future submission types are introduced.

## Option 1 - create separate 'updates' model

Creating a new updates model will consist of a new `SubmissionUpdate` entity which has its own status column to control which versions
of the submission is shown in different scenarios, e.g. which one appears in the work area and which one appears when reviewing the submission as a whole.

The `SubmissionUpdate` model would be:

| id  | submissionDetailId | fromSubmissionDetailId | updateReason                         | updateRequestedBy | status               |
|-----|--------------------|------------------------|--------------------------------------|-------------------|----------------------|
| 1   | 123                |                        | The initial submission was incorrect | 123               | SUBMITTED            |
| 2   | 456                | 123                    |                                      |                   | DRAFT_UPDATE         |

The work area and review screens will then need to filter based on the update status:
 - the regulator review screen will look at the latest `SUBMITTED` update
 - the operator work area will look at the submitted detail where the update status is `DRAFT_UPDATE` or the latest `SUBMITTED` update

Once the draft update is submitted the status becomes SUBMITTED and the review screen will switch to look at this one. 
The `SubmissionDetail` model will continue to work as is.

## Option 2 - introduce parent submission model

Creating a new parent submission model will consist of creating a new `Submission` entity which sits between the `Resource`
and the `SubmissionDetail`. A single `Resource` can still have multiple submissions against it, and those submissions can have multiple details. 

The `Submission` model could be:

| id  | resource |
|-----|----------|
| 1   | 123      |
| 2   | 456      |
| 3   | 456      |

The `SubmissionDetail` entity will then be updated to include the `Submission` as a new column. 
When a new update request comes in a new `DRAFT` `SubmissionDetail` will be created and the details copied forward from the previous version.
The new `SubmissionDetail` version will be incremented by 1 from the previous version.

The work area and review screens will then need to filter based on the following:
- the operator work area will look at the latest version of the submission whether that is `DRAFT` or `SUBMITTED`
- the review screen will look at the latest `SUBMITTED` version of the submission

## Pros and cons
### Option 1

Pros:
- Low impact on the service as it is currently built

Cons:
- Bespoke updates model which doesn't replicate other similar services
- Complex logic and non-intuitive

### Option 2

Pros:
- Standard updates model using a parent-child relationship and version numbers
- Simple logic and more maintainable going forward

Cons:
- Potential large impact on the service as it is currently built

## Decision

The decision is to introduce a `Submission` model (option 2). The direct link between `Resource` and `SubmissionDetail` will be removed. There will be some effort to ensure the work area and review items continue to work as expected,
but this will mean easier maintainability in the future as it will be very similar to other services that have an updates feature.
The system of record should be unaffected by this change as that uses the latest approved versions of submissions of which there will
only be one approved version of a `Submission`.