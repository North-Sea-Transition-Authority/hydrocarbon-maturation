package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class OilCaseOutputsSubmissionService implements ReservoirSectionSubmissionService {

  private final OilCaseOutputsFormService oilCaseOutputsFormService;
  private final OilCaseOutputsFormValidator oilCaseOutputsFormValidator;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public OilCaseOutputsSubmissionService(OilCaseOutputsFormService oilCaseOutputsFormService,
                                         OilCaseOutputsFormValidator oilCaseOutputsFormValidator,
                                         GeneralReservoirInformationService generalReservoirInformationService) {
    this.oilCaseOutputsFormService = oilCaseOutputsFormService;
    this.oilCaseOutputsFormValidator = oilCaseOutputsFormValidator;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = oilCaseOutputsFormService.getOilCaseOutputsForm(reservoirSubmissionDetail);
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    if (!submissionDetail.isProspectSubmission() && !generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)) {
      return true;
    }

    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return oilCaseOutputsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
