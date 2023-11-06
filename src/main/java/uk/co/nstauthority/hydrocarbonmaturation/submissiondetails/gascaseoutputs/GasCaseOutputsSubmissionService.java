package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasCaseOutputsSubmissionService implements ReservoirSectionSubmissionService {

  private final GasCaseOutputsFormService gasCaseOutputsFormService;
  private final GasCaseOutputsFormValidator gasCaseOutputsFormValidator;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public GasCaseOutputsSubmissionService(GasCaseOutputsFormService gasCaseOutputsFormService,
                                         GasCaseOutputsFormValidator gasCaseOutputsFormValidator,
                                         GeneralReservoirInformationService generalReservoirInformationService) {
    this.gasCaseOutputsFormService = gasCaseOutputsFormService;
    this.gasCaseOutputsFormValidator = gasCaseOutputsFormValidator;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = gasCaseOutputsFormService.getGasCaseOutputsForm(reservoirSubmissionDetail);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (!submissionDetail.isProspectSubmission() && !generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)) {
      return true;
    }
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
