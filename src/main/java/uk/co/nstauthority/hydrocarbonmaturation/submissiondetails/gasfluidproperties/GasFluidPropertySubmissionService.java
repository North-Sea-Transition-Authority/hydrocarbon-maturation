package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasFluidPropertySubmissionService implements ReservoirSectionSubmissionService {

  private final GasFluidPropertyFormService gasFluidPropertyFormService;

  private final GasFluidPropertyFormValidator gasFluidPropertyFormValidator;

  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public GasFluidPropertySubmissionService(GasFluidPropertyFormService gasFluidPropertyFormService,
                                           GasFluidPropertyFormValidator gasFluidPropertyFormValidator,
                                           GeneralReservoirInformationService generalReservoirInformationService) {
    this.gasFluidPropertyFormService = gasFluidPropertyFormService;
    this.gasFluidPropertyFormValidator = gasFluidPropertyFormValidator;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      var form = gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail);
      BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
      return gasFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
    }
    return true;
  }
}
