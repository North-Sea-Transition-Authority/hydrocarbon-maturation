<#include '../layout/layout.ftl'>
<#import 'resourceRecordSearchResultItem.ftl' as resourceRecordSearchResultItem>
<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="filtersAdded" type="Boolean" -->
<#-- @ftlvariable name="resourceRecordSearchResultItems" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSearchResultItem>" -->
<#-- @ftlvariable name="resourceApiEndpoint" type="String" -->
<#-- @ftlvariable name="organisationUnitEndpoint" type="String" -->
<#-- @ftlvariable name="clearFilterUrl" type="String" -->

<#assign pageTitle = "${pageTitle}" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  wrapperWidth=true
  noIndex=false
  backLinkUrl=springUrl(backLinkUrl)>

  <@fdsSearch.searchPage>
    <@fdsSearch.searchFilter oneThirdWidth=true>
      <@fdsSearch.searchFilterList clearFilterUrl=springUrl(clearFilterUrl)>
          <@fdsSearch.searchFilterItem itemName="Field name" expanded=resourceName?has_content>
              <@fdsSearchSelector.searchSelectorRest
              path="form.fieldNameForResourceSubmission"
              restUrl=springUrl(fieldApiEndpoint)
              labelText="Field name"
              labelClass="govuk-visually-hidden"
              inputClass="govuk-!-width-three-quarters"
              selectorMinInputLength=3
              preselectedItems=resourceName!{}/>
          </@fdsSearch.searchFilterItem>
          <@fdsSearch.searchFilterItem itemName="Field status">
              <@fdsSearch.searchCheckboxes path="form.fieldStatuses" checkboxes=fieldStatusOptions/>
          </@fdsSearch.searchFilterItem>
          <@fdsSearch.searchFilterItem itemName="Geographical area">
              <@fdsSearch.searchCheckboxes path="form.geographicalArea" checkboxes=geographicalArea/>
          </@fdsSearch.searchFilterItem>
          <@fdsSearch.searchFilterItem itemName="Parent field" expanded=parentFieldName?has_content>
              <@fdsSearchSelector.searchSelectorRest
              path="form.parentFieldName"
              restUrl=springUrl(fieldApiEndpoint)
              labelText="Parent field"
              labelClass="govuk-visually-hidden"
              inputClass="govuk-!-width-three-quarters"
              selectorMinInputLength=3
              preselectedItems=parentFieldName!{}/>
          </@fdsSearch.searchFilterItem>
      </@fdsSearch.searchFilterList>
    </@fdsSearch.searchFilter>
    <@fdsSearch.searchPageContent twoThirdsWidth=true>
      <#if filtersAdded>
        <#if resourceRecordSearchResultItems?has_content>
            <#if maxResultsReturned>
                <@fdsInsetText.insetText>
                  The search list has been limited to 200 results. Filter the search further to see more results.
                </@fdsInsetText.insetText>
            </#if>
            <@fdsResultList.resultList resultCount=resourceRecordSearchResultItems?size>
                <#list resourceRecordSearchResultItems as item>
                    <@resourceRecordSearchResultItem.fieldRecordSearchResultItem resultItem=item/>
                </#list>
            </@fdsResultList.resultList>
        <#else>
          <h3 class="govuk-heading-s">There are no matching results</h3>
          <p class="govuk-body">Improve your results by:</p>
          <ul class="govuk-list govuk-list--bullet">
            <li>removing filters</li>
            <li>double-checking your spelling</li>
            <li>using fewer keywords</li>
            <li>searching for something less specific</li>
          </ul>
        </#if>
      <#else>
        <@fdsInsetText.insetText>
          To search, add some filters and click the 'Filter results' button.
        </@fdsInsetText.insetText>
      </#if>

    </@fdsSearch.searchPageContent>
  </@fdsSearch.searchPage>
</@defaultPage>