<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="pageTitle" type="String"-->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
      <@fdsTextInput.textInput path="form.reservoirName" labelText="Reservoir name"/>

      <#if isFieldSubmission>
        <@fdsFieldset.fieldset
          legendHeadingSize="h2"
          legendHeadingClass="govuk-fieldset__legend--s"
          legendHeading="What discoveries reservoirs are associated with this field reservoir?">
            <@fdsAddToList.addToList
              pathForList="form.associatedDiscoveryReservoirs"
              pathForSelector="form.associatedDiscoveryReservoirSelect"
              alreadyAdded=alreadyAddedAssociatedDiscoveryReservoirs
              title=""
              itemName="Discovery reservoir"
              noItemText="No discovery reservoir"
              invalidItemText="This discovery reservoir is invalid"
              addToListId="associated-discovery-reservoir-table"
              selectorInputWidth="48"
              selectorLabelText="Select a discovery reservoir to associate to this field reservoir"
              restUrl=springUrl(discoveryReservoirsApiEndpoint)/>
            <@fdsDetails.summaryDetails summaryTitle="The discovery reservoir is not listed">
              <p class="govuk-body">
                You can only associate reservoirs from the following discoveries with this field:
                <#if resourceNames?size == 1>
                  <p>${resourceNames?first}</p>
                <#elseif resourceNames?size gt 1>
                  <ul class="govuk-list govuk-list--bullet">
                      <#list resourceNames as name>
                        <li>${name}</li>
                      </#list>
                  </ul>
                </#if>
              </p>

              <p class="govuk-body">
                If you want to associate a reservoir from a discovery not shown in the list above then contact NSTA for assistance.
              </p>
            </@fdsDetails.summaryDetails>
        </@fdsFieldset.fieldset>
      </#if>

      <@fdsRadio.radioGroup path="form.hydrocarbonsDiscovered" labelText="Hydrocarbons discovered">
          <#assign firstItem=true/>
          <#list hydrocarbonsDiscovered as key, value>
              <@fdsRadio.radioItem path="form.hydrocarbonsDiscovered" itemMap={key : value} isFirstItem=firstItem/>
              <#assign firstItem=false/>
          </#list>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>