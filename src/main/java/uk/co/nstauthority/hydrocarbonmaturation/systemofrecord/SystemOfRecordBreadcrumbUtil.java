package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

public class SystemOfRecordBreadcrumbUtil {

  private SystemOfRecordBreadcrumbUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static ModelAndView addSearchProspectAndDiscoveryRecordsBreadcrumbsToModelAndView(ModelAndView modelAndView,
                                                                                           String pageName) {
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addBreadcrumb(getSearchProspectAndDiscoveryRecordsBreadcrumb())
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  private static BreadcrumbItem getSearchProspectAndDiscoveryRecordsBreadcrumb() {
    return new BreadcrumbItem(
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE,
            ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null))
    );
  }

  public static ModelAndView addSearchFieldRecordsBreadcrumbsToModelAndView(ModelAndView modelAndView,
                                                                                           String pageName) {
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addBreadcrumb(getSearchFieldRecordsBreadcrumb())
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  private static BreadcrumbItem getSearchFieldRecordsBreadcrumb() {
    return new BreadcrumbItem(
        SearchFieldRecordsController.PAGE_TITLE,
        ReverseRouter.route(on(SearchFieldRecordsController.class).searchResourceRecords(null, null))
    );
  }
}
