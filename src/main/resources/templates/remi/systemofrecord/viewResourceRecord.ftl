<#include '../layout/layout.ftl'>

<#import '../submissiondetails/summary/_submissionSummary.ftl' as submissionSummary>
<#import '../systemofrecord/submissionHistorySearchSelector.ftl' as submissionSelector>
<#import '../summarylistcards/regulatorManagedInformationSummaryCard.ftl' as regulatorManagedInformationSummary>
<#import '../summary/_simpleSummary.ftl' as simpleSummary>

<#-- @ftlvariable name="submissionType" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType" -->
<#-- @ftlvariable name="resourceRecord" type="uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="canManageRegulatorManagementInfo" type="boolean"-->
<#-- @ftlvariable name="canViewRegulatorManagementInfo" type="boolean"-->
<#-- @ftlvariable name="regulatorManagedInformationUrl" type="String"-->
<#-- @ftlvariable name="submissionSelectedUrl" type="String"-->
<#-- @ftlvariable name="submissionsOnResource" type="java.util.Map<String, String>"-->
<#-- @ftlvariable name="resourceType" type="String"-->
<#-- @ftlvariable name="resourceGroupUrl" type="String"-->
<#-- @ftlvariable name="regulatorManagedInformationSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView" -->
<#-- @ftlvariable name="resourceGroupsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryView" -->
<#-- @ftlvariable name="isRegulator" type="boolean"-->

<#assign pageTitle = "${resourceRecord.resourceNameAndCentreOfInterest()}" />

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.FULL_WIDTH
breadcrumbsMap=breadcrumbsMap
wrapperWidth=true>

  <@fdsDataItems.dataItem>
    <#assign responsibleTeam = resourceRecord.responsibleTeam()
        ?has_content?then(resourceRecord.responsibleTeam().displayName, "")/>
    <#if isFieldResource>
      <@fdsDataItems.dataValues key="Field status" value="${resourceRecord.fieldStatus()}"/>
      <@fdsDataItems.dataValues key="Responsible organisation" value="${resourceRecord.organisationResponsible()}"/>
      <@fdsDataItems.dataValues key="Responsible team" value="${responsibleTeam}"/>
      <@fdsDataItems.dataValues key="Parent field" value="${resourceRecord.parentFieldName()}"/>
    <#else>
      <@fdsDataItems.dataValues key="Responsible team" value="${responsibleTeam}"/>
      <@fdsDataItems.dataValues key="Responsible organisation" value="${resourceRecord.organisationResponsible()}"/>
      <#assign licencesDisplay>
        <#if resourceRecord.licences()?has_content>
          <#list resourceRecord.licences() as licence>
            ${licence}<#if licence?has_next>,</#if>
          </#list>
        </#if>
      </#assign>
      <@fdsDataItems.dataValues key="Licenses" value="${licencesDisplay}"/>
    </#if>
  </@fdsDataItems.dataItem>
  <@fdsAction.buttonGroup>
    <#if canManageRegulatorManagementInfo>
      <@fdsAction.link
        linkText=manageResourceInformationText
        linkClass="govuk-button govuk-button--secondary"
        linkUrl=springUrl(regulatorManagedInformationUrl)/>
      <#if !isFieldResource>
        <@fdsAction.link
        linkText="Manage resource group"
        linkClass="govuk-button govuk-button--secondary"
        linkUrl=springUrl(resourceGroupUrl)/>
      </#if>
      <#if correctionUrl?has_content>
        <@fdsAction.link
        linkText="Correct submission"
        linkClass="govuk-button govuk-button--secondary"
        linkUrl=springUrl(correctionUrl)/>
      </#if>
    </#if>
    <#if canViewRegulatorManagementInfo
    && fieldStatusUrl?has_content>
      <@fdsAction.link
      linkText="Field status"
      linkClass="govuk-button govuk-button--secondary"
      linkUrl=springUrl(fieldStatusUrl)/>
    </#if>
  </@fdsAction.buttonGroup>
  <#if submissionsOnResource?has_content>
    <@fdsForm.htmlForm actionUrl=springUrl(submissionSelectedUrl)>
      <@submissionSelector.submissionHistorySearchSelector
        resourceType=resourceType
        submissionsOnResource=submissionsOnResource />
    </@fdsForm.htmlForm>
  </#if>

  <#assign regulatorManagedInformationHeading = submissionType?has_content?then(submissionType.getTypeName(), "") />
  <h2 class="govuk-heading-l">${regulatorManagedInformationHeading}</h2>
  <#if canViewRegulatorManagementInfo>
    <#if isFieldResource>
      <@simpleSummary.simpleSummary
      summaryDataView=fieldRegulatorManagedInformationSummaryCard.summaryData()
      summaryHeading=fieldRegulatorManagedInformationSummaryCard.displayName()!""/>
    <#else>
    <@regulatorManagedInformationSummary.regulatorManagedInformationSummaryCard
      regulatorManagedInformationValues=regulatorManagedInformationSummaryView
      resourceGroupsValues=resourceGroupsSummaryView/>
    </#if>
  </#if>

  <#if submissionsOnResource?has_content>
    <@submissionSummary.submissionSummary
    submissionType=submissionType.name()
    />
  </#if>

</@defaultPage>