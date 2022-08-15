package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

public class ProspectTaskListBreadcrumbUtil {

  private ProspectTaskListBreadcrumbUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static ModelAndView addTaskListAndWorkAreaToModelAndView(ModelAndView modelAndView, String pageName,
                                                                  UUID submissionDetailId) {
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(ProspectTaskListBreadcrumbUtil.getProspectTaskListBreadcrumb(submissionDetailId))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  static BreadcrumbItem getProspectTaskListBreadcrumb(UUID submissionDetailsId) {
    return new BreadcrumbItem(
        ProspectTaskListController.PAGE_NAME,
        ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailsId))
    );
  }
}
