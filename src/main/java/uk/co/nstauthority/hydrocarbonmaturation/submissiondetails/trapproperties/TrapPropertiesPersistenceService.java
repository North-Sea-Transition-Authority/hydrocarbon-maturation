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
    var trapProperty = getTrapProperty(submissionDetail).orElse(new TrapProperty());
    trapProperty.setSubmissionDetail(submissionDetail);
    trapProperty.setTrapDescription(form.getTrapDescription());
    trapProperty.setTrapType(form.getTrapType());
    trapProperty.setLatitudeDegrees(form.getLatitudeCoordinates().getDegrees().createBigDecimalOrNull());
    trapProperty.setLatitudeMinutes(form.getLatitudeCoordinates().getMinutes().createBigDecimalOrNull());
    trapProperty.setLatitudeSeconds(form.getLatitudeCoordinates().getSeconds().createBigDecimalOrNull());
    trapProperty.setLongitudeDegrees(form.getLongitudeCoordinates().getDegrees().createBigDecimalOrNull());
    trapProperty.setLongitudeMinutes(form.getLongitudeCoordinates().getMinutes().createBigDecimalOrNull());
    trapProperty.setLongitudeSeconds(form.getLongitudeCoordinates().getSeconds().createBigDecimalOrNull());
    trapProperty.setLongitudeHemisphere(form.getLongitudeHemisphere());

    if (form.getTrapType() == TrapType.OTHER) {
      trapProperty.setOtherTrapType(form.getOtherTrapType());
    } else {
      trapProperty.setOtherTrapType(null);
    }

    trapPropertiesRepository.save(trapProperty);
  }

  Optional<TrapProperty> getTrapProperty(SubmissionDetail submissionDetail) {
    return trapPropertiesRepository.findBySubmissionDetail(submissionDetail);
  }
}
