<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>


<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.FULL_WIDTH
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
        <#if hydrocarbonType == "OIL_ONLY">
            <@probabilityDistribution.probabilityDistributionSingularTemplate
            probabilityDistributionPath="form.oilInPlace"
            labelText="Oil in place"
            template=oilInPlaceTemplate
            error=oilInPlaceError/>
        </#if>

        <#if hydrocarbonType == "GAS_ONLY">
            <@probabilityDistribution.probabilityDistributionSingularTemplate
            probabilityDistributionPath="form.gasInPlace"
            labelText="Gas in place"
            template=gasInPlaceTemplate
            error=gasInPlaceError/>
        </#if>

        <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>