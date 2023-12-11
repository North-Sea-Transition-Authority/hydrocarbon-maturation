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
  private final PlannedSanctionedWellsService plannedSanctionedWellsService;

  @Autowired
  public PlannedSanctionedWellsSubmissionService(PlannedSanctionedWellsFormValidator plannedSanctionedWellsFormValidator,
                                                 PlannedSanctionedWellsService plannedSanctionedWellsService) {
    this.plannedSanctionedWellsFormValidator = plannedSanctionedWellsFormValidator;
    this.plannedSanctionedWellsService = plannedSanctionedWellsService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    // must be FDP or FDPA
    if (!(submissionDetail.isFieldDevelopmentPlanSubmission()
        || submissionDetail.isFieldDevelopmentPlanAddendumSubmission())) {
      return true;
    }
    var form = plannedSanctionedWellsService.submissionDetailToForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    return plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
