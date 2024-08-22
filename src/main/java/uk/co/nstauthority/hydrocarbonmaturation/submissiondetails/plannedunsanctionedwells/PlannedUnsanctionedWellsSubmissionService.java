package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class PlannedUnsanctionedWellsSubmissionService implements SectionSubmissionService {

  private final PlannedUnsanctionedWellsFormValidator plannedUnsanctionedWellsFormValidator;
  private final PlannedUnsanctionedWellsService plannedUnsanctionedWellsService;

  @Autowired
  public PlannedUnsanctionedWellsSubmissionService(PlannedUnsanctionedWellsFormValidator plannedUnsanctionedWellsFormValidator,
                                                   PlannedUnsanctionedWellsService plannedUnsanctionedWellsService) {
    this.plannedUnsanctionedWellsFormValidator = plannedUnsanctionedWellsFormValidator;
    this.plannedUnsanctionedWellsService = plannedUnsanctionedWellsService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail, User user) {
    var isNotFdpOrFdpa = !(SubmissionType.CREATE_FDP.equals(submissionDetail.getSubmissionType())
        || SubmissionType.CREATE_FDPA.equals(submissionDetail.getSubmissionType()));
    if (isNotFdpOrFdpa) {
      return true;
    }

    var form = plannedUnsanctionedWellsService.submissionDetailToForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    return plannedUnsanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
