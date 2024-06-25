package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class FdpaPromotedVolumesSummaryService {

  static final String GENERAL_INFORMATION_TITLE = "Promoted resources information";
  public static final String VOLUMETRICS_TITLE = "Promoted resources volumetrics";
  public static final String HAS_PROMOTED_RESOURCES = "Are any resources being promoted to reserves as a result of this FDPA?";
  public static final String REASON_PROMOTED_RESOURCES = "Reason resources are being promoted to reserves";

  private final FdpaPromotedVolumesRepository fdpaPromotedVolumesRepository;
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public FdpaPromotedVolumesSummaryService(
      FdpaPromotedVolumesRepository fdpaPromotedVolumesRepository,
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.fdpaPromotedVolumesRepository = fdpaPromotedVolumesRepository;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  public SummaryCard getGeneralInformationSummaryCard(SubmissionDetail submissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());

    fdpaPromotedVolumesRepository.findBySubmissionDetail(submissionDetail).ifPresentOrElse(
        promotedVolume -> {
          summaryDataView.addKeyValue(HAS_PROMOTED_RESOURCES,
              BooleanUtil.yesNoFromBoolean(promotedVolume.getResourcesAreBeingPromoted()));
          summaryDataView.addKeyValue(REASON_PROMOTED_RESOURCES, promotedVolume.getComment());
        },
        () -> {
          summaryDataView.addKeyValue(HAS_PROMOTED_RESOURCES, "");
          summaryDataView.addKeyValue(REASON_PROMOTED_RESOURCES, "");
        });

    return SummaryCard.simpleSummaryCardWithHeading(GENERAL_INFORMATION_TITLE, summaryDataView);
  }

  public SummaryCard getPromotedResourcesVolumetricsCard(SubmissionDetail submissionDetail) {
    ArrayList<ProbabilityDistributionFieldInstance> listOfInstances = new ArrayList<>();

    if (BooleanUtils.isTrue(isOilCase(submissionDetail))) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA);
    }

    listOfInstances.addAll(List.of(
        ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA,
        ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA
    ));

    var listOfSummaryViews =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewsFromFieldInstancesAndSubmission(
            listOfInstances, submissionDetail, null
        );

    return new SummaryCard(
        VOLUMETRICS_TITLE,
        SummaryCardType.ONE_P_TO_THREE_P_DISTRIBUTION_SUMMARY,
        listOfSummaryViews
    );
  }

  private Boolean isOilCase(SubmissionDetail submissionDetail) {
    return reservoirHydrocarbonCaseService.isOilCaseOnly(submissionDetail);
  }
}
