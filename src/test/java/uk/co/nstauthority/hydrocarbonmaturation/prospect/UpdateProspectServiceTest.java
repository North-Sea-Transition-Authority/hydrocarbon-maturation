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
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;

@ExtendWith(MockitoExtension.class)
class UpdateProspectServiceTest {

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private UpdateFormValidator updateFormValidator;

  @InjectMocks
  private UpdateProspectService updateProspectService;

  @Test
  void createUpdateProspect() {
    var form = new UpdateProspectForm();
    updateProspectService.createUpdateProspect(form);
    verify(submissionCreateService, times(1)).createUpdateProspect(form);
  }

  @Test
  void validate() {
    var form = new UpdateProspectForm();
    var bindingResult = updateProspectService.validate(form, emptyBindingResult());
    assertThat(bindingResult).isEqualTo(emptyBindingResult());
    verify(updateFormValidator, times(1)).validate(form, emptyBindingResult());
  }
}