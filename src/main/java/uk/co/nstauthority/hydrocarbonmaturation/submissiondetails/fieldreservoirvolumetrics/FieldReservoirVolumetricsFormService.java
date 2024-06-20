package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class FieldReservoirVolumetricsFormService {
  static final List<SubmissionType> INITIALLY_IN_PLACE_QUESTION_SUBMISSION_TYPES = List.of(SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_FDPA, SubmissionType.CREATE_COP);

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final FieldReservoirVolumetricsRepository fieldReservoirVolumetricsRepository;

  @Autowired
  public FieldReservoirVolumetricsFormService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      GeneralReservoirInformationService generalReservoirInformationService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      FieldReservoirVolumetricsRepository fieldReservoirVolumetricsRepository) {

    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.fieldReservoirVolumetricsRepository = fieldReservoirVolumetricsRepository;
  }

  public FieldReservoirVolumetricsForm getFieldReservoirVolumetricsForm(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new FieldReservoirVolumetricsForm();
    var reservoirHydrocarbons = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());

    if (generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
      ));
    }

    if (generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
      ));
    }

    if (BooleanUtils.isTrue(includeInitiallyInPlaceQuestion(reservoirSubmissionDetail))) {
      var fieldReservoirVolumetrics = getFieldReservoirVolumetrics(reservoirSubmissionDetail);
      form.setInitiallyInPlaceInformationCorrect(fieldReservoirVolumetrics.getInitiallyInPlaceCorrect());
    }

    return form;
  }

  @Transactional
  public void saveFieldReservoirVolumetrics(FieldReservoirVolumetricsForm form,
                                            ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirHydrocarbons = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .orElse(new ReservoirHydrocarbons());

    if (generalReservoirInformationService.isOilOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
          form.getOilInPlace(),
          reservoirSubmissionDetail
      );
    }

    if (generalReservoirInformationService.isGasOnly(reservoirHydrocarbons)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirHydrocarbons)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
          form.getGasInPlace(),
          reservoirSubmissionDetail
      );
    }

    if (BooleanUtils.isTrue(includeInitiallyInPlaceQuestion(reservoirSubmissionDetail))) {
      var fieldReservoirVolumetrics = getFieldReservoirVolumetrics(reservoirSubmissionDetail);
      fieldReservoirVolumetrics.setInitiallyInPlaceCorrect(form.getInitiallyInPlaceInformationCorrect());
      fieldReservoirVolumetrics.setReservoirSubmissionDetail(reservoirSubmissionDetail);
      fieldReservoirVolumetricsRepository.save(fieldReservoirVolumetrics);
    }
  }

  private boolean includeInitiallyInPlaceQuestion(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirWasCarriedForward = reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(
        reservoirSubmissionDetail.getReservoir()
    );
    var submissionType = reservoirSubmissionDetail.getSubmissionDetail().getSubmissionType();

    return reservoirWasCarriedForward && INITIALLY_IN_PLACE_QUESTION_SUBMISSION_TYPES.contains(submissionType);
  }

  private FieldReservoirVolumetrics getFieldReservoirVolumetrics(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return fieldReservoirVolumetricsRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail)
        .orElse(new FieldReservoirVolumetrics());
  }
}
