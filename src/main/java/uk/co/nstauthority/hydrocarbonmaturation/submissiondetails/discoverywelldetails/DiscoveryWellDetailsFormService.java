package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class DiscoveryWellDetailsFormService implements SubmissionFormService {

  private final DiscoveryWellDetailsFormValidator discoveryWellDetailsFormValidator;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;
  private final WellDetailsFormService wellDetailsFormService;

  @Autowired
  DiscoveryWellDetailsFormService(DiscoveryWellDetailsFormValidator discoveryWellDetailsFormValidator,
                                  DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService,
                                  WellDetailsFormService wellDetailsFormService) {
    this.discoveryWellDetailsFormValidator = discoveryWellDetailsFormValidator;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
    this.wellDetailsFormService = wellDetailsFormService;
  }

  DiscoveryWellDetailsForm getDiscoveryWellDetailsForm(SubmissionDetail submissionDetail) {
    return discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
        .map(this::discoveryWellDetailEntityToForm)
        .orElse(new DiscoveryWellDetailsForm());
  }

  public Map<String, String> getPreSelectedDryWell(String dryWell) {
    return wellDetailsFormService.getPreSelectedDryWell(dryWell);
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    discoveryWellDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    return bindingResult;
  }

  private DiscoveryWellDetailsForm discoveryWellDetailEntityToForm(DiscoveryWellDetail discoveryWellDetail) {
    var form = new DiscoveryWellDetailsForm();
    form.setWellDetailsForm(wellDetailsFormService.drillDetailToForm(discoveryWellDetail));
    form.setDryHoleCost(DecimalInput.createDecimalInput(discoveryWellDetail.getTotalWellCost()));
    form.setHydrocarbonsDiscovered(discoveryWellDetail.getHydrocarbonsDiscovered());
    form.setDiscoveryStatus(discoveryWellDetail.getDiscoveryStatus());
    form.setChanceOfCommercialityComments(discoveryWellDetail.getChanceOfCommercialityComments());
    form.setSuccessRelatedCosts(DecimalInput.createDecimalInput(discoveryWellDetail.getSuccessRelatedCosts()));

    return form;
  }
}
