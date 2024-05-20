# Table usability features
* Author: Alex Brown
* Status: APPROVED
* Approved by: Danny B, Matt E
  * Also used feedback from James B

## Context
There are various goals for this SPIKE including sticky table headers (1), column sorting (2) and restoring table state (3) when returning to the page.
Ideally this will be reusable. 

### Issue 1 - Sticky table headers
* When a table is used that contains a large volume of data, this will result in a user having to scroll far down a page
* Having the headers remain on screen as opposed to disappearing will make it easier to correlate values in a row to the relevant heading

### Issue 2 - Column sorting
* As it says on the tin, a user may want to sort by specific column values to compare data
* In our case, this will be a user selecting one column to sort by. At which point, if two values match they will then be sorted by a fixed secondary column.
* It should be clear to the user what they are sorting by and which direction (asc/desc)

### Issue 3 - Retain table state
* If a user has sorted the table, scrolled to a certain row, clicked a link to view more details and then returned to the original page it should restore the table state
* This ties in to usability feature 1 - if there is a large amount of data it will be time-consuming for the user to sort the table and locate the row they were at again
  * This is since the table will no longer be sorted. If it returns to the same point on the screen but the values are different, it will likely be frustrating and cumbersome to use.

## Possible approaches

### Issue 1 - sticky table headers
* This can be tackled solely through styling. 
  * For example, applying the following attributes to th elements:
  { top: 0, position: sticky, background-color: white }
  * More info [here](https://css-tricks.com/position-sticky-and-table-headers/) and [here](https://codyhouse.co/nuggets/sticky-table-header)
* With regard to accessibility, [this article](https://technology.blog.gov.uk/2018/05/21/sticky-elements-functionality-and-accessibility-testing/) suggests there could be instances where having a sticky element obscures the focussed element.
  * They were using a generic sticky element as opposed to working with tables
  * I briefly tested this via tabbing, and noticed the following:
    * Elements above the table had no issue with being obscured, which makes sense 
    * When rows in the table have action links, these became (partially) obscured by the header if they were in a row at the top of the page. This issue was more prominent after zooming in.
  * Worth looking into possible ways this could be prevented, or confirming if this is a concern on our pages.

### Issue 2 - column sorting

#### Option 1 - fields to define sort order
* Provide a set of fields that the user can select to customise their sort before pressing a "sort" button, for example.
* This would be more flexible and facilitate complex sort requirements in the future, although **not required for our current use case**.
* Need to track multiple fields, their sort directions, and the order of precedence in sorting while making it usable
  * This wouldn't be overly complicated to write the sort method in js, for example
  * Something like vue draggable could be useful and intuitive to set the order of precedence, for example
* **Considerable overhead in dev time required**

#### Option 2 - frontend column sort, i.e. click a header
* Allow a user to click a table header to sort by that column. Key points to note include:
    * Visible change to signify to the user how it is being sorted: which column, which direction
* In our case, we want a secondary sort by a fixed column. Although this could be implemented, the user hasn't specified this secondary sort.
    * Ordering may be intuitive to some, but would we need to make this more explicit? How would we do so?
* Can be implemented in vue / fds / custom js
  * IMO would be easier in Vue, although I am biased given exposure to Vue on eCase. 
  * I assume it wouldn't be overly complex as an FDS component - would be good to get some insights on this?

### Issue 3 - retain table state

#### Similarity to accordions - FDS component
* Currently, the accordions component in FDS restores state, specifically whether they are expanded. 
  * This is done by accessing the "aria-expanded" attribute and setting this in session storage (accessing again to set element state).
* If opting to create an FDS component for tables **and** option 2 for issue 2 (sorting), we could use a similar approach and store _sortedColumn_ and _sortDirection_ (for example). 
  * They store against a unique id for the accordion. We could do the same but suffix with appropriate values to differentiate column index/direction.
  * Store this on call to sort table in js.
* It is worth noting that if we opted for option 1 w.r.t. column sorting, the complexity of this would be increased
  * Could solve using some csv in order of precedence, i.e. "col1_asc, col2_desc..." and then rebuild the sort from that 

#### Vue
* We could likely use a similar approach and use session storage, setting the initial state early in component lifecycle
* There is a [keepalive component](https://vuejs.org/guide/built-ins/keep-alive) in vue that caches the state when a component is deactivated. 
  * I am under the impression this would likely not work in our use-case since we are embedding it within a page (not familiar with existing usage of vue in FDS-based projects). Since you would be leaving the page I am not sure if the Vue caching would work here - may be worth checking.

## Initial overview
* Holistically, it seems doable as an FDS component.
* Issues 1 and 3 seem achievable with negligible difference in dev time when it comes to freemarker/vue/custom js
  * Remaining questions over accessibility for sticky headers
  * Issue 3 dev time would increase if opting to facilitate complex sorts
* W.r.t. issue 2, if we choose option 1 I believe Vue would be easier and nicer to both implement and use, given the complexity around setting sort precedence with numerous columns.
  * Although, doing it in Vue means it would be harder to reuse currently. Given the dev time to implement this feature, it would be nice to reuse across projects.
  * Option two is much simpler with the main question being how to make it clear that/if there is a secondary sort applied

## Summary of feedback
Discussed in slack [here](https://fivium.slack.com/archives/C817PRELX/p1715852257249129) and PR [here](https://github.com/Fivium/hydrocarbon-maturation/pull/738)
* Should not be put off by it being the first Vue component in FDS - this is doable, and we have an idea of how to do it
* Preference towards option 2 for issue 2 (sorting) - simple frontend column sort should suffice
  * We don't need to worry about making a secondary sort explicit
* Confirmed that we can store sort column/order in sessionStorage **provided an ID is made mandatory**
* For the accessibility concern of a sticky table header obscuring a focussed element, we could explore an event listener on focusable elements - scroll into view on focus
* We should allow an optional attribute "sortValue" in the data structure passed in for instances where a standard sort wouldn't conform to expected behaviour
  * For example:
    * [
      { value: "1", sortValue: 1 },
      { value: "1a", sortValue: 2 },
      { value: "1b", sortValue: 3 },
      { value: "2", sortValue: 4 },
      { value: "12", sortValue: 5 }
      ]
  * If sorted by _value_ it would produce 1, 1a, 1b, 2, 12. 
  * If by _sortValue_ it would produce 1, 12, 1a, 1b, 2
* Some libraries could provide this functionality, i.e. tabulator

## Decision

### Issue 1 - sticky table headers
* Basic CSS as detailed above
* Look into a possible event listener on focusable elements to scroll into view on focus 
  * This could prevent them being obscured

### Issue 2 - column sorting
* Simple frontend sort by column 
  * User selects column to sort by
  * Dev can specify a secondary column to sort by
* If the data structure passed in makes use of optional _sortValue_ attribute, use this when sorting a given column
  * Else default to _value_

### Issue 3 - restore table state
* Use session storage - look at accordions for example
* Mandate an ID on the table component to store against
* Need to store sort column and order (asc/desc)

## Proposed implementation
Given we have an idea how we would incorporate Vue into FDS, and Vue is simpler for our requirements, I believe we should use Vue for this
* Simple condition for visible changes that signify sort order and column sorted by (i.e. up/down arrow in sorted column - get this approved)
* Create data structure that takes in a "sort value" - sort by this if present, else default to the value
  * An extra optional attribute for data type, i.e. numbers, dates etc and sort depending on data type, could be valuable
    * Again, only use if no sortValue provided
  * This would prevent backend having to calculate sortValue for instances where it is easily comparable on the FE
* For scrolling into view on focus, [this](https://tetralogical.com/blog/2023/06/08/focus-in-view/) could be useful
  * CSS attribute _scroll-padding_ although this requires knowledge of the size of the sticky header
    * Could look into getting the element height like [this](https://stackoverflow.com/questions/44948714/get-element-height-with-vuejs) at runtime
    * Or, look at way of inheriting/calculating this from the _govuk-table__header_ line height + padding.
* For session storage, refer to accordions implementation. Very simple.

### Vue integration
* Use field consents for example: use [vue rollup plugin](https://rollup-plugin-vue.vuejs.org/) in existing gulpfile
  * Ideally, use separate bundle to prevent breaking rest of FDS
* Experiment with SASS
  * Inline in vue component preferred, but may be limited by inheritance rules with GDS styling
    * Not mandatory, can reference gds directly if not possible

### Estimated time ~ 3 to 4 days