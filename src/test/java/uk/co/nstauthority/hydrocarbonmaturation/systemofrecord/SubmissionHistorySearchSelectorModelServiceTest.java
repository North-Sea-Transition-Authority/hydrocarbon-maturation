package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;

@ExtendWith(MockitoExtension.class)
class SubmissionHistorySearchSelectorModelServiceTest {

  @Mock
  private SubmissionHistoryQueryService submissionHistoryQueryService;

  @InjectMocks
  private SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService;

  @Test
  void addViewSorSubmissionSearchSelectorItemsToModelAndView_whenOneItem() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setApprovedTimestamp(Instant.now());
    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(submissionDetail));
    when(submissionHistoryQueryService.getApprovedSubmissionsOnResource(submissionDetail.getSubmission().getResource().getId()))
        .thenReturn(queryResult);
    var expectedMap = Map.of(
        submissionDetail.getId().toString(),
        "Submission 1: Prospect create (Pending approval)");
    var modelAndView = new ModelAndView();
    assertThat(submissionHistorySearchSelectorModelService.addViewSorSubmissionSearchSelectorItemsToModelAndView(
        modelAndView, submissionDetail.getSubmission().getResource().getId()).getModelMap())
        .containsExactly(
            entry("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("submissionsOnResource", expectedMap));
  }

  @Test
  void addReviewSubmissionSearchSelectorItemsToModelAndView_whenOneItem() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setApprovedTimestamp(Instant.now());
    var queryResult = List.of(SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(submissionDetail));
    when(submissionHistoryQueryService.getApprovedAndSubmittedSubmissionsOnResource(submissionDetail.getSubmission().getResource().getId()))
        .thenReturn(queryResult);
    var expectedMap = Map.of(
        submissionDetail.getId().toString(),
        "Submission 1: Prospect create (Pending approval)");
    var modelAndView = new ModelAndView();
    assertThat(submissionHistorySearchSelectorModelService.addReviewSubmissionSearchSelectorItemsToModelAndView(
        modelAndView, submissionDetail.getSubmission().getResource().getId()).getModelMap())
        .containsExactly(
            entry("submissionSelectedUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .viewSubmission(submissionDetail.getSubmission().getResource().getId(), null))),
            entry("submissionsOnResource", expectedMap));
  }

  @Test
  void addReviewSubmissionSearchSelectorItemsToModelAndView_whenTwoItems() {
    var selectedSubmissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    var approvedSubmissiondetail = SubmissionDetailTestUtils
        .createNewProspectSubmissionDetail();
    approvedSubmissiondetail.setStatus(SubmissionDetailStatus.APPROVED);
    approvedSubmissiondetail.setApprovedTimestamp(
        LocalDateTime.of(2012, 11, 11, 10, 10)
            .toInstant(ZoneOffset.UTC));
    selectedSubmissionDetail.setApprovedTimestamp(Instant.now());
    var queryResult = List.of(
        SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(selectedSubmissionDetail),
        SubmissionIntegrationTestUtils.getSubmissionQuerySearchRecord(approvedSubmissiondetail)
    );

    when(submissionHistoryQueryService.getApprovedAndSubmittedSubmissionsOnResource(
        selectedSubmissionDetail.getSubmission().getResource().getId()))
        .thenReturn(queryResult);
    var expectedMap = Map.of(
        selectedSubmissionDetail.getId().toString(),
        "Submission 2: Prospect update (Pending approval)",
        approvedSubmissiondetail.getId().toString(),
        "Submission 1: Prospect create (Approved: 11 November 2012)"
    );
    var modelAndView = new ModelAndView();
    assertThat(submissionHistorySearchSelectorModelService.addReviewSubmissionSearchSelectorItemsToModelAndView(
        modelAndView, selectedSubmissionDetail.getSubmission().getResource().getId()).getModelMap())
        .containsExactly(
            entry("submissionSelectedUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .viewSubmission(selectedSubmissionDetail.getSubmission().getResource().getId(), null))),
            entry("submissionsOnResource", expectedMap));
  }
}