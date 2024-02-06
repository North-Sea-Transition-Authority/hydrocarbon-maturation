package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorUsers;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@Controller
@RequestMapping("/field-status/{fieldIdentifier}")
@AccessibleByRegulatorUsers
public class ViewFieldStatusHistoryController {
  public static final String PAGE_TITLE = "Field status";

  private final FieldPersistenceService fieldPersistenceService;
  private final ViewFieldStatusHistoryService viewFieldStatusHistoryService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public ViewFieldStatusHistoryController(FieldPersistenceService fieldPersistenceService,
                                          ViewFieldStatusHistoryService viewFieldStatusHistoryService,
                                          SubmissionDetailService submissionDetailService) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.viewFieldStatusHistoryService = viewFieldStatusHistoryService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getViewFieldStatusHistory(@PathVariable("fieldIdentifier") Integer fieldIdentifier)  {
    var field = fieldPersistenceService.getFieldOrThrow(fieldIdentifier);

    var modelAndView = getViewFieldStatusHistoryModelAndView(field);
    addBreadCrumbsForSystemOfRecordView(modelAndView, field);

    return modelAndView;
  }

  @GetMapping("/{submissionDetailId}")
  public ModelAndView getViewFieldStatusHistoryWithSubmissionDetail(
      @PathVariable("fieldIdentifier") Integer fieldIdentifier,
      @PathVariable("submissionDetailId") UUID submissionDetailId) {

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    if (SubmissionDetailStatus.ACCEPTED.equals(submissionDetail.getStatus())) {
      return getViewFieldStatusHistory(fieldIdentifier);
    }

    var field = fieldPersistenceService.getFieldOrThrow(fieldIdentifier);

    var modelAndView = getViewFieldStatusHistoryModelAndView(field);
    addBreadCrumbsForReviewView(modelAndView, field);

    return modelAndView;
  }

  private ModelAndView getViewFieldStatusHistoryModelAndView(Field field) {
    var modelAndView = new ModelAndView("remi/field/fieldstatushistory/fieldStatusHistory");

    return modelAndView
        .addObject("pageTitle",  PAGE_TITLE)
        .addObject("pageCaption", field.getCorrectedLegalFieldName())
        .addObject("fieldStatusHistoryViews", viewFieldStatusHistoryService.getFieldStatusHistoryViews(field));
  }

  private void addBreadCrumbsForReviewView(ModelAndView modelAndView, Field field) {

    var breadcrumbUrl = ReverseRouter.route(on(ReviewSubmissionController.class)
        .showReviewPage(field.getResource().getId()));
    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_TITLE)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(field.getCorrectedLegalFieldName(),
            breadcrumbUrl)
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }

  private void addBreadCrumbsForSystemOfRecordView(ModelAndView modelAndView, Field field) {
    var breadcrumbUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(field.getResource().getId()));
    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_TITLE)
        .addBreadcrumb(
            SearchFieldRecordsController.PAGE_TITLE,
            ReverseRouter.route(on(SearchFieldRecordsController.class).searchResourceRecords(null, null)))
        .addBreadcrumb(field.getCorrectedLegalFieldName(),
            breadcrumbUrl)
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }
}
