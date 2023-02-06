<#include '../layout/layout.ftl'>
<#import 'resourceRecordSearchResultItem.ftl' as resourceRecordSearchResultItem>
<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="filtersAdded" type="Boolean" -->
<#-- @ftlvariable name="resourceRecordSearchResultItems" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSearchResultItem>" -->
<#-- @ftlvariable name="resourceApiEndpoint" type="String" -->
<#-- @ftlvariable name="organisationUnitEndpoint" type="String" -->

<#assign pageTitle = "${pageTitle}" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  noIndex=false>

  <@fdsSearch.searchPage>
    <@fdsSearch.searchFilter oneThirdWidth=true>
      <@fdsSearch.searchFilterList>
        <@fdsSearch.searchFilterItem itemName="Resource name" expanded=resourceName?has_content>
          <@fdsSearchSelector.searchSelectorRest
            path="form.generalDetailIdForResourceSubmission"
            restUrl=springUrl(resourceApiEndpoint)
            labelText="Resource name"
            labelClass="govuk-visually-hidden"
            selectorMinInputLength=1
            preselectedItems=resourceName!{}/>
        </@fdsSearch.searchFilterItem>
        <@fdsSearch.searchFilterItem itemName="Responsible organisation" expanded=responsibleOrganisation?has_content>
          <@fdsSearchSelector.searchSelectorRest
            path="form.responsibleOrganisationId"
            restUrl=springUrl(organisationUnitEndpoint)
            labelText="Responsible organisation"
            labelClass="govuk-visually-hidden"
            selectorMinInputLength=1
            preselectedItems=responsibleOrganisation!{}/>
        </@fdsSearch.searchFilterItem>
      </@fdsSearch.searchFilterList>
    </@fdsSearch.searchFilter>
    <@fdsSearch.searchPageContent twoThirdsWidth=true>
      <#if filtersAdded>
        <#if resourceRecordSearchResultItems?has_content>
            <@fdsResultList.resultList resultCount=resourceRecordSearchResultItems?size>
                <#list resourceRecordSearchResultItems as item>
                    <@resourceRecordSearchResultItem.resourceRecordSearchResultItem resultItem=item/>
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
          To search, add some filters and click the 'Filter' button.
        </@fdsInsetText.insetText>
      </#if>

    </@fdsSearch.searchPageContent>
  </@fdsSearch.searchPage>
</@defaultPage>