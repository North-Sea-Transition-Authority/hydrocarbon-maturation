<#macro tableCard
summaryCardId
headingText=""
headingSize="h2"
cardActionsContent="">
  <div class="govuk-summary-card" id="${summaryCardId}">
    <#if headingText?has_content>
      <div class="govuk-summary-card__title-wrapper">
        <${headingSize} class="govuk-summary-card__title">
          ${headingText}
        </${headingSize}>

        ${cardActionsContent}
      </div>
    </#if>
    <div class="govuk-summary-card__content">
      <#nested>
    </div>
  </div>
</#macro>
