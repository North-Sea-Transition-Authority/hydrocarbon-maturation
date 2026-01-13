package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.prospectmixedcaseoutputs.ProspectMixedCaseOutputsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class HydrocarbonCasePersistenceService {
  private final HydrocarbonCaseRepository hydrocarbonCaseRepository;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;
  private final GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService;
  private final ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService;

  @Autowired
  public HydrocarbonCasePersistenceService(
      HydrocarbonCaseRepository hydrocarbonCaseRepository,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService,
      GasCaseOutputsPersistenceService gasCaseOutputsPersistenceService,
      ProspectMixedCaseOutputsPersistenceService prospectMixedCaseOutputsPersistenceService) {
    this.hydrocarbonCaseRepository = hydrocarbonCaseRepository;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.oilCaseOutputsPersistenceService = oilCaseOutputsPersistenceService;
    this.gasCaseOutputsPersistenceService = gasCaseOutputsPersistenceService;
    this.prospectMixedCaseOutputsPersistenceService = prospectMixedCaseOutputsPersistenceService;
  }

  @Transactional
  public void saveHydrocarbonCase(HydrocarbonCaseForm form, SubmissionDetail submissionDetail) {
    var hydrocarbonCase = getHydrocarbonCaseDetail(submissionDetail).orElse(new HydrocarbonCaseDetail());
    hydrocarbonCase.setSubmissionDetail(submissionDetail);
    hydrocarbonCase.setOilOnly(form.getOilOnlyCaseType());
    hydrocarbonCase.setGasOnly(form.getGasOnlyCaseType());
    hydrocarbonCase.setOilAndGas(form.getOilAndGasCaseType());

    if (form.isOilOnlyCaseType()) {
      hydrocarbonCase.setOilOnlyComments(form.getOilOnlyComments());
      hydrocarbonCase.setOilOnlyProbability(form.getOilOnlyProbability().createBigDecimalOrNull());
    } else {
      hydrocarbonCase.setOilOnlyComments(null);
      hydrocarbonCase.setOilOnlyProbability(null);
    }

    if (form.isGasOnlyCaseType()) {
      hydrocarbonCase.setGasOnlyComments(form.getGasOnlyComments());
      hydrocarbonCase.setGasOnlyProbability(form.getGasOnlyProbability().createBigDecimalOrNull());
    } else {
      hydrocarbonCase.setGasOnlyComments(null);
      hydrocarbonCase.setGasOnlyProbability(null);
    }

    if (form.isOilAndGasCaseType()) {
      hydrocarbonCase.setOilAndGasComments(form.getOilAndGasComments());
      hydrocarbonCase.setOilAndGasProbability(form.getOilAndGasProbability().createBigDecimalOrNull());
    } else {
      hydrocarbonCase.setOilAndGasComments(null);
      hydrocarbonCase.setOilAndGasProbability(null);
    }

    hydrocarbonCaseRepository.save(hydrocarbonCase);
    cleanHydrocarbonCase(form, submissionDetail);
  }

  public Optional<HydrocarbonCaseDetail> getHydrocarbonCaseDetail(SubmissionDetail submissionDetail) {
    return hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail);
  }

  //Checks if there is gas present in the hydrocarbon case
  public boolean isGasOnlyPresent(HydrocarbonCaseDetail hydrocarbonCaseDetail) {
    return hydrocarbonCaseDetail.getGasOnly() != null && hydrocarbonCaseDetail.getGasOnly();
  }

  public boolean isOilOnlyPresent(HydrocarbonCaseDetail hydrocarbonCaseDetail) {
    return hydrocarbonCaseDetail.getOilOnly() != null && hydrocarbonCaseDetail.getOilOnly();
  }

  public boolean isMixedOilAndGasPresent(HydrocarbonCaseDetail hydrocarbonCaseDetail) {
    return hydrocarbonCaseDetail.getOilAndGas() != null && hydrocarbonCaseDetail.getOilAndGas();
  }

  public HydrocarbonsDiscovered getHydrocarbonsDiscoveredForReporting(SubmissionDetail submissionDetail) {
    var hydrocarbonCaseDetail = getHydrocarbonCaseDetail(submissionDetail);
    if (hydrocarbonCaseDetail.isPresent()) {
      var hydrocarbonCase = hydrocarbonCaseDetail.get();
      if (isMixedOilAndGasPresent(hydrocarbonCase)) {
        return HydrocarbonsDiscovered.MIXED_OIL_AND_GAS;
      } else if (isGasOnlyPresent(hydrocarbonCase) && !isOilOnlyPresent(hydrocarbonCase)) {
        return HydrocarbonsDiscovered.GAS_ONLY;
      } else if (isOilOnlyPresent(hydrocarbonCase) && !isGasOnlyPresent(hydrocarbonCase)) {
        return HydrocarbonsDiscovered.OIL_ONLY;
      } else if (isGasOnlyPresent(hydrocarbonCase)
          && isOilOnlyPresent(hydrocarbonCase)
          && hydrocarbonCase.getGasOnlyProbability().doubleValue() > hydrocarbonCase.getOilOnlyProbability().doubleValue()
      ) {
        return HydrocarbonsDiscovered.GAS_ONLY;
      } else if (isGasOnlyPresent(hydrocarbonCase)
          && isOilOnlyPresent(hydrocarbonCase)
          && hydrocarbonCase.getGasOnlyProbability().doubleValue() < hydrocarbonCase.getOilOnlyProbability().doubleValue()
      ) {
        return HydrocarbonsDiscovered.OIL_ONLY;
        // if both gas and oil are present and have the same probability, return null as cannot determine for reporting
      } else if (isGasOnlyPresent(hydrocarbonCase)
          && isOilOnlyPresent(hydrocarbonCase)
          && hydrocarbonCase.getGasOnlyProbability().doubleValue() == hydrocarbonCase.getOilOnlyProbability().doubleValue()
      ) {
        return null;
      }
    } else {
      return null;
    }
    return null;
  }

  private void cleanHydrocarbonCase(HydrocarbonCaseForm form, SubmissionDetail submissionDetail) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(
        submissionDetail
    );
    if (BooleanUtils.isNotTrue(form.getGasOnlyCaseType())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(
          List.of(
              ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_PROSPECT,
              ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
              ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
              ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
              ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_PROSPECT,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_PROSPECT,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
              ProbabilityDistributionFieldInstance.GAS_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
          ),
          reservoirSubmissionDetail
      );
      gasCaseOutputsPersistenceService.deleteGasCaseOutputForReservoirSubmissionDetail(reservoirSubmissionDetail);
    }

    if (BooleanUtils.isNotTrue(form.getOilOnlyCaseType())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(
          List.of(
              ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_PROSPECT,
              ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
              ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_PROSPECT,
              ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
              ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT,
              ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT,
              ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
              ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
          ),
          reservoirSubmissionDetail
      );
      oilCaseOutputsPersistenceService.deleteOilCaseOutputForReservoirSubmissionDetail(reservoirSubmissionDetail);
    }

    if (BooleanUtils.isNotTrue(form.getOilAndGasCaseType())) {
      probabilityDistributionPersistenceService.deleteProbabilityDistributions(
          List.of(
              ProbabilityDistributionFieldInstance.MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
              ProbabilityDistributionFieldInstance.MIXED_GAS_EXPANSION_FACTOR,
              ProbabilityDistributionFieldInstance.MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
              ProbabilityDistributionFieldInstance.MIXED_GAS_RECOVERY_FACTOR,
              ProbabilityDistributionFieldInstance.MIXED_OIL_RECOVERY_FACTOR_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_IN_PLACE_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_RECOVERABLE_RESOURCES_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_IN_PLACE_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_CASE_GAS_RECOVERABLE_RESOURCES_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_CASE_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_PROSPECT,
              ProbabilityDistributionFieldInstance.MIXED_CASE_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT
          ),
          reservoirSubmissionDetail
      );
      prospectMixedCaseOutputsPersistenceService.deleteMixedCaseOutputForReservoirSubmissionDetail(reservoirSubmissionDetail);
    }
  }
}
