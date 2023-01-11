<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="java.lang.String" -->
<#-- @ftlvariable name="teamTypeRouteMap" type="java.util.Map<uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType, java.lang.String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  topNavigation=true
  pageSize=PageSize.FULL_WIDTH>
  <ul class="govuk-list">
    <#list teamTypeRouteMap as teamType, url>
      <li class="govuk-list__item">
        <#assign hintIdentifier="team-type-${teamType.name()?lower_case}-hint" />
        <@fdsAction.link
          linkText="${teamType.getDisplayText()}"
          linkClass="govuk-link govuk-link--no-visited-state"
          linkUrl=springUrl(url)
          ariaDescribedBy=hintIdentifier/>
        <span class="govuk-hint" id="${hintIdentifier}">${teamType.getHintText()}</span>
      </li>
    </#list>
  </ul>
</@defaultPage>