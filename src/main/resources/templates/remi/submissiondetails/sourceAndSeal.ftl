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
        <@fdsSearchSelector.searchSelectorEnhanced path="form.sourceAgeStage" options=ageSeries labelText="Age stage or series"/>
        <@fdsSearchSelector.searchSelectorEnhanced path="form.sourceGroupName" options=groupNames labelText="Group name"/>
        <@fdsSearchSelector.searchSelectorRest
          path="form.sourceFormationName"
          restUrl=springUrl(formationRestEndpoint)
          labelText="Formation name"
          preselectedItems=preSelectedSourceFormation!{}
        />
      <@fdsTextInput.textInput path="form.sourceLithology" labelText="Lithology" optionalLabel=true/>
    </@fdsFieldset.fieldset>

    <@fdsFieldset.fieldset legendHeading="Top seal">
        <@fdsSearchSelector.searchSelectorEnhanced path="form.topSealAgeStage" options=ageSeries labelText="Age stage or series"/>
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

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>