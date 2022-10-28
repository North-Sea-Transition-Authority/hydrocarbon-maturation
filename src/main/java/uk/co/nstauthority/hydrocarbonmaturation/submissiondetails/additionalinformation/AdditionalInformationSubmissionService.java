package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.submission.ProspectSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class AdditionalInformationSubmissionService implements ProspectSectionSubmissionService {

  private final AdditionalInformationFormService additionalInformationFormService;

  @Autowired
  public AdditionalInformationSubmissionService(AdditionalInformationFormService additionalInformationFormService) {
    this.additionalInformationFormService = additionalInformationFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = additionalInformationFormService.getAdditionalInformationForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = additionalInformationFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
