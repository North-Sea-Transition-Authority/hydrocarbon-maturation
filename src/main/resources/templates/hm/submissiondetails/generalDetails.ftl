<#include '../layout/layout.ftl'>

<#assign pageTitle = "General details" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="onshoreOffshore" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="informationSources" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)
errorItems=errorList>

    <@fdsForm.htmlForm>

      <@fdsTextInput.textInput path="form.prospectName" labelText="Prospect name"/>

      <@fdsRadio.radio path="form.onshoreOffshore" radioItems=onshoreOffshore labelText="Is this prospect onshore or offshore?"/>

      <@fdsRadio.radioGroup path="form.prospectLocatedInUkcs" labelText="Is the prospect wholly located within UKCS?" hiddenContent=true>
          <@fdsRadio.radioYes path="form.prospectLocatedInUkcs"/>
          <@fdsRadio.radioNo path="form.prospectLocatedInUkcs">
              <@fdsTextInput.textInput path="form.percentageLocatedInUkcs.decimalInputValue"
                labelText="What percentage of the hydrocarbon volume is located within the UKCS?"
                suffix="%"
                suffixScreenReaderPrompt="percent"
                hintText="Must be between 0.1 and 100"
                inputClass="govuk-!-width-one-third"
                nestingPath="form.prospectLocatedInUkcs"/>
          </@fdsRadio.radioNo>
      </@fdsRadio.radioGroup>

      <@fdsRadio.radioGroup path="form.informationSource" labelText="Source of Information" hiddenContent=true>
          <#assign firstItem=true/>
          <#list informationSources as key, value>
              <@fdsRadio.radioItem path="form.informationSource" itemMap={key : value} isFirstItem=firstItem>
                <#if key == "OTHER">
                  <@fdsTextInput.textInput path="form.otherSourceOfInformation"
                  labelText="Provide the source of information for this prospect submission" nestingPath="form.informationSource"/>
                </#if>
                <#assign firstItem=false/>
              </@fdsRadio.radioItem>
          </#list>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>