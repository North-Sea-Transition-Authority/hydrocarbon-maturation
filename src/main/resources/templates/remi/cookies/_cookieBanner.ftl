<#include '../../fds/layout.ftl'>

<#-- @ftlvariable name="serviceBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties" -->
<#-- @ftlvariable name="cookiesStatementUrl" type="String" -->

<#macro cookieBanner>
    <@fdsCookieBanner.analyticsCookieBanner
    serviceName=serviceBranding.mnemonic()
    cookieSettingsUrl=springUrl(cookiesStatementUrl)
    />
</#macro>