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
  noIndex=false>

  <@fdsSearch.searchPage>
    <@fdsSearch.searchFilter oneThirdWidth=true>
      <@fdsSearch.searchFilterList clearFilterUrl=springUrl(clearFilterUrl)>
        <@fdsSearch.searchFilterItem itemName="Resource name" expanded=resourceName?has_content>
          <@fdsSearchSelector.searchSelectorRest
            path="form.resourceDetailIdForResourceSubmission"
            restUrl=springUrl(resourceApiEndpoint)
            labelText="Resource name"
            labelClass="govuk-visually-hidden"
            inputClass="govuk-!-width-three-quarters"
            selectorMinInputLength=1
            preselectedItems=resourceName!{}/>
        </@fdsSearch.searchFilterItem>
        <@fdsSearch.searchFilterItem itemName="Responsible organisation" expanded=responsibleOrganisation?has_content>
          <@fdsSearchSelector.searchSelectorRest
            path="form.responsibleOrganisationId"
            restUrl=springUrl(organisationUnitEndpoint)
            labelText="Responsible organisation"
            labelClass="govuk-visually-hidden"
            inputClass="govuk-!-width-three-quarters"
            selectorMinInputLength=1
            preselectedItems=responsibleOrganisation!{}/>
        </@fdsSearch.searchFilterItem>
        <@fdsSearch.searchFilterItem
          itemName="Centre of interest quadrant and block"
          expanded=centreOfInterestBlock?has_content>
          <@fdsSearchSelector.searchSelectorRest
            path="form.centreOfInterestBlock"
            restUrl=springUrl(quadrantBlocksApiEndpoint)
            labelText="Responsible organisation"
            labelClass="govuk-visually-hidden"
            inputClass="govuk-!-width-three-quarters"
            selectorMinInputLength=1
            preselectedItems=centreOfInterestBlock!{}/>
        </@fdsSearch.searchFilterItem>
        <@fdsSearch.searchFilterItem itemName="Licence" expanded=preselectedLicence?has_content>
          <@fdsSearchSelector.searchSelectorRest
            path="form.licenceId"
            restUrl=springUrl(licenceEndpoint)
            labelText="Licence"
            labelClass="govuk-visually-hidden"
            inputClass="govuk-!-width-three-quarters"
            selectorMinInputLength=1
            preselectedItems=preselectedLicence!{}/>
        </@fdsSearch.searchFilterItem>
        <@fdsSearch.searchFilterItem itemName="Licence round and reference" expanded=preselectedLicenceRound?has_content>
          <@fdsTextInput.textInput
            path="form.licenceRound"
            labelText=""/>
        </@fdsSearch.searchFilterItem>
        <@fdsSearch.searchFilterItem itemName="Resource type">
          <@fdsSearch.searchCheckboxes path="form.resourceTypes" checkboxes=resourceTypes/>
        </@fdsSearch.searchFilterItem>
        <@fdsSearch.searchFilterItem itemName="Resource status">
          <@fdsSearch.searchCheckboxes path="form.opportunityTypes" checkboxes=opportunityTypes/>
        </@fdsSearch.searchFilterItem>
        <@fdsSearch.searchFilterItem itemName="Geographical area">
          <@fdsSearch.searchCheckboxes path="form.geographicalArea" checkboxes=geographicalArea/>
        </@fdsSearch.searchFilterItem>
        <#if isRegulatorOrAreaTeamUser>
          <@fdsSearch.searchFilterItem itemName="Preferred view">
            <@fdsCheckbox.checkboxGroup smallCheckboxes=true path="form.showOnlyPreferredViews">
              <@fdsCheckbox.checkbox path="form.showOnlyPreferredViews" labelText="Show only preferred views"/>
            </@fdsCheckbox.checkboxGroup>
          </@fdsSearch.searchFilterItem>
        </#if>
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