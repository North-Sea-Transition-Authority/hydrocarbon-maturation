package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetrics.FieldGasVolumetricsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasCaseVolumetricsSubmissionService implements ReservoirSectionSubmissionService {

  private final GasCaseVolumetricsFormService gasCaseVolumetricsFormService;
  private final GasCaseVolumetricsFormValidator gasCaseVolumetricsFormValidator;
  private final FieldGasVolumetricsService fieldGasVolumetricsService;

  @Autowired
  public GasCaseVolumetricsSubmissionService(GasCaseVolumetricsFormService gasCaseVolumetricsFormService,
                                             GasCaseVolumetricsFormValidator gasCaseVolumetricsFormValidator,
                                             FieldGasVolumetricsService fieldGasVolumetricsService) {
    this.gasCaseVolumetricsFormService = gasCaseVolumetricsFormService;
    this.gasCaseVolumetricsFormValidator = gasCaseVolumetricsFormValidator;
    this.fieldGasVolumetricsService = fieldGasVolumetricsService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    GasCaseVolumetricsForm form;
    if (reservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType() == ResourceType.FIELD) {
      form = fieldGasVolumetricsService.getGasVolumetricsForm(reservoirSubmissionDetail);
    } else {
      form = gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail);
    }
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return gasCaseVolumetricsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
