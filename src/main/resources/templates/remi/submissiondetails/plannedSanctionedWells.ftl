<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <#if submissionType == "CREATE_FDPA">
        <@fdsInsetText.insetText>
            Only provide information on the planned sanctioned wells related to this FDPA
        </@fdsInsetText.insetText>
    </#if>

    <@fdsForm.htmlForm>
        <@fdsTextInput.textInput
        path="form.totalCapex.decimalInputValue"
        labelText="Total CAPEX for all planned sanctioned wells"
        inputClass="govuk-input--width-3"
        prefix="£"
        suffix="million"
        />

        <@fdsTextInput.textInput
        path="form.totalNumberOfPlannedSanctionedWells.decimalInputValue"
        labelText="Total number of planned sanctioned wells"
        inputClass="govuk-input--width-3"/>

        <@fdsTextInput.textInput
        path="form.numberOfInjectors.decimalInputValue"
        labelText="Number of injectors"
        inputClass="govuk-input--width-3"
        />

        <@fdsTextInput.textInput
        path="form.numberOfProducers.decimalInputValue"
        labelText="Number of producers"
        inputClass="govuk-input--width-3"
        />

        <@fdsTextInput.textInput
        path="form.numberOfMonitors.decimalInputValue"
        labelText="Number of monitors"
        inputClass="govuk-input--width-3"
        />

        <@fdsRadio.radioGroup
        path="form.anyHorizontalWells"
        labelText="Are there any horizontal wells?"
        hintText="Wells where the departure of the wellbore from vertical exceeds 80° C"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anyHorizontalWells">
                <@fdsTextInput.textInput
                path="form.numberOfHorizontalWells.decimalInputValue"
                nestingPath="form.anyHorizontalWells"
                labelText="Number of horizontal wells"
                inputClass="govuk-input--width-3"
                />
                <@fdsTextInput.textInput
                path="form.numberOfHorizontalWellsWithErd.decimalInputValue"
                nestingPath="form.anyHorizontalWells"
                labelText="Number of horizontal wells with extended reach drilling"
                hintText="Wells with horizontal distance-to-depth, or H:V, ratios less than two"
                inputClass="govuk-input--width-3"
                />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.anyHorizontalWells" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.anyHighPressureTemperatureWells"
        labelText="Are there any high pressure or high temperature wells?"
        hintText="Wells with a pressure greater than 15,000 psi and temperature greater than 177° C"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anyHighPressureTemperatureWells">
                <@fdsTextInput.textInput
                path="form.numberOfHighPressureTemperatureWells.decimalInputValue"
                nestingPath="form.anyHighPressureTemperatureWells"
                labelText="Number of high pressure or high temperature wells"
                inputClass="govuk-input--width-3"
                />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.anyHighPressureTemperatureWells" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.anyWellsWithinNarrowOrTightPpfgWindow"
        labelText="Are any wells within a narrow or tight pore pressure fracture gradient (PPFG) window?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anyWellsWithinNarrowOrTightPpfgWindow">
                <@fdsTextInput.textInput
                path="form.numberOfWellsWithinNarrowOrTightPpfgWindow.decimalInputValue"
                nestingPath="form.anyWellsWithinNarrowOrTightPpfgWindow"
                labelText="Number of wells within a narrow or tight pore pressure fracture gradient (PPFG) window"
                inputClass="govuk-input--width-3"
                />
                <@fdsRadio.radioGroup
                path="form.isManagedPressureDrillingUtilized"
                nestingPath="form.anyWellsWithinNarrowOrTightPpfgWindow"
                labelText="Is managed pressure drilling (MPD) utilised?">
                    <@fdsRadio.radioYes path="form.isManagedPressureDrillingUtilized"/>
                    <@fdsRadio.radioNo path="form.isManagedPressureDrillingUtilized" />
                </@fdsRadio.radioGroup>
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.anyWellsWithinNarrowOrTightPpfgWindow" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.anyWellboreStabilityChallenges"
        labelText="Are there wellbore stability challenges with any of the wells?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anyWellboreStabilityChallenges">
                <@fdsTextInput.textInput
                path="form.numberOfWellboreStabilityChallenges.decimalInputValue"
                nestingPath="form.anyWellboreStabilityChallenges"
                labelText="Number of wells with wellbore stability challenges"
                inputClass="govuk-input--width-3"
                />
                <@fdsTextarea.textarea
                path="form.stabilityChallengesInformation"
                nestingPath="form.anyWellboreStabilityChallenges"
                labelText="Provide information on stability challenges" />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.anyWellboreStabilityChallenges" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.anyWellsExpectedToCrossFaults"
        labelText="Are any of the wells expected to cross any faults?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anyWellsExpectedToCrossFaults">
                <@fdsTextInput.textInput
                path="form.numberOfWellsExpectedToCrossFaults.decimalInputValue"
                nestingPath="form.anyWellsExpectedToCrossFaults"
                labelText="Number of wells expected to cross a fault"
                inputClass="govuk-input--width-3"
                />
                <@fdsCheckbox.checkboxes
                path="form.expectedFaults"
                nestingPath="form.anyWellsExpectedToCrossFaults"
                fieldsetHeadingText="Where are faults expected?"
                checkboxes=faultOptions />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.anyWellsExpectedToCrossFaults" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.anyExpectedLossesFromDrilling"
        labelText="Do you expect to incur losses in drilling these wells?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anyExpectedLossesFromDrilling">
                <@fdsTextarea.textarea
                path="form.whyLossesExpected"
                nestingPath="form.anyExpectedLossesFromDrilling"
                labelText="Why are losses expected?" />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.anyExpectedLossesFromDrilling" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.anyExpectedDrillingChallenges"
        labelText="Do you expect to encounter drilling challenges?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anyExpectedDrillingChallenges">
                <@fdsTextarea.textarea
                path="form.drillingChallengesMitigations"
                nestingPath="form.anyExpectedDrillingChallenges"
                labelText="What mitigations do you have in place?" />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.anyExpectedDrillingChallenges" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.stimulationTypeRequired"
        labelText="What type of well stimulation is required?"
        hiddenContent=true>
            <#assign firstitem=true />
            <#list stimulationOptions?keys as key>
                <@fdsRadio.radioItem
                path="form.stimulationTypeRequired"
                itemMap={key : stimulationOptions[key]}
                isFirstItem=firstItem>
                    <#if key != "NO_WELL_STIMULATION">
                        <@fdsTextInput.textInput
                        path="form.stimulationTypeDecimalInputHashMap[${key}].decimalInputValue"
                        nestingPath="form.stimulationTypeRequired"
                        labelText="Number of wells requiring stimulation"
                        inputClass="govuk-input--width-3"
                        />
                    </#if>
                </@fdsRadio.radioItem>
                <#assign firstitem=false />
            </#list>
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.isExpectingFormationDamage"
        labelText="Do you envisage any formation damage?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.isExpectingFormationDamage">
                <@fdsTextarea.textarea
                path="form.expectedFormationDamage"
                nestingPath="form.isExpectingFormationDamage"
                labelText="Describe the type of formation damage expected" />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.isExpectingFormationDamage" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.anyHeavyOil"
        labelText="Is there heavy oil with an API below 20 degrees?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anyHeavyOil" />
            <@fdsRadio.radioNo path="form.anyHeavyOil" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.anySpecialisedDrillingEquipmentRequired"
        labelText="Are specialised drilling equipment or services required?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.anySpecialisedDrillingEquipmentRequired">
                <@fdsTextarea.textarea
                path="form.specialisedDrillingEquipmentRequired"
                nestingPath="form.anySpecialisedDrillingEquipmentRequired"
                labelText="What specialised drilling equipment or services are required?" />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.anySpecialisedDrillingEquipmentRequired" />
        </@fdsRadio.radioGroup>

        <#assign firstItem = holeCompletionOptions?keys[0] >
        <@fdsCheckbox.checkboxGroup
        path="form.selectedHoleCompletionTypes[${firstItem}]"
        fieldsetHeadingText="Hole completion types"
        hiddenContent=true>
            <#list holeCompletionOptions?keys as holeCompletionOption>
                <@fdsCheckbox.checkboxItem
                path="form.selectedHoleCompletionTypes[${holeCompletionOption}]"
                labelText=holeCompletionOptions[holeCompletionOption]>
                    <@fdsTextInput.textInput
                    path="form.numberOfHoleCompletionTypes[${holeCompletionOption}].decimalInputValue"
                    nestingPath="form.selectedHoleCompletionTypes[${holeCompletionOption}]"
                    labelText="Number of wells with this hole completion type"
                    inputClass="govuk-input--width-3"
                    />
                </@fdsCheckbox.checkboxItem>
            </#list>
        </@fdsCheckbox.checkboxGroup>

        <#assign firstItem = plannedCompletionOptions?keys[0] >
        <@fdsCheckbox.checkboxGroup
        path="form.selectedPlannedCompletionTypes[${firstItem}]"
        fieldsetHeadingText="Planned completion types"
        hiddenContent=true>
            <#list plannedCompletionOptions?keys as plannedCompletionOption>
                <@fdsCheckbox.checkboxItem
                path="form.selectedPlannedCompletionTypes[${plannedCompletionOption}]"
                labelText=plannedCompletionOptions[plannedCompletionOption]>
                    <#if plannedCompletionOption == "OTHER">
                        <@fdsTextInput.textInput
                        path="form.otherPlannedCompletionType"
                        nestingPath="form.selectedPlannedCompletionTypes[${plannedCompletionOption}]"
                        labelText="What other completion types apply to these wells?"
                        />
                    </#if>

                    <@fdsTextInput.textInput
                    path="form.numberOfPlannedCompletionTypes[${plannedCompletionOption}].decimalInputValue"
                    nestingPath="form.selectedPlannedCompletionTypes[${plannedCompletionOption}]"
                    labelText="Number of wells with this planned completion type"
                    inputClass="govuk-input--width-3"
                    />
                </@fdsCheckbox.checkboxItem>
            </#list>
        </@fdsCheckbox.checkboxGroup>

        <#assign firstItem = artificialLiftMechanismsOptions?keys[0] >
        <@fdsCheckbox.checkboxGroup
        path="form.selectedArtificialLiftMechanisms[${firstItem}]"
        nestingPath="form"
        fieldsetHeadingText="Artifical lift mechanisms"
        hiddenContent=true>
            <#list artificialLiftMechanismsOptions?keys as artificialLiftMechanismsOption>
                <@fdsCheckbox.checkboxItem
                path="form.selectedArtificialLiftMechanisms[${artificialLiftMechanismsOption}]"
                labelText=artificialLiftMechanismsOptions[artificialLiftMechanismsOption]>
                    <#if artificialLiftMechanismsOption == "OTHER">
                        <@fdsTextInput.textInput
                        path="form.otherArtificialLiftMechanism"
                        nestingPath="form.selectedArtificialLiftMechanisms[${artificialLiftMechanismsOption}]"
                        labelText="What other artifical lift mechanisms are used?"
                        />
                    </#if>

                    <#if artificialLiftMechanismsOption != "NONE">
                        <@fdsTextInput.textInput
                        path="form.numberOfArtificialLiftMechanisms[${artificialLiftMechanismsOption}].decimalInputValue"
                        nestingPath="form.selectedArtificialLiftMechanisms[${artificialLiftMechanismsOption}]"
                        labelText="Number of wells with this artificial lift mechanism"
                        inputClass="govuk-input--width-3"
                        />
                    </#if>
                </@fdsCheckbox.checkboxItem>
            </#list>
        </@fdsCheckbox.checkboxGroup>

        <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>