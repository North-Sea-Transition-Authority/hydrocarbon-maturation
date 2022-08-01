package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class OpportunityDetailsService {

  private final OpportunityDetailsFormValidator opportunityDetailsFormValidator;
  private final OpportunityDetailsRepository opportunityDetailsRepository;

  @Autowired
  public OpportunityDetailsService(OpportunityDetailsFormValidator opportunityDetailsFormValidator,
                                   OpportunityDetailsRepository opportunityDetailsRepository) {
    this.opportunityDetailsFormValidator = opportunityDetailsFormValidator;
    this.opportunityDetailsRepository = opportunityDetailsRepository;
  }

  BindingResult validate(OpportunityDetailsForm form, BindingResult bindingResult) {
    opportunityDetailsFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  @Transactional
  public void saveOpportunityDetails(OpportunityDetailsForm form, SubmissionDetail submissionDetail) {
    var opportunityDetail = new OpportunityDetail();
    opportunityDetail.setSubmissionDetail(submissionDetail);
    opportunityDetail.setOpportunityType(form.getOpportunityType());
    opportunityDetail.setOtherTypeOfOpportunity(form.getOtherTypeOfOpportunity());
    opportunityDetail.setOpportunityComments(form.getOpportunityComments());
    opportunityDetail.setKeyEvaluationWorkCarriedOut(form.getKeyEvaluationWorkCarriedOut());
    opportunityDetail.setKeyWorkRequiredForFurtherMaturation(form.getKeyWorkRequiredForFurtherMaturation());

    opportunityDetailsRepository.save(opportunityDetail);
  }
}
