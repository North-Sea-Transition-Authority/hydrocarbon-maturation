package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class SubmissionHistorySearchSelectorModelService {
  private final SubmissionHistoryQueryService submissionHistoryQueryService;

  SubmissionHistorySearchSelectorModelService(SubmissionHistoryQueryService submissionHistoryQueryService) {
    this.submissionHistoryQueryService = submissionHistoryQueryService;
  }

  ModelAndView addViewSorSubmissionSearchSelectorItemsToModelAndView(
      ModelAndView modelAndView, SubmissionDetail submissionDetail) {
    var querySearchResults = submissionHistoryQueryService
        .getApprovedSubmissionsOnResource(submissionDetail.getResourceId().getId());
    var submissionSearchSelectorItems = getSubmissionSearchSelectorItems(querySearchResults);

    return modelAndView
        .addObject("submissionSelectedUrl", ReverseRouter.route(on(ViewResourceRecordController.class)
            .viewSubmission(submissionDetail.getResourceId().getId(), null)))
        .addObject("submissionNumber",
            getCurrentSubmissionNumber(submissionDetail, querySearchResults))
        .addObject("submissionsOnResource", submissionSearchSelectorItems);
  }

  public ModelAndView addReviewSubmissionSearchSelectorItemsToModelAndView(
      ModelAndView modelAndView, SubmissionDetail submissionDetail) {
    var querySearchResults = submissionHistoryQueryService
        .getApprovedAndSubmittedSubmissionsOnResource(submissionDetail.getResourceId().getId());
    var submissionSearchSelectorItems = getSubmissionSearchSelectorItems(querySearchResults);
    return modelAndView
        .addObject("submissionNumber",
            getCurrentSubmissionNumber(submissionDetail, querySearchResults))
        .addObject("submissionSelectedUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
            .viewSubmission(submissionDetail.getResourceId().getId(), null)))
        .addObject("submissionsOnResource", submissionSearchSelectorItems);
  }

  private String getCurrentSubmissionNumber(SubmissionDetail submissionDetail,
                                            List<SubmissionQuerySearchRecord> queryResults) {
    for (int i = queryResults.size() - 1; i >= 0; i--) {
      if (queryResults.get(i).getSubmissionDetailId().equals(submissionDetail.getId())) {
        return String.valueOf(queryResults.size() - i);
      }
    }
    return String.valueOf(queryResults.size());
  }

  @NotNull
  private Map<String, String> getSubmissionSearchSelectorItems(
      List<SubmissionQuerySearchRecord> submissionRecords) {
    var searchSelectorItems = new LinkedHashMap<String, String>();
    for (int i = 1; i <= submissionRecords.size(); i++) {
      searchSelectorItems.put(submissionRecords.get(i - 1).getSubmissionDetailId().toString(),
          getRecordDisplay(submissionRecords.get(i - 1), (submissionRecords.size() + 1) - i));
    }
    return searchSelectorItems;
  }

  private String getRecordDisplay(SubmissionQuerySearchRecord submissionQuerySearchRecord, Integer number) {
    return "(%s) %s (%s)"
        .formatted(number.toString(),
            submissionQuerySearchRecord.getSubmissionType().getTypeName(),
            getApprovedDateDisplay(submissionQuerySearchRecord));
  }

  private String getApprovedDateDisplay(SubmissionQuerySearchRecord submissionQuerySearchRecord) {
    if (submissionQuerySearchRecord.getStatus() == SubmissionDetailStatus.APPROVED) {
      return "Approved: %s".formatted(DateUtil.formatLongDate(LocalDate.from(
          submissionQuerySearchRecord.getApprovedTimestamp().toLocalDateTime())));
    } else {
      return "Pending approval";
    }
  }
}
