<#include '../layout/layout.ftl'>

<#assign pageTitle = "Source and seal" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm>
    <@fdsFieldset.fieldset legendHeading="Source">
        <@fdsSearchSelector.searchSelectorRest
          path="form.sourceAgeStage"
          restUrl=springUrl(ageSeriesRestEndpoint)
          labelText="Age stage or series"
          preselectedItems=preSelectedSourceAgeStage!{}
        />
        <@fdsSearchSelector.searchSelectorEnhanced path="form.sourceGroupName" options=groupNames labelText="Group name"/>
        <@fdsSearchSelector.searchSelectorRest
          path="form.sourceFormationName"
          restUrl=springUrl(formationRestEndpoint)
          labelText="Formation name"
          preselectedItems=preSelectedSourceFormation!{}
        />
      <@fdsRadio.radioGroup path="form.sourceLithology" labelText="Lithology" hiddenContent=true>
          <#assign firstItem=true/>
          <#list sourceLithologyOptions as key, value>
              <@fdsRadio.radioItem path="form.sourceLithology" itemMap={key : value} isFirstItem=firstItem>
                  <#if key == "OTHER">
                      <@fdsTextInput.textInput
                        path="form.sourceLithologyOther"
                        labelText="Provide the other lithology"
                        nestingPath="form.sourceLithology"
                      />
                  </#if>
                  <#assign firstItem=false/>
              </@fdsRadio.radioItem>
          </#list>
      </@fdsRadio.radioGroup>
    </@fdsFieldset.fieldset>

    <@fdsFieldset.fieldset legendHeading="Top seal">
        <@fdsSearchSelector.searchSelectorRest
          path="form.topSealAgeStage"
          restUrl=springUrl(ageSeriesRestEndpoint)
          labelText="Age stage or series"
          preselectedItems=preSelectedTopSealAgeStage!{}
        />
        <@fdsSearchSelector.searchSelectorEnhanced path="form.topSealGroupName" options=groupNames labelText="Group name"/>
        <@fdsSearchSelector.searchSelectorRest
          path="form.topSealFormationName"
          restUrl=springUrl(formationRestEndpoint)
          labelText="Formation name"
          preselectedItems=preSelectedTopSealFormation!{}
        />
        <@fdsRadio.radioGroup path="form.topSealLithology" labelText="Lithology" hiddenContent=true>
            <#assign firstItem=true/>
            <#list topSealLithologyOptions as key, value>
                <@fdsRadio.radioItem path="form.topSealLithology" itemMap={key : value} isFirstItem=firstItem>
                    <#if key == "OTHER">
                        <@fdsTextInput.textInput
                          path="form.topSealLithologyOther"
                          labelText="Provide the other lithology"
                          nestingPath="form.topSealLithology"
                        />
                    </#if>
                    <#assign firstItem=false/>
                </@fdsRadio.radioItem>
            </#list>
        </@fdsRadio.radioGroup>
    </@fdsFieldset.fieldset>

      <#if fieldSubmission>
          <@fdsTextInput.textInput
          path="form.sealThickness.decimalInputValue"
          labelText="Seal thickness"
          suffix="m"
          suffixScreenReaderPrompt="in meters"
          inputClass="govuk-input--width-5"
          optionalLabel=false
          />

          <@fdsTextInput.textInput
          path="form.topSealFracturePressure.decimalInputValue"
          labelText="Top seal fracture pressure"
          suffix="psi"
          suffixScreenReaderPrompt="in psi"
          inputClass="govuk-input--width-5"
          optionalLabel=true
          />
      </#if>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>