package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

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
public class GasCaseVolumetricParametersSubmissionService implements ReservoirSectionSubmissionService {

  private final GasCaseVolumetricParametersFormService gasCaseVolumetricParametersFormService;
  private final GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator;
  private final FieldGasVolumetricsService fieldGasVolumetricsService;

  @Autowired
  public GasCaseVolumetricParametersSubmissionService(
      GasCaseVolumetricParametersFormService gasCaseVolumetricParametersFormService,
      GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator,
      FieldGasVolumetricsService fieldGasVolumetricsService) {
    this.gasCaseVolumetricParametersFormService = gasCaseVolumetricParametersFormService;
    this.gasCaseVolumetricParametersFormValidator = gasCaseVolumetricParametersFormValidator;
    this.fieldGasVolumetricsService = fieldGasVolumetricsService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    GasCaseVolumetricParametersForm form;
    if (reservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType() == ResourceType.FIELD) {
      form = fieldGasVolumetricsService.getGasVolumetricsForm(reservoirSubmissionDetail);
    } else {
      form = gasCaseVolumetricParametersFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail);
    }
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
