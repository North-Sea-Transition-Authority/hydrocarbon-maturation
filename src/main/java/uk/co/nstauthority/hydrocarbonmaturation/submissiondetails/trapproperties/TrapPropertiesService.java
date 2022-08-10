package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class TrapPropertiesService {

  private final TrapPropertiesFormValidator trapPropertiesFormValidator;
  private final TrapPropertiesRepository trapPropertiesRepository;

  @Autowired
  TrapPropertiesService(TrapPropertiesFormValidator trapPropertiesFormValidator,
                        TrapPropertiesRepository trapPropertiesRepository) {
    this.trapPropertiesFormValidator = trapPropertiesFormValidator;
    this.trapPropertiesRepository = trapPropertiesRepository;
  }

  BindingResult validate(TrapPropertiesForm form, BindingResult bindingResult) {
    trapPropertiesFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  @Transactional
  public void saveTrapProperties(TrapPropertiesForm form, SubmissionDetail submissionDetail) {
    var trapProperty = new TrapProperty()
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
}
