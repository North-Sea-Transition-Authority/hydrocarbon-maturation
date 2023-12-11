package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class PlannedSanctionedWellsSubmissionService implements SectionSubmissionService {

  private final PlannedSanctionedWellsFormValidator plannedSanctionedWellsFormValidator;
  private final PlannedSanctionedWellsFormService plannedSanctionedWellsFormService;

  @Autowired
  public PlannedSanctionedWellsSubmissionService(PlannedSanctionedWellsFormValidator plannedSanctionedWellsFormValidator,
                                                 PlannedSanctionedWellsFormService plannedSanctionedWellsFormService) {
    this.plannedSanctionedWellsFormValidator = plannedSanctionedWellsFormValidator;
    this.plannedSanctionedWellsFormService = plannedSanctionedWellsFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    // must be FDP or FDPA
    if (!(submissionDetail.isFieldDevelopmentPlanSubmission()
        || submissionDetail.isFieldDevelopmentPlanAddendumSubmission())) {
      return true;
    }
    var form = plannedSanctionedWellsFormService.submissionDetailToForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    return plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
