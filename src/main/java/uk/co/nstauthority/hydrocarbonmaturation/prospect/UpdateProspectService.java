package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@Service
public class UpdateProspectService {

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionCreateService submissionCreateService;
  private final UpdateFormValidator updateFormValidator;

  @Autowired
  public UpdateProspectService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                               SubmissionDetailService submissionDetailService,
                               SubmissionCreateService submissionCreateService,
                               UpdateFormValidator updateFormValidator) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionCreateService = submissionCreateService;
    this.updateFormValidator = updateFormValidator;
  }

  public List<GeneralDetail> getLatestProspectByName(String name) {
    var generalDetails = generalDetailsPersistenceService.getGeneralDetailsByName(name);
    var prospectResources = new HashSet<Resource>();

    generalDetails
        .forEach(detail -> prospectResources.add(detail.getSubmissionDetail().getResourceId()));

    return prospectResources
        .stream()
        .map(resource -> generalDetailsPersistenceService.getGeneralDetailOrThrow(
            submissionDetailService.getLatestSubmissionDetailForResourceOrThrow(resource)))
        .toList();
  }

  public SubmissionDetail createUpdateProspect(UpdateProspectForm form) {
    return submissionCreateService.createUpdateProspect(form);
  }

  public BindingResult validate(UpdateProspectForm form, BindingResult bindingResult) {
    updateFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}