<#include '../../fds/layout.ftl'>
<#import '_pageSizes.ftl' as PageSize>
<#import '/spring.ftl' as spring>
<#import '_header.ftl' as pageHeader>

<#-- @ftlvariable name="serviceBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties" -->
<#-- @ftlvariable name="customerBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties" -->
<#-- @ftlvariable name="serviceHomeUrl" type="String" -->
<#-- @ftlvariable name="loggedInUser" type="uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail" -->

<#assign SERVICE_NAME = serviceBranding.name() />
<#assign CUSTOMER_MNEMONIC = customerBranding.mnemonic() />
<#assign SERVICE_HOME_URL = springUrl(serviceHomeUrl) />

<#macro defaultPage
  htmlTitle
  pageHeading=""
  phaseBanner=true
  errorItems=[]
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=""
  breadcrumbsMap={}
  wrapperWidth=false
  notificationBannerContent="">

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
    <#if backLinkUrl?has_content>
        <#assign backLink=true/>
    </#if>

    <#assign allScripts = springUrl('/assets/remi/javascript/all.js')/>
    <#assign hydrocarbonScripts>
      <script type="module" src="${allScripts}"></script>
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
      fullWidthColumn=fullWidthColumn
      oneHalfColumn=oneHalfColumn
      oneThirdColumn=oneThirdColumn
      twoThirdsColumn=twoThirdsColumn
      twoThirdsOneThirdColumn=twoThirdsOneThirdColumn
      oneQuarterColumn=oneQuarterColumn
      errorItems=errorItems
      backLink=backLink
      backLinkUrl=backLinkUrl
      breadcrumbs=useBreadCrumbs
      breadcrumbsList=breadcrumbsMap
      headerContent=serviceHeader
      wrapperWidth=wrapperWidth
      notificationBannerContent=notificationBannerContent
      customScriptContent=hydrocarbonScripts
    >
        <#nested />
    </@fdsDefaultPageTemplate>
</#macro>