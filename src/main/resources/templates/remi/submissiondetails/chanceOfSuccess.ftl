<#include '../layout/layout.ftl'>

<#assign pageTitle = "Chance of success" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <div data-module="chance-of-success">
  <@fdsForm.htmlForm>
    <h2 class="govuk-heading-l">Play chance of success</h2>
    <@fdsDetails.summaryDetails summaryTitle="How is a play defined?">
      <p class="govuk-body">
        A play is a series of traps, some of which may already be tested, that share a common petroleum system of
        charge (source/timing/migration) and areal juxtaposition of regionally significant master seal and primary
        reservoir.
      </p>
    </@fdsDetails.summaryDetails>

    <@fdsTextInput.textInput suffixScreenReaderPrompt="Chance of success" path="form.playChanceOfSuccess.sourcePercentage.decimalInputValue" labelText="Source" suffix="%" inputClass="govuk-input--width-3"
      hintText="The presence of mature source rocks within the play boundary, with a viable migration pathway to the reservoir"/>
    <@fdsTextInput.textInput path="form.playChanceOfSuccess.sourceComments" labelText="Source knowledge level such as low, moderate, or high, and how work programme results could impact the chance of success"
      optionalLabel=true/>

    <@fdsTextInput.textInput suffixScreenReaderPrompt="Chance of success" path="form.playChanceOfSuccess.reservoirPercentage.decimalInputValue" labelText="Reservoir" suffix="%" inputClass="govuk-input--width-3"
      hintText="The presence of reservoir rocks within the play boundary capable of sustaining a minimum flow rate to surface"/>
    <@fdsTextInput.textInput path="form.playChanceOfSuccess.reservoirComments" labelText="Reservoir knowledge level such as low, moderate, or high, and how work programme results could impact the chance of success"
      optionalLabel=true/>

    <@fdsTextInput.textInput suffixScreenReaderPrompt="Chance of success" path="form.playChanceOfSuccess.topSealPercentage.decimalInputValue" labelText="Top seal" suffix="%" inputClass="govuk-input--width-3"
      hintText="The presence of a top seal lithology that is areally juxtaposed with the reservoir"/>
    <@fdsTextInput.textInput path="form.playChanceOfSuccess.topSealComments" labelText="Top seal knowledge level such as low, moderate, or high, and how work programme results could impact the chance of success"
      optionalLabel=true/>


    <@fdsDataItems.dataItem dataItemListClasses="">
      <@fdsDataItems.dataValues key="0.00%" value="Total play chance of success"/>
    </@fdsDataItems.dataItem>

    <h2 class="govuk-heading-l">Prospect-specific chance of success</h2>

      <@fdsDetails.summaryDetails summaryTitle="How is this determined?">
        <p class="govuk-body">
          The prospect-specific geological chance of success is conditional on the play success,
          and is defined as the chance of encountering a minimal recoverable volume based on the P99.
        </p>
        <p class="govuk-body">
          Hence each risk element below should be considered in relation to that minimal volume (P99).
        </p>
      </@fdsDetails.summaryDetails>

      <@fdsTextInput.textInput suffixScreenReaderPrompt="Chance of success" path="form.prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourcePercentage.decimalInputValue" labelText="Source" suffix="%" inputClass="govuk-input--width-3"/>
      <@fdsTextInput.textInput path="form.prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.sourceComments" labelText="Source knowledge level such as low, moderate, or high, and how work programme results could impact the chance of success"
        optionalLabel=true/>

      <@fdsTextInput.textInput suffixScreenReaderPrompt="Chance of success" path="form.prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirPercentage.decimalInputValue" labelText="Reservoir" suffix="%" inputClass="govuk-input--width-3"/>
      <@fdsTextInput.textInput path="form.prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.reservoirComments" labelText="Reservoir knowledge level such as low, moderate, or high, and how work programme results could impact the chance of success"
        optionalLabel=true/>

      <@fdsTextInput.textInput suffixScreenReaderPrompt="Chance of success" path="form.prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealPercentage.decimalInputValue" labelText="Top seal" suffix="%" inputClass="govuk-input--width-3"/>
      <@fdsTextInput.textInput path="form.prospectSpecificChanceOfSuccess.chanceOfSuccessDetailsForm.topSealComments" labelText="Top seal knowledge level such as low, moderate, or high, and how work programme results could impact the chance of success"
        optionalLabel=true/>

      <@fdsTextInput.textInput suffixScreenReaderPrompt="Chance of success" path="form.prospectSpecificChanceOfSuccess.trapPercentage.decimalInputValue" labelText="Trap" suffix="%" inputClass="govuk-input--width-3"/>
      <@fdsTextInput.textInput path="form.prospectSpecificChanceOfSuccess.trapComments" labelText="Trap knowledge level such as low, moderate, or high, and how work programme results could impact the chance of success"
        optionalLabel=true/>

      <@fdsTextInput.textInput suffixScreenReaderPrompt="Chance of success" path="form.prospectSpecificChanceOfSuccess.migrationPercentage.decimalInputValue" labelText="Migration" suffix="%" inputClass="govuk-input--width-3"/>
      <@fdsTextInput.textInput path="form.prospectSpecificChanceOfSuccess.migrationComments" labelText="Migration knowledge level such as low, moderate, or high, and how work programme results could impact the chance of success"
        optionalLabel=true/>

      <@fdsDataItems.dataItem>
        <@fdsDataItems.dataValues key="0.00%" value="Total prospect-specific chance of success"/>
      </@fdsDataItems.dataItem>
      <@fdsDataItems.dataItem>
        <@fdsDataItems.dataValues key="0.00%" value="Overall chance of success"/>
      </@fdsDataItems.dataItem>

      <@fdsRadio.radioGroup path="form.geophysicallyModified"
        fieldsetHeadingClass="govuk-fieldset__legend--l"
        labelText="Do you have a geophysically-modified chance of success?"
        hiddenContent=true>
        <@fdsRadio.radioYes path="form.geophysicallyModified">
          <@fdsTextInput.textInput path="form.geophysicallyModifiedPercentage.decimalInputValue" labelText="Geophysically-modified chance of success"
            suffix="%" suffixScreenReaderPrompt="percent" inputClass="govuk-input--width-3" nestingPath="form.geophysicallyModified"/>
          <@fdsTextInput.textInput path="form.geophysicallyModifiedComments" labelText="Please describe any geophysical evidence used in the volumetric and risk assessment"
            optionalLabel=true nestingPath="form.geophysicallyModified"/>
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.geophysicallyModified"/>
      </@fdsRadio.radioGroup>

      <@fdsRadio.radioGroup
        path="form.relatedToSpecificWellLocation"
        fieldsetHeadingClass="govuk-fieldset__legend--l"
        labelText="Is the chance of success being quoted in relation to a specific well location?"
        hiddenContent=true>
        <@fdsRadio.radioYes path="form.relatedToSpecificWellLocation">

          <@fdsTextInput.textInput path="form.wellModifiedPercentage.decimalInputValue" labelText="Well-modified chance of success"
            suffix="%" suffixScreenReaderPrompt="percent" inputClass="govuk-input--width-3" nestingPath="form.relatedToSpecificWellLocation"/>
          <@fdsTextInput.textInput path="form.wellModifiedComments" labelText="Comments" optionalLabel=true nestingPath="form.relatedToSpecificWellLocation"/>

        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.relatedToSpecificWellLocation"/>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>

  </@fdsForm.htmlForm>
  </div>
</@defaultPage>