package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceView;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = NewFieldController.class)
class NewFieldControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private NewFieldService newFieldService;

  @SecurityTest
  void getNewFieldPage_whenNoPermission_assertForbidden() throws Exception {
    var form = new FieldForm();
    when(permissionService.hasPermission(USER, Set.of(RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewFieldController.class).getNewFieldPage(form)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void getNewFieldPage_assertModelProperties() throws Exception {
    var form = new FieldForm();

    var preselectedOrganisation = Map.of("25", "Shell Gas");
    when(newFieldService.getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId()))
        .thenReturn(preselectedOrganisation);

    var geographicalAreas = Map.of("1", "Area1");
    when(newFieldService.getGeographicalAreaDisplayableOptions())
        .thenReturn(geographicalAreas);

    var responsibleTeamTypes = Map.of("1", "Team1");
    when(newFieldService.getResponsibleTeamTypeDisplayableOptions())
        .thenReturn(responsibleTeamTypes);

    var parentDeterminedField = Map.of("1", "Field1");
    when(newFieldService.getPreSelectedParentDeterminedField(form.getParentDeterminedField()))
        .thenReturn(parentDeterminedField);

    var alreadyAddedDiscovery = List.of(
        new ResourceView(UUID.randomUUID(), "Discovery1")
    );
    when(newFieldService.getPreSelectedAssociatedDiscoveriesMap(form.getAssociatedDiscoveries()))
        .thenReturn(alreadyAddedDiscovery);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(NewFieldController.class)
                        .getNewFieldPage(form)))
                        .with(user(USER))
                )
                .andReturn()
                .getModelAndView()
        )
        .getModelMap();

    assertThat(modelMap)
        .contains(
            entry("backLinkUrl", ReverseRouter.route(on(StartFieldController.class).getStartPage())),
            entry("pageTitle", NewFieldController.PAGE_NAME),
            entry("organisationUnitEndpoint", SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null))),
            entry("responsibleOrganisation", preselectedOrganisation),
            entry("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore()),
            entry("geographicalAreas", geographicalAreas),
            entry("responsibleTeamTypes", responsibleTeamTypes),
            entry("determinationStatus", DisplayableEnumOptionUtil.getDisplayableOptions(DeterminationStatus.class)),
            entry("parentDeterminedFieldEndpoint", SearchSelectorService.route(on(DeterminedFieldRestController.class).searchDeterminedFields(null))),
            entry("parentDeterminedField", parentDeterminedField),
            entry("alreadyAddedAssociatedDiscoveries", alreadyAddedDiscovery),
            entry("cancelUrl", ReverseRouter.route(on(StartFieldController.class).getStartPage()))
        );
  }

  @Test
  void saveResourceDetailsPage_whenInValidForm_assertStatusOk() throws Exception {
    var form = new FieldForm();
    var bindingResult = new BeanPropertyBindingResult(FieldForm.class, "form");
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(newFieldService.validate(any(), any())).thenReturn(bindingResult);
    mockMvc.perform(
            post(ReverseRouter.route(on(NewFieldController.class)
                .createField(form, bindingResult, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(newFieldService, never()).createNewField(form);
  }

  @Test
  void saveResourceDetails_whenValidForm_assertSaveAndRedirect() throws Exception {
    var form = new FieldForm();
    var bindingResult = new BeanPropertyBindingResult(FieldForm.class, "form");
    when(newFieldService.validate(any(), any())).thenReturn(bindingResult);

    var field = new Field();
    when(newFieldService.createNewField(any(FieldForm.class))).thenReturn(field);

    mockMvc.perform(
            post(ReverseRouter.route(on(NewFieldController.class)
                .createField(form, bindingResult, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null))));

    verify(newFieldService, times(1)).createNewField(any(FieldForm.class));
  }
}