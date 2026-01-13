<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelurl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageName
pageHeading=pageName
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap
caption=resourceDisplay>
    <@fdsForm.htmlForm>
        <@fdsTextInput.textInput
        path="form.resourceName"
        labelText="Resource name"/>

        <@fdsRadio.radioGroup path="form.hydrocarbonCase" labelText="Hydrocarbon case">
            <#assign firstItem=true/>
            <#list hydrocarbonCase as key, value>
                <@fdsRadio.radioItem path="form.hydrocarbonCase" itemMap={key : value} isFirstItem=firstItem/>
                <#assign firstItem=false/>
            </#list>
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup path="form.opportunityType" labelText="Opportunity type" hiddenContent=true>
            <#assign firstItem=true/>
            <#list opportunityTypes as key, value>
                <@fdsRadio.radioItem path="form.opportunityType" itemMap={key : value} isFirstItem=firstItem>
                    <#if key == "OTHER">
                        <@fdsTextInput.textInput path="form.otherTypeOfOpportunity"
                        labelText="Enter the other opportunity type" nestingPath="form.opportunityType"/>
                    </#if>
                    <#assign firstItem=false/>
                </@fdsRadio.radioItem>
            </#list>
        </@fdsRadio.radioGroup>

        <@fdsSearchSelector.searchSelectorRest
        path="form.well"
        restUrl=springUrl(wellEndpoint)
        labelText="Principal well"
        selectorMinInputLength=3
        preselectedItems=well!{}
        optionalLabel=true/>

        <@fdsAction.submitButtons primaryButtonText="Save and complete"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl=springUrl(cancelUrl)/>
    </@fdsForm.htmlForm>
</@defaultPage>