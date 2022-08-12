package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class TrapPropertiesPersistenceService {

  private final TrapPropertiesRepository trapPropertiesRepository;

  @Autowired
  public TrapPropertiesPersistenceService(TrapPropertiesRepository trapPropertiesRepository) {
    this.trapPropertiesRepository = trapPropertiesRepository;
  }

  @Transactional
  public void saveTrapProperties(TrapPropertiesForm form, SubmissionDetail submissionDetail) {
    var trapProperty = getTrapProperty(submissionDetail).orElse(new TrapProperty())
        .setSubmissionDetail(submissionDetail)
        .setTrapDescription(form.getTrapDescription())
        .setTrapType(form.getTrapType())
        .setOtherTrapType(form.getOtherTrapType())
        .setLatitudeDegrees(form.getLatitudeCoordinates().getDegrees().createBigDecimalOrNull())
        .setLatitudeMinutes(form.getLatitudeCoordinates().getMinutes().createBigDecimalOrNull())
        .setLatitudeSeconds(form.getLatitudeCoordinates().getSeconds().createBigDecimalOrNull())
        .setLongitudeDegrees(form.getLongitudeCoordinates().getDegrees().createBigDecimalOrNull())
        .setLongitudeMinutes(form.getLongitudeCoordinates().getMinutes().createBigDecimalOrNull())
        .setLongitudeSeconds(form.getLongitudeCoordinates().getSeconds().createBigDecimalOrNull())
        .setLongitudeHemisphere(form.getLongitudeHemisphere());

    trapPropertiesRepository.save(trapProperty);
  }

  Optional<TrapProperty> getTrapProperty(SubmissionDetail submissionDetail) {
    return trapPropertiesRepository.findBySubmissionDetail(submissionDetail);
  }
}
