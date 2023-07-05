<#include '../layout/layout.ftl'>
<#import 'fileUploadSummaryCard.ftl' as fileUploadSummary>
<#import 'shapeMapSummaryCard.ftl' as shapeMapSummaryCard>

<#-- @ftlvariable name="outlineForMaximumCaseValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView" -->
<#assign shapeName>Outline for maximum case</#assign>
<#macro outlineForMaximumCaseSummaryCard outlineForMaximumCaseValues>
  <@fdsSummaryList.summaryListCard headingText=shapeName summaryListId="outlineformaximumcase">
    <@fileUploadSummary.fileUploadSummary
      filesUploaded=outlineForMaximumCaseValues.outlineForMaximumCaseShapefile()
      fieldName="Outline for maximum case shapefile"/>
    <#if outlineForMaximumCaseValues.hasVerifiedShapefile()>
      <@shapeMapSummaryCard.shapeMapSummary
        geoJsonDataUrl=springUrl(outlineForMaximumCaseValues.mapGeoJsonDataRoute())
        dataMapAttributions=outlineForMaximumCaseValues.mapAttributions()
        shapeName=shapeName
      />
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>