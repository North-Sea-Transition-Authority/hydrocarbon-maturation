<#include '../layout/layout.ftl'>
<#import '../prospect/newSubmissionGuidance.ftl' as guidance>

<#assign pageTitle = "Prospect update" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="prospectSelectedOption" type="String" -->
<#-- @ftlvariable name="otherSourceOfInformation" type="String" -->
<#-- @ftlvariable name="informationSource" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)>
  <@fdsForm.htmlForm actionUrl=springUrl(actionUrl)>
    <@fdsSearchSelector.searchSelectorRest path="form.prospectSelectedOption" restUrl=springUrl(prospectApiEndpoint) labelText="What prospect is being updated?" selectorMinInputLength=3/>
    <@fdsDetails.summaryDetails summaryTitle="The prospect I want to update is not listed">
      <p class="govuk-body">
        Only prospects that exist on an extant licence are shown. Prospects that have already had a post-drill or relinquishment submission can no longer be updated here.
      </p>
      <@guidance.newSubmissionGuidanceText resourceTypeString="prospect" actionString="update"/>

    </@fdsDetails.summaryDetails>

    <@fdsRadio.radioGroup path="form.informationSource" labelText="Source of Information" hiddenContent=true>
      <#assign firstItem=true/>
      <#list informationSources as key, value>
        <@fdsRadio.radioItem path="form.informationSource" itemMap={key : value} isFirstItem=firstItem>
          <#if key == "LICENCE_APPLICATION">
            <@fdsSearchSelector.searchSelectorRest
              path="form.licenceApplication"
              restUrl=springUrl(licenceApplicationEndpoint)
              labelText="Licence application round and reference"
              selectorMinInputLength=3
              nestingPath="form.informationSource"
              preselectedItems=licenceApplication!{}/>
          </#if>
          <#if key == "OTHER">
            <@fdsTextInput.textInput path="form.otherSourceOfInformation"
            labelText="Provide the source of information for this prospect submission" nestingPath="form.informationSource"/>
          </#if>
          <#assign firstItem=false/>
        </@fdsRadio.radioItem>
      </#list>
    </@fdsRadio.radioGroup>
    <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>