package uk.co.nstauthority.hydrocarbonmaturation.breadcrumb;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.LinkedHashSet;
import java.util.Set;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

public class Breadcrumbs {

  private final Set<BreadcrumbItem> breadcrumbItems;
  private final String currentPageName;

  private Breadcrumbs(Set<BreadcrumbItem> breadcrumbItems, String currentPageName) {
    this.breadcrumbItems = breadcrumbItems;
    this.currentPageName = currentPageName;
  }

  Set<BreadcrumbItem> getBreadcrumbItems() {
    return breadcrumbItems;
  }

  String getCurrentPageName() {
    return currentPageName;
  }

  public static BreadcrumbsBuilder builder(String currentPageName) {
    return new BreadcrumbsBuilder(currentPageName);
  }

  public static class BreadcrumbsBuilder {
    private final String currentPage;
    private final Set<BreadcrumbItem> breadcrumbs = new LinkedHashSet<>();

    public BreadcrumbsBuilder(String currentPage) {
      this.currentPage = currentPage;
    }

    public BreadcrumbsBuilder addWorkAreaBreadcrumb() {
      breadcrumbs.add(
          new BreadcrumbItem(
              WorkAreaController.WORK_AREA_TITLE,
              ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null))
          )
      );
      return this;
    }

    public BreadcrumbsBuilder addTaskListBreadcrumb(SubmissionDetail submissionDetail) {
      breadcrumbs.add(
          new BreadcrumbItem(
              TaskListController.PAGE_NAME,
              ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId(), null))
          )
      );
      return this;
    }

    public BreadcrumbsBuilder addReservoirTaskListBreadcrumb(SubmissionDetail submissionDetail) {
      breadcrumbs.add(
          new BreadcrumbItem(
              ReservoirTaskListController.PAGE_TITLE,
              ReverseRouter.route(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetail.getId(), null))
          )
      );
      return this;
    }

    public BreadcrumbsBuilder addBreadcrumb(String prompt, String endpoint) {
      breadcrumbs.add(new BreadcrumbItem(prompt, endpoint));
      return this;
    }

    public BreadcrumbsBuilder addBreadcrumb(BreadcrumbItem breadcrumbItem) {
      breadcrumbs.add(breadcrumbItem);
      return this;
    }

    public Breadcrumbs build() {
      return new Breadcrumbs(this.breadcrumbs, this.currentPage);
    }
  }
}
