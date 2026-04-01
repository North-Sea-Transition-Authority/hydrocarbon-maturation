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
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionQuerySearchRecord;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ContextConfiguration(classes = CorrectionSelectionController.class)
class CorrectionSelectionControllerTest extends AbstractControllerTest {

  @MockitoBean
  private ResourceService resourceService;

  @MockitoBean
  private CorrectionService correctionService;

  @MockitoBean
  private CorrectionFormValidator correctionFormValidator;

  @MockitoBean
  private SubmissionHistorySelectorUtil submissionHistorySelectorUtil;

  @MockitoBean
  private SubmissionHistoryQueryService submissionHistoryQueryService;

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
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
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

    verify(correctionService, times(1)).startCorrectionForTypeAndRedirect(any(CorrectionForm.class), eq(user), any(Resource.class));
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
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
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
    verify(correctionService, never()).startCorrectionForTypeAndRedirect(any(),any(),any());
  }

  @Test
  void getCorrectPage_assertModelAndView_oneApprovedSubmission() throws Exception {
    List<SubmissionQuerySearchRecord> approvedSubmissions = List.of(
        SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(
            SubmissionDetailTestUtils.createNewProspectSubmissionDetail()
        )
    );
    when(submissionHistoryQueryService.getAcceptedSubmissionsOnResource(resourceId)).thenReturn(approvedSubmissions);
    var correctionTypes = CorrectionType.getCorrectionTypes();
    when(correctionService.getCorrectionTypes(resource)).thenReturn(correctionTypes);
    var submissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(approvedSubmissions);
    var removableSubmissionMap = submissionsOnResource.entrySet().stream().findFirst().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resourceId, null)))
                .with(user(user)))
        .andExpect(view().name("remi/systemofrecord/corrections/corrections"))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(CorrectionForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("correctionTypes", correctionTypes),
            entry("submissionsOnResource", submissionsOnResource),
            entry("removableSubmissions", removableSubmissionMap),
            entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
        );
  }

  @Test
  void getCorrectPage_assertModelAndView_multipleApprovedSubmissions() throws Exception {
    var initialSubmission = SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(
            SubmissionDetailTestUtils.createNewProspectSubmissionDetail()
    );

    var additionalSubmission1 = SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(
            SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail()
    );

    var additionalSubmission2 = SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(
            SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail()
    );

    List<SubmissionQuerySearchRecord> approvedSubmissions = List.of(
          initialSubmission,
          additionalSubmission1,
          additionalSubmission2
    );

    List<SubmissionQuerySearchRecord> filteredSubmissions = List.of(
            additionalSubmission1,
            additionalSubmission2
    );

    when(submissionHistoryQueryService.getAcceptedSubmissionsOnResource(resourceId)).thenReturn(approvedSubmissions);
    var correctionTypes = CorrectionType.getCorrectionTypes();
    when(correctionService.getCorrectionTypes(resource)).thenReturn(correctionTypes);
    var submissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(approvedSubmissions);

    var filteredSubmissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(filteredSubmissions);
    var removableSubmissionMap = filteredSubmissionsOnResource.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    var modelMap = Objects.requireNonNull(mockMvc.perform(
                    get(ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resourceId, null)))
                            .with(user(user)))
            .andExpect(view().name("remi/systemofrecord/corrections/corrections"))
            .andReturn()
            .getModelAndView()).getModelMap();

    assertEquals(CorrectionForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
            .contains(
                    entry("correctionTypes", correctionTypes),
                    entry("submissionsOnResource", submissionsOnResource),
                    entry("removableSubmissions", removableSubmissionMap),
                    entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null)))
            );
  }

  @Test
  void getCorrectPage_assertModelAndView_fieldSubmission() throws Exception {
    resource.setType(ResourceType.FIELD);

    List<SubmissionQuerySearchRecord> approvedSubmissions = List.of(
        SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(
            SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail()
        )
    );
    when(submissionHistoryQueryService.getAcceptedSubmissionsOnResource(resourceId)).thenReturn(approvedSubmissions);
    var correctionTypes = CorrectionType.getCorrectionTypes();
    when(correctionService.getCorrectionTypes(resource)).thenReturn(correctionTypes);
    var submissionsOnResource = SubmissionHistorySelectorUtil.getSubmissionSelectorItems(approvedSubmissions);
    var removableSubmissionMap = submissionsOnResource.entrySet().stream().findFirst().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resourceId, null)))
                .with(user(user)))
        .andExpect(view().name("remi/systemofrecord/corrections/corrections"))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(CorrectionForm.class, modelMap.get("form").getClass());
    assertThat(modelMap)
        .contains(
            entry("correctionTypes", correctionTypes),
            entry("submissionsOnResource", submissionsOnResource),
            entry("removableSubmissions", removableSubmissionMap),
            entry("cancelUrl", ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(resourceId, null))),
            entry("submissionTypes", List.of(SubmissionType.CREATE_CSR, SubmissionType.CREATE_FDP, SubmissionType.CREATE_FDPA, SubmissionType.CREATE_COP))
        );
  }
}
