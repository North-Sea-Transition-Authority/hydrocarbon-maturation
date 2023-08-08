package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralReservoirInformationSubmissionService implements ReservoirSectionSubmissionService {

  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final GeneralReservoirInformationFormValidator generalReservoirInformationFormValidator;

  @Autowired
  public GeneralReservoirInformationSubmissionService(
      GeneralReservoirInformationService generalReservoirInformationService,
      GeneralReservoirInformationFormValidator generalReservoirInformationFormValidator) {
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.generalReservoirInformationFormValidator = generalReservoirInformationFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    return generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
  }
}
