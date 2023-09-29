<#include '../../layout/layout.ftl'>
<#import '../../summarylistcards/drillstemtest/drillStemTestResultSummaryCard.ftl' as drillStemTestResultCard>

<#macro drillStemTest drillStemTestValues>
    <@fdsSummaryList.summaryListCard headingText="Well test results" summaryListId="drillstemtestresultlist">
      <#list drillStemTestValues.drillStemTestResultSummaryViews() as drillStemTestResultValue>
        <@drillStemTestResultCard.drillStemTestResult drillStemTestResultValues=drillStemTestResultValue withResultNumber=false/>
      </#list>
    </@fdsSummaryList.summaryListCard>
</#macro>