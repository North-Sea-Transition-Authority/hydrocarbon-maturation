<#include '../layout/layout.ftl'>

<#assign pageTitle = "General details" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="onshoreOffshore" type="java.util.Map<String, String>" -->
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

    <@fdsForm.htmlForm>

      <@fdsTextInput.textInput path="form.resourceName" labelText="${resourceType?capitalize} name"/>

      <@fdsSearchSelector.searchSelectorRest
        path="form.responsibleOrganisationId"
        restUrl=springUrl(organisationUnitEndpoint)
        labelText="What organisation is responsible for this ${resourceType}?"
        selectorMinInputLength=3
        nestingPath="form.responsibleOrganisationId"
        preselectedItems=responsibleOrganisation!{}/>
      <@fdsRadio.radioGroup
        path="form.resourceOnALicence"
        labelText="Is this ${resourceType} on a licence?"
        hiddenContent=true>
        <@fdsRadio.radioYes path="form.resourceOnALicence">
          <@fdsAddToList.addToList
            pathForList="form.licences"
            pathForSelector="form.licenceSelect"
            alreadyAdded=alreadyAddedLicences
            title=""
            itemName="Licences"
            noItemText="No licence added"
            invalidItemText="This licence is invalid"
            selectorMinInputLength=1
            selectorNestingPath="form.resourceOnALicence"
            addToListId="licence-table"
            selectorLabelText="Select a licence to add to this ${resourceType}"
            restUrl=springUrl(licenceEndpoint)/>
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.resourceOnALicence"/>
      </@fdsRadio.radioGroup>

      <@fdsRadio.radio path="form.onshoreOffshore" radioItems=onshoreOffshore labelText="Is this ${resourceType} onshore or offshore?"/>

      <@fdsRadio.radioGroup path="form.resourceLocatedInUkcs" labelText="Is the ${resourceType} wholly located within UKCS?" hiddenContent=true>
          <@fdsRadio.radioYes path="form.resourceLocatedInUkcs"/>
          <@fdsRadio.radioNo path="form.resourceLocatedInUkcs">
              <@fdsTextInput.textInput path="form.percentageLocatedInUkcs.decimalInputValue"
                labelText="What percentage of the hydrocarbon volume is located within the UKCS?"
                suffix="%"
                suffixScreenReaderPrompt="percent"
                hintText="Must be between 0.1 and 100"
                inputClass="govuk-!-width-one-third"
                nestingPath="form.resourceLocatedInUkcs"/>
          </@fdsRadio.radioNo>
      </@fdsRadio.radioGroup>

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
                  labelText="Provide the source of information for this ${resourceType} submission" nestingPath="form.informationSource"/>
                </#if>
                <#assign firstItem=false/>
              </@fdsRadio.radioItem>
          </#list>
      </@fdsRadio.radioGroup>

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

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>