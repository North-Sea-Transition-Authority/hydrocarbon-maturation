<#include '../layout/layout.ftl'>
<#import 'fileUploadSummaryCard.ftl' as fileUploadSummary>
<#import 'shapeMapSummaryCard.ftl' as shapeMapSummaryCard>

<#-- @ftlvariable name="outlineForMaximumCaseValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView" -->

<#macro outlineForMaximumCaseSummaryCard outlineForMaximumCaseValues>
  <@fdsSummaryList.summaryListCard headingText="Outline for maximum case" summaryListId="outlineformaximumcase">
    <@fileUploadSummary.fileUploadSummary
      filesUploaded=outlineForMaximumCaseValues.outlineForMaximumCaseShapefile()
      fieldName="Outline for maximum case shapefile"/>
    <#if outlineForMaximumCaseValues.hasVerifiedShapefile()>
      <@shapeMapSummaryCard.shapeMapSummary
        geoJsonDataUrl=springUrl(outlineForMaximumCaseValues.mapGeoJsonDataRoute())
        dataMapAttributions=outlineForMaximumCaseValues.mapAttributions()/>
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>