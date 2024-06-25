<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageSize=PageSize.FULL_WIDTH
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup
        path="form.changeToInPlaceVolumes"
        labelText="Is there a change to the in place volumes on this reservoir as a result of this FDPA?"
        fieldsetHeadingSize="h1"
        fieldsetHeadingClass="govuk-fieldset__legend--xl"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.changeToInPlaceVolumes">
                <#if hydrocarbonCase == "OIL_ONLY" || hydrocarbonCase == "MIXED_OIL_AND_GAS">
                    <@probabilityDistribution.probabilityDistributionSingularTemplate
                    probabilityDistributionPath="form.incrementalIncreaseOil"
                    labelText="Incremental increase to the oil initially in place as a result of this FDPA"
                    template=incrementalIncreaseOilTemplate
                    error=incrementalIncreaseOilError
                    provideComments=false/>
                </#if>

                <#if hydrocarbonCase == "GAS_ONLY" || hydrocarbonCase == "MIXED_OIL_AND_GAS">
                    <@probabilityDistribution.probabilityDistributionSingularTemplate
                    probabilityDistributionPath="form.incrementalIncreaseGas"
                    labelText="Incremental increase to the gas initially in place as a result of this FDPA"
                    template=incrementalIncreaseGasTemplate
                    error=incrementalIncreaseGasError
                    provideComments=false/>
                </#if>

                <@fdsTextarea.textarea
                path="form.comment"
                labelText="Why are the volumes changing as a result of this FDPA?"
                rows="3"
                inputClass="govuk-!-width-two-thirds"/>
            </@fdsRadio.radioYes>

            <@fdsRadio.radioNo path="form.changeToInPlaceVolumes"/>
        </@fdsRadio.radioGroup>

        <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>