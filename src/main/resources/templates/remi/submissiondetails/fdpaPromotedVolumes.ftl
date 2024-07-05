<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>
<#import './_hydrocarbonCaseInsetText.ftl' as hydrocarbonCaseInsetText>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<#assign hintText = "Enter 0 if there have been no promoted resources" />

<@defaultPage
htmlTitle=pageTitle
pageSize=PageSize.FULL_WIDTH
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup
        path="form.hasPromotedVolumes"
        labelText="Are any resources being promoted to reserves as a result of this FDPA?"
        fieldsetHeadingSize="h1"
        fieldsetHeadingClass="govuk-fieldset__legend--xl"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.hasPromotedVolumes">
                <@hydrocarbonCaseInsetText.hydrocarbonCaseInsetText hydrocarbonCase=hydrocarbonCase/>

                <#if hydrocarbonCase == "OIL_ONLY">
                    <@probabilityDistribution.probabilityDistributionSingularTemplate
                    probabilityDistributionPath="form.oilReserves"
                    labelText="What is the increase to oil reserves as a result of the promotion from resources?"
                    template=oilReservesTemplate
                    error=oilReservesError
                    provideComments=false
                    hintText=hintText/>
                </#if>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.associatedCondensateReserves"
                labelText="What is the increase to associated condensate reserves as a result of the promotion from resources?"
                template=associatedCondensateReservesTemplate
                error=associatedCondensateReservesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.naturalGasLiquidsReserves"
                labelText="What is the increase to natural gas liquids reserves as a result of the promotion from resources?"
                template=naturalGasLiquidsReservesTemplate
                error=naturalGasLiquidsReservesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.salesGasReserves"
                labelText="What is the increase to sales gas reserves as a result of the promotion from resources?"
                template=salesGasReservesTemplate
                error=salesGasReservesError
                provideComments=false
                hintText=hintText/>

                <@probabilityDistribution.probabilityDistributionSingularTemplate
                probabilityDistributionPath="form.oilEquivalentReserves"
                labelText="What is the increase to oil equivalent reserves as a result of the promotion from resources?"
                template=oilEquivalentReservesTemplate
                error=oilEquivalentReservesError
                provideComments=false
                hintText=hintText/>

                <@fdsTextarea.textarea
                path="form.comment"
                labelText="Provide reasons why resources are being promoted"
                rows="3"
                optionalLabel=true
                inputClass="govuk-!-width-two-thirds"/>
            </@fdsRadio.radioYes>

            <@fdsRadio.radioNo path="form.hasPromotedVolumes"/>
        </@fdsRadio.radioGroup>

        <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>