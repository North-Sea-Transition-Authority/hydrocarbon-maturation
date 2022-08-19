package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

@WithMockUser
@ContextConfiguration(classes = OpportunityDetailsController.class)
class OpportunityDetailsControllerTest extends AbstractControllerTest {

  @MockBean
  OpportunityDetailsFormService opportunityDetailsFormService;

  @MockBean
  OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  @MockBean
  SubmissionDetailService submissionDetailService;

  private OpportunityDetailsForm form;
  private BindingResult bindingResult;
  private UUID submissionDetailId;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    form = new OpportunityDetailsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");

    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getOpportunityDetailsPage_assertStatusOk() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail)).thenReturn(new OpportunityDetailsForm());

    mockMvc.perform(
        get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetailId, form))))
        .andExpect(status().isOk());
  }

  @Test
  void saveOpportunityDetailsPage_whenValidForm_assertRedirection() throws Exception {
    when(opportunityDetailsFormService.validate(any(), any())).thenReturn(bindingResult);
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetails(submissionDetailId, form, bindingResult)))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))));

    verify(opportunityDetailsPersistenceService, times(1)).saveOpportunityDetails(any(OpportunityDetailsForm.class), eq(submissionDetail));
  }

  @Test
  void saveOpportunityDetailsPage_whenInvalidForm_assertStatusOk() throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(opportunityDetailsFormService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetails(submissionDetailId, form, bindingResult)))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(opportunityDetailsPersistenceService, never()).saveOpportunityDetails(any(), eq(submissionDetail));
  }

  @Test
  void getOpportunityDetailsModelAndView_assertModelProperties() throws Exception {
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    when(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail)).thenReturn(new OpportunityDetailsForm());

    ModelMap modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails(submissionDetailId, form))))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(OpportunityDetailsForm.class, modelMap.get("form").getClass());
    assertThat(modelMap).contains(
        entry("opportunityTypes", OpportunityType.getOpportunityType()),
        entry("pageTitle", OpportunityDetailsController.PAGE_NAME),
        entry("actionUrl", ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetails(submissionDetailId, form, null)))
    )
    .containsKey("breadcrumbsMap");
  }

}