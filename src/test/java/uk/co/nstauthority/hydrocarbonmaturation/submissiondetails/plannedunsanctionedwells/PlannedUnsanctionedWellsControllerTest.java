package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = PlannedUnsanctionedWellsController.class)
class PlannedUnsanctionedWellsControllerTest extends AbstractSubmissionControllerTest {
  @MockitoBean
  private PlannedUnsanctionedWellsService plannedUnsanctionedWellsService;
  @MockitoBean
  private PlannedUnsanctionedWellsFormValidator plannedUnsanctionedWellsFormValidator;

  private PlannedUnsanctionedWellsForm form;
  private BindingResult bindingResult;

  private UUID submissionDetailId;
  @BeforeEach
  void setup() {
    form = new PlannedUnsanctionedWellsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);
  }

  @SecurityTest
  void getPlannedUnsanctionedWells_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId))
        .thenReturn(submissionDetail);
    when(plannedUnsanctionedWellsService.submissionDetailToForm(submissionDetail))
        .thenReturn(new PlannedUnsanctionedWellsForm());
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class).getPlannedUnsanctionedWells(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/plannedUnsanctionedWells"));
  }

  @SecurityTest
  void getPlannedUnsanctionedWells_andStatusIsNotDraft_assertForbidden() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class).getPlannedUnsanctionedWells(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getPlannedUnsanctionedWells_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class)
                .getPlannedUnsanctionedWells(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void savePlannedUnsanctionedWells_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class).savePlannedUnsanctionedWells(
                submissionDetailId,  null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      names = "DRAFT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void savePlannedUnsanctionedWells_andStatusIsNotDraft_assertForbidden(SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class).getPlannedUnsanctionedWells(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonFdpOrFdpaSubmissionTypes")
  void getPlannedUnsanctionedWells_andSubmissionNotFdpOrFdpA_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class).getPlannedUnsanctionedWells(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonFdpOrFdpaSubmissionTypes")
  void savePlannedUnsanctionedWells_andSubmissionNotFdpOrFdpa_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            post(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class)
                .savePlannedUnsanctionedWells(submissionDetailId, null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getPlannedUnsanctionedWellsModelAndView_assertModelProperties() throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    when(plannedUnsanctionedWellsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
    var modelMap = Objects.requireNonNull(
            mockMvc.perform(
                    get(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class)
                        .getPlannedUnsanctionedWells(submissionDetailId)))
                        .with(user(USER)))
                .andReturn()
                .getModelAndView())
        .getModelMap();

    assertThat(modelMap.get("form").getClass()).isEqualTo(PlannedUnsanctionedWellsForm.class);

    assertThat(modelMap)
        .containsEntry("form", form)
        .containsEntry("pageTitle", PlannedUnsanctionedWellsController.PAGE_NAME)
        .containsEntry("submissionType", submissionDetail.getSubmissionType());
  }

  @ParameterizedTest(name = "controller validation - {0}")
  @EnumSource(value = ValidationType.class, names = {"FULL", "PARTIAL"})
  void savePlannedUnsanctionedWellsModelAndView_valid_assertRedirection(ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    when(plannedUnsanctionedWellsFormValidator.isValid(any(PlannedUnsanctionedWellsForm.class), any(Errors.class), eq(submissionDetail), eq(validationType)))
        .thenReturn(false);
    when(plannedUnsanctionedWellsService.submissionDetailToForm(submissionDetail)).thenReturn(form);
            mockMvc.perform(
                    post(ReverseRouter.route(on(PlannedUnsanctionedWellsController.class)
                        .savePlannedUnsanctionedWells(submissionDetailId, null, null, null)))
                        .params(ControllerTestUtils.validationPostParams(validationType))
                        .with(user(USER))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("remi/submissiondetails/plannedUnsanctionedWells"));
  verify(plannedUnsanctionedWellsService, never())
      .savePlannedUnsanctionedWells(any(PlannedUnsanctionedWellsForm.class), eq(submissionDetail));
  }

  private static Stream<Arguments> allNonFdpOrFdpaSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType ->
            !(SubmissionType.CREATE_FDP.equals(submissionType)
              || SubmissionType.CREATE_FDPA.equals(submissionType)))
        .map(Arguments::of);
  }
}