<#include '../layout/layout.ftl'>

<#macro reviewFieldNotificationBanner fieldStatusAfterSubmiit>
    <@fdsNotificationBanner.notificationBannerInfo fullWidth=true bannerTitleText="Field status will be changed">
        <@fdsNotificationBanner.notificationBannerContent headingText="Accepting this submission will update the field status">
            This will set the field status to "${fieldStatusAfterSubmiit}". Ensure any relevant documents are issued to the operator
        </@fdsNotificationBanner.notificationBannerContent>
    </@fdsNotificationBanner.notificationBannerInfo>
</#macro>