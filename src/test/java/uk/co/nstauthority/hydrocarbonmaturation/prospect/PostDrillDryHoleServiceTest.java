package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;

@ExtendWith(MockitoExtension.class)
class PostDrillDryHoleServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private PostDrillDryHoleFormValidator postDrillDryHoleFormValidator;

  @InjectMocks
  private PostDrillDryHoleService postDrillDryHoleService;

  @Test
  void createPostDrillDryHoleSubmission() {
    var form = new PostDrillDryHoleForm();
    postDrillDryHoleService.createPostDrillDryHoleSubmission(form);
    verify(submissionCreateService, times(1)).createPostDrillDryHoleSubmission(form);
  }

  @Test
  void validate() {
    var form = new PostDrillDryHoleForm();
    var bindingResult = postDrillDryHoleService.validate(form, emptyBindingResult());
    assertThat(bindingResult).isEqualTo(emptyBindingResult());
    verify(postDrillDryHoleFormValidator, times(1)).validate(form, emptyBindingResult());
  }
}