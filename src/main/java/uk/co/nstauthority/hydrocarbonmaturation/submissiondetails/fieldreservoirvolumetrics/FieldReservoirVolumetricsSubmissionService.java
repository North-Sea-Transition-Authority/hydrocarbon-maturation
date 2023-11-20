package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FieldReservoirVolumetricsSubmissionService implements ReservoirSectionSubmissionService {

  private final FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;
  private final FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator;

  public FieldReservoirVolumetricsSubmissionService(
      FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService,
      FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator) {

    this.fieldReservoirVolumetricsFormService = fieldReservoirVolumetricsFormService;
    this.fieldReservoirVolumetricsFormValidator = fieldReservoirVolumetricsFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      return true;
    }
    var form = fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
  }
}
