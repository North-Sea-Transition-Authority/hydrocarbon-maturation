package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListSection;

@Service
class ProspectSummaryService {

  private final GeneralDetailSummaryService generalDetailSummaryService;
  private final SourceAndSealSummaryService sourceAndSealSummaryService;
  private final ChanceOfSuccessSummaryService chanceOfSuccessSummaryService;
  private final HydrocarbonCaseSummaryService hydrocarbonCaseSummaryService;
  private final ReservoirPropertiesSummaryService reservoirPropertiesSummaryService;
  private final MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService;
  private final MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService;
  private final OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService;
  private final OilCaseOutputsSummaryService oilCaseOutputsSummaryService;
  private final GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService;
  private final GasCaseOutputsSummaryService gasCaseOutputsSummaryService;
  private final List<ProspectTaskListSection> prospectTaskListSections;
  private final List<ProspectTaskListItem> prospectTaskListItems;
  private List<SubNavigationSectionItem> subNavigationSectionItems;

  @Autowired
  public ProspectSummaryService(GeneralDetailSummaryService generalDetailSummaryService,
                                SourceAndSealSummaryService sourceAndSealSummaryService,
                                ChanceOfSuccessSummaryService chanceOfSuccessSummaryService,
                                HydrocarbonCaseSummaryService hydrocarbonCaseSummaryService,
                                ReservoirPropertiesSummaryService reservoirPropertiesSummaryService,
                                OilCaseVolumetricsSummaryService oilCaseVolumetricsSummaryService,
                                OilCaseOutputsSummaryService oilCaseOutputsSummaryService,
                                GasCaseVolumetricsSummaryService gasCaseVolumetricsSummaryService,
                                GasCaseOutputsSummaryService gasCaseOutputsSummaryService,
                                MixedCaseVolumetricsSummaryService mixedCaseVolumetricsSummaryService,
                                MixedCaseOutputsSummaryService mixedCaseOutputsSummaryService,
                                List<ProspectTaskListSection> prospectTaskListSections,
                                List<ProspectTaskListItem> prospectTaskListItems) {
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
    this.prospectTaskListSections = prospectTaskListSections;
    this.prospectTaskListItems = prospectTaskListItems;
    this.subNavigationSectionItems = new ArrayList<>();
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public SourceAndSealSummaryView getSourceAndSealSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return sourceAndSealSummaryService.getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  public ChanceOfSuccessSummaryView getChanceOfSuccessSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return chanceOfSuccessSummaryService.getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
  }

  public HydrocarbonCaseSummaryView getHydrocarbonCaseSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return hydrocarbonCaseSummaryService.getHydrocarbonSummaryFromSubmissionDetail(submissionDetail);
  }

  public ReservoirPropertiesSummaryView getReservoirPropertiesSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
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

  public List<SubNavigationSectionItem> getProspectSubNavigation(SubmissionDetail submissionDetail) {
    subNavigationSectionItems = new ArrayList<>();
    return getProspectSubNavigationItems(prospectTaskListItems, prospectTaskListSections, submissionDetail);
  }

  private List<SubNavigationSectionItem> getProspectSubNavigationItems(
      List<ProspectTaskListItem> items,
      List<ProspectTaskListSection> sections,
      SubmissionDetail submissionDetail) {
    var taskListType = new ProspectTaskListItemType(submissionDetail);

    sections
        .stream()
        .filter(item -> !item.getClass().equals(ReviewAndSubmitTaskListSection.class))
        .map(SubNavigationSectionItem::new)
        .sorted(Comparator.comparing(SubNavigationSectionItem::getOrder))
        .forEach(subNavigationSectionItem -> {
          items
              .stream()
              .filter(taskListItem ->
                  isTaskListForSectionAndVisible(taskListType, taskListItem, subNavigationSectionItem.getSection()))
              .forEach(subNavigationSectionItem::addTaskListItem);
          subNavigationSectionItems.add(subNavigationSectionItem);
        });

    return subNavigationSectionItems;
  }

  private boolean isTaskListForSectionAndVisible(ProspectTaskListItemType taskListItemType,
                                                 ProspectTaskListItem prospectTaskListItem,
                                                 ProspectTaskListSection section) {
    return taskListItemType != null
        && prospectTaskListItem.isVisible(taskListItemType)
        && prospectTaskListItem.getTaskListSection().equals(section.getClass());
  }
}
