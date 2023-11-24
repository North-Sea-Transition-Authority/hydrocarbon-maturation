<#include '../layout/layout.ftl'>

<#assign pageTitle = "General details" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="quadrantBlocksApiEndpoint" type="String" -->
<#-- @ftlvariable name="alreadyAddedQuadrantBlocks" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.addtolist.AddToListItem>" -->
<#-- @ftlvariable name="informationSources" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
  <#if hasLicencesOnEarlierSubmission>
    <#assign licenceHintText>
      If this ${resourceType} is no longer licensed then you cannot remove all of the licences on this submission.
      You must submit a relinquishment submission.
    </#assign>
  <#else>
    <#assign licenceHintText = ""/>
  </#if>
  <#if relinquishmentSubmission>
    <#assign guidanceTextLicence>
      The licence will be removed from this ${resourceType} when the relinquishment submission is accepted.
    </#assign>
  <#else>
    <#assign guidanceTextLicence>
    </#assign>
  </#if>
    <@fdsForm.htmlForm>
      <@fdsFieldset.fieldset
        legendHeadingSize="h2"
        legendHeadingClass="govuk-fieldset__legend--s"
        legendHeading="What licences are on this ${resourceType}?">
          <@fdsAddToList.addToList
            pathForList="form.licences"
            pathForSelector="form.licenceSelect"
            alreadyAdded=alreadyAddedLicences
            title=""
            selectorHintText=guidanceTextLicence
            itemName="Licences"
            noItemText="No licence added"
            invalidItemText="This licence is not extant"
            selectorMinInputLength=1
            addToListId="licence-table"
            selectorLabelText="Select a licence to add to this ${resourceType}"
            restUrl=springUrl(licenceEndpoint)/>
      </@fdsFieldset.fieldset>

      <#if isOffshore>
        <@resourceLocatedInRegion formId="resourceLocatedInUkcs" percentageFormId="percentageLocatedInUkcs" regionName="the UKCS"/>
      <#else>
        <@resourceLocatedInRegion formId="resourceLocatedInEngland" percentageFormId="percentageLocatedInEngland" regionName="England"/>
      </#if>

      <#if (discoverySubmission || prospectSubmission) && !relinquishmentSubmission>
        <@fdsRadio.radioGroup path="form.informationSource" labelText="Source of information" hiddenContent=true>
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
                    labelText="Provide the source of information for this ${resourceType} submission" nestingPath="form.informationSource"/>
                </#if>
                <#assign firstItem=false/>
              </@fdsRadio.radioItem>
          </#list>
        </@fdsRadio.radioGroup>
      </#if>
      <@fdsFieldset.fieldset
        legendHeadingSize="h2"
        legendHeadingClass="govuk-fieldset__legend--s"
        legendHeading="What quadrants and blocks is this ${resourceType} located in?">
        <@fdsAddToList.addToList
          pathForList="form.quadrantBlocks"
          pathForSelector="form.quadrantBlockSelect"
          alreadyAdded=alreadyAddedQuadrantBlocks
          title=""
          itemName="Quadrant and block"
          noItemText="No quadrant and block added"
          invalidItemText="This quadrant and block is invalid"
          addToListId="quadrant-block-table"
          selectorInputWidth="48"
          selectorLabelText="Select a quadrant and block to add to this ${resourceType}"
          restUrl=springUrl(quadrantBlocksApiEndpoint)/>
      </@fdsFieldset.fieldset>
      <#if relinquishmentSubmission>
        <@fdsDateInput.dateInput
          formId="relinquishment-date-input"
          dayPath="form.relinquishmentDay"
          monthPath="form.relinquishmentMonth"
          yearPath="form.relinquishmentYear"
          labelText="Date of relinquishment"/>
      </#if>

      <#if submissionType == "CREATE_COP">
        <@fdsDateInput.dateInput
        formId="fieldStoppedProducing-date-input"
        dayPath="form.fieldStoppedProducingDay"
        monthPath="form.fieldStoppedProducingMonth"
        yearPath="form.fieldStoppedProducingYear"
        labelText="What date did the field stop producing?"/>
      </#if>

      <#if submissionType == "CREATE_FDP">
        <@fdsTextarea.textarea
        path="form.capitalProjectScopeDelivered"
        labelText="Provide a short summary of the capital project scope delivered within this FDP"
        hintText="For example, fixed 4 Slot Wellhead platform with gas export pipeline and 1 well"/>
      <#elseif submissionType == "CREATE_FDPA">
        <@fdsTextarea.textarea
        path="form.capitalProjectScopeDelivered"
        labelText="Provide a short summary of the capital project scope delivered within this FDPA"
        hintText="For example, fixed 4 Slot Wellhead platform with gas export pipeline and 1 well"/>
      </#if>

      <#if fieldSubmission>
        <@fdsRadio.radioGroup path="form.currentDriveMechanism" labelText="Current drive mechanism" hiddenContent=true>
          <#assign firstItem=true/>
          <#list driveMechanismOptions as key, value>
            <#list value as displayName, hintText>
              <@fdsRadio.radioItem
              path="form.currentDriveMechanism"
              itemMap={key : displayName}
              isFirstItem=firstItem
              itemHintText=hintText>
                <#if key == "OTHER">
                  <@fdsTextInput.textInput
                  path="form.otherDriveMechanism"
                  labelText="Provide the other drive mechanism"
                  nestingPath="form.currentDriveMechanism"
                  />
                </#if>
                <#assign firstItem=false/>
              </@fdsRadio.radioItem>
            </#list>
          </#list>
        </@fdsRadio.radioGroup>
      </#if>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>

<#macro resourceLocatedInRegion formId percentageFormId regionName>
    <@fdsRadio.radioGroup path="form.${formId}" labelText="Is the ${resourceType} wholly located within ${regionName}?" hiddenContent=true>
        <@fdsRadio.radioYes path="form.${formId}"/>
        <@fdsRadio.radioNo path="form.${formId}">
            <@fdsTextInput.textInput path="form.${percentageFormId}.decimalInputValue"
            labelText="What percentage of the hydrocarbon volume is located within ${regionName}?"
            suffix="%"
            suffixScreenReaderPrompt="percent"
            hintText="Must be between 0.1 and 100"
            inputClass="govuk-!-width-one-third"
            nestingPath="form.${formId}"/>
        </@fdsRadio.radioNo>
    </@fdsRadio.radioGroup>
</#macro>