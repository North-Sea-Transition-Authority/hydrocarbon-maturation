<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>
<#import './_hydrocarbonCaseInsetText.ftl' as hydrocarbonCaseInsetText>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<#assign hintText = "Only provide the increase in volumes as a result of the work this FDPA is for, or 0 if there has been no increase" />

<@defaultPage
htmlTitle=pageTitle
pageSize=PageSize.FULL_WIDTH
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup
        path="form.changeInVolumetrics"
        labelText="Is there a change in volumetrics as a result of this FDPA?"
        fieldsetHeadingSize="h1"
        fieldsetHeadingClass="govuk-fieldset__legend--xl"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.changeInVolumetrics">
                <@hydrocarbonCaseInsetText.hydrocarbonCaseInsetText hydrocarbonCase=hydrocarbonCase/>

                <#if hydrocarbonCase == "OIL_ONLY" || hydrocarbonCase == "MIXED_OIL_AND_GAS">
                    <@probabilityDistribution.probabilityDistributionSingularTemplate
                    probabilityDistributionPath="form.oilInitiallyInPlace"
                    labelText="Incremental increase to oil initially in place as a result of this FDPA"
                    template=oilInitiallyInPlaceTemplate
                    error=oilInitiallyInPlaceError
                    provideComments=false
                    hintText=hintText/>
                </#if>

                <#if hydrocarbonCase == "GAS_ONLY" || hydrocarbonCase == "MIXED_OIL_AND_GAS">
                    <@probabilityDistribution.probabilityDistributionSingularTemplate
                    probabilityDistributionPath="form.gasInitiallyInPlace"
                    labelText="Incremental increase to gas initially in place as a result of this FDPA"
                    template=gasInitiallyInPlaceTemplate
                    error=gasInitiallyInPlaceError
                    provideComments=false
                    hintText=hintText/>
                </#if>

                <#if hydrocarbonCase == "OIL_ONLY" || hydrocarbonCase == "MIXED_OIL_AND_GAS">
                    <@probabilityDistribution.probabilityDistributionSingularTemplate
                    probabilityDistributionPath="form.oilRecoverableResources"
                    labelText="Incremental increase to oil recoverable resources as a result of this FDPA"
                    template=oilRecoverableResourcesTemplate
                    error=oilRecoverableResourcesError
                    provideComments=false
                    hintText=hintText/>

                    <@probabilityDistribution.probabilityDistributionSingularTemplate
                    probabilityDistributionPath="form.oilReserves"
                    labelText="Incremental increase to oil reserves as a result of this FDPA"
                    template=oilReservesTemplate
                    error=oilReservesError
                    provideComments=false
                    hintText=hintText/>
                </#if>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.associatedCondensateRecoverableResources"
                labelText="Incremental increase to associated condensate recoverable resources as a result of this FDPA"
                template=associatedCondensateRecoverableResourcesTemplate
                error=associatedCondensateRecoverableResourcesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.associatedCondensateReserves"
                labelText="Incremental increase to associated condensate reserves as a result of this FDPA"
                template=associatedCondensateReservesTemplate
                error=associatedCondensateReservesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.naturalGasLiquidsRecoverableResources"
                labelText="Incremental increase to natural gas liquids recoverable resources as a result of this FDPA"
                template=naturalGasLiquidsRecoverableResourcesTemplate
                error=naturalGasLiquidsRecoverableResourcesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.naturalGasLiquidsReserves"
                labelText="Incremental increase to natural gas liquids reserves as a result of this FDPA"
                template=naturalGasLiquidsReservesTemplate
                error=naturalGasLiquidsReservesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.salesGasRecoverableResources"
                labelText="Incremental increase to sales gas recoverable resources as a result of this FDPA"
                template=salesGasRecoverableResourcesTemplate
                error=salesGasRecoverableResourcesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.salesGasReserves"
                labelText="Incremental increase to sales gas reserves as a result of this FDPA"
                template=salesGasReservesTemplate
                error=salesGasReservesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.oilEquivalentRecoverableResources"
                labelText="Incremental increase to oil equivalent recoverable resources as a result of this FDPA"
                template=oilEquivalentRecoverableResourcesTemplate
                error=oilEquivalentRecoverableResourcesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.oilEquivalentReserves"
                labelText="Incremental increase to oil equivalent reserves as a result of this FDPA"
                template=oilEquivalentReservesTemplate
                error=oilEquivalentReservesError
                provideComments=false
                hintText=hintText/>

                <@fdsTextarea.textarea
                path="form.comment"
                labelText="Comments on incremental increase"
                rows="3"
                inputClass="govuk-!-width-two-thirds"/>
            </@fdsRadio.radioYes>

            <@fdsRadio.radioNo path="form.changeInVolumetrics"/>
        </@fdsRadio.radioGroup>

        <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>