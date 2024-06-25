package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumincreasedvolumes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FieldDevelopmentPlanAddendumIncreasedVolumesSubmissionService implements SectionSubmissionService {

  private final FieldDevelopmentPlanAddendumIncreasedVolumesFormValidator fdpaIncreasedVolumesFormValidator;
  private final FieldDevelopmentPlanAddendumIncreasedVolumesService fdpaIncreasedVolumesService;

  @Autowired
  public FieldDevelopmentPlanAddendumIncreasedVolumesSubmissionService(
      FieldDevelopmentPlanAddendumIncreasedVolumesFormValidator fdpaIncreasedVolumesFormValidator,
      FieldDevelopmentPlanAddendumIncreasedVolumesService fdpaIncreasedVolumesService) {
    this.fdpaIncreasedVolumesFormValidator = fdpaIncreasedVolumesFormValidator;
    this.fdpaIncreasedVolumesService = fdpaIncreasedVolumesService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = fdpaIncreasedVolumesService.submissionDetailToForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    return fdpaIncreasedVolumesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
