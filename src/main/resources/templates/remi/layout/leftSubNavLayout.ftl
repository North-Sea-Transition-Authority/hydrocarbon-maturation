<#include '../../fds/objects/layouts/leftSubNavLayout.ftl'>
<#import '_pageSizes.ftl' as PageSize>
<#import '/spring.ftl' as spring>

<#function springUrl url>
  <#local springUrl>
    <@spring.url url/>
  </#local>
  <#return springUrl>
</#function>

<#macro defaultLeftSubNavPage
  htmlTitle
  htmlAppTitle=""
  wrapperWidth=false
  phaseBanner=true
  customScriptContent="">

  <@fdsLeftSubNavPageTemplate
    htmlTitle=htmlTitle
    htmlAppTitle=htmlAppTitle
    wrapperWidth=wrapperWidth
    phaseBanner=phaseBanner
    customScriptContent=customScriptContent>

    <#nested>
  </@fdsLeftSubNavPageTemplate>
</#macro>

<#macro defaultLeftSubNavPageContent
  mainClasses=""
  captionClass="govuk-caption-xl"
  caption=""
  pageHeadingClass="govuk-heading-xl"
  pageHeading=""
  errorItems=[]
  singleErrorMessage=""
  notificationBannerContent=""
  breadcrumbsMap={}>

  <div class="fds-pane__content">
    <main id="main-content" class="fds-content ${mainClasses}" role="main">
      <#if breadcrumbsMap?has_content>
        <@fdsBreadcrumbs.breadcrumbs crumbsList=breadcrumbsMap currentPage=currentPage/>
      </#if>

      <div class="fds-content__header">
        <@defaultHeading
          caption=caption
          captionClass=captionClass
          pageHeading=pageHeading
          pageHeadingClass=pageHeadingClass
          errorItems=errorItems
          singleErrorMessage=singleErrorMessage
          notificationBannerContent=notificationBannerContent/>
      </div>
      <#nested>
    </main>
  </div>
</#macro>