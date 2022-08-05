package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;

public class ProspectTaskListBreadcrumbUtil {

  private ProspectTaskListBreadcrumbUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static BreadcrumbItem getProspectTaskListBreadcrumb(UUID submissionDetailsId) {
    return new BreadcrumbItem(
        ProspectTaskListController.PAGE_NAME,
        ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailsId))
    );
  }
}
