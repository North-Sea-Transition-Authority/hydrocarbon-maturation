<#include '../../layout/layout.ftl'>

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
breadcrumbsMap=breadcrumbsMap
errorItems=errorList>
    <@fdsForm.htmlForm>
        <@fdsAddAField.addAField
            path="form.reservoirs"
            fieldListSize=reservoirCount
            fieldLabelText="Reservoir name"
            fieldHintText="The reservoir name must be unique across all reservoirs on the field"
            actionLinkText="Add another reservoir"/>
        <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>