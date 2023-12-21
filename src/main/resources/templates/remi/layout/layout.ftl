<#include '../../fds/layout.ftl'>
<#import '_pageSizes.ftl' as PageSize>
<#import '/spring.ftl' as spring>
<#import '_header.ftl' as pageHeader>

<#-- @ftlvariable name="serviceBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties" -->
<#-- @ftlvariable name="customerBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties" -->
<#-- @ftlvariable name="serviceHomeUrl" type="String" -->
<#-- @ftlvariable name="feedbackPageUrl" type="String" -->
<#-- @ftlvariable name="accessibilityStatementUrl" type="String" -->
<#-- @ftlvariable name="singleErrorMessage" type="String" -->
<#-- @ftlvariable name="contactInformationUrl" type="String" -->
<#-- @ftlvariable name="loggedInUser" type="uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail" -->
<#-- @ftlvariable name="flash" type="uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner" -->

<#assign SERVICE_NAME = serviceBranding.name() />
<#assign CUSTOMER_MNEMONIC = customerBranding.mnemonic() />
<#assign SERVICE_HOME_URL = springUrl(serviceHomeUrl) />
<#assign FEEDBACK_PAGE_URL = springUrl(feedbackPageUrl) />
<#assign ACCESSIBILITY_PAGE_URL = springUrl(accessibilityStatementUrl) />
<#assign CONTACT_PAGE_URL = springUrl(contactInformationUrl) />

<#macro defaultPage
  htmlTitle
  pageHeading=""
  phaseBanner=true
  errorItems=[]
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=""
  backLinkEnabled=false
  caption=""
  breadcrumbsMap={}
  wrapperWidth=false
  notificationBanner=""
  singleErrorMessage=""
  topNavigation=true
  noIndex=true>

    <#assign fullWidthColumn=false />
    <#assign oneHalfColumn=false />
    <#assign oneThirdColumn=false />
    <#assign twoThirdsColumn=false />
    <#assign twoThirdsOneThirdColumn=false />
    <#assign oneQuarterColumn=false />


    <#if pageSize == PageSize.FULL_WIDTH>
        <#assign fullWidthColumn=true/>
    <#elseif pageSize == PageSize.ONE_HALF_COLUMN>
        <#assign oneHalfColumn=true/>
    <#elseif pageSize == PageSize.ONE_THIRD_COLUMN>
        <#assign oneThirdColumn=true/>
    <#elseif pageSize == PageSize.TWO_THIRDS_ONE_THIRD_COLUMN>
        <#assign twoThirdsOneThirdColumn=true/>
    <#elseif pageSize == PageSize.ONE_QUARTER>
        <#assign oneQuarterColumn=true/>
    <#else>
        <#assign twoThirdsColumn=true/>
    </#if>
    <#assign useBreadCrumbs=false>
    <#if breadcrumbsMap?has_content>
        <#assign useBreadCrumbs=true>
    </#if>

    <#assign backLink = false>
    <#if backLinkUrl?has_content || backLinkEnabled>
      <#assign backLink=true/>
    </#if>

    <#if flash?has_content>
      <#assign notificationBannerContent>
          <#local bannerContent>
              <#if flash.heading?has_content>
                  <@fdsNotificationBanner.notificationBannerContent headingText=flash.heading moreContent=flash.content/>
              <#else>
                <p class="govuk-body">
                    ${flash.content}
                </p>
              </#if>
          </#local>

          <#if flash.type.name() == "INFO">
              <@fdsNotificationBanner.notificationBannerInfo bannerTitleText=flash.title>
                  ${bannerContent}
              </@fdsNotificationBanner.notificationBannerInfo>
          <#elseif flash.type.name() == "SUCCESS">
              <@fdsNotificationBanner.notificationBannerSuccess bannerTitleText=flash.title>
                  ${bannerContent}
              </@fdsNotificationBanner.notificationBannerSuccess>
          </#if>
        </#assign>
    <#else>
        <#assign notificationBannerContent=notificationBanner>
    </#if>

    <#assign hydrocarbonScripts>
      <#assign allScripts = springUrl('/assets/remi/javascript/all.js')/>
      <script type="module" src="${allScripts}"></script>

      <#-- GIS -->
      <#assign gisScript = springUrl('/assets/static/js/remi-gis-bundle.js')/>
      <script type="module" src="${gisScript}"></script>
      <link rel="stylesheet" href="<@spring.url'/assets/static/css/ol.css'/>">
    </#assign>

    <#local footerMetaContent>
      <@fdsFooter.footerMeta footerMetaHiddenHeading="Support links">
        <@fdsFooter.footerMetaLink linkText="Accessibility statement" linkUrl=ACCESSIBILITY_PAGE_URL/>
        <@fdsFooter.footerMetaLink linkText="Contact" linkUrl=CONTACT_PAGE_URL/>
        <@fdsFooter.footerMetaLink linkText="Feedback" linkUrl=FEEDBACK_PAGE_URL/>
      </@fdsFooter.footerMeta>
    </#local>

    <#assign REMI_FOOTER>
        <@fdsNstaFooter.nstaFooter metaLinks=true footerMetaContent=footerMetaContent/>
    </#assign>

    <#assign serviceHeader>
        <@pageHeader.header
          serviceName=SERVICE_NAME
          customerMnemonic=CUSTOMER_MNEMONIC
          serviceHomeUrl=SERVICE_HOME_URL
          signedInUserName=(loggedInUser?has_content)?then(loggedInUser.displayName(), "")
          signOutUrl=springUrl("/logout")
          wrapperWidth=wrapperWidth
        />
    </#assign>

    <@fdsDefaultPageTemplate
      htmlTitle=htmlTitle
      serviceName=SERVICE_NAME
      htmlAppTitle=SERVICE_NAME
      pageHeading=pageHeading
      headerLogo="GOV_CREST"
      logoProductText=CUSTOMER_MNEMONIC
      phaseBanner=phaseBanner
      serviceUrl=SERVICE_HOME_URL
      homePageUrl=SERVICE_HOME_URL
      phaseBannerLink=FEEDBACK_PAGE_URL
      fullWidthColumn=fullWidthColumn
      footerContent=REMI_FOOTER
      oneHalfColumn=oneHalfColumn
      oneThirdColumn=oneThirdColumn
      twoThirdsColumn=twoThirdsColumn
      twoThirdsOneThirdColumn=twoThirdsOneThirdColumn
      oneQuarterColumn=oneQuarterColumn
      topNavigation=topNavigation
      errorItems=errorItems
      backLink=backLink
      backLinkUrl=backLinkUrl
      breadcrumbs=useBreadCrumbs
      breadcrumbsList=breadcrumbsMap
      headerContent=serviceHeader
      wrapperWidth=wrapperWidth
      notificationBannerContent=notificationBannerContent
      caption=caption
      customScriptContent=hydrocarbonScripts
      singleErrorMessage=singleErrorMessage
      noIndex=noIndex
    >
        <#nested />
    </@fdsDefaultPageTemplate>
</#macro>