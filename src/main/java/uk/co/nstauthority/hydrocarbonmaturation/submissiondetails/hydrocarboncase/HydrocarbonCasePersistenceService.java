package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class HydrocarbonCasePersistenceService {
  private final HydrocarbonCaseRepository hydrocarbonCaseRepository;

  @Autowired
  public HydrocarbonCasePersistenceService(HydrocarbonCaseRepository hydrocarbonCaseRepository) {
    this.hydrocarbonCaseRepository = hydrocarbonCaseRepository;
  }

  @Transactional
  public void saveHydrocarbonCase(HydrocarbonCaseForm form, SubmissionDetail submissionDetail) {
    var hydrocarbonCase = getHydrocarbonCaseDetail(submissionDetail).orElse(new HydrocarbonCaseDetail());
    hydrocarbonCase.setSubmissionDetail(submissionDetail);
    hydrocarbonCase.setOilOnly(form.getOilOnlyCaseType());
    hydrocarbonCase.setGasOnly(form.getGasOnlyCaseType());
    hydrocarbonCase.setOilAndGas(form.getOilAndGasCaseType());

    if (form.getOilOnlyCaseType() != null && form.getOilOnlyCaseType()) {
      hydrocarbonCase.setOilOnlyComments(form.getOilOnlyComments());
      hydrocarbonCase.setOilOnlyProbability(form.getOilOnlyProbability().createBigDecimalOrNull());
    } else {
      hydrocarbonCase.setOilOnlyComments(null);
      hydrocarbonCase.setOilOnlyProbability(null);
    }

    if (form.getGasOnlyCaseType() != null && form.getGasOnlyCaseType()) {
      hydrocarbonCase.setGasOnlyComments(form.getGasOnlyComments());
      hydrocarbonCase.setGasOnlyProbability(form.getGasOnlyProbability().createBigDecimalOrNull());
    } else {
      hydrocarbonCase.setGasOnlyComments(null);
      hydrocarbonCase.setGasOnlyProbability(null);
    }

    if (form.getOilAndGasCaseType() != null && form.getOilAndGasCaseType()) {
      hydrocarbonCase.setOilAndGasComments(form.getOilAndGasComments());
      hydrocarbonCase.setOilAndGasProbability(form.getOilAndGasProbability().createBigDecimalOrNull());
    } else {
      hydrocarbonCase.setOilAndGasComments(null);
      hydrocarbonCase.setOilAndGasProbability(null);
    }

    hydrocarbonCaseRepository.save(hydrocarbonCase);
  }

  Optional<HydrocarbonCaseDetail> getHydrocarbonCaseDetail(SubmissionDetail submissionDetail) {
    return hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail);
  }

  //Checks if there is gas present in the hydrocarbon case
  public boolean isGasPresent(SubmissionDetail submissionDetail) {
    return getHydrocarbonCaseDetail(submissionDetail)
        .stream().anyMatch(hydrocarbonCaseDetail ->
            (hydrocarbonCaseDetail.getGasOnly() != null
                && hydrocarbonCaseDetail.getGasOnly()
                && hydrocarbonCaseDetail.getGasOnlyProbability().compareTo(BigDecimal.ZERO) > 0)
                || (hydrocarbonCaseDetail.getOilAndGas() != null
                && hydrocarbonCaseDetail.getOilAndGas()
                && hydrocarbonCaseDetail.getOilAndGasProbability().compareTo(BigDecimal.ZERO) > 0));
  }
}
