<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
        <@fdsRadio.radio path="form.viscosityRange" labelText="Vicosity" radioItems=viscosityRangeMap />

        <@fdsRadio.radio path="form.verticalHeterogeneityRange" labelText="Vertical heterogeneity" radioItems=verticalHeterogeneityRangeMap />

        <@fdsRadio.radio path="form.arealDensityRange" labelText="Areal density" radioItems=arealDensityRangeMap />

        <@fdsRadio.radio path="form.structuralComplexityRange" labelText="Structural complexity" radioItems=structuralComplexityRangeMap />

        <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>