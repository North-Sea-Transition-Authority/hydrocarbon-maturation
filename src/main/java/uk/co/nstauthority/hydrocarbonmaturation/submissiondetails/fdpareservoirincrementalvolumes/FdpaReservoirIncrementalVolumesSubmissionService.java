package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FdpaReservoirIncrementalVolumesSubmissionService
    implements ReservoirSectionSubmissionService {

  private final FdpaReservoirIncrementalVolumesService reservoirIncrementalVolumesService;
  private final FdpaReservoirIncrementalVolumesFormValidator formValidator;

  @Autowired
  public FdpaReservoirIncrementalVolumesSubmissionService(
      FdpaReservoirIncrementalVolumesService reservoirIncrementalVolumesService,
      FdpaReservoirIncrementalVolumesFormValidator formValidator) {
    this.reservoirIncrementalVolumesService = reservoirIncrementalVolumesService;
    this.formValidator = formValidator;
  }

  @Override
  public boolean isSectionSubmittable(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (!reservoirSubmissionDetail.getSubmissionDetail().isFieldDevelopmentPlanAddendumSubmission()) {
      return true;
    }
    var form = reservoirIncrementalVolumesService.submissionDetailToForm(reservoirSubmissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    return formValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
  }
}
