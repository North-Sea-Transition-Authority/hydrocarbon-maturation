<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput path="form.legalFieldName"
      labelText="Legal field name"
      hintText="This is the determined field name agreed with the Secretary of State"
      inputClass="govuk-input--width-20"/>

    <@fdsTextInput.textInput path="form.correctedLegalFieldName"
      labelText="Corrected legal field name"
      hintText="This name is used in all Energy Portal services and documents. It allows typographical errors in the legal field name to be corrected."
      inputClass="govuk-input--width-20"/>

    <@fdsTextInput.textInput path="form.commonNameForField"
      labelText="Common name for field"
      hintText="This name is used to improve public reports by removing operator names or changing the compass position to be at the end of the name."
      inputClass="govuk-input--width-20"/>

    <@fdsFieldset.fieldset
      legendHeadingSize="h2"
      legendHeadingClass="govuk-fieldset__legend--s"
      legendHeading="What discoveries are associated with this field?">
        <@fdsAddToList.addToList
          pathForList="form.associatedDiscoveries"
          pathForSelector="form.associatedDiscoverySelect"
          alreadyAdded=alreadyAddedAssociatedDiscoveries
          title=""
          itemName="Discovery"
          noItemText="No discovery"
          invalidItemText="This discovery is invalid"
          addToListId="associated-discovery-table"
          selectorInputWidth="48"
          selectorLabelText="Select a discovery to associate to this field"
          restUrl=springUrl(discoveriesApiEndpoint)/>
      <@fdsDetails.summaryDetails summaryTitle="The discovery the field is associated with is not listed">
        <p class="govuk-body">
          Relinquished discoveries cannot be associated with a field.
        </p>

        <p class="govuk-body">
          If there are multiple views of the same discovery then only the preferred NSTA view can be associated with the field.
        </p>

        <p class="govuk-body">
          If the discovery does not exist then it will need to be created by the exploration team.
        </p>
      </@fdsDetails.summaryDetails>
    </@fdsFieldset.fieldset>

    <@fdsSearchSelector.searchSelectorRest
      path="form.responsibleOrganisationId"
      restUrl=springUrl(organisationUnitEndpoint)
      labelText="What organisation is responsible for this field?"
      selectorMinInputLength=3
      nestingPath="form.responsibleOrganisationId"
      preselectedItems=responsibleOrganisation!{}
      optionalLabel=true/>

    <@fdsRadio.radioGroup
      path="form.onshoreOffshore"
      labelText="Is this field onshore or offshore?"
      hiddenContent=true>
        <#assign firstItem=true/>
        <#list onshoreOffshore as key, value>
          <@fdsRadio.radioItem path="form.onshoreOffshore" itemMap={key : value} isFirstItem=firstItem>
            <#if key == "OFFSHORE">
              <@fdsRadio.radioGroup
                path="form.geographicalArea"
                labelText="Geographical area"
                nestingPath="form.onshoreOffshore">
                  <#assign firstItem=true/>
                  <#list geographicalAreas as key, value>
                      <@fdsRadio.radioItem path="form.geographicalArea" itemMap={key : value} isFirstItem=firstItem>
                          <#assign firstItem=false/>
                      </@fdsRadio.radioItem>
                  </#list>
              </@fdsRadio.radioGroup>
              <@fdsRadio.radioGroup
                path="form.responsibleTeamType"
                labelText="What area team is responsible for this field?"
                nestingPath="form.onshoreOffshore">
                  <#assign firstItem=true/>
                  <#list responsibleTeamTypes as key, value>
                      <@fdsRadio.radioItem path="form.responsibleTeamType" itemMap={key : value} isFirstItem=firstItem>
                          <#assign firstItem=false/>
                      </@fdsRadio.radioItem>
                  </#list>
              </@fdsRadio.radioGroup>
            </#if>
            <#assign firstItem=false/>
          </@fdsRadio.radioItem>
        </#list>
    </@fdsRadio.radioGroup>

    <@fdsRadio.radioGroup
      path="form.determinationStatus"
      labelText="What is the determination status of field?"
      hiddenContent=true>
        <#assign firstItem=true/>
        <#list determinationStatus as key, value>
          <@fdsRadio.radioItem path="form.determinationStatus" itemMap={key : value} isFirstItem=firstItem>
            <#if key == "SUB_AREA_OF_DETERMINED_FIELD">
              <@fdsSearchSelector.searchSelectorRest
                path="form.parentDeterminedField"
                restUrl=springUrl(parentDeterminedFieldEndpoint)
                labelText="What is the parent field?"
                selectorMinInputLength=3
                nestingPath="form.determinationStatus"
                preselectedItems=parentDeterminedField!{}/>
            </#if>
            <#assign firstItem=false/>
          </@fdsRadio.radioItem>
        </#list>
      </@fdsRadio.radioGroup>

    <@fdsTextInput.textInput path="form.fieldNumber.decimalInputValue"
      labelText="Field number"
      inputClass="govuk-input--width-4"/>

    <@fdsRadio.radioGroup
      path="form.infrastructureHost"
      labelText="Does this field's infrastructure host other fields?">
      <@fdsRadio.radioYes path="form.infrastructureHost"/>
      <@fdsRadio.radioNo path="form.infrastructureHost"/>
    </@fdsRadio.radioGroup>

    <@fdsAction.submitButtons
      primaryButtonText="Save and continue"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>