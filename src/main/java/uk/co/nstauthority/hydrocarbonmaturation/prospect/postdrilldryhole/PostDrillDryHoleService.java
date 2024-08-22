package uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class PostDrillDryHoleService {
  private final SubmissionCreateService submissionCreateService;
  private final PostDrillDryHoleFormValidator postDrillDryHoleFormValidator;

  @Autowired
  public PostDrillDryHoleService(SubmissionCreateService submissionCreateService,
                                 PostDrillDryHoleFormValidator postDrillDryHoleFormValidator) {
    this.submissionCreateService = submissionCreateService;
    this.postDrillDryHoleFormValidator = postDrillDryHoleFormValidator;
  }

  public SubmissionDetail createPostDrillDryHoleSubmission(PostDrillDryHoleForm form, User user) {
    return submissionCreateService.createPostDrillDryHoleSubmission(form, user);
  }

  public BindingResult validate(PostDrillDryHoleForm form, BindingResult bindingResult) {
    postDrillDryHoleFormValidator.validate(form, bindingResult);
    return bindingResult;
  }
}