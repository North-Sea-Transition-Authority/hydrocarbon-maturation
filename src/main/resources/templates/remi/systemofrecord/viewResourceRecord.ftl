<#include '../layout/layout.ftl'>

<#import '../submissiondetails/summary/_submissionSummary.ftl' as submissionSummary>

<#-- @ftlvariable name="submissionType" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType" -->
<#-- @ftlvariable name="resourceRecord" type="uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<#assign pageTitle = "${resourceRecord.resourceNameAndCentreOfInterest()}" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  breadcrumbsMap=breadcrumbsMap>

  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues key="Responsible team" value="${resourceRecord.responsibleTeam()}"/>
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

  <@fdsAction.buttonGroup>
    <@fdsActionDropdown.actionDropdown dropdownButtonText="Actions">

    </@fdsActionDropdown.actionDropdown>
  </@fdsAction.buttonGroup>

  <h2 class="govuk-heading-l">${submissionType.getTypeName()}</h2>

  <@submissionSummary.submissionSummary
    submissionType=submissionType.name()
    />

</@defaultPage>