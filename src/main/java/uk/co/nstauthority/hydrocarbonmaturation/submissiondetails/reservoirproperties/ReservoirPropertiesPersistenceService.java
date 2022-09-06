package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class ReservoirPropertiesPersistenceService {

  private final ReservoirPropertiesRepository reservoirPropertiesRepository;

  @Autowired
  public ReservoirPropertiesPersistenceService(ReservoirPropertiesRepository reservoirPropertiesRepository) {
    this.reservoirPropertiesRepository = reservoirPropertiesRepository;
  }

  @Transactional
  public void saveReservoirProperty(ReservoirPropertiesForm form, SubmissionDetail submissionDetail) {
    var reservoirProperty = getReservoirProperty(submissionDetail).orElse(new ReservoirProperty());
    reservoirProperty.setSubmissionDetail(submissionDetail);
    reservoirProperty.setFormationName(form.getFormationName());
    reservoirProperty.setAgeStage(form.getAgeStageOrSeries());
    reservoirProperty.setLithology(form.getLithology());

    reservoirPropertiesRepository.save(reservoirProperty);
  }

  Optional<ReservoirProperty> getReservoirProperty(SubmissionDetail submissionDetail) {
    return reservoirPropertiesRepository.findBySubmissionDetail(submissionDetail);
  }
}
