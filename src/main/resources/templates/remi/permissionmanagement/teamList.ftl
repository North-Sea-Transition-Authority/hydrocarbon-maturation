
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="allTeams" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.teams.TeamView>" -->
<#-- @ftlvariable name="pageTitle" type="java.lang.String" -->
<#-- @ftlvariable name="newTeamFormUrl" type="java.lang.String" -->

<#include "../layout/layout.ftl">
<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  errorItems=errorList
  pageSize=PageSize.FULL_WIDTH
>
    <#list teamGroupMap as teamType,teams>
      <h2 class="govuk-heading-l">${teamType.getDisplayText()}</h2>
      <#if teamType == "INDUSTRY" && newIndustryTeamFormUrl?has_content>
          <@fdsAction.link linkText="Create organisation group team" linkClass="govuk-button" linkUrl=springUrl(newIndustryTeamFormUrl)/>
      </#if>
      <div class="govuk-!-margin-bottom-6">
          <@fdsLinkList.linkList>
              <#list teams as team>
                  <@fdsLinkList.linkListItem
                    linkName=team.displayName()
                    linkUrl=team.manageUrl()
                  />
              </#list>
          </@fdsLinkList.linkList>
      </div>
    </#list>
</@defaultPage>