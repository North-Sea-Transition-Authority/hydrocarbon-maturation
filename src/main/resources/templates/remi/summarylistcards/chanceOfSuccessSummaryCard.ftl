<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="chanceOfSuccessValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView" -->

<#macro chanceOfSuccessCards chanceOfSuccessValues>
  <@fdsSummaryList.summaryListCard headingText="Play chance of success" summaryListId="chanceofsuccess">
    <@fdsSummaryList.summaryListRowNoAction keyText="Play source percentage">
      ${chanceOfSuccessValues.playSourcePercentage()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Play source comments">
      ${chanceOfSuccessValues.playSourceComments()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Play reservoir percentage">
      ${chanceOfSuccessValues.playReservoirPercentage()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Play reservoir comments">
      ${chanceOfSuccessValues.playReservoirComments()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Play top seal percentage">
      ${chanceOfSuccessValues.playTopSealPercentage()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Play top seal comments">
      ${chanceOfSuccessValues.playTopSealComments()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total play chance of success">
      ${chanceOfSuccessValues.totalPlayChanceOfSuccess()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

  <@fdsSummaryList.summaryListCard headingText="Prospect-specific chance of success" summaryListId="prospectChanceOfSuccess">
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific trap presence">
          ${chanceOfSuccessValues.prospectSpecificTrapPresence()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific trap presence comments">
          ${chanceOfSuccessValues.prospectSpecificTrapPresenceComments()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific trap effectiveness">
          ${chanceOfSuccessValues.prospectSpecificTrapEffectiveness()}
      </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific trap effectiveness comments">
      ${chanceOfSuccessValues.prospectSpecificTrapEffectivenessComments()}
    </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific source presence">
          ${chanceOfSuccessValues.prospectSpecificSourcePresence()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific source presence comments">
          ${chanceOfSuccessValues.prospectSpecificSourcePresenceComments()}
      </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific source effectiveness">
      ${chanceOfSuccessValues.prospectSpecificSourceEffectiveness()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific source effectiveness comments">
      ${chanceOfSuccessValues.prospectSpecificSourceEffectivenessComments()}
    </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific reservoir presence">
          ${chanceOfSuccessValues.prospectSpecificReservoirPresence()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific reservoir presence comments">
          ${chanceOfSuccessValues.prospectSpecificReservoirPresenceComments()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific reservoir effectiveness">
          ${chanceOfSuccessValues.prospectSpecificReservoirEffectiveness()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Prospect-specific reservoir effectiveness comments">
          ${chanceOfSuccessValues.prospectSpecificReservoirEffectivenessComments()}
      </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total prospect-specific chance of success">
      ${chanceOfSuccessValues.totalProspectSpecificChance()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Overall chance of success">
      ${chanceOfSuccessValues.overallChanceOfSuccess()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

  <@fdsSummaryList.summaryListCard headingText="Modified chance of success" summaryListId="otherChanceOfSuccess">
    <@fdsSummaryList.summaryListRowNoAction keyText="Geophysically modified">
      ${chanceOfSuccessValues.geophysicallyModified()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if chanceOfSuccessValues.geophysicallyModified() == "Yes">
      <@fdsSummaryList.summaryListRowNoAction keyText="Geophysically modified chance of success">
        ${chanceOfSuccessValues.geophysicallyModifiedPercentage()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Geophysically modified chance of success comments">
        ${chanceOfSuccessValues.geophysicallyModifiedComments()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Is chance of success in relation to a specific well location?">
      ${chanceOfSuccessValues.specificWellLocation()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if chanceOfSuccessValues.specificWellLocation() == "Yes">
      <@fdsSummaryList.summaryListRowNoAction keyText="Well-modified chance of success">
        ${chanceOfSuccessValues.wellModifiedPercentage()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Well-modified chance of success comments">
        ${chanceOfSuccessValues.wellModifiedComments()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>