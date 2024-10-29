<#include '../layout/layout.ftl'>

<#macro submissionBlockedNotificationBanner>
    <@fdsNotificationBanner.notificationBannerInfo fullWidth=true bannerTitleText="Submission blocked">
        <@fdsNotificationBanner.notificationBannerContent headingText="Submissions against this discovery cannot be accepted">
            You cannot make submissions against this discovery as it is now managed as part of a field.
        </@fdsNotificationBanner.notificationBannerContent>
    </@fdsNotificationBanner.notificationBannerInfo>
</#macro>