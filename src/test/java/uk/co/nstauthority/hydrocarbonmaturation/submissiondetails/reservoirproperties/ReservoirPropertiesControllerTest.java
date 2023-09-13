package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ReservoirPropertiesController.class)
class ReservoirPropertiesControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private ReservoirPropertiesFormService reservoirPropertiesFormService;

  @MockBean
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @MockBean
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @MockBean
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  @MockBean
  private FormationSearchService formationSearchService;

  @MockBean
  private ReservoirSubUnitSearchService reservoirSubUnitSearchService;

  @MockBean
  private AgeSeriesSearchService ageSeriesSearchService;

  @MockBean
  private ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  private ReservoirPropertiesForm form;

  private UUID submissionDetailId;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;
  private UUID reservoirSubmissionDetailId;

  @BeforeEach
  void setup() {
    form = new ReservoirPropertiesForm();
    submissionDetailId = submissionDetail.getId();

    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    reservoirSubmissionDetailId = reservoirSubmissionDetail.getId();
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId))
        .thenReturn(reservoirSubmissionDetail);
  }

  @SecurityTest
  void getReservoirProperties_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)).thenReturn(new ReservoirPropertiesForm());
    setUpWhenUserIsResponsibleOrganisation_passSecurity(USER);

    mockMvc.perform(
        get(ReverseRouter.route(on(ReservoirPropertiesController.class)
            .getReservoirProperties(submissionDetailId, reservoirSubmissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/reservoirProperties"));
  }

  @SecurityTest
  void getReservoirProperties_andStatusIsNotDraft_assertForbidden() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .getReservoirProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getReservoirProperties_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .getReservoirProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveReservoirPropertiesPage_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirPropertiesController.class).saveReservoirProperties(
                submissionDetailId, reservoirSubmissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = SubmissionType.class, names = {"CREATE_DISCOVERY", "UPDATE_DISCOVERY"})
  void getReservoirProperties_whenDiscovery_assertStatusOk() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)).thenReturn(new ReservoirPropertiesForm());
    var discoveryWellDetails = new DiscoveryWellDetail();
    discoveryWellDetails.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetails));

    mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .getReservoirProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/reservoirProperties"));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveReservoirProperties_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    when(reservoirPropertiesFormValidator.isValid(any(), any(), eq(reservoirSubmissionDetail), eq(validationType))).thenReturn(true);

    mockMvc.perform(
        post(ReverseRouter.route(on(ReservoirPropertiesController.class)
            .saveReservoirProperties(submissionDetailId, reservoirSubmissionDetailId, form, null, null)))
            .params(ControllerTestUtils.validationPostParams(validationType))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));

    verify(reservoirPropertiesPersistenceService, times(1))
        .saveReservoirProperty(any(ReservoirPropertiesForm.class), eq(reservoirSubmissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveReservoirProperties_whenValidForm_assertSaveAndRedirection_discovery(ValidationType validationType) throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
    var discoveryReservoirSubmissionDetailId = discoveryReservoirSubmissionDetail.getId();

    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailOrThrow(discoveryReservoirSubmissionDetailId))
        .thenReturn(discoveryReservoirSubmissionDetail);
    when(reservoirPropertiesFormValidator.isValid(any(), any(), eq(discoveryReservoirSubmissionDetail), eq(validationType))).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .saveReservoirProperties(submissionDetailId, discoveryReservoirSubmissionDetailId, form, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetailId, null))));

    verify(reservoirPropertiesPersistenceService, times(1))
        .saveReservoirProperty(any(ReservoirPropertiesForm.class), eq(discoveryReservoirSubmissionDetail));
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveReservoirProperties_whenInvalidForm_assertStatusOk(ValidationType validationType) throws Exception {
    when(reservoirPropertiesFormValidator.isValid(any(), any(), eq(reservoirSubmissionDetail), eq(validationType))).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .saveReservoirProperties(submissionDetailId, reservoirSubmissionDetailId, form, null, ValidationType.PARTIAL)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk());

    verify(reservoirPropertiesPersistenceService, never())
        .saveReservoirProperty(any(), any());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT", "PROSPECT_UPDATE", "RELINQUISHMENT_PROSPECT"})
  void saveReservoirProperties_whenInvalidForm_andProspectSubmission_assertModelProperties(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesFormValidator.isValid(any(), any(), eq(reservoirSubmissionDetail), eq(ValidationType.FULL))).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSubUnit = Map.of("SUBUNIT", "sub unit");
    when(reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit())).thenReturn(preSelectedSubUnit);

    var modelMap = mockMvc.perform(
        post(ReverseRouter.route(on(ReservoirPropertiesController.class)
            .saveReservoirProperties(submissionDetailId, reservoirSubmissionDetailId, form, null, ValidationType.PARTIAL)))
            .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
            .with(user(USER))
            .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView()
        .getModelMap();

    verify(reservoirPropertiesPersistenceService, never())
        .saveReservoirProperty(any(), any());

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedFormation", preSelectedFormation)
        .containsEntry("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .containsEntry("preSelectedSubUnit", preSelectedSubUnit)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .containsEntry("depthToHydrocarbonContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToHydrocarbonContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT))
        .containsEntry("averageNetPayThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT))
        .doesNotContainKeys("depthToGasOilContactTemplate",
            "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = SubmissionType.class, names = {"UPDATE_DISCOVERY", "CREATE_DISCOVERY", "CREATE_DISCOVERY_FROM_PROSPECT", "RELINQUISHMENT_DISCOVERY"})
  void saveReservoirProperties_whenInvalidForm_andDiscoverySubmission_assertModelProperties(SubmissionType submissionType) throws Exception {
    var discoverySubmissionDetailId = UUID.randomUUID();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(submissionType,
        discoverySubmissionDetailId);
    discoverySubmissionDetail.setApprovedTimestamp(Instant.now());
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var discoveryReservoirSubmissionDetailId = discoveryReservoirSubmissionDetail.getId();
    when(submissionDetailService.getSubmissionDetail(discoverySubmissionDetailId)).thenReturn(Optional.of(discoverySubmissionDetail));
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailOrThrow(discoveryReservoirSubmissionDetailId))
        .thenReturn(discoveryReservoirSubmissionDetail);

    when(reservoirPropertiesFormValidator.isValid(any(), any(), eq(reservoirSubmissionDetail), eq(ValidationType.FULL))).thenReturn(false);
    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSubUnit = Map.of("SUBUNIT", "sub unit");
    when(reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit())).thenReturn(preSelectedSubUnit);

    var modelMap = mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .saveReservoirProperties(discoverySubmissionDetailId, discoveryReservoirSubmissionDetailId, form, null, ValidationType.PARTIAL)))
                .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView()
        .getModelMap();

    verify(reservoirPropertiesPersistenceService, never())
        .saveReservoirProperty(any(), any());

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(discoverySubmissionDetailId)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(discoverySubmissionDetailId, null)), ReservoirTaskListController.PAGE_TITLE)
    );

    assertThat(modelMap)
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedFormation", preSelectedFormation)
        .containsEntry("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .containsEntry("preSelectedSubUnit", preSelectedSubUnit)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .containsEntry("averageNetPayThicknessOptionalError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessOptionalTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY))
        .doesNotContainKeys("depthToGasOilContactTemplate",
            "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");

  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = SubmissionType.class, names = {"POST_DRILL_DRY_HOLE"})
  void saveReservoirProperties_whenInvalidForm_andDryWellSubmission_assertModelProperties(SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    when(reservoirPropertiesFormValidator.isValid(any(), any(), eq(reservoirSubmissionDetail), eq(ValidationType.FULL))).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);
    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSubUnit = Map.of("SUBUNIT", "sub unit");
    when(reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit())).thenReturn(preSelectedSubUnit);

    var modelMap = mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .saveReservoirProperties(submissionDetailId, reservoirSubmissionDetailId, form, null, ValidationType.PARTIAL)))
                .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView()
        .getModelMap();

    verify(reservoirPropertiesPersistenceService, never())
        .saveReservoirProperty(any(), any());

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedFormation", preSelectedFormation)
        .containsEntry("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .containsEntry("preSelectedSubUnit", preSelectedSubUnit)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .containsEntry("averageNetReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT))
        .doesNotContainKeys("depthToGasOilContactTemplate",
            "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");
  }

  @Test
  void getReservoirPropertiesModelAndView_whenProspectSubmission_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)).thenReturn(form);

    var hydrocarbonCaseDetail = mock(HydrocarbonCaseDetail.class);
    when(hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail))
        .thenReturn(Optional.of(hydrocarbonCaseDetail));
    when(hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail))
        .thenReturn(true);
    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSubUnit = Map.of("SUBUNIT", "sub unit");
    when(reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit())).thenReturn(preSelectedSubUnit);
    Map<String, String> preSelectedAgeStage = Map.of("AGE_STAGE", "age stage");
    when(ageSeriesSearchService.getPreSelectedAgeSeries(form.getAgeStageOrSeries())).thenReturn(preSelectedAgeStage);


    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .getReservoirProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedFormation", preSelectedFormation)
        .containsEntry("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .containsEntry("preSelectedSubUnit", preSelectedSubUnit)
        .containsEntry("ageSeriesRestEndpoint",
            SearchSelectorService.route(on(AgeSeriesRestController.class).getAgeSeriesList(null))
        )
        .containsEntry("preSelectedAgeStage", preSelectedAgeStage)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .containsEntry("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToHydrocarbonContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT))
        .containsEntry("depthToHydrocarbonContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT))
        .containsEntry("mixedCase", true)
        .doesNotContainKeys("depthToGasOilContactTemplate",
            "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");
  }

  @Test
  void getReservoirPropertiesModelAndView_whenDiscoverySubmission_mixedCase_assertModelProperties() throws Exception {
    var discoverySubmissionDetailId = UUID.randomUUID();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY,
        discoverySubmissionDetailId);
    discoverySubmissionDetail.setApprovedTimestamp(Instant.now());
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var discoveryReservoirSubmissionDetailId = discoveryReservoirSubmissionDetail.getId();
    when(submissionDetailService.getSubmissionDetail(discoverySubmissionDetailId)).thenReturn(Optional.of(discoverySubmissionDetail));

    var form = new ReservoirPropertiesForm();
    when(submissionDetailService.getSubmissionDetailForIdOrError(discoverySubmissionDetailId)).thenReturn(discoverySubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailOrThrow(discoveryReservoirSubmissionDetailId)).thenReturn(discoveryReservoirSubmissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(discoveryReservoirSubmissionDetail)).thenReturn(form);
    var discoveryWellDetails = new DiscoveryWellDetail();
    when(discoveryWellDetailsPersistenceService.isDiscoveryMixed(any())).thenReturn(true);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(discoverySubmissionDetail))
        .thenReturn(Optional.of(discoveryWellDetails));
    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSubUnit = Map.of("SUBUNIT", "sub unit");
    when(reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit())).thenReturn(preSelectedSubUnit);
    Map<String, String> preSelectedAgeStage = Map.of("AGE_STAGE", "age stage");
    when(ageSeriesSearchService.getPreSelectedAgeSeries(form.getAgeStageOrSeries())).thenReturn(preSelectedAgeStage);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                        .getReservoirProperties(discoverySubmissionDetailId, discoveryReservoirSubmissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(discoverySubmissionDetailId)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(discoverySubmissionDetailId, null)), ReservoirTaskListController.PAGE_TITLE)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedFormation", preSelectedFormation)
        .containsEntry("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .containsEntry("preSelectedSubUnit", preSelectedSubUnit)
        .containsEntry("ageSeriesRestEndpoint",
            SearchSelectorService.route(on(AgeSeriesRestController.class).getAgeSeriesList(null))
        )
        .containsEntry("preSelectedAgeStage", preSelectedAgeStage)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .containsEntry("averageNetPayThicknessOptionalError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToOilWaterContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToGasWaterContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToGasOilContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessOptionalTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY))
        .containsEntry("depthToOilWaterContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT))
        .containsEntry("depthToGasWaterContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT))
        .containsEntry("depthToGasOilContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT))
        .containsEntry("mixedCase", true);
  }

  @Test
  void getReservoirPropertiesModelAndView_whenDiscoverySubmission_oilCase_assertModelProperties() throws Exception {
    var discoverySubmissionDetailId = UUID.randomUUID();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY,
        discoverySubmissionDetailId);
    discoverySubmissionDetail.setApprovedTimestamp(Instant.now());
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var discoveryReservoirSubmissionDetailId = discoveryReservoirSubmissionDetail.getId();
    when(submissionDetailService.getSubmissionDetail(discoverySubmissionDetailId)).thenReturn(Optional.of(discoverySubmissionDetail));

    var form = new ReservoirPropertiesForm();
    when(submissionDetailService.getSubmissionDetailForIdOrError(discoverySubmissionDetailId)).thenReturn(discoverySubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailOrThrow(discoveryReservoirSubmissionDetailId)).thenReturn(discoveryReservoirSubmissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(discoveryReservoirSubmissionDetail)).thenReturn(form);
    var discoveryWellDetails = new DiscoveryWellDetail();
    when(discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(any())).thenReturn(true);
    when(discoveryWellDetailsPersistenceService.isDiscoveryMixed(any())).thenReturn(false);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(discoverySubmissionDetail))
        .thenReturn(Optional.of(discoveryWellDetails));
    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSubUnit = Map.of("SUBUNIT", "sub unit");
    when(reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit())).thenReturn(preSelectedSubUnit);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                        .getReservoirProperties(discoverySubmissionDetailId, discoveryReservoirSubmissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(discoverySubmissionDetailId)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(discoverySubmissionDetailId, null)), ReservoirTaskListController.PAGE_TITLE)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedFormation", preSelectedFormation)
        .containsEntry("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .containsEntry("preSelectedSubUnit", preSelectedSubUnit)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .containsEntry("averageNetPayThicknessOptionalError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToOilWaterContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessOptionalTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY))
        .containsEntry("depthToOilWaterContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT))
        .containsEntry("mixedCase", false)
        .doesNotContainKeys("depthToGasWaterContactTemplate", "depthToGasWaterContactError",
            "depthToGasOilContactTemplate", "depthToGasOilContactError");
  }

  @Test
  void getReservoirPropertiesModelAndView_whenDiscoverySubmission_gasCase_assertModelProperties() throws Exception {
    var discoverySubmissionDetailId = UUID.randomUUID();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY,
        discoverySubmissionDetailId);
    var discoveryReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(discoverySubmissionDetail);
    var discoveryReservoirSubmissionDetailId = discoveryReservoirSubmissionDetail.getId();
    discoverySubmissionDetail.setApprovedTimestamp(Instant.now());
    when(submissionDetailService.getSubmissionDetail(discoverySubmissionDetailId)).thenReturn(Optional.of(discoverySubmissionDetail));

    var form = new ReservoirPropertiesForm();
    when(submissionDetailService.getSubmissionDetailForIdOrError(discoverySubmissionDetailId)).thenReturn(discoverySubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailOrThrow(discoveryReservoirSubmissionDetailId)).thenReturn(discoveryReservoirSubmissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(discoveryReservoirSubmissionDetail)).thenReturn(form);
    var discoveryWellDetails = new DiscoveryWellDetail();
    when(discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(any())).thenReturn(true);
    when(discoveryWellDetailsPersistenceService.isDiscoveryMixed(any())).thenReturn(false);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(discoverySubmissionDetail))
        .thenReturn(Optional.of(discoveryWellDetails));
    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSubUnit = Map.of("SUBUNIT", "sub unit");
    when(reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit())).thenReturn(preSelectedSubUnit);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                        .getReservoirProperties(discoverySubmissionDetailId, discoveryReservoirSubmissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(discoverySubmissionDetailId)), TaskListController.PAGE_NAME),
        entry(ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(discoverySubmissionDetailId, null)), ReservoirTaskListController.PAGE_TITLE)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedFormation", preSelectedFormation)
        .containsEntry("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .containsEntry("preSelectedSubUnit", preSelectedSubUnit)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .containsEntry("averageNetPayThicknessOptionalError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("depthToGasWaterContactError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetPayThicknessOptionalTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY))
        .containsEntry("depthToGasWaterContactTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT))
        .containsEntry("mixedCase", false)
        .doesNotContainKeys("depthToOilWaterContactTemplate", "depthToOilWaterContactError",
            "depthToGasOilContactTemplate", "depthToGasOilContactError");
  }

  @Test
  void getReservoirPropertiesModelAndView_whenDryHoleSubmission_assertModelProperties() throws Exception {
    var form = new ReservoirPropertiesForm();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)).thenReturn(form);

    Map<String, String> preSelectedFormation = Map.of("FORMATION", "formation");
    when(formationSearchService.getPreSelectedFormation(form.getFormationName())).thenReturn(preSelectedFormation);
    Map<String, String> preSelectedSubUnit = Map.of("SUBUNIT", "sub unit");
    when(reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit())).thenReturn(preSelectedSubUnit);
    Map<String, String> preSelectedAgeStage = Map.of("AGE_STAGE", "age stage");
    when(ageSeriesSearchService.getPreSelectedAgeSeries(form.getAgeStageOrSeries())).thenReturn(preSelectedAgeStage);

    var modelMap = Objects.requireNonNull(
        mockMvc.perform(
            get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .getReservoirProperties(submissionDetailId, reservoirSubmissionDetailId)))
                .with(user(USER)))
            .andReturn()
            .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(ReservoirPropertiesForm.class);

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)), WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId)), TaskListController.PAGE_NAME)
    );

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .containsEntry("preSelectedFormation", preSelectedFormation)
        .containsEntry("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .containsEntry("preSelectedSubUnit", preSelectedSubUnit)
        .containsEntry("ageSeriesRestEndpoint",
            SearchSelectorService.route(on(AgeSeriesRestController.class).getAgeSeriesList(null))
        )
        .containsEntry("preSelectedAgeStage", preSelectedAgeStage)
        .containsEntry("groupNames", GroupName.getGroupNames())
        .containsEntry("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .containsEntry("averageNetReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .containsEntry("averageNetReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL))
        .containsEntry("grossReservoirThicknessTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT))
        .containsEntry("grossRockVolumeTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT))
        .containsEntry("percentOfGrvOilBearingTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT))
        .containsEntry("netToGrossTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT))
        .containsEntry("netPorosityTemplate", probabilityDistributionTemplateService
            .getProbabilityDistributionTemplateForInstance(ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT))
        .doesNotContainKeys("mixedCase", "depthToGasOilContactTemplate",
            "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");
  }

  @Test
  void getReservoirPropertiesModelAndView_whenDiscoverySubmission_whenNoDiscoveryWell_assertHydrocarbonCasesFalse() throws Exception {
    var form = new ReservoirPropertiesForm();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)).thenReturn(form);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(ReservoirPropertiesController.class)
                        .getReservoirProperties(submissionDetailId, reservoirSubmissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap)
        .containsEntry("mixedCase", false)
        .containsEntry("gasCase", false)
        .containsEntry("oilCase", false)
        .doesNotContainKeys("depthToGasOilContactTemplate",
        "depthToGasWaterContactTemplate", "depthToOilWaterContactTemplate");
  }

  @Test
  void saveReservoirProperties_whenDiscoverySubmission_whenErrors_assertErrorsAdded() throws Exception {
    var form = new ReservoirPropertiesForm();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail)).thenReturn(form);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    when(reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL)).thenReturn(false);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId())).thenReturn(submissionDetail);

    var modelMap = mockMvc.perform(
            post(ReverseRouter.route(on(ReservoirPropertiesController.class)
                .saveReservoirProperties(submissionDetailId, reservoirSubmissionDetailId, null, null, ValidationType.FULL)))
                .params(ControllerTestUtils.validationPostParams(ValidationType.FULL))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView()
        .getModelMap();

    assertThat(modelMap)
        .containsEntry("depthToOilWaterContactError",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(bindingResult,
                    "depthToOilWaterContact", form.getDepthToOilWaterContact()))
        .containsEntry("depthToGasWaterContactError",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(bindingResult,
                    "depthToGasWaterContact", form.getDepthToGasWaterContact()))
        .containsEntry("depthToGasOilContactError",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTypeError(bindingResult,
                    "depthToGasOilContact", form.getDepthToGasOilContact()))
        .containsEntry("mixedCase", false)
        .containsEntry("gasCase", false)
        .containsEntry("oilCase", false);
  }
}