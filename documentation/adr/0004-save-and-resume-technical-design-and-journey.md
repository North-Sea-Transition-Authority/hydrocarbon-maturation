# Save and resume technical design and journey

## Context statement

The most common way of locating and resuming previous tasks available to users on our services, is through a work-area.
A work area gives the user or a team an overview of the work they need to carry out in the service. An example work area
can be seen on the [FDS work-area pattern](https://design-system.fivium.co.uk/patterns/work-area). ReMi requires
the ability to access all the submissions that a user with a certain membership of a team such as the exploration
team can view. The work area =pattern for ReMi requires a large amount of data retrieval, due to the range of business rules 
for accessing submissions and displaying the work area items, this consists of information such as:

- the type of submission
- the name of resource and the centre of interest quad block
- the organisation responsible
- the licence(s) the resource is located in
- the status of the submission
- the user team

This data is split across multiple tables in the database such as:

- resource
- submission_details
- general_details
- general_details_quadrant_and_blocks
- trap_properties
- teams
- team_member_roles

This will require an efficient way of accessing all of these items. 
So any approach chosen must consider the performance implications. The number of work area items to be recovered
could be up to dozens, our services such as [PWA](https://jira.fivium.co.uk/browse/EDU-6556)
had suffered from slow work-area performance. With work area taking an upwards of 14 seconds to load. Even in the case
of there being a low number of items on the work-area. 

## Display Implementation

The data to be displayed on the work-area needs to be displayed in the form of result cards outlined on the
[FDS work-area pattern](https://design-system.fivium.co.uk/patterns/work-area). These will need to be represented in Java to be passed to the Freemarker template for display. 

## Chosen approach [WIOS](https://github.com/Fivium/offshore-safety-directive/pull/119/files#diff-1234f50adcf347b188d4263cab594cd6091f8dfa168e8852d78315c6536cc1b0) style work area record with a map of model properties

The work area template will be kept simple. A map of model properties
will allow the easy of adding and removing values without the need of changing fields, getters and setters. The model properties
map can easily be tested in tests.

On WIOS the approach taken is a WorkAreaItem record where ModelAndView properties are stored in a map in the WorkAreaItem. Furthermore the items all have a type, allowing an easy check which macro on the freemarker template should be called in the event
of multiple work area item types. This is a significant advantage over using a record or a DTO with fields representing different values, since it only requires a single class to represent all types of work area items
whereas the other approach requires a new WorkAreaItem definition for each type, and changes in the service. For the purposes of ReMi this enum will currently only contain the value of 'SUBMISSION'
but in the future this could be expanded to contain other types of items, such as a relinquishments and corrections if needed. Below you can see an example WorkAreaItem record. 

```java
public record WorkAreaItem(
  WorkAreaItemType type,
  String headingText,
  String actionUrl,
  WorkAreaItemModelProperties modelProperties
) {}
```

The WorkAreaItemModelProperties type is a wrapper around a map, providing a builder method for adding properties allowing the chaining of values. Which works similarly
to passing values to a model and view in a controller. The simplified example below creates a work area item based on a query result item.

```java

@Service
class WorkAreaItemService {
  private final WorkAreaQueryService workAreaQueryService;
  
  @Autowired
  WorkAreaItemService(WorkAreaQueryService workAreaQueryService) {
    this.workAreaQueryService = workAreaQueryService;
  }
  
  List<WorkAreaItem> getWorkAreaItems() {
    return workAreaQueryService.getAllWorkAreaQueryResultItems()
        .stream()
        .map(this::convertToWorkAreaItem)
        .toList();
  }
  
  private WorkAreaItem convertToWorkAreaItem(WorkAreaQueryResultItem workAreaQueryResultItem) {
    var dataItems = new LinkedHashSet<DataItem>();
    dataItems.put("Licence", "AL10");
    dataItems.put("Status", "Draft");
    
    var modelProperties = new WorkAreaItemModelProperties()
      .addProperty("organisationResponsible", "SHELL")
      .addProperty("dataItems", dataItems);
    
    return new WorkAreaItem(
       WorkAreaItemType.SUBMISSION,
       "resource name",
       "/url",
       modelProperties
    );
  }
}
```

A big benefit of this method is that WIOS had chosen this approach, this allows some re-use of [their code](https://github.com/Fivium/offshore-safety-directive/pull/116/files#diff-71fde33e39a6031886d8f2d210b29f41321724f7e83578946bff2500cde0548a).
Any issues with this pattern could have already been worked out by their team and any questions could be directed towards the WIOS team if needed.

## Other options considered

### A simple work area item record
The simplest approach would be to create a work area item record. Since the work area items all contain the same types of information, and there is currently no plan on ReMi to have different types of work-area items, therefore the solution does not need to be very generic or account for different work-area item types. There
could be a single WorkAreaItem record representing each work area item. These records would be instantiated in a WorkAreaItemProviderService,
which would receive a list of DTO objects retrieved by the query and would map them to a list of work area items. Any missing data items could be handled in the Freemarker template.

```java
record WorkAreaItem(String resourceName,
                    CaptionType caption,
                    String actionUrl) {
}
```

```java
@Service
class WorkAreaItemProviderService {
  
  public List<WorkAreaItem> getWorkAreaItemsFromDto(List<WorkAreaQueryResultItem> workAreaQueryResultItemList) {
    return workAreaQueryResultItemList
            .stream()
            .map(resultItem -> new WorkAreaItem(resultItem.getResourceName(), resultItem.getCaption(), resultItem.getActionUrl()))
            .toList();
  }
}
```

The record could be provided with as many values and types of information as required. While the WorkAreaItemProvider service
creates the list of items to be passed to the template. The advantage of this approach is that
it is very similar to the currently used SummaryView pattern we use on ReMi for our review and submit page. Therefore, it should
be easy to understand for anyone familiar with ReMi. Furthermore, the service could do some processing on the result items,
such as providing default values. Representation of the work area items on the freemarker template would be a 
general macro for the WorkAreaItem, which would be called from the work area page template for each work area item passed to the template by the controller. 

This was dismissed as an option due to its lack of flexibility, if we had to implement a different type of work-area, with different items to be displayed
it would require another record type and a new service. 

### Option 2 Work area item builder pattern
Another approach would be something such as outlined in [WIOS](https://github.com/Fivium/offshore-safety-directive/pull/74/files).
Where each work area item contains a builder class which allows you to build the item. A service would call the methods required
to be provided for the work area item. 

Something such as
```java
class WorkAreaItem {
  private final String resourceName;
  private final String caption;
  private final String actionUrl;
  private WorkAreaItem(String resourceName, String caption, String actionUrl) {
    this.resourceName = resourceName;
    this.caption = caption;
    this.actionUrl = actionUrl;
  }
  static WorkAreaItemBuilder builder() {
    return new WorkAreaItemBuilder();
  }
  static class WorkAreaItemBuilder {
    private String resourceName = "MISSING_RESOURCE_NAME";
    private String caption;
    private String actionUrl = "/url";
    WorkAreaItemBuilder withName(String resourceName) {
      this.resourceName = resourceName;
      return this;
    }
    WorkAreaItemBuilder withCaption(String caption) {
      this.caption = caption;
      return this;
    }
    WorkAreaItemBuilder withActionUrl(String actionUrl) {
      this.actionUrl = actionUrl;
      return this;
    }
    WorkAreaItem build() {
      return new WorkAreaItem(resourceName, caption, actionUrl);
    }
  }
}
```
Once the user loads the work area page, each submission returned by the query would create a WorkAreaItem for that submission, 
the missing values could be provided by the builder or the user of the builder depending on the submission. The
items provided as WorkAreaItem could be accessed from the Freemarker template such as. 

```html
<#list workAreaItems as workAreaItem>
    <#-- render -->
</#list>
```
There could be if statements for handling missing information if no default is provided such as dataItem1?has_content.
The big benefit of this method is that it is quite simple, the work-area freemarker template would be kept minimal and the code
should be easy to understand. Constructing a WorkAreaItem would be a simple operation on a DTO returned by the query. 
Although every time the component would be changed, it would require changes to the Freemarker template for the item. This
would be very similar to the record approach outlined in the previous section. 

This is a good approach but dismissed primarily due to the preference of using the record and a map which requires less code than the builder. 

## Data retrieval

For data retrieval, we will go with WIOS' approach of using [JOOQ](https://www.jooq.org/) which is an effective
readable way of writing SQL queries in Java. The java queries written in JOOQ bear heavy resemblance to raw SQL whilst 
being readable Java code. 

```
Result<Record> result =
create.select()
    .from(AUTHOR.as("a"))
    .join(BOOK.as("b")).on(a.ID.eq(b.AUTHOR_ID))
    .where(a.YEAR_OF_BIRTH.gt(1950)
    .and(a.FIRST_NAME.eq("Bob")))
    .orderBy(b.TITLE)
    .fetch();
```
Above you can see a simple example of a JOOQ query with a join operation. Even to someone unfamiliar with JOOQ this is relatively easy to understand.
The query above, retrieves books and authors by authors with 1950 year of birth and first name Bob. JOOQ is very flexible, since a query is just a series of builder methods,
it's easy to apply predicates conditionally. Although an issue with JOOQ is that the setup and configuration of JOOQ for a project is quite difficult, but this is mitigated since WIOS already did the work integrating it in their project. 
Furthermore, since JOOQ supports many database systems, if ReMi ever switched away from Postgres, migrating the JOOQ queries to the new DB system would be a simple configuration change. 
The biggest benefit will be the ease of readability which will allow any new team members on ReMi to quickly learn what each JOOQ query does, by simply reading it. Since understanding it requires only basic knowledge of SQL. 
Although type safety stuff with the AUTHOR and BOOK tables outlined above is not acquired out of the box in JOOQ, initially work will be done without it due to the difficulty of the setup of the JOOQ code generation tools necessary,
In WIOS they have a Jira issue about implementing it in their backlog. Although using string literals for the table names won't be too much of a problem since we don't envision them being changed often. 

The example below will be the same query but written with JPA's CriteriaBuilder.

```
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<Record> query = cb.createQuery(Record.class);
Root<Author> a = query.from(Author.class);
Join<Author, Book> b = a.join(Author_.books);

query
    .select(cb.construct(Record.class, a, b))
    .where(cb.and(
        cb.gt(a.get(Author_.yearOfBirth), 1950),
        cb.equal(a.get(Author_.firstName), "Bob"))).orderBy(cb.asc(b.get(Book_.title)));

List<Record> result = entityManager.createQuery(query)
    .getResultList();
```

As you can see the CriteriaBuilder query is a lot harder to read and follow, CriteriaBuilder queries being hard to read and follow results in a high likelihood
of errors or bugs slipping in. Although it has the big benefit of being built-in in JPA and not requiring bringing in another library, and it is therefore most likely that people on the team are at least somewhat familiar with it. 

We are dismissing this option, due to its lack of readability and complexity of the code.

Another option, HQL(Hibernate Query Language) had been considered. Below is the same query once again, this time using HQL.

```
String hql = "SELECT a, b FROM Author a INNER JOIN a.books b " +
    "WHERE a.yearOfBirth > :yearOfBirth AND a.firstName = :firstName " +
    "ORDER BY b.title";

Query query = entityManager.createQuery(hql);
query.setParameter("yearOfBirth", 1950);
query.setParameter("firstName", "Bob");

List<Object[]> result = query.list();
```

As can be seen by the above code, HQL is closest to raw SQL out of the options considered. This has some benefits, especially
of being very readable. The major issue with HQL is that the queries are just strings, adding or removing predicates
dynamically necessitates re-writing the whole query. Which can result in very hard to trace errors. This is the 
approach initially chosen by [SCAP](https://github.com/Fivium/supply-chain-action-plan/pull/106/files#diff-237d1596c35e90f2e900d51b1c178e5a2258aad17738c9d5b542f8836952ec44) but abandoned in favour of JOOQ due to its greater ease of use and readability since
it became apparent using strings for queries can very quickly get hard to follow and manipulate if certain aspects of the query are conditional.
Furthermore HQL uses entities rather than tables which can be confusing despite looking like SQL code. In addition it is very similar to JQL (JPA query language), which means likely many of the team members would be at least familiar with it. 

HQL has not been chosen primarily due to the issue of it using strings. 

Another possibility was using JPA repository methods and doing multiple queries to retrieve the required information. But
this was quickly discarded due to the performance implications of doing large numbers of queries. Running a single large
query with a lot of join operations is [generally faster](https://dba.stackexchange.com/questions/76973/what-is-faster-one-big-query-or-many-small-queries) than running many small queries.
 
 
 ## Work going forward
 
The preliminary work on the work-area will be undertaken in [HM-276](https://ogajira2.atlassian.net/browse/HM-276) which will implement the work area for the Exploration team, where the chosen pattern will be created and implemented. 
Though it must be noted certain things such as pagination and tabs may be added later to the work area with other issues when needed. 
Initially the work area will consist of a single page with the items, like on the [FDS work-area pattern](https://design-system.fivium.co.uk/patterns/work-area). 
With the items representing submissions. Furthermore this will only be the preliminary work, in [HM-357](https://ogajira2.atlassian.net/browse/HM-357) 
a work area for the area team will be created which will presumably utilise the same pattern. In addition JOOQ code generation might be implemented in a future issue to add in type safety to the JOOQ queries,
following some further discussion whether its necessary for REMI.
