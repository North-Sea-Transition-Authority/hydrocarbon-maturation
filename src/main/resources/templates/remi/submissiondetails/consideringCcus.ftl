<#include '../layout/layout.ftl'>

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup
        path="form.isConnectedToProject"
        labelText="Is the field hydrodynamically connected to a known carbon storage project?"
        hiddenContent=true>
            <@fdsRadio.radioYes path="form.isConnectedToProject">
                <@fdsTextarea.textarea
                path="form.connectedProjects"
                nestingPath="form.isConnectedToProject"
                labelText="Which projects is it connected to?"
                />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.isConnectedToProject" />
        </@fdsRadio.radioGroup>

        <@fdsRadio.radioGroup
        path="form.isAnyShallowerHorizons"
        labelText="Are there any shallower horizons above the reservoir that could act as secondary containment for a
        carbon storage development?"
        hiddenContent=true>
            <@fdsRadio.radioYes
            path="form.isAnyShallowerHorizons">
                <@fdsTextarea.textarea
                path="form.ageAndFormationOfHorizons"
                nestingPath="form.isConnectedToProject"
                labelText="Provide the age and formation of all horizons that could act as secondary containment for a
                carbon storage development"
                />
            </@fdsRadio.radioYes>
            <@fdsRadio.radioNo path="form.isAnyShallowerHorizons" />
        </@fdsRadio.radioGroup>

        <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
    </@fdsForm.htmlForm>
</@defaultPage>