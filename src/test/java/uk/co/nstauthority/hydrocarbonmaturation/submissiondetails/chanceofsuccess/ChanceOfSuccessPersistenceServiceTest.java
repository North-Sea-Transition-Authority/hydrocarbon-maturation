package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

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
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class ChanceOfSuccessPersistenceServiceTest {

  @Mock
  private static ChanceOfSuccessRepository chanceOfSuccessRepository;
  private static ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    chanceOfSuccessPersistenceService = new ChanceOfSuccessPersistenceService(chanceOfSuccessRepository);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }
  
  @Test
  void getChanceOfSuccess_verifyMethodCall() {
    chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail);
    verify(chanceOfSuccessRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void saveChanceOfSuccess_whenGivenAForm_verifyCreatedEntity() {
    var form = getChanceOfSuccess();

    var chanceOfSuccessArgumentCaptor = ArgumentCaptor.forClass(ChanceOfSuccess.class);

    chanceOfSuccessPersistenceService.saveChanceOfSuccess(form, submissionDetail);

    verify(chanceOfSuccessRepository, times(1)).save(chanceOfSuccessArgumentCaptor.capture());
    var savedHydrocarbonCase = (ChanceOfSuccess) chanceOfSuccessArgumentCaptor.getValue();

    assertThat(savedHydrocarbonCase)
        .extracting(
            ChanceOfSuccess::getPlayChanceOfSuccess,
            ChanceOfSuccess::getProspectSpecificChanceOfSuccess,
            ChanceOfSuccess::getGeophysicallyModified,
            ChanceOfSuccess::getGeophysicallyModifiedPercentage,
            ChanceOfSuccess::getGeophysicallyModifiedComments,
            ChanceOfSuccess::getRelatedToSpecificWellLocation,
            ChanceOfSuccess::getWellModifiedPercentage,
            ChanceOfSuccess::getWellModifiedComments
        )
        .containsExactly(
            form.getPlayChanceOfSuccess(),
            form.getProspectSpecificChanceOfSuccess(),
            form.getGeophysicallyModified(),
            form.getGeophysicallyModifiedPercentage().createBigDecimalOrNull(),
            form.getGeophysicallyModifiedComments(),
            form.getRelatedToSpecificWellLocation(),
            form.getWellModifiedPercentage().createBigDecimalOrNull(),
            form.getWellModifiedComments()
        );
  }

  private ChanceOfSuccessForm getChanceOfSuccess() {
    var form = new ChanceOfSuccessForm();

    form.setProspectSpecificChanceOfSuccess(new ProspectSpecificChanceOfSuccess());
    form.setPlayChanceOfSuccess(new ChanceOfSuccessDetails());
    form.setGeophysicallyModified(false);
    form.setGeophysicallyModifiedPercentage(new DecimalInput());
    form.setGeophysicallyModifiedComments(null);
    form.setRelatedToSpecificWellLocation(false);
    form.setWellModifiedPercentage(new DecimalInput());
    form.setWellModifiedComments(null);

    return form;
  }
}