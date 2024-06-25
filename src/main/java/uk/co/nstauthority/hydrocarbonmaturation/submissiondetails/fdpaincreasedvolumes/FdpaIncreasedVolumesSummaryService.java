package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class FdpaIncreasedVolumesSummaryService {

  static final String GENERAL_INFORMATION_TITLE = "Incremental increase information";
  static final String CHANGE_IN_VOLUMETRICS = "Is there a change in volumetrics as a result of this FDPA?";
  static final String REASON_CHANGE_IN_VOLUMETRICS = "Comments on incremental increase";

  private final FdpaIncreasedVolumesRepository fdpaIncreasedVolumesRepository;
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public FdpaIncreasedVolumesSummaryService(
      FdpaIncreasedVolumesRepository fdpaIncreasedVolumesRepository,
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.fdpaIncreasedVolumesRepository = fdpaIncreasedVolumesRepository;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  public SummaryCard getGeneralInformationSummaryCard(SubmissionDetail submissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());

    fdpaIncreasedVolumesRepository.findBySubmissionDetail(submissionDetail).ifPresentOrElse(
        increasedVolume -> {
          summaryDataView.addKeyValue(CHANGE_IN_VOLUMETRICS,
              BooleanUtil.yesNoFromBoolean(increasedVolume.getChangeInVolumetrics()));
          summaryDataView.addKeyValue(REASON_CHANGE_IN_VOLUMETRICS, increasedVolume.getComment());
        },
        () -> {
          summaryDataView.addKeyValue(CHANGE_IN_VOLUMETRICS, "");
          summaryDataView.addKeyValue(REASON_CHANGE_IN_VOLUMETRICS, "");
        });

    return SummaryCard.simpleSummaryCardWithHeading(GENERAL_INFORMATION_TITLE, summaryDataView);
  }

  public List<SummaryCard> getVolumetricsSummaryCards(SubmissionDetail submissionDetail) {
    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail);

    return List.of(
        getProbabilitySummaryCard(submissionDetail, hydrocarbonsDiscovered),
        getFieldProbabilitySummaryCard(submissionDetail, hydrocarbonsDiscovered)
    );
  }

  private SummaryCard getProbabilitySummaryCard(SubmissionDetail submissionDetail,
                                                HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    // P90/50/10 summary card
    ArrayList<ProbabilityDistributionFieldInstance> listProbabilityInstances = new ArrayList<>();

    if (BooleanUtils.isTrue(isOilOrCombinedCase(hydrocarbonsDiscovered))) {
      listProbabilityInstances.add(ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA);
    }

    if (BooleanUtils.isTrue(isGasOrCombinedCase(hydrocarbonsDiscovered))) {
      listProbabilityInstances.add(ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA);
    }

    return SummaryCard.probabilitySummaryCard(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listProbabilityInstances, submissionDetail, null
        )
    );
  }

  private SummaryCard getFieldProbabilitySummaryCard(SubmissionDetail submissionDetail,
                                                     HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    // 1P / 1C - 3P / 3C summary card
    ArrayList<ProbabilityDistributionFieldInstance> listFieldProbabilityInstances = new ArrayList<>();

    if (BooleanUtils.isTrue(isOilOrCombinedCase(hydrocarbonsDiscovered))) {
      listFieldProbabilityInstances.add(
          ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
      listFieldProbabilityInstances.add(ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA);
    }

    listFieldProbabilityInstances.add(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
    listFieldProbabilityInstances.add(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA);
    listFieldProbabilityInstances.add(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
    listFieldProbabilityInstances.add(
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA);
    listFieldProbabilityInstances.add(
        ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
    listFieldProbabilityInstances.add(ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA);
    listFieldProbabilityInstances.add(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA);
    listFieldProbabilityInstances.add(
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA);

    return SummaryCard.fieldProbabilitySummaryCard(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listFieldProbabilityInstances, submissionDetail, null
        )
    );
  }

  private boolean isOilOrCombinedCase(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    return hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.OIL_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
  }

  private boolean isGasOrCombinedCase(HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    return hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.GAS_ONLY)
        || hydrocarbonsDiscovered.equals(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
  }
}
