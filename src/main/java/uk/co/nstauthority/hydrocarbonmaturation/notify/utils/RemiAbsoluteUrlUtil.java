package uk.co.nstauthority.hydrocarbonmaturation.notify.utils;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

public class RemiAbsoluteUrlUtil {

  private RemiAbsoluteUrlUtil() {
    throw new IllegalUtilClassInstantiationException(getClass());
  }

  public static String getSystemOfRecordResourceUrl(SubmissionDetail submissionDetail) {
    var resourceSystemOfRecordUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(submissionDetail.getSubmission().getResource().getId()));
    return "%s%s".formatted(getBaseUrl(), resourceSystemOfRecordUrl);
  }

  public static String getReviewSubmissionPageUrl(SubmissionDetail submissionDetail) {
    var reviewPageUrl = ReverseRouter.route(on(ReviewSubmissionController.class)
        .showReviewPage(submissionDetail.getSubmission().getResource().getId()));
    return "%s%s".formatted(getBaseUrl(), reviewPageUrl);
  }

  public static String getTasklistPageUrl(SubmissionDetail submissionDetail) {
    var tasklistUrl = ReverseRouter.route(on(TaskListController.class)
        .getTaskList(submissionDetail.getId()));
    return "%s%s".formatted(getBaseUrl(), tasklistUrl);
  }

  public static String getWorkAreaUrl() {
    var workAreaUrl = ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null));
    return "%s%s".formatted(getBaseUrl(), workAreaUrl);
  }

  private static String getBaseUrl() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
  }
}
