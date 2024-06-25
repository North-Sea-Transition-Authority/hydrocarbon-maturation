package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class FdpaPromotedVolumesSubmissionService implements SectionSubmissionService {

  private final FdpaPromotedVolumesFormValidator fdpaPromotedVolumesFormValidator;
  private final FdpaPromotedVolumesService fdpaPromotedVolumesService;

  @Autowired
  public FdpaPromotedVolumesSubmissionService(
      FdpaPromotedVolumesFormValidator fdpaPromotedVolumesFormValidator,
      FdpaPromotedVolumesService fdpaPromotedVolumesService) {
    this.fdpaPromotedVolumesFormValidator = fdpaPromotedVolumesFormValidator;
    this.fdpaPromotedVolumesService = fdpaPromotedVolumesService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = fdpaPromotedVolumesService.submissionDetailToForm(submissionDetail);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    return fdpaPromotedVolumesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
  }
}
