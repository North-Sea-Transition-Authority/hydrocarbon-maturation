package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = FdpaVolumesController.class)
class FdpaPromotedVolumesControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private FdpaPromotedVolumesService volumesService;

  @MockBean
  private FdpaPromotedVolumesFormValidator formValidator;

  @MockBean
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Autowired
  private ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  @Test
  void getFdpaPromotedVolumes_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var form = new FdpaPromotedVolumesForm();

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
        .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    when(volumesService.submissionDetailToForm(submissionDetail))
        .thenReturn(form);

    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(FdpaVolumesController.class)
                        .getFdpaPromotedVolumes(SUBMISSION_DETAIL_ID)))
                        .with(user(USER)))
                .andExpect(view().name("remi/submissiondetails/fdpaPromotedVolumes"))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap).contains(
        entry("pageTitle", FdpaVolumesController.PAGE_TITLE),
        entry("form", form),
        entry("hydrocarbonCase", HydrocarbonsDiscovered.GAS_ONLY),
        entry("oilReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA)),
        entry("associatedCondensateReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA)),
        entry("naturalGasLiquidsReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA)),
        entry("salesGasReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA)),
        entry("oilEquivalentReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA)),
        entry("oilReservesError", ProbabilityDistributionTypeError.emptyError()),
        entry("associatedCondensateReservesError", ProbabilityDistributionTypeError.emptyError()),
        entry("naturalGasLiquidsReservesError", ProbabilityDistributionTypeError.emptyError()),
        entry("salesGasReservesError", ProbabilityDistributionTypeError.emptyError()),
        entry("oilEquivalentReservesError", ProbabilityDistributionTypeError.emptyError())
    );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDPA"}, mode = EnumSource.Mode.EXCLUDE)
  void getFdpaPromotedVolumes_andTypeIsNotFdpa_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaVolumesController.class)
                .getFdpaPromotedVolumes(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFdpaPromotedVolumes_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaVolumesController.class)
                .getFdpaPromotedVolumes(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
  void getFdpaPromotedVolumes_whenIsNotDraft_assertForbidden(
      SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaVolumesController.class)
                .getFdpaPromotedVolumes(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getFdpaPromotedVolumes_whenNoUser_assertUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(FdpaVolumesController.class)
                .getFdpaPromotedVolumes(SUBMISSION_DETAIL_ID))))
        .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFdpaPromotedVolumes_whenValidForm_assertSaveAndRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var form = new FdpaPromotedVolumesForm();

    when(formValidator.isValid(eq(form), any(Errors.class), eq(submissionDetail), eq(validationType)))
        .thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaVolumesController.class)
                .saveFdpaPromotedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(SUBMISSION_DETAIL_ID, null))));

    verify(volumesService, times(1))
        .saveFdpaPromotedVolumes(form, submissionDetail);
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void saveFdpaPromotedVolumes_whenInvalidForm_assertNotSaved_andNotRedirected(
      ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
        .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var form = new FdpaPromotedVolumesForm();

    when(formValidator.isValid(eq(form), any(Errors.class), eq(submissionDetail), eq(validationType)))
        .thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaVolumesController.class)
                .saveFdpaPromotedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/fdpaPromotedVolumes"));

    verify(volumesService, never())
        .saveFdpaPromotedVolumes(any(), any());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_FDPA"}, mode = EnumSource.Mode.EXCLUDE)
  void saveFdpaPromotedVolumes_andTypeIsNotFdpa_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaVolumesController.class)
                .saveFdpaPromotedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveFdpaPromotedVolumes_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaVolumesController.class)
                .saveFdpaPromotedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT"}, mode = EnumSource.Mode.EXCLUDE)
  void saveFdpaPromotedVolumes_whenIsNotDraft_assertForbidden(
      SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaVolumesController.class)
                .saveFdpaPromotedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveFdpaPromotedVolumes_whenNoUser_assertUnauthorized() throws Exception {
    mockMvc.perform(
            post(ReverseRouter.route(on(FdpaVolumesController.class)
                .saveFdpaPromotedVolumes(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(csrf()))
        .andExpect(status().isUnauthorized());
  }
}