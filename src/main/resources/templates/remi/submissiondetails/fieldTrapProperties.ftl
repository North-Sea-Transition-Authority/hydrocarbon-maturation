<#include '../layout/layout.ftl'>
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

        <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.depthToCrest"
        labelText="Depth to crest distribution used"
        template=depthToCrestTemplate
        error=depthToCrestError/>

        <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.depthToSpillPoint"
        labelText="Depth to spill-point distribution used"
        template=depthToSpillPointTemplate
        error=depthToSpillPointError/>

        <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.productiveArea"
        labelText="Productive area distribution used"
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