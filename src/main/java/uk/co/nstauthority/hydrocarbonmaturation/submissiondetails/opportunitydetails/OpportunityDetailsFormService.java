package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class OpportunityDetailsFormService implements SubmissionFormService {

  private final OpportunityDetailsFormValidator opportunityDetailsFormValidator;
  private final OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  @Autowired
  public OpportunityDetailsFormService(OpportunityDetailsFormValidator opportunityDetailsFormValidator,
                                       OpportunityDetailsPersistenceService opportunityDetailsPersistenceService) {
    this.opportunityDetailsFormValidator = opportunityDetailsFormValidator;
    this.opportunityDetailsPersistenceService = opportunityDetailsPersistenceService;
  }

  OpportunityDetailsForm getOpportunityDetailsForm(SubmissionDetail submissionDetail) {
    return opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail)
        .map(this::opportunityDetailEntityToForm)
        .orElse(new OpportunityDetailsForm());
  }

  private OpportunityDetailsForm opportunityDetailEntityToForm(OpportunityDetail opportunityDetail) {
    var form = new OpportunityDetailsForm();
    form.setOpportunityComments(opportunityDetail.getOpportunityComments());
    form.setOpportunityType(opportunityDetail.getOpportunityType());
    form.setOtherTypeOfOpportunity(opportunityDetail.getOtherTypeOfOpportunity());
    form.setKeyEvaluationWorkCarriedOut(opportunityDetail.getKeyEvaluationWorkCarriedOut());
    form.setKeyWorkRequiredForFurtherMaturation(opportunityDetail.getKeyWorkRequiredForFurtherMaturation());
    return form;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    opportunityDetailsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }
}
