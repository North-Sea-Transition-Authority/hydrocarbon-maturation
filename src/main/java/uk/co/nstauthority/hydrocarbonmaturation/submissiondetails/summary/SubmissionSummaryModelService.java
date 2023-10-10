package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;

@Service
public class SubmissionSummaryModelService {
  private final ProspectSummaryService prospectSummaryService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final DiscoverySummaryService discoverySummaryService;
  private final PostDrillDryWellSummaryService postDrillDryWellSummaryService;
  private final SubmissionUpdateService submissionUpdateService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionSummaryService submissionSummaryService;
  private final FieldSummaryService fieldSummaryService;

  @Autowired
  SubmissionSummaryModelService(ProspectSummaryService prospectSummaryService,
                                HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                DiscoverySummaryService discoverySummaryService,
                                PostDrillDryWellSummaryService postDrillDryWellSummaryService,
                                SubmissionUpdateService submissionUpdateService,
                                ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                SubmissionResourceService submissionResourceService,
                                SubmissionSummaryService submissionSummaryService,
                                FieldSummaryService fieldSummaryService) {
    this.prospectSummaryService = prospectSummaryService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.discoverySummaryService = discoverySummaryService;
    this.postDrillDryWellSummaryService = postDrillDryWellSummaryService;
    this.submissionUpdateService = submissionUpdateService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.submissionResourceService = submissionResourceService;
    this.submissionSummaryService = submissionSummaryService;
    this.fieldSummaryService = fieldSummaryService;
  }

  public ModelAndView createSummaryForSubmissionTypeToModelAndView(SubmissionDetail submissionDetail, String viewName) {
    return switch (submissionDetail.getSubmissionType()) {
      case CREATE_PROSPECT, PROSPECT_UPDATE, RELINQUISHMENT_PROSPECT ->
          createProspectSubmissionSummaryModelAndView(submissionDetail, viewName);
      case POST_DRILL_DRY_HOLE ->
          createPostDrillDryWellSubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_DISCOVERY, UPDATE_DISCOVERY, CREATE_DISCOVERY_FROM_PROSPECT, RELINQUISHMENT_DISCOVERY ->
          createDiscoverySubmissionSummaryModelAndView(submissionDetail, viewName);
      case CREATE_FIELD, CREATE_CSR, CREATE_FDP, CREATE_FDPA, CREATE_COP ->
          createFieldSubmissionSummaryModelAndView(submissionDetail, viewName);
    };
  }

  public ModelAndView createProspectSubmissionSummaryModelAndView(SubmissionDetail submissionDetail,
                                                                  String viewName) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetail(submissionDetail);
    var modelAndView = new ModelAndView(viewName)
        .addObject("generalDetailsSummaryView",
            prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("sourceAndSealSummaryView",
            prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("chanceOfSuccessSummaryView",
            prospectSummaryService.getChanceOfSuccessSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("hydrocarbonCaseSummaryView",
            prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("reservoirPropertiesSummaryView",
            prospectSummaryService.getReservoirPropertiesSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("trapPropertiesSummaryView",
            prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilCaseVolumetricsSummaryView",
            prospectSummaryService.getOilCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("oilCaseOutputsSummaryView",
            prospectSummaryService.getOilCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("gasCaseVolumetricsSummaryView",
            prospectSummaryService.getGasCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("gasCaseOutputsSummaryView",
            prospectSummaryService.getGasCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("mixedCaseVolumetricsSummaryView",
            prospectSummaryService.getMixedCaseVolumetricsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("mixedCaseOutputsSummaryView",
            prospectSummaryService.getMixedCaseOutputsSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("opportunityDetailsSummaryView",
            prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("outlineForMaximumCaseSummaryView",
            prospectSummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail));

    hydrocarbonCasePersistenceService.getHydrocarbonCaseDetail(submissionDetail)
        .ifPresentOrElse(hydrocarbonCaseDetail ->
                modelAndView
                    .addObject("gasCase",
                        hydrocarbonCasePersistenceService.isGasOnlyPresent(hydrocarbonCaseDetail))
                    .addObject("oilCase",
                        hydrocarbonCasePersistenceService.isOilOnlyPresent(hydrocarbonCaseDetail))
                    .addObject("mixedCase",
                        hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)),
            () ->
                modelAndView
                    .addObject("gasCase", false)
                    .addObject("oilCase", false)
                    .addObject("mixedCase", false)
        );
    addCommonAttributes(submissionDetail, modelAndView);
    return modelAndView;
  }

  public ModelAndView createPostDrillDryWellSubmissionSummaryModelAndView(SubmissionDetail submissionDetail, String viewName) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetail(submissionDetail);
    var modelAndView = new ModelAndView(viewName)
        .addObject("dryWellDetailsSummaryView",
            postDrillDryWellSummaryService.getDryWellDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("generalDetailsSummaryView",
            postDrillDryWellSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("permeabilitySummaryView",
            postDrillDryWellSummaryService.getPermeabilitySummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("reservoirPropertiesSummaryView",
            postDrillDryWellSummaryService
                .getReservoirPropertiesSummaryViewFromReservoirSubmissionDetail(reservoirSubmissionDetail))
        .addObject("opportunityDetailsSummaryView",
            prospectSummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("outlineForMaximumCaseSummaryView",
            prospectSummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("sourceAndSealSummaryView",
            prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("trapPropertiesSummaryView",
            prospectSummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail));

    addCommonAttributes(submissionDetail, modelAndView);
    return modelAndView;
  }

  public ModelAndView createDiscoverySubmissionSummaryModelAndView(SubmissionDetail submissionDetail,
                                                                   String viewName) {
    var modelAndView = new ModelAndView(viewName);
    modelAndView
        .addObject("gasFluidPropertiesSummaryView",
            discoverySummaryService.getGasFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("generalFluidPropertiesSummaryView",
            discoverySummaryService.getGeneralFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("oilFluidPropertiesSummaryView",
            discoverySummaryService.getOilFluidPropertySummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("generalDetailsSummaryView",
            discoverySummaryService.getGeneralDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("opportunityDetailsSummaryView",
            discoverySummaryService.getOpportunityDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("outlineForMaximumCaseSummaryView",
            discoverySummaryService.getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("discoveryWellDetailsSummaryView",
            discoverySummaryService.getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("sourceAndSealSummaryView",
            discoverySummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("trapPropertiesSummaryView",
            discoverySummaryService.getTrapPropertiesSummaryViewFromSubmissionDetail(submissionDetail))
        .addObject("reservoirSummaries",
            discoverySummaryService.getReservoirSummariesFromSubmissionDetail(submissionDetail));

    addCommonAttributes(submissionDetail, modelAndView);
    return modelAndView;
  }

  public ModelAndView createFieldSubmissionSummaryModelAndView(SubmissionDetail submissionDetail, String viewName) {
    var modelAndView = new ModelAndView(viewName)
        .addObject("summarySections", submissionSummaryService.getSummarySections(submissionDetail))
        .addObject("reservoirSummaries",
            fieldSummaryService.getReservoirSummariesFromSubmissionDetail(submissionDetail));
    addCommonAttributes(submissionDetail, modelAndView);
    return modelAndView;
  }

  private void addCommonAttributes(SubmissionDetail submissionDetail, ModelAndView modelAndView) {
    modelAndView
        .addObject("accordionId", submissionDetail.getId());

    var submissionUpdateOpt = submissionUpdateService.getLatestUnresolvedUpdateForUpdateSubmissionDetail(submissionDetail);
    submissionUpdateOpt.ifPresent(submissionUpdate ->
        modelAndView
            .addObject("submissionUpdateReason", submissionUpdate.getReason()));
    modelAndView.addObject("isSubmissionUpdate", submissionUpdateOpt.isPresent());
    modelAndView.addObject("resourceName", submissionResourceService.getResourceNameOrDefault(submissionDetail));

    if (!submissionDetail.isFieldSubmission()) {
      var resourceDetailSummaryView = prospectSummaryService.getResourceDetailSummaryViewFromSubmissionDetail(submissionDetail);
      modelAndView.addObject("resourceDetailSummaryView", resourceDetailSummaryView);
    }
  }
}
