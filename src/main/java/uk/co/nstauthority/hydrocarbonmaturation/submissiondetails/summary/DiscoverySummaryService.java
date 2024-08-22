package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedCaseVolTotalsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverycominbedcasevolumetrictotals.DiscoveryCombinedVolTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoverygascasevolumetrictotals.DiscoveryGasCaseVolumetricTotalsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

@Service
class DiscoverySummaryService {

  private final GeneralDetailSummaryService generalDetailSummaryService;
  private final DiscoveryWellDetailsSummaryService discoveryWellDetailsSummaryService;
  private final OutlineForMaximumCaseSummaryService outlineForMaximumCaseSummaryService;
  private final OpportunityDetailsSummaryService opportunityDetailsSummaryService;
  private final TrapPropertiesSummaryService trapPropertiesSummaryService;
  private final SourceAndSealSummaryService sourceAndSealSummaryService;
  private final ReservoirSummaryService reservoirSummaryService;
  private final DiscoveryOilCaseVolumetricTotalsSummaryService discoveryOilCaseVolumetricTotalsSummaryService;
  private final DiscoveryGasCaseVolumetricTotalsSummaryService discoveryGasCaseVolumetricTotalsSummaryService;
  private final DiscoveryCombinedVolTotalsSummaryService discoveryCombinedVolTotalsSummaryService;
  private final ChanceOfSuccessSummaryService chanceOfSuccessSummaryService;


  @Autowired
  DiscoverySummaryService(GeneralDetailSummaryService generalDetailSummaryService,
                          DiscoveryWellDetailsSummaryService discoveryWellDetailsSummaryService,
                          OutlineForMaximumCaseSummaryService outlineForMaximumCaseSummaryService,
                          OpportunityDetailsSummaryService opportunityDetailsSummaryService,
                          TrapPropertiesSummaryService trapPropertiesSummaryService,
                          SourceAndSealSummaryService sourceAndSealSummaryService,
                          ReservoirSummaryService reservoirSummaryService,
                          DiscoveryOilCaseVolumetricTotalsSummaryService discoveryOilCaseVolumetricTotalsSummaryService,
                          DiscoveryGasCaseVolumetricTotalsSummaryService discoveryGasCaseVolumetricTotalsSummaryService,
                          DiscoveryCombinedVolTotalsSummaryService discoveryCombinedVolTotalsSummaryService,
                          ChanceOfSuccessSummaryService chanceOfSuccessSummaryService) {
    this.generalDetailSummaryService = generalDetailSummaryService;
    this.discoveryWellDetailsSummaryService = discoveryWellDetailsSummaryService;
    this.outlineForMaximumCaseSummaryService = outlineForMaximumCaseSummaryService;
    this.opportunityDetailsSummaryService = opportunityDetailsSummaryService;
    this.trapPropertiesSummaryService = trapPropertiesSummaryService;
    this.sourceAndSealSummaryService = sourceAndSealSummaryService;
    this.reservoirSummaryService = reservoirSummaryService;
    this.discoveryOilCaseVolumetricTotalsSummaryService = discoveryOilCaseVolumetricTotalsSummaryService;
    this.discoveryGasCaseVolumetricTotalsSummaryService = discoveryGasCaseVolumetricTotalsSummaryService;
    this.discoveryCombinedVolTotalsSummaryService = discoveryCombinedVolTotalsSummaryService;
    this.chanceOfSuccessSummaryService = chanceOfSuccessSummaryService;
  }

  public DiscoveryWellDetailsSummaryView getDiscoveryWellDetailsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return discoveryWellDetailsSummaryService.getDiscoveryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public SourceAndSealSummaryView getSourceAndSealSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return sourceAndSealSummaryService.getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  public GeneralDetailSummaryView getGeneralDetailsSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public TrapPropertiesSummaryView getTrapPropertiesSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return trapPropertiesSummaryService.getTrapPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  public OpportunityDetailsSummaryView getOpportunityDetailsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return opportunityDetailsSummaryService.getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);
  }

  public OutlineForMaximumCaseSummaryView getOutlineForMaximumCaseSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return outlineForMaximumCaseSummaryService.getOutlineForMaximumCaseSummaryFromSubmissionDetail(submissionDetail);
  }

  public List<ReservoirSummary> getReservoirSummariesFromSubmissionDetail(SubmissionDetail submissionDetail, User user) {
    return reservoirSummaryService.getReservoirSummaries(submissionDetail, user);
  }

  public DiscoveryOilCaseVolumetricTotalsSummaryView getOilCaseVolumetricTotalsFromSubmissionDetail(
      SubmissionDetail submissionDetail
  ) {
    return discoveryOilCaseVolumetricTotalsSummaryService.createSummaryView(submissionDetail);
  }

  public DiscoveryGasCaseVolumetricTotalsSummaryView getGasCaseVolumetricTotalsFromSubmissionDetail(
      SubmissionDetail submissionDetail
  ) {
    return discoveryGasCaseVolumetricTotalsSummaryService.getGasCaseVolumetricTotalsSummaryFromSubmissionDetail(submissionDetail);
  }

  public DiscoveryCombinedCaseVolTotalsSummaryView getCombinedCaseVolumetricTotalsFromSubmissionDetail(
      SubmissionDetail submissionDetail
  ) {
    return discoveryCombinedVolTotalsSummaryService
        .getCombinedCaseVolumetricTotalsSummaryFromSubmissionDetail(submissionDetail);
  }

  public ChanceOfSuccessSummaryView getChanceOfSuccessSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return chanceOfSuccessSummaryService.getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
  }
}