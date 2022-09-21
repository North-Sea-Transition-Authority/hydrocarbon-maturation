package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = SubmissionConfirmationController.class)
class SubmissionConfirmationControllerTest extends AbstractControllerTest {

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  GeneralDetailsPersistenceService generalDetailsPersistenceService;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;
  private static String prospectName;
  private static GeneralDetail generalDetail;

  @BeforeAll
  static void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();

    generalDetail = new GeneralDetail();
    prospectName = "Test prospect name";
    generalDetail.setProspectName(prospectName);
  }

  @Test
  void showSubmissionConfirmation_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail)).thenReturn(generalDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/submissiondetails/submissionConfirmation"));
  }

  @Test
  void getSubmissionConfirmationModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetail)).thenReturn(generalDetail);

    ModelMap modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionConfirmationController.class).showSubmissionConfirmation(submissionDetailId))))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap).contains(
            entry("assetName", prospectName),
            entry("typeOfUpdate", submissionDetail.getSubmissionType().getDisplayName()),
            entry("workAreaUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea()))
        );
  }
}