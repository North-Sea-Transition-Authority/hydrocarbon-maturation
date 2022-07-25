package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

@Service
public class OpportunityDetailsService {

  private final OpportunityDetailsFormValidator opportunityDetailsFormValidator;

  @Autowired
  public OpportunityDetailsService(OpportunityDetailsFormValidator opportunityDetailsFormValidator) {
    this.opportunityDetailsFormValidator = opportunityDetailsFormValidator;
  }

  ModelAndView getOpportunityDetailsModelAndView(OpportunityDetailsForm form) {
    return new ModelAndView("hm/submissiondetails/opportunityDetails")
        .addObject("opportunityTypes", OpportunityType.getOpportunityType())
        .addObject("form", form)
        .addObject("actionUrl", "#")
        .addObject("backLinkUrl", "#"); // TODO add back link to task list
  }

  BindingResult validate(OpportunityDetailsForm form, BindingResult bindingResult) {
    opportunityDetailsFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}
