package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes;

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

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = FdpaIncreasedVolumesController.class)
class FdpaIncreasedVolumesControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  private FdpaIncreasedVolumesService increasedVolumesService;

  @MockitoBean
  private FdpaIncreasedVolumesFormValidator formValidator;

  @MockitoBean
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  @Test
  void getFdpaIncreasedVolumes_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var form = new FdpaIncreasedVolumesForm();

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
        .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    when(increasedVolumesService.submissionDetailToForm(submissionDetail))
        .thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                        .getFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID)))
                        .with(user(USER)))
                .andExpect(view().name("remi/submissiondetails/fdpaIncreasedVolumes"))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", FdpaIncreasedVolumesController.PAGE_TITLE),
        entry("form", form),
        entry("hydrocarbonCase", HydrocarbonsDiscovered.GAS_ONLY),
        entry("oilInitiallyInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA)),
        entry("gasInitiallyInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA)),
        entry("oilRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA)),
        entry("oilReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA)),
        entry("associatedCondensateRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA)),
        entry("associatedCondensateReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA)),
        entry("naturalGasLiquidsRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA)),
        entry("naturalGasLiquidsReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA)),
        entry("salesGasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA)),
        entry("salesGasReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA)),
        entry("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA)),
        entry("oilEquivalentReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA)),
        entry("oilInitiallyInPlaceError", ProbabilityDistributionTypeError.emptyError()),
        entry("gasInitiallyInPlaceError", ProbabilityDistributionTypeError.emptyError()),
        entry("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
        entry("oilReservesError", ProbabilityDistributionTypeError.emptyError()),
        entry("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
        entry("associatedCondensateReservesError", ProbabilityDistributionTypeError.emptyError()),
        entry("naturalGasLiquidsRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
        entry("naturalGasLiquidsReservesError", ProbabilityDistributionTypeError.emptyError()),
        entry("salesGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
        entry("salesGasReservesError", ProbabilityDistributionTypeError.emptyError()),
        entry("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError()),
        entry("oilEquivalentReservesError", ProbabilityDistributionTypeError.emptyError())
    );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDPA"}, mode = EnumSource.Mode.EXCLUDE)
  void getFdpaIncreasedVolumes_andTypeIsNotFdpa_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .getFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFdpaIncreasedVolumes_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .getFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
  void getFdpaIncreasedVolumes_whenIsNotDraft_assertForbidden(
      SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .getFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFdpaIncreasedVolumes_whenNoUser_assertUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .getFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID))))
        .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFdpaIncreasedVolumes_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var form = new FdpaIncreasedVolumesForm();

    when(formValidator.isValid(eq(form), any(Errors.class), eq(submissionDetail), eq(validationType)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .saveFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(SUBMISSION_DETAIL_ID, null))));

    verify(increasedVolumesService, times(1))
        .saveFdpaIncreasedVolumes(form, submissionDetail);
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFdpaIncreasedVolumes_whenInvalidForm_assertNotSaved_andNotRedirected(
      ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
        .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var form = new FdpaIncreasedVolumesForm();

    when(formValidator.isValid(eq(form), any(Errors.class), eq(submissionDetail), eq(validationType)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .saveFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fdpaIncreasedVolumes"));

    verify(increasedVolumesService, never())
        .saveFdpaIncreasedVolumes(any(), any());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDPA"}, mode = EnumSource.Mode.EXCLUDE)
  void saveFdpaIncreasedVolumes_andTypeIsNotFdpa_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .saveFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveFdpaIncreasedVolumes_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .saveFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
  void saveFdpaIncreasedVolumes_whenIsNotDraft_assertForbidden(
      SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .saveFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveFdpaIncreasedVolumes_whenNoUser_assertUnauthorized() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaIncreasedVolumesController.class)
                .saveFdpaIncreasedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }
}