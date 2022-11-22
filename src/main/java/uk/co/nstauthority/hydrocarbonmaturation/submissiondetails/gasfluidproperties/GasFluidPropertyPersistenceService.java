package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class GasFluidPropertyPersistenceService {

  private final GasFluidPropertyRepository gasFluidPropertyRepository;

  @Autowired
  GasFluidPropertyPersistenceService(GasFluidPropertyRepository gasFluidPropertyRepository) {
    this.gasFluidPropertyRepository = gasFluidPropertyRepository;
  }

  @Transactional
  public void saveGasFluidProperty(GasFluidPropertyForm gasFluidPropertyForm, SubmissionDetail submissionDetail) {
    var gasFluidProperty = getGasFluidProperty(submissionDetail).orElse(new GasFluidProperty());
    gasFluidProperty.setSubmissionDetail(submissionDetail);
    gasFluidProperty.setGasCo2(gasFluidPropertyForm.getGasCo2().createBigDecimalOrNull());
    gasFluidProperty.setGasH2s(gasFluidPropertyForm.getGasH2s().createBigDecimalOrNull());
    gasFluidProperty.setGasSpecificGravity(gasFluidPropertyForm.getGasSpecificGravity().createBigDecimalOrNull());
    gasFluidProperty.setGasN2(gasFluidPropertyForm.getGasN2().createBigDecimalOrNull());
    gasFluidProperty.setCondensatePresent(gasFluidPropertyForm.getCondensatePresent());
    gasFluidProperty.setAdviseOtherGasesConcentration(gasFluidPropertyForm.getAdviseOtherGasesConcentration());

    if (BooleanUtils.isTrue(gasFluidPropertyForm.getCondensatePresent())) {
      gasFluidProperty.setCondensateGravity(gasFluidPropertyForm.getCondensateGravity().createBigDecimalOrNull());
      gasFluidProperty.setCondensateViscosity(gasFluidPropertyForm.getCondensateViscosity().createBigDecimalOrNull());
      gasFluidProperty.setCondensateSulphur(gasFluidPropertyForm.getCondensateSulphur().createBigDecimalOrNull());
      gasFluidProperty.setCondensateWax(gasFluidPropertyForm.getCondensateWax().createBigDecimalOrNull());
      gasFluidProperty.setCondensateOtherInclWax(
          gasFluidPropertyForm.getCondensateOtherInclWax().createBigDecimalOrNull());
    }
    gasFluidPropertyRepository.save(gasFluidProperty);
  }

  Optional<GasFluidProperty> getGasFluidProperty(SubmissionDetail submissionDetail) {
    return gasFluidPropertyRepository.findBySubmissionDetail(submissionDetail);
  }
}
