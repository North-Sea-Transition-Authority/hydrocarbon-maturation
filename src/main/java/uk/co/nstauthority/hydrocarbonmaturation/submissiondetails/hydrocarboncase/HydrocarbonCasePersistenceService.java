package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class HydrocarbonCasePersistenceService {
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
    hydrocarbonCase.setOilOnlyProbability(form.getOilOnlyProbability().createBigDecimalOrNull());
    hydrocarbonCase.setGasOnlyProbability(form.getGasOnlyProbability().createBigDecimalOrNull());
    hydrocarbonCase.setOilAndGasProbability(form.getOilAndGasProbability().createBigDecimalOrNull());
    hydrocarbonCase.setOilOnlyComments(form.getOilOnlyComments());
    hydrocarbonCase.setGasOnlyComments(form.getGasOnlyComments());
    hydrocarbonCase.setOilAndGasComments(form.getOilAndGasComments());

    hydrocarbonCaseRepository.save(hydrocarbonCase);
  }

  Optional<HydrocarbonCaseDetail> getHydrocarbonCaseDetail(SubmissionDetail submissionDetail) {
    return hydrocarbonCaseRepository.findBySubmissionDetail(submissionDetail);
  }
}
