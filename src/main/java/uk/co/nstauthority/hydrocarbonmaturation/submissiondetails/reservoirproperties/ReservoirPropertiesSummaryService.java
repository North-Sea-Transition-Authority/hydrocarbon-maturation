package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.ArrayList;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@Service
public class ReservoirPropertiesSummaryService {
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private ProbabilityDistributionSummaryService probabilityDistributionSummaryService;

  @Autowired
  public ReservoirPropertiesSummaryService(
      ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
      ProbabilityDistributionSummaryService probabilityDistributionSummaryService) {
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
  }

  public ReservoirPropertiesSummaryView getReservoirPropertiesSummaryFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    var reservoirPropertyOptional = reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail);
    return reservoirPropertyOptional.map(
            reservoirProperty -> createSummaryViewFromReservoirProperty(submissionDetail, reservoirProperty))
        .orElse(createSummaryViewFromReservoirProperty(submissionDetail, new ReservoirProperty()));
  }

  private ReservoirPropertiesSummaryView createSummaryViewFromReservoirProperty(SubmissionDetail submissionDetail,
                                                                                ReservoirProperty reservoirProperty) {
    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    ProbabilityDistributionSummaryView averageNetPayThickness;
    if (submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      averageNetPayThickness = probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY,
              submissionDetail
      );
    } else {
      averageNetPayThickness = probabilityDistributionSummaryService
          .getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
              submissionDetail
      );
    }
    listOfSummaryViews.add(averageNetPayThickness);

    var grossReservoirThickness =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(grossReservoirThickness);

    var grossRockVolume =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME,
            submissionDetail);
    listOfSummaryViews.add(grossRockVolume);

    var percentOfGrvOilBearing =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING,
            submissionDetail);
    listOfSummaryViews.add(percentOfGrvOilBearing);

    var netToGross =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(netToGross);

    var netPorosity =
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY,
            submissionDetail);
    listOfSummaryViews.add(netPorosity);

    if (submissionDetail.getSubmissionType() == SubmissionType.CREATE_PROSPECT
        || submissionDetail.getSubmissionType() == SubmissionType.PROSPECT_UPDATE) {
      var depthToHydrocarbonContact =
          probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
              ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT,
              submissionDetail);
      listOfSummaryViews.add(depthToHydrocarbonContact);
    }

    return new ReservoirPropertiesSummaryView(
        submissionDetail.getSubmissionType(),
        Objects.requireNonNullElse(reservoirProperty.getFormationName(), ""),
        Objects.requireNonNullElse(reservoirProperty.getAgeStage(), ""),
        Objects.requireNonNullElse(reservoirProperty.getLithology(), ""),
        Objects.requireNonNullElse(reservoirProperty.getReservoirFacies(), ""),
        Objects.requireNonNullElse(reservoirProperty.getReservoirConnectivity(), ""),
        listOfSummaryViews
    );
  }
}
