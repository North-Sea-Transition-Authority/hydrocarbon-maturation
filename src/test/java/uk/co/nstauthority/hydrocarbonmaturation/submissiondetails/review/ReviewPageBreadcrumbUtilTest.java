package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

class ReviewPageBreadcrumbUtilTest {

  @Test
  void addWorkAreaAndManagementPageToModelAndView_assertBreadcrumbsAdded() {
    var modelAndView = new ModelAndView();
    var pageName = "test page";
    var submissionName = "Good submission";
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    var modelAndViewWithBreadCrumbs = ReviewPageBreadcrumbUtil
        .addWorkAreaAndManagementPageToModelAndView(
            modelAndView, pageName, submissionName, submissionDetail);

    var breadcrumbsMap = ((Map<String, String>) modelAndViewWithBreadCrumbs
        .getModelMap().get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()),
            WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getResourceId().getId())),
            submissionName)
    );
  }
}