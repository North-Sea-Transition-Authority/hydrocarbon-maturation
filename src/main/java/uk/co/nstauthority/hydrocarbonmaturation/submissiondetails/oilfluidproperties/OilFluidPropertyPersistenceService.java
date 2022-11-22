package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class OilFluidPropertyPersistenceService {

  private final OilFluidPropertyRepository oilFluidPropertyRepository;

  @Autowired
  OilFluidPropertyPersistenceService(OilFluidPropertyRepository oilFluidPropertyRepository) {
    this.oilFluidPropertyRepository = oilFluidPropertyRepository;
  }

  @Transactional
  public void saveOilFluidProperty(OilFluidPropertyForm oilFluidPropertyForm, SubmissionDetail submissionDetail) {
    var oilFluidProperty = getOilFluidProperty(submissionDetail).orElse(new OilFluidProperty());
    oilFluidProperty.setSubmissionDetail(submissionDetail);
    oilFluidProperty.setOilGravity(oilFluidPropertyForm.getOilGravity().createIntegerOrNull());
    oilFluidProperty.setOilSulphur(oilFluidPropertyForm.getOilSulphur().createIntegerOrNull());
    oilFluidProperty.setOilViscosity(oilFluidPropertyForm.getOilViscosity().createBigDecimalOrNull());
    oilFluidProperty.setOilWax(oilFluidPropertyForm.getOilWax().createBigDecimalOrNull());
    oilFluidProperty.setOilOtherWaxAppearanceTemperature(
        oilFluidPropertyForm.getOilOtherWaxAppearanceTemperature().createIntegerOrNull());
    oilFluidPropertyRepository.save(oilFluidProperty);
  }

  Optional<OilFluidProperty> getOilFluidProperty(SubmissionDetail submissionDetail) {
    return oilFluidPropertyRepository.findBySubmissionDetail(submissionDetail);
  }
}
