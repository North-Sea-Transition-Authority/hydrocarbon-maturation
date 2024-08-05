<#include '../layout/layout.ftl'>
<#import 'fileUploadSummaryRows.ftl' as fileUploadSummary>
<#import 'shapeMapSummaryCard.ftl' as shapeMapSummaryCard>

<#-- @ftlvariable name="outlineForMaximumCaseValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView" -->
<#assign shapeName>Outline for maximum case</#assign>
<#macro outlineForMaximumCaseSummaryCard outlineForMaximumCaseValues>
  <@fdsSummaryList.summaryListCard headingText=shapeName summaryListId="outlineformaximumcase">
    <@fileUploadSummary.fileUploadSummary filesUploaded=outlineForMaximumCaseValues.outlineForMaximumCaseShapefile()/>
    <#if outlineForMaximumCaseValues.hasVerifiedShapefile()>
      <@shapeMapSummaryCard.shapeMapSummary
        geoJsonDataUrl=springUrl(outlineForMaximumCaseValues.mapGeoJsonDataRoute())
        dataMapAttributions=outlineForMaximumCaseValues.mapAttributions()
        shapeName=shapeName
        showShapeNameKey=true
      />
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>