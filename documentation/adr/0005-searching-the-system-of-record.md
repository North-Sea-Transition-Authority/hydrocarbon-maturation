# Searching the system of record

## Problem statement

We have a requirement for users to be able to search for hydrocarbon resources (prospect, discovery, field) using a variety of search criteria.

Industry users should only have visibility of resources they are authorised to access

The user should be able to filter by the following criteria:
- The name of the resource
- The centre of interest quad/block for the resource
- The responsible organisation
  - The options will be limited to the organisations that the regulator/industry user has responsibility for
- The licence(s) the resource is located in
- The licence round the prospect or discovery was awarded on
- The geographical area for the resource
- The status of the resource

For each resource, the user should see the following information from the latest approved submission of the resource:
- The name of the resource the submission is for (including centre of interest quad/block)
- The type of resource (prospect, discovery, field)
- The responsible organisation
- The licence(s) the resource is located in

## Solution

### Querying

Similar to the work area, we will use JOOQ for querying the database. However unlike the work area, we will build the query dynamically.
Depending on the filters applied, any appropriate joins and condition predicates will be added conditionally.

The JOOQ query will be similar to:

```java
import java.util.Objects;

class ResourceQueryService {

  List<ResourceResultItemDto> searchResources(ResourcesSearchForm resourcesSearchForm) {
    return context
        .select(
            field("resources.id"),
            field("submission_details.id"),
            field("general_details.resource_name"),
            field("trap_properties.centre_of_interest_quadrant_block"),
            field("general_details.organisation_id"),
            DSL.arrayAgg(field("general_details_licences.licence_id")).filterWhere(
                field("general_details_licences.licence_id").isNotNull())
        )
        .from(getFromClause(resourcesSearchForm))
        .where(getAuthorisationConditions())
        .and(getPredicateConditions(resourcesSearchForm))
        .fetchInto(ResourceResultItemDto.class);
  }

  private Table getFromClause(ResourcesSearchForm resourcesSearchForm) {
    var fromClause = table("resources")
        .join(table("submission_details"))
          .on(field("submission_details.resource_id").eq(field("resources.id")))
          .and(field("submission_details.id").eq(
            // subquery to get the latest approved submission for the resource
          ))
        .join(table("general_details"))
          .on(field("general_details.submission_detail_id").eq(field("submission_details.id")))
        .join(table("general_details_licences"))
          .on(field("general_details_licences.general_detail_id").eq(field("general_details.id")))
        .join(table("trap_properties"))
          .on(field("trap_properties.submission_detail_id").eq(field("submission_details.id")))
        .join(table("responsible_teams"))
          .on(field("responsible_teams.resource_id").eq(field("resources.id")));

    // if resource status provided from input form then add join

    if (Objects.nonNull(resourcesSearchForm.getResourceStatus())) {
      fromClause = fromClause
          .join(table("opportunity_details"))
            .on(field("opportunity_details.submission_detail_id").eq(field("submission_detail_id.id")));
    }

    // if licence round provided from input form then add join

    // if geographical area of resource provided from input form then add join

    return fromClause;
  }

  private List<Condition> getAuthorisationConditions() {
    var user = userDetailService.getUserDetail();

    // add conditions limiting results to resources the industry user is authorised for

    // if regulator user
    var roles = teamMemberService.getUserAsTeamMembers(user)
        .stream()
        .flatMap(teamMember -> teamMember.roles().stream())
        .toList();

    if (roles.contains(RegulatorTeamRole.EXPLORATION)) {
      return field("responsible_teams.responsible_team").eq(ResponsibleTeamType.EXPLORATION.name());
    }

    return falseCondition();
  }

  private List<Condition> getPredicateConditions(ResourcesSearchForm resourcesSearchForm) {

    List<Condition> predicateList = new ArrayList<>();
    
    // if responsible filter provided from input form then add predicate
    if (StringUtils.isNotBlank(resourcesSearchForm.getResponsibleOrgansiationId())) {
      predicateList.add(
          field("general_details.organisation_id")
              .eq(Integer.parseInt(resourcesSearchForm.getResponsibleOrgansiationId()))
      );
    }

    // if resource name provided from input form then add predicate

    // if centre of interest quad/block provided from input form then add predicate

    // if licence(s) provided from input form then add predicate

    // if licence round provided from input form then add predicate

    // if geographical area of resource provided from input form then add predicate

    // if resource status provided from input form then add predicate

    // ...

    return predicateList;
  }
}
```

### Rendering resources

We will take similar approach to the work area and WIOS' approach to system of records. We will populate a `ResourceResultItemModelProperties`
class which wraps a `Map` of model and view properties. This approach allows us to keep this functionality extensible to future requirements,
and we would have the flexibility without needing to refactor large parts of the code.

```java
public record ResourceResultItem(
    String headingText,
    String captionText,
    String actionUrl,
    ResourceResultItemModelProperties modelProperties
) {}
```