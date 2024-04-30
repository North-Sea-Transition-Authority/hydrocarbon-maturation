<#include '../layout/layout.ftl'>
<#import './multiVolumetricInput.ftl' as multiVolumetricInput>
<#import './inPlaceVolumetricInput.ftl' as inPlaceVolumetricInput>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  caption=pageCaption
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsDateInput.dateInput
      formId="discoveryInformationDate-date-input"
      dayPath="form.discoveryInformationDateDay"
      monthPath="form.discoveryInformationDateMonth"
      yearPath="form.discoveryInformationDateYear"
      labelText="Date discovery information is relevant to"/>
    <@fdsFieldset.fieldset
      legendHeadingSize="h2"
      legendHeadingClass="govuk-fieldset__legend--l"
      legendHeading="Discovery volumetrics">
      <@inPlaceVolumetricInput.inPlaceVolumetric
        path="form.volumetricsForm.oilInPlace.decimalInputValue"
        hydrocarbon="Oil"
        units="mmbbl"/>
      <@multiVolumetricInput.multiVolumetricInput
        oneCPath="form.volumetricsForm.oilRecoverable.oneC.decimalInputValue"
        twoCPath="form.volumetricsForm.oilRecoverable.twoC.decimalInputValue"
        threeCPath="form.volumetricsForm.oilRecoverable.threeC.decimalInputValue"
        labelText="Oil recoverable resources (mmbbl)"
        formId="form.volumetricsForm.oilRecoverable"/>
      <@inPlaceVolumetricInput.inPlaceVolumetric
        path="form.volumetricsForm.gasInPlace.decimalInputValue"
        hydrocarbon="Gas"
        units="bcf"/>
      <@multiVolumetricInput.multiVolumetricInput
        oneCPath="form.volumetricsForm.gasRecoverable.oneC.decimalInputValue"
        twoCPath="form.volumetricsForm.gasRecoverable.twoC.decimalInputValue"
        threeCPath="form.volumetricsForm.gasRecoverable.threeC.decimalInputValue"
        labelText="Gas recoverable resources (bcf)"
        formId="form.volumetricsForm.gasRecoverable"/>
      <@fdsTextarea.textarea
        path="form.volumetricsForm.volumetricDifferenceReason"
        labelText="Provide reasons why volumetrics are being provided where they differ from the current operator provided figures"
        rows="4"
        optionalLabel=true/>
    </@fdsFieldset.fieldset>
    <@fdsFieldset.fieldset
      legendHeadingSize="h2"
      legendHeadingClass="govuk-fieldset__legend--l"
      legendHeading="Discovery complexity">
      <@fdsRadio.radioGroup
        path="form.provideDiscoveryComplexity"
        labelText="Do you want to provide discovery complexity information for this discovery?"
        hiddenContent=true>
          <@fdsRadio.radioYes path="form.provideDiscoveryComplexity">
            <@fdsRadio.radio path="form.complexityForm.viscosityRange" labelText="Viscosity (cP)"
              radioItems=viscosityRangeMap/>
            <@fdsRadio.radio path="form.complexityForm.verticalHeterogeneityRange" labelText="Vertical heterogeneity (kmax/kmin)"
              radioItems=verticalHeterogeneityRangeMap />
            <@fdsRadio.radio path="form.complexityForm.arealDensityRange" labelText="Areal density (millions sm3/km2)"
              radioItems=arealDensityRangeMap/>
            <@fdsRadio.radio path="form.complexityForm.structuralComplexityRange" labelText="Structural complexity"
              radioItems=structuralComplexityRangeMap/>
          </@fdsRadio.radioYes>
          <@fdsRadio.radioNo path="form.provideDiscoveryComplexity"/>
      </@fdsRadio.radioGroup>
    </@fdsFieldset.fieldset>

    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>