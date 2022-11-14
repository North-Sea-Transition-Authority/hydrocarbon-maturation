package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics.MixedCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ShowOnSubNav;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView;

@Service
class ProspectSummaryService {

  private final GeneralDetailSummaryService generalDetailSummaryService;
  private final SourceAndSealSummaryService sourceAndSealSummaryService;
  private final ChanceOfSuccessSummaryService chanceOfSuccessSummaryService;
  private final HydrocarbonCaseSummaryService hydrocarbonCaseSummaryService;
  private final ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;
  private final TrapPropertiesSummaryService trapPropertiesSummaryService;
  private final MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService;
  private final MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService;
  private final OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService;
  private final OilCaseOutputsSummaryService oilCaseOutputsSummaryService;
  private final GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService;
  private final GasCaseOutputsSummaryService gasCaseOutputsSummaryService;
  private final AdditionalInformationSummaryService additionalInformationSummaryService;
  private final OpportunityDetailsSummaryService opportunityDetailsSummaryService;
  private final List<TaskListSectionService> prospectTaskListSections;

  @Autowired
  public ProspectSummaryService(GeneralDetailSummaryService generalDetailSummaryService,
                                SourceAndSealSummaryService sourceAndSealSummaryService,
                                ChanceOfSuccessSummaryService chanceOfSuccessSummaryService,
                                HydrocarbonCaseSummaryService hydrocarbonCaseSummaryService,
                                ReservoirPropertiesSummaryService reservoirPropertiesSummaryService,
                                TrapPropertiesSummaryService trapPropertiesSummaryService,
                                OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService,
                                OilCaseOutputsSummaryService oilCaseOutputsSummaryService,
                                GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService,
                                GasCaseOutputsSummaryService gasCaseOutputsSummaryService,
                                MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService,
                                MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService,
                                AdditionalInformationSummaryService additionalInformationSummaryService,
                                OpportunityDetailsSummaryService opportunityDetailsSummaryService,
                                List<TaskListSectionService> prospectTaskListSections) {
    this.generalDetailSummaryService = generalDetailSummaryService;
    this.sourceAndSealSummaryService = sourceAndSealSummaryService;
    this.chanceOfSuccessSummaryService = chanceOfSuccessSummaryService;
    this.hydrocarbonCaseSummaryService = hydrocarbonCaseSummaryService;
    this.reservoirPropertiesSummaryService = reservoirPropertiesSummaryService;
    this.oilCaseVolumetricsSummaryService = oilCaseVolumetricsSummaryService;
    this.oilCaseOutputsSummaryService = oilCaseOutputsSummaryService;
    this.gasCaseVolumetricsSummaryService = gasCaseVolumetricsSummaryService;
    this.gasCaseOutputsSummaryService = gasCaseOutputsSummaryService;
    this.mixedCaseVolumetricsSummaryService = mixedCaseVolumetricsSummaryService;
    this.mixedCaseOutputsSummaryService = mixedCaseOutputsSummaryService;
    this.trapPropertiesSummaryService = trapPropertiesSummaryService;
    this.additionalInformationSummaryService = additionalInformationSummaryService;
    this.opportunityDetailsSummaryService = opportunityDetailsSummaryService;
    this.prospectTaskListSections = prospectTaskListSections;
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public SourceAndSealSummaryView getSourceAndSealSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return sourceAndSealSummaryService.getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  public ChanceOfSuccessSummaryView getChanceOfSuccessSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return chanceOfSuccessSummaryService.getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
  }

  public HydrocarbonCaseSummaryView getHydrocarbonCaseSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return hydrocarbonCaseSummaryService.getHydrocarbonSummaryFromSubmissionDetail(submissionDetail);
  }

  public ReservoirPropertiesSummaryView getReservoirPropertiesSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return reservoirPropertiesSummaryService.getReservoirPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getOilCaseVolumetricsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return oilCaseVolumetricsSummaryService.getOilCaseVolumetricsSummaryView(submissionDetail);
  }

  public OilCaseOutputsSummaryView getOilCaseOutputsSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return oilCaseOutputsSummaryService.getOilCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getGasCaseVolumetricsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return gasCaseVolumetricsSummaryService.getGasCaseVolumetricsSummaryView(submissionDetail);
  }

  public GasCaseOutputsSummaryView getGasCaseOutputsSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return gasCaseOutputsSummaryService.getGasCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  public ProbabilityDistributionsFormSummaryView getMixedCaseVolumetricsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return mixedCaseVolumetricsSummaryService.getMixedCaseVolumetricsSummaryView(submissionDetail);
  }

  public MixedCaseOutputsSummaryView getMixedCaseOutputsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return mixedCaseOutputsSummaryService.getMixedCaseOutputsSummaryFromSubmissionDetail(submissionDetail);
  }

  public TrapPropertiesSummaryView getTrapPropertiesSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return trapPropertiesSummaryService.getTrapPropertiesSummaryFromSubmissionDetail(submissionDetail);
  }

  public OpportunityDetailsSummaryView getOpportunityDetailsSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return opportunityDetailsSummaryService.getOpportunityDetailsSummaryFromSubmissionDetail(submissionDetail);
  }

  public AdditionalInformationSummaryView getAdditionalInformationSummaryViewFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    return additionalInformationSummaryService.getAdditionalInformationSummaryFromSubmissionDetail(submissionDetail);
  }

  public List<SubNavigationItem> getProspectSubNavigation(SubmissionDetail submissionDetail) {
    return getProspectSubNavigationItems(prospectTaskListSections, submissionDetail);
  }

  private List<SubNavigationItem> getProspectSubNavigationItems(
      List<TaskListSectionService> sections,
      SubmissionDetail submissionDetail) {

    List<SubNavigationItem> listOfSubNavItems = new ArrayList<>();
    sections.stream()
        .map(tlss -> tlss.getSection(submissionDetail))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(TaskListSection::displayOrder))
        .forEach(section ->
          section.items()
              .stream()
              .filter(item -> item.showOnSubNav() == ShowOnSubNav.TRUE)
              .forEach(item -> listOfSubNavItems.add(new SubNavigationItem(item.displayName(),
                  getActionUrl(item))))
        );

    return listOfSubNavItems;
  }

  private String getActionUrl(TaskListItem item) {
    return "#" + item.displayName().replace(" ", "").toLowerCase();
  }
}
