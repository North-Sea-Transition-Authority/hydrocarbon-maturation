package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = RelatedResourcesController.class)
class RelatedResourcesControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  RelatedResourcesFormService relatedResourcesFormService;

  @MockBean
  RelatedResourcesPersistenceService relatedResourcesPersistenceService;

  private RelatedResourcesForm form;
  private BindingResult bindingResult;
  private UUID submissionDetailId;

  @BeforeEach
  void setUp() {
    form = new RelatedResourcesForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
  }

  @SecurityTest
  void getRelatedResourcesPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(relatedResourcesFormService.getRelatedResourcesForm(submissionDetail)).thenReturn(new RelatedResourcesForm());
    setUpWhenUserIsResponsibleTeam_passSecurity();

    mockMvc.perform(
        get(ReverseRouter.route(on(RelatedResourcesController.class).getRelatedResources(submissionDetailId, form)))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void getRelatedResourcesPage_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(RelatedResourcesController.class).getRelatedResources(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getRelatedResourcesPage_andTypeIsNotCreateResource_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.PROSPECT_UPDATE);
    mockMvc.perform(
            get(ReverseRouter.route(on(RelatedResourcesController.class).getRelatedResources(submissionDetailId, form)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getRelatedResourcesPage_andUserIsNotRegulatorAndNotResponsibleParty_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(RelatedResourcesController.class)
                .getRelatedResources(submissionDetailId, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveRelatedResourcesPage_andUserIsNotRegulatorAndNotResponsibleParty_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(RelatedResourcesController.class)
                .saveRelatedResources(submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveRelatedResourcesPage_whenValidForm_assertRedirection(ValidationType validationType) throws Exception {
    when(relatedResourcesFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
    mockMvc.perform(
        post(ReverseRouter.route(on(RelatedResourcesController.class).saveRelatedResources(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(relatedResourcesPersistenceService, times(1)).saveRelatedResources(any(RelatedResourcesForm.class), eq(submissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveRelatedResourcesPage_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    when(relatedResourcesFormService.validate(any(), any(), any(), eq(submissionDetail))).thenReturn(bindingResult);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
    mockMvc.perform(
        post(ReverseRouter.route(on(RelatedResourcesController.class).saveRelatedResources(submissionDetailId, form, bindingResult, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(relatedResourcesPersistenceService, never()).saveRelatedResources(any(), eq(submissionDetail));
  }

  @Test
  void getRelatedResourcesModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(relatedResourcesFormService.getRelatedResourcesForm(submissionDetail)).thenReturn(new RelatedResourcesForm());
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(RelatedResourcesController.class).getRelatedResources(submissionDetailId, form)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(RelatedResourcesForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
        entry("pageTitle", "Related prospects"),
        entry("resourceType", "prospect"),
        entry("resourceGroupsEndpoint",
            SearchSelectorService.route(on(ResourceGroupRestController.class)
                .searchProspects(null, submissionDetail.getSubmission().getResource().getId())))
    )
    .containsKeys("breadcrumbsMap");
  }

  @Test
  void getRelatedResourcesModelAndView_whenDiscovery_assertModelProperties() throws Exception {
    submissionDetail.getSubmission().getResource().setType(ResourceType.DISCOVERY);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(relatedResourcesFormService.getRelatedResourcesForm(submissionDetail)).thenReturn(new RelatedResourcesForm());
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(RelatedResourcesController.class).getRelatedResources(submissionDetailId, form)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();


    assertEquals(RelatedResourcesForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
        entry("pageTitle", "Related discoveries"),
        entry("resourceType", "discovery"),
        entry("resourceGroupsEndpoint",
            SearchSelectorService.route(on(ResourceGroupRestController.class)
                .searchDiscoveries(null, submissionDetail.getSubmission().getResource().getId())))
        )
        .containsKeys("breadcrumbsMap");
  }

  @SecurityTest
  void getRelatedResources_notRelatedTeam_assertForbidden() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(RelatedResourcesController.class).getRelatedResources(submissionDetailId, form)))
            .with(user(USER)))
        .andExpect(status().isForbidden());
  }
}