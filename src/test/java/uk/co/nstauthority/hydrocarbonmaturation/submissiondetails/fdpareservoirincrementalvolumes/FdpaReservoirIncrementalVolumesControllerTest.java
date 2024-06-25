package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = FdpaReservoirIncrementalVolumesController.class)
class FdpaReservoirIncrementalVolumesControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private FdpaReservoirIncrementalVolumesService incrementalVolumesService;

  @MockBean
  private FdpaReservoirIncrementalVolumesFormValidator formValidator;

  @MockBean
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  @Test
  void getIncrementalVolumes_assertModelProperties() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var form = new FdpaReservoirIncrementalVolumesForm();

    // WHEN reservoir exists and was carried forward
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(
        reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(
        reservoirSubmissionDetail.getReservoir()))
        .thenReturn(true);

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
        .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
    when(incrementalVolumesService.submissionDetailToForm(reservoirSubmissionDetail))
        .thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                        .getIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId())))
                        .with(user(USER)))
                .andExpect(view().name("remi/submissiondetails/fdpaReservoirIncrementalVolumes"))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", FdpaReservoirIncrementalVolumesController.PAGE_TITLE),
        entry("form", form),
        entry("hydrocarbonCase", HydrocarbonsDiscovered.GAS_ONLY),
        entry("incrementalIncreaseOilTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA)),
        entry("incrementalIncreaseGasTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA)),
        entry("incrementalIncreaseOilError", ProbabilityDistributionTypeError.emptyError()),
        entry("incrementalIncreaseGasError", ProbabilityDistributionTypeError.emptyError())
    );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDPA"}, mode = EnumSource.Mode.EXCLUDE)
  void getFdpaReservoirIncrementalVolumes_andTypeIsNotFdpa_assertForbidden(
      SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .getIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFdpaReservoirIncrementalVolumes_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .getIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
  void getFdpaReservoirIncrementalVolumes_whenIsNotDraft_assertForbidden(
      SubmissionDetailStatus submissionDetailStatus) throws Exception {
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .getIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFdpaReservoirIncrementalVolumes_whenNoUser_assertUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .getIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId()))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getIncrementalVolumes_whenReservoirNotCarriedForward_assertForbidden() throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(
        reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(
        reservoirSubmissionDetail.getReservoir()))
        .thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .getIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId())))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFdpaReservoirIncrementalVolumes_whenValidForm_assertSaveAndRedirection(
      ValidationType validationType) throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(
        reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(
        reservoirSubmissionDetail.getReservoir()))
        .thenReturn(true);

    var form = new FdpaReservoirIncrementalVolumesForm();

    when(formValidator.isValid(eq(form), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .saveIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(
            on(ReservoirTaskListController.class).getReservoirTaskList(SUBMISSION_DETAIL_ID, null))));

    verify(incrementalVolumesService, times(1))
        .saveFdpaReservoirIncrementalVolume(form, reservoirSubmissionDetail);
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFdpaReservoirIncrementalVolumes_whenInvalidForm_assertNotSaved_andNotRedirected(
      ValidationType validationType) throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(
        reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(
        reservoirSubmissionDetail.getReservoir()))
        .thenReturn(true);

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(List.of(reservoirSubmissionDetail)))
        .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

    var form = new FdpaReservoirIncrementalVolumesForm();

    when(formValidator.isValid(eq(form), any(Errors.class), eq(reservoirSubmissionDetail), eq(validationType)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .saveIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fdpaReservoirIncrementalVolumes"));

    verify(incrementalVolumesService, never())
        .saveFdpaReservoirIncrementalVolume(any(), any());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDPA"}, mode = EnumSource.Mode.EXCLUDE)
  void saveFdpaReservoirIncrementalVolumes_andTypeIsNotFdpa_assertForbidden(
      SubmissionType submissionType) throws Exception {
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .saveIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId(), null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveFdpaReservoirIncrementalVolumes_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .saveIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId(), null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
  void saveFdpaReservoirIncrementalVolumes_whenIsNotDraft_assertForbidden(
      SubmissionDetailStatus submissionDetailStatus) throws Exception {
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .saveIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId(), null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveFdpaReservoirIncrementalVolumes_whenNoUser_assertUnauthorized() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .saveIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId(), null, null, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFdpaReservoirIncrementalVolumes_whenReservoirNotCarriedForward_assertForbidden(
      ValidationType validationType) throws Exception {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetailOrThrow(
        reservoirSubmissionDetail.getId()))
        .thenReturn(reservoirSubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(
        reservoirSubmissionDetail.getReservoir()))
        .thenReturn(false);

    var form = new FdpaReservoirIncrementalVolumesForm();

    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaReservoirIncrementalVolumesController.class)
                .saveIncrementalVolumes(SUBMISSION_DETAIL_ID, reservoirSubmissionDetail.getId(), null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }
}