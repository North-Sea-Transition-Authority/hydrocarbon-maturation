package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasCaseOutputsSubmissionService implements ReservoirSectionSubmissionService {

  private final GasCaseOutputsFormService gasCaseOutputsFormService;
  private final GasCaseOutputsFormValidator gasCaseOutputsFormValidator;

  @Autowired
  public GasCaseOutputsSubmissionService(GasCaseOutputsFormService gasCaseOutputsFormService,
                                         GasCaseOutputsFormValidator gasCaseOutputsFormValidator) {
    this.gasCaseOutputsFormService = gasCaseOutputsFormService;
    this.gasCaseOutputsFormValidator = gasCaseOutputsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = gasCaseOutputsFormService.getGasCaseOutputsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return gasCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
