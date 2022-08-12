package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsPersistenceServiceTest {

  @Mock
  private static OpportunityDetailsRepository opportunityDetailsRepository;

  private static OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    opportunityDetailsPersistenceService = new OpportunityDetailsPersistenceService(opportunityDetailsRepository);

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
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

    opportunityDetailsPersistenceService.saveOpportunityDetails(form, submissionDetail);

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

  @Test
  void getOpportunityDetail_verifyMethodCall() {
    opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail);
    verify(opportunityDetailsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}