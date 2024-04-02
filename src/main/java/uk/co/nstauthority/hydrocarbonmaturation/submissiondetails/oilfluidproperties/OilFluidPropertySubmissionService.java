package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilFluidPropertySubmissionService implements ReservoirSectionSubmissionService {

  private final OilFluidPropertyFormService oilFluidPropertyFormService;
  private final OilFluidPropertyFormValidator oilFluidPropertyFormValidator;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public OilFluidPropertySubmissionService(OilFluidPropertyFormService oilFluidPropertyFormService,
                                           OilFluidPropertyFormValidator oilFluidPropertyFormValidator,
                                           GeneralReservoirInformationService generalReservoirInformationService) {
    this.oilFluidPropertyFormService = oilFluidPropertyFormService;
    this.oilFluidPropertyFormValidator = oilFluidPropertyFormValidator;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    // section is not present on CoP so can always submit
    if (reservoirSubmissionDetail.getSubmissionDetail().isCessationOfProductionSubmission()) {
      return true;
    }

    if (generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)
        || generalReservoirInformationService.isCombinedOilAndGas(reservoirSubmissionDetail)) {
      var form = oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail);
      BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
      return oilFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
    }

    return true;
  }
}
