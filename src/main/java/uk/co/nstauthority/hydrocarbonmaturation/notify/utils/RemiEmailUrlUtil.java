package uk.co.nstauthority.hydrocarbonmaturation.notify.utils;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

public class RemiEmailUrlUtil {

  private RemiEmailUrlUtil() {
    throw new IllegalUtilClassInstantiationException(getClass());
  }

  public static String getSystemOfRecordResourceUrl(SubmissionDetail submissionDetail) {
    var resourceSystemOfRecordUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(submissionDetail.getResourceId().getId()));
    return "%s%s".formatted(getBaseUrl(), resourceSystemOfRecordUrl);
  }

  public static String getReviewSubmissionPageUrl(SubmissionDetail submissionDetail) {
    var reviewPageUrl = ReverseRouter.route(on(ReviewSubmissionController.class)
        .showReviewPage(submissionDetail.getResourceId().getId()));
    return "%s%s".formatted(getBaseUrl(), reviewPageUrl);
  }

  private static String getBaseUrl() {
    return ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
  }
}
