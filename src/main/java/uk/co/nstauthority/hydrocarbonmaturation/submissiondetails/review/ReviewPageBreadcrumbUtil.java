package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

public class ReviewPageBreadcrumbUtil {

  private ReviewPageBreadcrumbUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static ModelAndView addWorkAreaAndManagementPageToModelAndView(ModelAndView modelAndView,
                                                                        String pageName,
                                                                        String submissionName,
                                                                        SubmissionDetail submissionDetail) {
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(getReviewPageBreadcrumb(submissionDetail, submissionName))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  private static BreadcrumbItem getReviewPageBreadcrumb(SubmissionDetail submissionDetail, String submissionName) {
    return new BreadcrumbItem(
        submissionName,
        ReverseRouter.route(on(ReviewSubmissionController.class)
            .showReviewPage(submissionDetail.getSubmission().getResource().getId()))
    );
  }
}
