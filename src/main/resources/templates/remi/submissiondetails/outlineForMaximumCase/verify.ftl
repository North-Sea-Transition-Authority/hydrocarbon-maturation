<#include '../../layout/layout.ftl'>
<#import '../../maps/shapeMap.ftl' as maps/>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="geoJsonDataUrl" type="String" -->
<#-- @ftlvariable name="dataMapAttributions" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm>

      <@maps.shapeMap
        geoJsonDataUrl=springUrl(geoJsonDataUrl)
        dataMapAttributions=dataMapAttributions
        shapeName="Outline for maximum case"
        showFieldDeterminationBoundariesLayer=true
        showQuadBlocksLayer=true
        showLicenceBlocksLayer=true
        showFieldBlobsLayer=true
        />

      <@fdsRadio.radioGroup
        path="form.shapeIsCorrect"
        fieldsetHeadingClass="govuk-fieldset__legend--l"
        labelText="Is the outline for maximum case shape correct?"
        hiddenContent=true>
          <@fdsRadio.radioYes path="form.shapeIsCorrect"/>
          <@fdsRadio.radioItem path="form.shapeIsCorrect" itemMap={"false":  "No, I want to provide a different shapefile"}/>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons
        primaryButtonText="Save and continue"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"
        />

    </@fdsForm.htmlForm>
</@defaultPage>