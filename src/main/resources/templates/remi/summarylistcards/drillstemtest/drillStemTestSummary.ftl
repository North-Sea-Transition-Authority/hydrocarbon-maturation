<#include '../../layout/layout.ftl'>
<#import '../../summarylistcards/drillstemtest/drillStemTestResultSummaryCard.ftl' as drillStemTestResultCard>

<#macro drillStemTest drillStemTestValues>
  <#list drillStemTestValues.drillStemTestResultSummaryViews() as drillStemTestResultValue>
    <@drillStemTestResultCard.drillStemTestResult drillStemTestResultValues=drillStemTestResultValue withResultNumber=false/>
  </#list>
  <@fdsSummaryList.summaryListCard
    headingText="Drill stem test results"
    summaryListId="drillstemtest">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you have any more drill stem test results?">
      ${drillStemTestValues.hasMoreDrillStemTestResults()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>