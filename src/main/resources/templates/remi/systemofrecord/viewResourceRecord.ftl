<#include '../layout/layout.ftl'>

<#import '../submissiondetails/summary/_submissionSummary.ftl' as submissionSummary>
<#import '../systemofrecord/submissionHistorySearchSelector.ftl' as submissionSelector>
<#import '../summarylistcards/regulatorManagedInformationSummaryCard.ftl' as regulatorManagedInformationSummary>

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
  breadcrumbsMap=breadcrumbsMap>

  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues key="Responsible team" value="${resourceRecord.responsibleTeam().displayName}"/>
    <@fdsDataItems.dataValues key="Responsible organisation" value="${resourceRecord.organisationResponsible()}"/>
    <#assign licencesDisplay>
        <#if resourceRecord.licences()?has_content>
            <#list resourceRecord.licences() as licence>
                ${licence}<#if licence?has_next>,</#if>
            </#list>
        </#if>
    </#assign>
    <@fdsDataItems.dataValues key="Licenses" value="${licencesDisplay}"/>
  </@fdsDataItems.dataItem>
  <#if canManageRegulatorManagementInfo>
    <@fdsAction.buttonGroup>
      <@fdsAction.link
        linkText="Manage resource information"
        linkClass="govuk-button govuk-button--secondary"
        linkUrl=springUrl(regulatorManagedInformationUrl)/>
      <@fdsAction.link
        linkText="Manage resource group"
        linkClass="govuk-button govuk-button--secondary"
        linkUrl=springUrl(resourceGroupUrl)/>
    </@fdsAction.buttonGroup>
  </#if>
  <@fdsForm.htmlForm actionUrl=springUrl(submissionSelectedUrl)>
    <@submissionSelector.submissionHistorySearchSelector
      resourceType=resourceType
      submissionsOnResource=submissionsOnResource />
  </@fdsForm.htmlForm>

  <h2 class="govuk-heading-l">${submissionType.getTypeName()}</h2>
  <#if canViewRegulatorManagementInfo>
    <@regulatorManagedInformationSummary.regulatorManagedInformationSummaryCard
      regulatorManagedInformationValues=regulatorManagedInformationSummaryView
      resourceGroupsValues=resourceGroupsSummaryView/>
  </#if>
  <@submissionSummary.submissionSummary
    submissionType=submissionType.name()
    />

</@defaultPage>