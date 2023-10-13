package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;

public class TaskListBreadcrumbUtil {

  private TaskListBreadcrumbUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static ModelAndView addTaskListAndWorkAreaToModelAndView(ModelAndView modelAndView,
                                                                  String pageName,
                                                                  SubmissionDetail submissionDetail) {
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(getTaskListBreadcrumb(submissionDetail))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  public static ModelAndView addReservoirTaskListBreadcrumbsToModelAndView(ModelAndView modelAndView,
                                                                           String pageName,
                                                                           ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(getTaskListBreadcrumb(submissionDetail))
        .addBreadcrumb(getReservoirTaskListBreadCrumb(reservoirSubmissionDetail))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  private static BreadcrumbItem getTaskListBreadcrumb(SubmissionDetail submissionDetail) {
    return new BreadcrumbItem(
          TaskListController.PAGE_NAME,
          ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId()))
    );
  }

  private static BreadcrumbItem getReservoirTaskListBreadCrumb(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return new BreadcrumbItem(
        ReservoirTaskListController.PAGE_TITLE,
        ReverseRouter.route(on(ReservoirTaskListController.class)
            .getReservoirTaskList(reservoirSubmissionDetail.getSubmissionDetail().getId(), null))
    );
  }
}
