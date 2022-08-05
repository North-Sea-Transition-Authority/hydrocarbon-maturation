package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsServiceTest {

  private static OpportunityDetailsFormValidator opportunityDetailsFormValidator;
  private static OpportunityDetailsRepository opportunityDetailsRepository;
  private static OpportunityDetailsService opportunityDetailsService;
  private static SubmissionDetail submissionDetail;

  @BeforeAll
  static void setup() {
    opportunityDetailsFormValidator = mock(OpportunityDetailsFormValidator.class);
    opportunityDetailsRepository = mock(OpportunityDetailsRepository.class);

    opportunityDetailsService = new OpportunityDetailsService(opportunityDetailsFormValidator, opportunityDetailsRepository);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new OpportunityDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsService.validate(form, bindingResult);
    verify(opportunityDetailsFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void saveOpportunityDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new OpportunityDetailsForm();
    form.setOpportunityType(OpportunityType.OTHER);
    form.setOtherTypeOfOpportunity("Other type");
    form.setOpportunityComments("Comments");
    form.setKeyEvaluationWorkCarriedOut("Key evluation");
    form.setKeyWorkRequiredForFurtherMaturation("Key work");

    var opportunityDetailsArgumentCaptor = ArgumentCaptor.forClass(OpportunityDetail.class);

    opportunityDetailsService.saveOpportunityDetails(form, submissionDetail);

    verify(opportunityDetailsRepository, times(1)).save(opportunityDetailsArgumentCaptor.capture());
    var savedOpportunityDetail = (OpportunityDetail) opportunityDetailsArgumentCaptor.getValue();

    assertThat(savedOpportunityDetail)
        .extracting(
            OpportunityDetail::getOpportunityType,
            OpportunityDetail::getOtherTypeOfOpportunity,
            OpportunityDetail::getOpportunityComments,
            OpportunityDetail::getKeyEvaluationWorkCarriedOut,
            OpportunityDetail::getKeyWorkRequiredForFurtherMaturation
        )
        .containsExactly(
            form.getOpportunityType(),
            form.getOtherTypeOfOpportunity(),
            form.getOpportunityComments(),
            form.getKeyEvaluationWorkCarriedOut(),
            form.getKeyWorkRequiredForFurtherMaturation()
        );
  }
}