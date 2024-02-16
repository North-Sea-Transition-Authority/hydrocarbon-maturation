<#include '../layout/layout.ftl'>
<#import '../coordinates/coordinateInput.ftl' as coordinateInput/>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Trap properties" />

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup path="form.trapType" labelText="Trap type" hiddenContent=true>
            <#assign firstItem=true/>
            <#list trapTypes as key, value>
                <@fdsRadio.radioItem path="form.trapType" itemMap={key : value} isFirstItem=firstItem>
                    <#if key == "OTHER">
                        <@fdsTextInput.textInput path="form.otherTrapType" labelText="Provide the trap type" nestingPath="form.trapType"/>
                    </#if>
                    <#assign firstItem=false/>
                </@fdsRadio.radioItem>
            </#list>
        </@fdsRadio.radioGroup>

        <@fdsFieldset.fieldset legendHeading="Quadrant and block the centre of interest is within"
        legendHeadingClass="govuk-fieldset__legend--s"
        legendHeadingSize="h2"
        hintText="You can only select from quadrants and blocks added in the general details section of the form">
            <@fdsSearchSelector.searchSelectorEnhanced
            path="form.centreOfInterestQuadrantBlock"
            labelHeadingClass="govuk-fieldset__heading"
            inputWidth="33"
            pageHeading=false
            options=quadrantAndBlocks
            labelText=""/>
        </@fdsFieldset.fieldset>

        <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.depthToCrest"
        labelText="Depth to crest"
        template=depthToCrestTemplate
        error=depthToCrestError/>

        <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.depthToSpillPoint"
        labelText="Depth to spill-point"
        template=depthToSpillPointTemplate
        error=depthToSpillPointError/>

        <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.productiveArea"
        labelText="Productive area"
        template=productiveAreaTemplate
        error=productiveAreaError/>

        <@fdsTextarea.textarea path="form.closureMechanisms" labelText="Closure mechanisms"/>

        <@fdsTextInput.textInput
        path="form.verticallySeparateHcPools.decimalInputValue"
        labelText="Number of vertically separate HC pools"
        inputClass="govuk-!-width-one-third"
        />

        <@fdsTextInput.textInput
        path="form.structuralCompartments.decimalInputValue"
        labelText="Number of structural compartments"
        inputClass="govuk-!-width-one-third"
        />

        <@fdsTextInput.textInput
        path="form.originalHydrocarbonColumnHeight.decimalInputValue"
        labelText="Original hydrocarbon column height"
        suffix="m"
        inputClass="govuk-!-width-one-third"
        />

        <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>