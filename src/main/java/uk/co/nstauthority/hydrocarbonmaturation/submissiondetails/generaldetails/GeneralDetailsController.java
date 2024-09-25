package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/general-details")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class GeneralDetailsController {

  public static final String PAGE_NAME = "General details";

  public static final String CSR_FIELD_REPORT = "Provide the final Concept Select Report";
  public static final String FDP_FIELD_REPORT = "Provide the final Field Development Plan";
  public static final String FDPA_FIELD_REPORT = "Provide the final Field Development Plan Addendum";
  public static final String COP_FIELD_REPORT = "Provide your Cessation of Production report if available";

  private final GeneralDetailsFormService generalDetailsFormService;
  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final GeneralDetailsFormValidator generalDetailsFormValidator;
  private final OnshoreOffshoreService onshoreOffshoreService;
  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final FieldPersistenceService fieldPersistenceService;

  @Autowired
  public GeneralDetailsController(GeneralDetailsFormService generalDetailsFormService,
                                  SubmissionDetailService submissionDetailService,
                                  GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                  GeneralDetailsFormValidator generalDetailsFormValidator,
                                  OnshoreOffshoreService onshoreOffshoreService,
                                  SubmissionSupportingDocumentService supportingDocumentService,
                                  FieldPersistenceService fieldPersistenceService) {
    this.generalDetailsFormService = generalDetailsFormService;
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.onshoreOffshoreService = onshoreOffshoreService;
    this.supportingDocumentService = supportingDocumentService;
    this.fieldPersistenceService = fieldPersistenceService;
  }

  @GetMapping
  public ModelAndView getGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @ModelAttribute("form") GeneralDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var onshoreOffshore = onshoreOffshoreService.getOnshoreOffshore(submissionDetail);
    return getGeneralDetailsModelAndView(
        generalDetailsFormService.getGeneralDetailsForm(submissionDetail), submissionDetail, onshoreOffshore);
  }

  @PostMapping
  public ModelAndView saveGeneralDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @ModelAttribute("form") GeneralDetailsForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var onshoreOffshore = onshoreOffshoreService.getOnshoreOffshore(submissionDetail);
    if (!generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getGeneralDetailsModelAndView(form, submissionDetail, onshoreOffshore);
    } else {
      generalDetailsPersistenceService.saveGeneralDetails(form, submissionDetail, onshoreOffshore);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId, null));
    }
  }

  private ModelAndView getGeneralDetailsModelAndView(GeneralDetailsForm form, SubmissionDetail submissionDetail,
                                                     OnshoreOffshore onshoreOffshore) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/generalDetails")
        .addObject("licenceApplicationEndpoint",
            SearchSelectorService.route(on(LicenceApplicationRestController.class)
                .searchLicenceApplicationsWithManualEntry(null)))
        .addObject("discoverySubmission", submissionDetail.isDiscoverySubmission())
        .addObject("prospectSubmission", submissionDetail.isProspectSubmission())
        .addObject("fieldSubmission", submissionDetail.isFieldSubmission())
        .addObject("relinquishmentSubmission",
            submissionDetail.isDiscoveryRelinquishment() || submissionDetail.isProspectRelinquishment())
        .addObject("licenceApplication",
            generalDetailsFormService.getPreSelectedLicenceApplication(form.getLicenceApplication()))
        .addObject("resourceType",
            submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase())
        .addObject("quadrantBlocksApiEndpoint", getQuadrantBlocksApiEndpoint(submissionDetail))
        .addObject("alreadyAddedQuadrantBlocks", generalDetailsFormService.getQuadrantAndBlockViews(form))
        .addObject("form", form)
        .addObject("licenceEndpoint",
            Boolean.TRUE.equals(submissionDetail.isCorrection())
                ? SearchSelectorService.route(on(LicenceRestController.class).searchLiveLicences(null))
                : SearchSelectorService.route(on(LicenceRestController.class).searchExtantLicences(null))
        )
        .addObject("alreadyAddedLicences", generalDetailsFormService.getLicenceViews(
            form))
        .addObject("hasLicencesOnEarlierSubmission", generalDetailsFormService
            .hasLicencesOnEarlierSubmission(submissionDetail))
        .addObject("isOffshore", onshoreOffshore == OnshoreOffshore.OFFSHORE)
        .addObject("driveMechanismOptions", DriveMechanism.getDriveMechanismOptionsWithHintText())
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("isRegulatorSubmission", SubmitterType.REGULATOR.equals(submissionDetail.getSubmitterType()));

    // Relinquishment information source is automatically set to relinquishment
    if (!submissionDetail.isRelinquishment()) {
      if (submissionDetail.isProspectSubmission()) {
        modelAndView.addObject("informationSources", InformationSource.getInformationSource());
      } else if (submissionDetail.isDiscoverySubmission()) {
        modelAndView.addObject("informationSources", InformationSource.getDiscoveryInformationSource());
      }
    }

    if (submissionDetail.isFieldSubmission()) {
      var field = fieldPersistenceService.getFieldOrThrow(submissionDetail.getSubmission().getResource());

      boolean fieldWhollyLocatedInArea;
      if (onshoreOffshore == OnshoreOffshore.OFFSHORE) {
        fieldWhollyLocatedInArea = field.getFieldLocatedInUkcs();
      } else {
        fieldWhollyLocatedInArea = field.getFieldLocatedInEngland();
      }

      modelAndView
          .addObject("fieldReportTemplate",
              supportingDocumentService.submissionFileUploadComponentAttributes(
                  form.getFieldReport(),
                  FileDocumentType.FIELD_REPORT, submissionDetail))
          .addObject("fieldReportType",
              getFieldReportTypeForSubmissionType(submissionDetail))
          .addObject("fieldWhollyLocatedInArea", fieldWhollyLocatedInArea);
    }

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  @NotNull
  private static String getFieldReportTypeForSubmissionType(SubmissionDetail submissionDetail) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_CSR -> CSR_FIELD_REPORT;
      case CREATE_FDP -> FDP_FIELD_REPORT;
      case CREATE_FDPA -> FDPA_FIELD_REPORT;
      case CREATE_COP -> COP_FIELD_REPORT;
      default -> throw new IllegalArgumentException("There is no field report type for submission with id %s"
          .formatted(submissionDetail.getId()));
    };
  }

  private String getQuadrantBlocksApiEndpoint(SubmissionDetail submissionDetail) {
    var onshoreOffshore = onshoreOffshoreService.getOnshoreOffshore(submissionDetail);
    return onshoreOffshore == OnshoreOffshore.ONSHORE
        ? SearchSelectorService.route(on(ReferenceBlockRestController.class).searchOnshoreQuadrantBlocks(null))
        : SearchSelectorService.route(on(ReferenceBlockRestController.class).searchOffshoreQuadrantBlocks(null));
  }
}
