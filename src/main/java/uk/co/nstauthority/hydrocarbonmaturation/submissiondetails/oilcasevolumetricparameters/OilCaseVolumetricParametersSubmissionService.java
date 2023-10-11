package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetricparameters.FieldOilVolumetricParametersService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilCaseVolumetricParametersSubmissionService implements ReservoirSectionSubmissionService {

  private final OilCaseVolumetricParametersFormService oilCaseVolumetricParametersFormService;
  private final FieldOilVolumetricParametersService fieldOilVolumetricParametersService;
  private final OilCaseVolumetricParametersFormValidator oilCaseVolumetricParametersFormValidator;

  @Autowired
  public OilCaseVolumetricParametersSubmissionService(
      OilCaseVolumetricParametersFormService oilCaseVolumetricParametersFormService,
      FieldOilVolumetricParametersService fieldOilVolumetricParametersService,
      OilCaseVolumetricParametersFormValidator oilCaseVolumetricParametersFormValidator) {
    this.oilCaseVolumetricParametersFormService = oilCaseVolumetricParametersFormService;
    this.fieldOilVolumetricParametersService = fieldOilVolumetricParametersService;
    this.oilCaseVolumetricParametersFormValidator = oilCaseVolumetricParametersFormValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    OilCaseVolumetricParametersForm form;
    if (reservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType() == ResourceType.FIELD) {
      form = fieldOilVolumetricParametersService.getOilVolumetricsForm(reservoirSubmissionDetail);
    } else {
      form = oilCaseVolumetricParametersFormService.getOilCaseVolumetricsForm(reservoirSubmissionDetail);
    }
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    return oilCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
