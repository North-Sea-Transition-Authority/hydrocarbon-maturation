<#include '../../layout/layout.ftl'>

<#macro submissionSummaryNotificationBanner isRegulator>
  <@fdsNotificationBanner.notificationBannerInfo fullWidth=true bannerTitleText="Missing information">
    <@fdsNotificationBanner.notificationBannerContent headingText="Not all sections shown on the task list have been completed.">
      <#if isRegulator>
        You will be allowed to submit without this information but this will mean the information will not be available in
        the approved view of the resource.
      <#else>
        You will not be allowed to submit without completing all sections on the task list.
      </#if>
    </@fdsNotificationBanner.notificationBannerContent>
</@fdsNotificationBanner.notificationBannerInfo>
</#macro>