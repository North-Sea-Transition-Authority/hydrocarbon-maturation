<#include '../layout/layout.ftl'>
<#import '../summary/_undevelopedDiscoverySummaryCard.ftl' as undevelopedDiscoverySummary>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="summaryCard" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard>" -->
<#-- @ftlvariable name="summarySections" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
    <#list summarySections[0].summaryItems()![] as summaryItem>
      <#list summaryItem.summaryCards() as summaryCard>
        <@undevelopedDiscoverySummary.undevelopedDiscoverySummaryCard summaryCard=summaryCard/>
      </#list>
    </#list>
    <@fdsAccordion.accordion accordionId="summaryaccordian-${accordionId}">
      <#list 1..summarySections?size-1 as summarySectionIndex>
        <#assign summarySection = summarySections[summarySectionIndex]/>
        <#list summarySection.summaryItems() as summaryItem>
          <@fdsAccordion.accordionSection sectionHeading=summaryItem.displayName()
            openSection=(summarySectionIndex == 1 && summaryItem?index == 0)>
            <#list summaryItem.summaryCards() as summaryCard>
              <@undevelopedDiscoverySummary.undevelopedDiscoverySummaryCard summaryCard=summaryCard/>
            </#list>
          </@fdsAccordion.accordionSection>
        </#list>
      </#list>
    </@fdsAccordion.accordion>
    <@fdsAction.link
      linkText="Add discovery information"
      linkClass="govuk-button"
      linkUrl=springUrl(addDiscoveryInformationUrl)/>
</@defaultPage>