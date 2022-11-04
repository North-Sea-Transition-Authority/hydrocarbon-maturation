package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

public class PostDrillDryHoleTaskListBreadcrumbUtil {

  private PostDrillDryHoleTaskListBreadcrumbUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static ModelAndView addTaskListAndWorkAreaToModelAndView(ModelAndView modelAndView, String pageName,
                                                                  UUID submissionDetailId) {
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(getPostDrillDryHoleTaskListBreadcrumb(submissionDetailId))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  private static BreadcrumbItem getPostDrillDryHoleTaskListBreadcrumb(UUID submissionDetailsId) {
    return new BreadcrumbItem(
        PostDrillDryHoleTaskListController.PAGE_NAME,
        ReverseRouter.route(on(PostDrillDryHoleTaskListController.class).getTaskList(submissionDetailsId))
    );
  }
}
