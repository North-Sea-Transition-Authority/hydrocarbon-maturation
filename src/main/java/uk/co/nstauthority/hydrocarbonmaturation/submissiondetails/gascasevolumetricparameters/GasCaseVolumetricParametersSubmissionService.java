package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetricparameters.FieldGasVolumetricParametersService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GasCaseVolumetricParametersSubmissionService implements ReservoirSectionSubmissionService {

  private final GasCaseVolumetricParametersFormService gasCaseVolumetricParametersFormService;
  private final GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator;
  private final FieldGasVolumetricParametersService fieldGasVolumetricParametersService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public GasCaseVolumetricParametersSubmissionService(
      GasCaseVolumetricParametersFormService gasCaseVolumetricParametersFormService,
      GasCaseVolumetricParametersFormValidator gasCaseVolumetricParametersFormValidator,
      FieldGasVolumetricParametersService fieldGasVolumetricParametersService,
      GeneralReservoirInformationService generalReservoirInformationService) {
    this.gasCaseVolumetricParametersFormService = gasCaseVolumetricParametersFormService;
    this.gasCaseVolumetricParametersFormValidator = gasCaseVolumetricParametersFormValidator;
    this.fieldGasVolumetricParametersService = fieldGasVolumetricParametersService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    GasCaseVolumetricParametersForm form;
    if (reservoirSubmissionDetail.getSubmissionDetail().getSubmission().getResource().getType() == ResourceType.FIELD) {
      form = fieldGasVolumetricParametersService.getGasVolumetricsForm(reservoirSubmissionDetail);
    } else {
      form = gasCaseVolumetricParametersFormService.getGasCaseVolumetricsForm(reservoirSubmissionDetail);
    }

    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    if (!submissionDetail.isProspectSubmission() && generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)) {
      return true;
    }

    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return gasCaseVolumetricParametersFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
