package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.SectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class DrillStemTestSubmissionService implements SectionSubmissionService {

  private final DrillStemTestInitialFormService drillStemTestInitialFormService;

  @Autowired
  public DrillStemTestSubmissionService(DrillStemTestInitialFormService drillStemTestInitialFormService) {
    this.drillStemTestInitialFormService = drillStemTestInitialFormService;
  }

  @Override
  public boolean isSectionSubmittable(SubmissionDetail submissionDetail) {
    var form = drillStemTestInitialFormService.getDrillStemTestInitialForm(submissionDetail);
    BindingResult bindingResult = new BeanPropertyBindingResult(form, "form");
    bindingResult = drillStemTestInitialFormService.validate(form, bindingResult, ValidationType.FULL);
    return !bindingResult.hasErrors();
  }
}
