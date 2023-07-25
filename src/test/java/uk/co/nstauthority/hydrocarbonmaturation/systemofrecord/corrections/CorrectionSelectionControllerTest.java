package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionQuerySearchRecord;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = CorrectionSelectionController.class)
class CorrectionSelectionControllerTest extends AbstractControllerTest {

  @MockBean
  private ResourceService resourceService;

  @MockBean
  private CorrectionService correctionService;

  @MockBean
  private CorrectionFormValidator correctionFormValidator;

  @MockBean
  private SubmissionHistorySelectorUtil submissionHistorySelectorUtil;

  @MockBean
  private SubmissionHistoryQueryService submissionHistoryQueryService;

  @MockBean
  private PermissionService permissionService;

  private Resource resource;
  private UUID resourceId;
  private CorrectionForm form;
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resourceId = resource.getId();
    form = new CorrectionForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    when(resourceService.getResourceById(resource.getId())).thenReturn(Optional.of(resource));
    when(permissionService.hasPermission(user, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(true);
    when(correctionService.canStartCorrection(resource, user)).thenReturn(true);
  }

  @SecurityTest
  void getCorrectionPage_assertStatusOk() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(CorrectionSelectionController.class)
                .startCorrection(resourceId, null)))
                .with(user(user)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void postCorrectionPage_noErrors_assertStatusOk_verifyRedirectCall() throws Exception {
    when(correctionFormValidator.isValid(any(CorrectionForm.class), any(Resource.class), any(BindingResult.class)))
        .thenReturn(true);
    mockMvc.perform(
            post(ReverseRouter.route(on(CorrectionSelectionController.class)
                .startCorrection(resourceId, form, bindingResult, null)))
                .with(user(user))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(correctionService, times(1)).startCorrectionForTypeAndRedirect(any(CorrectionForm.class), eq(user));
  }

  @SecurityTest
  void getCorrectionPage_whenNoResourceFound_assertStatusNotFound() throws Exception {
    var invalidResourceId = UUID.randomUUID();
    when(resourceService.getResourceById(invalidResourceId))
        .thenReturn(Optional.empty());

    mockMvc.perform(
            get(ReverseRouter.route(on(CorrectionSelectionController.class)
                .startCorrection(invalidResourceId, null)))
                .with(user(user)))
        .andExpect(status().isNotFound());
  }

  @SecurityTest
  void getCorrectionPage_whenUserDoesntHaveValidPermission() throws Exception {
    when(permissionService.hasPermission(user, Set.of(
        RolePermission.EXPLORATION, RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.ONSHORE_AREA_TEAM_MEMBER)))
        .thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(CorrectionSelectionController.class)
                .startCorrection(resourceId, null)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getCorrectionPage_whenUserCantStartCorrection() throws Exception {
    when(correctionService.canStartCorrection(resource, user)).thenReturn(false);
    mockMvc.perform(
            get(ReverseRouter.route(on(CorrectionSelectionController.class)
                .startCorrection(resourceId, null)))
                .with(user(user)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void postCorrectionPage_withInvalidForm_assertIsOk() throws Exception {
    when(correctionFormValidator.isValid(any(CorrectionForm.class), any(Resource.class), any(BindingResult.class)))
        .thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(CorrectionSelectionController.class)
                .startCorrection(resourceId, form, bindingResult, null)))
                .with(user(user))
                .with(csrf()))
        .andExpect(status().isOk());
    verify(correctionService, never()).startCorrectionForTypeAndRedirect(any(),any());
  }

  @Test
  void getCorrectPage_assertModelAndView() throws Exception {
    List<SubmissionQuerySearchRecord> approvedSubmissions = List.of(
        SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(
            SubmissionDetailTestUtils.createNewProspectSubmissionDetail()
        )
    );
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(resourceId)).thenReturn(approvedSubmissions);
    Map<String, String> submissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(approvedSubmissions);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resourceId, null)))
                .with(user(user)))
        .andExpect(view().name("remi/systemofrecord/corrections/corrections"))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(CorrectionForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("correctionTypes", CorrectionType.getCorrectionTypes()),
            entry("submissionsOnResource", submissionsOnResource),
            entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId)))
        );
  }
}
