# Viewing a resource record

## Problem statement

We have a requirement for users to be able to view in detail, hydrocarbon resources (prospect, discovery, field). They will have access to this screen from the search system of records screen.

Industry users should only have visibility of resources they are authorised to access and only if the resource is associated with an extant licence.

The user should be able to view a summary of key information about the resource including
- the resource name (from latest approved submission)
- the quad/block centre of interest (from latest approved submission)
- the responsible team (from central resource data)
- the responsible organisation (from latest approved submission)
- the licences (from latest approved submission)

The user should be able to see a history of approved submissions for a resource via a submissions drop down menu.
For each approved submission in the history, the user should be able to select and then view the full summary of the submission. 
By default, the latest approved submission will be selected and shown.

## Solution

### Security

We already have an annotation called [IsFromResponsibleTeam.java](..%2F..%2Fsrc%2Fmain%2Fjava%2Fuk%2Fco%2Fnstauthority%2Fhydrocarbonmaturation%2Fauthorisation%2FIsFromResponsibleTeam.java) that limits controller access to regulator/industry users that are members of the responsible team.
We would need to create another annotation that works very similarly, but that allows any NSTA user access regardless of whether they are on the responsible team or not.

### Summary of resource

As the summary at the top of the page contains key information from disparate entities:
- the resource name from resource details
- the quad/block centre of interest from trap properties entity
- the responsible team from responsible team entity
- the responsible organisation from resource details entity
- the licences general detail licenses entity

We will grab the key information values from the various entities and have them available in a single DTO.

There are two approaches we could use for doing this: 
1. Query out the individual entities and manually map them into the DTO.  
**Pro:**  Easier to read and can be tested with simple unit tests.  
**Con:**  Potentially requires multiple queries to get the values from all the entities. However, many of the entities will be in the JPA query cache, so this maybe mitigated in the best case.  

2. Use a JOOQ query, similar to the queries used in the work area and search system of records, that gets the values we need directly into the DTO in one query.  
**Pro:**  One query to get all the values.  
**Con:**  Requires integration tests and harder to read. However, functionality will be very similar to the work area and system of record query services. So the solution is well understood and will not be difficult to replicate and maintain, including the integration tests.

We will be going with the second option.

### Submissions drop down menu

In order to populate the submission drop down menu, we will need to add a service that queries out all the approved submission for a resource, ordered by approval date, and returned as a list of Displayable records.

### Full summary of submissions

We already have template macros and service methods to display the full summary of a given submission, for any submission type. We will simply reuse these.
