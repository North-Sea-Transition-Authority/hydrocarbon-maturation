<#include '../layout/layout.ftl'>

<#import '../submissiondetails/summary/_submissionSummary.ftl' as submissionSummary>
<#import '../systemofrecord/submissionHistorySearchSelector.ftl' as submissionSelector>
<#import '../summarylistcards/regulatorManagedInformationSummaryCard.ftl' as regulatorManagedInformationSummary>

<#-- @ftlvariable name="submissionType" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType" -->
<#-- @ftlvariable name="submissionRecord" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.SubmissionRecordSummaryView" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="canReview" type="boolean"-->
<#-- @ftlvariable name="isApproved" type="boolean"-->
<#-- @ftlvariable name="regulatorManagedInformationUrl" type="String"-->
<#-- @ftlvariable name="withdrawUrl" type="String" -->
<#-- @ftlvariable name="submissionSelectedUrl" type="String"-->
<#-- @ftlvariable name="submissionNumber" type="String"-->
<#-- @ftlvariable name="submissionsOnResource" type="java.util.Map<String, String>"-->
<#-- @ftlvariable name="resourceType" type="String"-->
<#-- @ftlvariable name="isRegulator" type="boolean"-->


<#assign pageTitle = "${submissionRecord.resourceNameAndCentreOfInterest()}" />
<#assign caption = "${submissionRecord.submissionType().getTypeName()}" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  caption=caption
  pageSize=PageSize.FULL_WIDTH
  breadcrumbsMap=breadcrumbsMap>

  <@fdsDataItems.dataItem>
      <@fdsDataItems.dataValues key="Responsible team" value="${submissionRecord.responsibleTeam()}"/>
      <@fdsDataItems.dataValues key="Responsible organisation" value="${submissionRecord.organisationResponsible()}"/>
      <#assign licencesDisplay>
          <#if submissionRecord.licences()?has_content>
              <#list submissionRecord.licences() as licence>
                  ${licence}<#if licence?has_next>,</#if>
              </#list>
          </#if>
      </#assign>
      <@fdsDataItems.dataValues key="Licenses" value="${licencesDisplay}"/>
  </@fdsDataItems.dataItem>
  <#if canReview>
    <@fdsAction.buttonGroup>
      <@fdsAction.link linkText="Approve submission" linkClass="govuk-button" linkUrl=springUrl(approveUrl)/>
      <@fdsAction.link
        linkText="Withdraw submission"
        linkClass="govuk-button govuk-button--secondary"
        linkUrl=springUrl(withdrawUrl)/>
      <@fdsAction.link
        linkText="Manage resource information"
        linkClass="govuk-button govuk-button--secondary"
        linkUrl=springUrl(regulatorManagedInformationUrl)/>
    </@fdsAction.buttonGroup>
  </#if>

 <@fdsForm.htmlForm actionUrl=springUrl(submissionSelectedUrl)>
   <@submissionSelector.submissionHistorySearchSelector
      resourceType=resourceType
      submissionsOnResource=submissionsOnResource/>
 </@fdsForm.htmlForm>

  <h2 class="govuk-heading-l">(${submissionNumber}) ${submissionType.getTypeName()}</h2>
  <#if isRegulator>
    <@regulatorManagedInformationSummary.regulatorManagedInformationSummaryCard
     regulatorManagedInformationValues=regulatorManagedInformationSummaryView/>
  </#if>
  <@submissionSummary.submissionSummary
    submissionType="${submissionRecord.submissionType().name()}"/>

</@defaultPage>