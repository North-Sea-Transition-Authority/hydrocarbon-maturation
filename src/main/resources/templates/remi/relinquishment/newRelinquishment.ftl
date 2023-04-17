<#include '../layout/layout.ftl'>

<#assign pageTitle = "Relinquishment" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="prospectSelectedOption" type="String" -->
<#-- @ftlvariable name="otherSourceOfInformation" type="String" -->
<#-- @ftlvariable name="informationSource" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource" -->
<#-- @ftlvariable name="customerBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup path="form.chosenResourceType" labelText="What type of resource are you relinquishing?"
      hiddenContent=true
      nestingPath="form.chosenResourceType">
      <@fdsRadio.radioItem path="form.chosenResourceType" itemMap={"PROSPECT":  "Prospect"}>
        <@fdsSearchSelector.searchSelectorRest
          path="form.prospectSelectedOption"
          nestingPath="form.chosenResourceType"
          restUrl=springUrl(prospectApiEndpoint)
          labelText="What prospect is being relinquished?"
          selectorMinInputLength=3/>
          <@fdsDetails.summaryDetails summaryTitle="The prospect I want to relinquish is not listed">
            <p class="govuk-body">
              Only prospects with your organisation are shown.
              If a prospect has already progressed to a discovery then you should relinquish the discovery instead.            </p>
            <p class="govuk-body">
              If the prospect you want to relinquish is not shown in the list then you must contact the operator responsible for the prospect to provide you with access to their organisation.
            </p>
            <p class="govuk-body">
              If you are already part of the organisation then contact the ${customerBranding.mnemonic()} for advice.
            </p>
          </@fdsDetails.summaryDetails>
      </@fdsRadio.radioItem>
      <@fdsRadio.radioItem
        path="form.chosenResourceType"
        itemMap={"DISCOVERY":  "Discovery"}>
        <@fdsSearchSelector.searchSelectorRest
          path="form.discoverySelectedOption"
          nestingPath="form.chosenResourceType"
          restUrl=springUrl(discoveryApiEndpoint)
          labelText="What discovery is being relinquished?"
          selectorMinInputLength=3/>
          <@fdsDetails.summaryDetails summaryTitle="The discovery I want to relinquish is not listed">
            <p>
              If the discovery you want to relinquish is not shown in the list then you must contact the operator responsible for the discovery
              to provide you with access to their organisation.
            </p>
            <p>
              If you are already part of the organisation then contact the ${customerBranding.mnemonic()} for advice
            </p>
          </@fdsDetails.summaryDetails>
      </@fdsRadio.radioItem>
    </@fdsRadio.radioGroup>
    <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>