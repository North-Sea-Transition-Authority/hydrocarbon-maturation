package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

class SystemOfRecordBreadcrumbUtil {

  private SystemOfRecordBreadcrumbUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static ModelAndView addSearchSystemOfRecordToModelAndView(ModelAndView modelAndView,
                                                                   String pageName) {
    var breadcrumbs = Breadcrumbs.builder(pageName)
        .addBreadcrumb(getSearchSystemOfRecordBreadcrumb())
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
    return modelAndView;
  }

  private static BreadcrumbItem getSearchSystemOfRecordBreadcrumb() {
    return new BreadcrumbItem(
            SearchResourceRecordsController.PAGE_TITLE,
            ReverseRouter.route(on(SearchResourceRecordsController.class).searchResourceRecords(null))
    );
  }
}
