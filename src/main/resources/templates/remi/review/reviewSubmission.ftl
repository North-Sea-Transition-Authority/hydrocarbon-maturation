<#include '../layout/layout.ftl'>

<#import '../submissiondetails/summary/_submissionSummary.ftl' as submissionSummary>
<#import '../systemofrecord/submissionHistorySearchSelector.ftl' as submissionSelector>
<#import '../summarylistcards/regulatorManagedInformationSummaryCard.ftl' as regulatorManagedInformationSummary>
<#import '../review/updateSummaryDetails.ftl' as updateSummaryDetails>
<#import '../summary/_simpleSummary.ftl' as simpleSummary>
<#import '../summary/_regulatorInformationSummary.ftl' as regulatorSummary>

<#-- @ftlvariable name="submissionType" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType" -->
<#-- @ftlvariable name="submissionRecord" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.SubmissionRecordSummaryView" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="canReview" type="boolean"-->
<#-- @ftlvariable name="isApproved" type="boolean"-->
<#-- @ftlvariable name="regulatorManagedInformationUrl" type="String"-->
<#-- @ftlvariable name="withdrawUrl" type="String" -->
<#-- @ftlvariable name="approveUrl" type="String" -->
<#-- @ftlvariable name="requestUpdateUrl" type="String" -->
<#-- @ftlvariable name="canRequestUpdate" type="boolean" -->
<#-- @ftlvariable name="submissionSelectedUrl" type="String"-->
<#-- @ftlvariable name="submissionsOnResource" type="java.util.Map<String, String>"-->
<#-- @ftlvariable name="resourceType" type="String"-->
<#-- @ftlvariable name="updateSubmissionUrl" type="String"-->
<#-- @ftlvariable name="submissionUpdateReason" type="String"-->
<#-- @ftlvariable name="updateRequested" type="boolean"-->
<#-- @ftlvariable name="isIndustrySubmitter" type="boolean"-->
<#-- @ftlvariable name="isInResponsibleTeam" type="boolean"-->
<#-- @ftlvariable name="regulatorManagedInformationSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView" -->
<#-- @ftlvariable name="resourceGroupsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryView" -->

<#assign pageTitle = "${submissionRecord.resourceNameCentreOfInterestAndLicenceApplicationReference()}" />
<#assign caption = "${submissionRecord.submissionType().getTypeName()}" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  caption=caption
  pageSize=PageSize.FULL_WIDTH
  breadcrumbsMap=breadcrumbsMap
  wrapperWidth=true>

  <@fdsDataItems.dataItem>
      <#if isFieldSubmission>
          <@fdsDataItems.dataValues key="Field status" value="${submissionRecord.fieldStatus()}"/>
          <@fdsDataItems.dataValues key="Responsible organisation" value="${submissionRecord.organisationResponsible()}"/>
          <@fdsDataItems.dataValues key="Responsible team" value="${submissionRecord.responsibleTeam()}"/>
          <@fdsDataItems.dataValues key="Parent field" value="${submissionRecord.parentFieldName()}"/>
      <#else>
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
      </#if>
  </@fdsDataItems.dataItem>
    <#if updateRequested>
      <#assign submissionUpdateDetails>
        <@updateSummaryDetails.updateDetails submissionUpdateReason=submissionUpdateReason/>
      </#assign>
      <@fdsNotificationBanner.notificationBannerInfo bannerTitleText="An update has been requested on this submission">
        <@fdsNotificationBanner.notificationBannerContent
          headingText="This submission requires an update to the information provided."
          moreContent=submissionUpdateDetails/>
      </@fdsNotificationBanner.notificationBannerInfo>
    </#if>
    <#if updateRequested && isIndustrySubmitter>
      <@fdsAction.buttonGroup>
        <@fdsAction.link linkText="Update submission" linkClass="govuk-button" linkUrl=springUrl(updateSubmissionUrl)/>
      </@fdsAction.buttonGroup>
    <#elseif canReview>
      <@fdsAction.buttonGroup>
        <#if !updateRequested>
          <@fdsAction.link linkText="Accept submission" linkClass="govuk-button" linkUrl=springUrl(approveUrl)/>
          <#if canRequestUpdate>
            <@fdsAction.link
              linkText="Request update"
              linkClass="govuk-button govuk-button--secondary"
              linkUrl=springUrl(requestUpdateUrl)/>
          </#if>
          <@fdsAction.link
            linkText="Withdraw submission"
            linkClass="govuk-button govuk-button--secondary"
            linkUrl=springUrl(withdrawUrl)/>
        </#if>
        <@fdsAction.link
          linkText=manageResourceInformationText
          linkClass="govuk-button govuk-button--secondary"
          linkUrl=springUrl(regulatorManagedInformationUrl)/>
        <#if isFieldSubmission>
           <@fdsAction.link
             linkText="Field status"
             linkClass="govuk-button govuk-button--secondary"
             linkUrl=springUrl(fieldStatusUrl)/>
        </#if>
    </@fdsAction.buttonGroup>
  </#if>

 <@fdsForm.htmlForm actionUrl=springUrl(submissionSelectedUrl)>
   <@submissionSelector.submissionHistorySearchSelector
      resourceType=resourceType
      submissionsOnResource=submissionsOnResource/>
 </@fdsForm.htmlForm>

  <h2 class="govuk-heading-l">${submissionType.getTypeName()}</h2>
  <#if isInResponsibleTeam>
      <#if isFieldSubmission>
        <@regulatorSummary.regulatorInformationSummary
          fieldRegulatorInformationCard=fieldRegulatorManagedInformationSummaryCard
          fieldMapView=fieldMapView!""/>
      <#else>
        <@regulatorManagedInformationSummary.regulatorManagedInformationSummaryCard
          regulatorManagedInformationValues=regulatorManagedInformationSummaryView
          resourceGroupsValues=resourceGroupsSummaryView/>
      </#if>
  </#if>
  <@submissionSummary.submissionSummary
    submissionType="${submissionType.name()}"/>

</@defaultPage>