<#include '../layout/layout.ftl'>
<#import 'resourceRecordSearchResultItem.ftl' as resourceRecordSearchResultItem>
<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="filtersAdded" type="Boolean" -->
<#-- @ftlvariable name="resourceRecordSearchResultItems" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSearchResultItem>" -->

<#assign pageTitle = "${pageTitle}" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  noIndex=false>

  <@fdsSearch.searchPage>
    <@fdsSearch.searchFilter>
      <@fdsSearch.searchFilterList>
<#--        TODO HM-132 - Enable once functioning filters are added-->
<#--        <@fdsSearch.searchFilterItem itemName="Resource name">-->
<#--          <@fdsSearch.searchTextInput path="form.resourceName" labelText="Resource name" labelClass="govuk-visually-hidden"/>-->
<#--        </@fdsSearch.searchFilterItem>-->
      </@fdsSearch.searchFilterList>
    </@fdsSearch.searchFilter>
    <@fdsSearch.searchPageContent>
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