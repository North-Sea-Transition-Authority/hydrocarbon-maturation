<#include '../layout/layout.ftl'>

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
errorItems=[]
pageSize=PageSize.TWO_THIRDS_COLUMN
>
    <ul class="govuk-list">
        <li class="govuk-list__item">
            <@_systemOrRecordLandingLink
            linkText="Search for prospects and discoveries"
            linkUrl=springUrl(prospectAndDiscoverySearchUrl)
            ariaDescribedByHintId="prospects-and-discoveries-hint"
            />
            <div class="govuk-hint" id="prospects-and-discoveries-hint">
                All prospects and discovery submissions that have been added to
                the system of record
            </div>
        </li>

        <li class="govuk-list__item">
            <@_systemOrRecordLandingLink
            linkText="Search for fields"
            linkUrl=springUrl(fieldSearchUrl)
            ariaDescribedByHintId="fields-hint"
            />
            <div class="govuk-hint" id="fields-hint">
                All field submissions that have been added to the system of record
            </div>
        </li>
    </ul>
</@defaultPage>

<#macro _systemOrRecordLandingLink linkText, linkUrl, ariaDescribedByHintId>
    <@fdsAction.link
    linkUrl=linkUrl
    linkText=linkText
    linkClass="govuk-link govuk-link--no-visited-state govuk-!-font-size-24 govuk-!-font-weight-bold"
    ariaDescribedBy=ariaDescribedByHintId
    />
</#macro>