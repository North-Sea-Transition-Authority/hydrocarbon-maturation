package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = SubmissionConfirmationController.class)
class SubmissionConfirmationControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  GeneralDetailsPersistenceService generalDetailsPersistenceService;

  private UUID submissionDetailId;
  private String prospectName;
  private GeneralDetail generalDetail;

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();

    generalDetail = new GeneralDetail();
    prospectName = "Test prospect name";
    generalDetail.setResourceName(prospectName);
  }

  @SecurityTest
  void showSubmissionConfirmation_andSubmissionAutoApproved_assertStatusOk() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail)).thenReturn(generalDetail);

    mockMvc.perform(
        get(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/submissionConfirmation"));
  }

  @SecurityTest
  void showSubmissionConfirmation_andSubmissionSubmitted_assertStatusOk() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail)).thenReturn(generalDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/submissionConfirmation"));
  }

  @SecurityTest
  void showSubmissionConfirmation_andStatusIsNotDraft_assertForbidden() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getSubmissionConfirmationModelAndView_assertModelProperties() throws Exception {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail)).thenReturn(generalDetail);

    ModelMap modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId)))
            .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap).contains(
            entry("assetName", prospectName),
            entry("typeOfUpdate", submissionDetail.getSubmissionType().getDisplayName()),
            entry("workAreaUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()))
        );
  }
}