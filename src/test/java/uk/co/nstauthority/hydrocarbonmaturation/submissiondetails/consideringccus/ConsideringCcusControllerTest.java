package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.ControllerTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ContextConfiguration(classes = ConsideringCcusController.class)
class ConsideringCcusControllerTest extends AbstractSubmissionControllerTest {

  @MockitoBean
  private ConsideringCcusFormService consideringCcusFormService;

  @MockitoBean ConsideringCcusFormValidator consideringCcusFormValidator;

  private ConsideringCcusForm form;
  private UUID submissionDetailId;

  @BeforeEach
  void setup() {
    form = new ConsideringCcusForm();
    submissionDetailId = submissionDetail.getId();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
  }

  @SecurityTest
  void getConsideringCcus_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(consideringCcusFormService.submissionDetailToForm(submissionDetail)).thenReturn(new ConsideringCcusForm());
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(ConsideringCcusController.class).getConsideringCcus(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/consideringCcus"));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      names = "DRAFT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void getConsideringCcus_andStatusIsNotDraft_assertForbidden(SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            get(ReverseRouter.route(on(ConsideringCcusController.class).getConsideringCcus(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      names = "DRAFT",
      mode = EnumSource.Mode.EXCLUDE
  )
  void saveConsideringCcus_andStatusIsNotDraft_assertForbidden(SubmissionDetailStatus submissionDetailStatus) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setStatus(submissionDetailStatus);
    mockMvc.perform(
            get(ReverseRouter.route(on(ConsideringCcusController.class).getConsideringCcus(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void getConsideringCcus_andSubmissionNotFieldType_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            get(ReverseRouter.route(on(ConsideringCcusController.class).getConsideringCcus(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void saveConsideringCcus_andSubmissionNotFieldType_assertForbidden(SubmissionType submissionType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    submissionDetail.setSubmissionType(submissionType);
    mockMvc.perform(
            post(ReverseRouter.route(on(ConsideringCcusController.class)
                .saveConsideringCcus(submissionDetailId, null, null, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void getConsideringCcus_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(ConsideringCcusController.class)
                .getConsideringCcus(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void saveConsideringCcus_whenIsNotResponsibleOrgOrTeam_assertForbidden() throws Exception {
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_field_failSecurity(USER, submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(on(ConsideringCcusController.class).saveConsideringCcus(
                submissionDetailId, null, null, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void saveConsideringCcus_validForm (ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    when(consideringCcusFormValidator.isValid(
        any(ConsideringCcusForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        eq(validationType))
    ).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ConsideringCcusController.class)
                .saveConsideringCcus(submissionDetailId, form, ReverseRouter.emptyBindingResult(), validationType)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId, null))));

    verify(consideringCcusFormService, times(1))
        .saveConsideringCcus(any(ConsideringCcusForm.class), eq(submissionDetail));
    verifyNoMoreInteractions(consideringCcusFormService);
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void saveConsideringCcus_invalidForm (ValidationType validationType) throws Exception {
    setUpWhenUserIsResponsibleOrganisation_field_passSecurity(USER, submissionDetail);
    when(consideringCcusFormValidator.isValid(
        any(ConsideringCcusForm.class),
        any(BindingResult.class),
        eq(submissionDetail),
        any(ValidationType.class))
    ).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(ConsideringCcusController.class)
                .saveConsideringCcus(submissionDetailId, null, null, null)))
                .params(ControllerTestUtils.validationPostParams(validationType))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("remi/submissiondetails/consideringCcus"));

    verifyNoInteractions(consideringCcusFormService);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}