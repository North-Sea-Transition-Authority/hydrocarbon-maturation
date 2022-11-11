package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.discovery.DiscoveryTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole.PostDrillDryHoleTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListController;

public class TaskListBreadcrumbUtil {

  private TaskListBreadcrumbUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static ModelAndView addTaskListAndWorkAreaToModelAndView(ModelAndView modelAndView, String pageName,
                                                                  SubmissionDetail submissionDetail) {
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(getTaskListBreadcrumb(submissionDetail))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  private static BreadcrumbItem getTaskListBreadcrumb(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE -> new BreadcrumbItem(
          ProspectTaskListController.PAGE_NAME,
          ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetail.getId())));
      case POST_DRILL_DRY_HOLE -> new BreadcrumbItem(
          PostDrillDryHoleTaskListController.PAGE_NAME,
          ReverseRouter.route(on(PostDrillDryHoleTaskListController.class).getTaskList(submissionDetail.getId())));
      case CREATE_DISCOVERY -> new BreadcrumbItem(
          DiscoveryTaskListController.PAGE_NAME,
          ReverseRouter.route(on(DiscoveryTaskListController.class).getTaskList(submissionDetail.getId()))
      );
    };
  }
}
