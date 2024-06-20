<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.FULL_WIDTH
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
        <#if hydrocarbonType == "OIL_ONLY" || hydrocarbonType == "MIXED_OIL_AND_GAS">
            <@probabilityDistribution.probabilityDistributionSingularTemplate
            probabilityDistributionPath="form.oilInPlace"
            labelText="Oil initially in place"
            template=oilInPlaceTemplate
            error=oilInPlaceError/>
        </#if>

        <#if hydrocarbonType == "GAS_ONLY" || hydrocarbonType == "MIXED_OIL_AND_GAS">
            <@probabilityDistribution.probabilityDistributionSingularTemplate
            probabilityDistributionPath="form.gasInPlace"
            labelText="Gas initially in place"
            template=gasInPlaceTemplate
            error=gasInPlaceError/>
        </#if>

        <#if reservoirCarriedForward?? && reservoirCarriedForward==true>
            <@fdsCheckbox.checkboxItem
            labelText="The reservoir initially in place information is correct"
            path="form.initiallyInPlaceInformationCorrect"/>
        </#if>

        <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>