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
    var form = ChanceOfSuccessTestUtil.createChanceOfSuccessForm();

    var chanceOfSuccessArgumentCaptor = ArgumentCaptor.forClass(ChanceOfSuccess.class);

    chanceOfSuccessPersistenceService.saveChanceOfSuccess(form, submissionDetail);

    verify(chanceOfSuccessRepository, times(1)).save(chanceOfSuccessArgumentCaptor.capture());
    var savedChanceOfSuccess = (ChanceOfSuccess) chanceOfSuccessArgumentCaptor.getValue();

    assertThat(savedChanceOfSuccess)
        .extracting(
            ChanceOfSuccess::getSourcePercentage,
            ChanceOfSuccess::getSourceComments,
            ChanceOfSuccess::getReservoirPercentage,
            ChanceOfSuccess::getReservoirComments,
            ChanceOfSuccess::getTopSealPercentage,
            ChanceOfSuccess::getTopSealComments,
            ChanceOfSuccess::getProspectTrapPercentage,
            ChanceOfSuccess::getProspectTrapComments,
            ChanceOfSuccess::getProspectMigrationPercentage,
            ChanceOfSuccess::getProspectMigrationComments,
            ChanceOfSuccess::getProspectSourcePercentage,
            ChanceOfSuccess::getProspectSourceComments,
            ChanceOfSuccess::getProspectReservoirPercentage,
            ChanceOfSuccess::getProspectReservoirComments,
            ChanceOfSuccess::getProspectTopSealPercentage,
            ChanceOfSuccess::getProspectTopSealComments,
            ChanceOfSuccess::getGeophysicallyModified,
            ChanceOfSuccess::getGeophysicallyModifiedPercentage,
            ChanceOfSuccess::getGeophysicallyModifiedComments,
            ChanceOfSuccess::getRelatedToSpecificWellLocation,
            ChanceOfSuccess::getWellModifiedPercentage,
            ChanceOfSuccess::getWellModifiedComments
        )
        .containsExactly(
            form.getPlayChanceOfSuccess().getSourcePercentage().createBigDecimalOrNull(),
            form.getPlayChanceOfSuccess().getSourceComments(),
            form.getPlayChanceOfSuccess().getReservoirPercentage().createBigDecimalOrNull(),
            form.getPlayChanceOfSuccess().getReservoirComments(),
            form.getPlayChanceOfSuccess().getTopSealPercentage().createBigDecimalOrNull(),
            form.getPlayChanceOfSuccess().getTopSealComments(),
            form.getProspectSpecificChanceOfSuccess().getTrapPercentage().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getTrapComments(),
            form.getProspectSpecificChanceOfSuccess().getMigrationPercentage().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getMigrationComments(),
            form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getSourcePercentage().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getSourceComments(),
            form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getReservoirPercentage().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getReservoirComments(),
            form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getTopSealPercentage().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getChanceOfSuccessDetailsForm().getTopSealComments(),
            form.getGeophysicallyModified(),
            form.getGeophysicallyModifiedPercentage().createBigDecimalOrNull(),
            form.getGeophysicallyModifiedComments(),
            form.getRelatedToSpecificWellLocation(),
            form.getWellModifiedPercentage().createBigDecimalOrNull(),
            form.getWellModifiedComments()
        );
  }

  @Test
  void saveChanceOfSuccess_whenGeophysicallyModifiedAndSpecificWellLocationAreFalse() {
    var form = ChanceOfSuccessTestUtil.createChanceOfSuccessForm();

    form.setGeophysicallyModified(false);
    form.setRelatedToSpecificWellLocation(false);

    var chanceOfSuccessArgumentCaptor = ArgumentCaptor.forClass(ChanceOfSuccess.class);

    chanceOfSuccessPersistenceService.saveChanceOfSuccess(form, submissionDetail);

    verify(chanceOfSuccessRepository, times(1)).save(chanceOfSuccessArgumentCaptor.capture());
    var savedChanceOfSuccess = (ChanceOfSuccess) chanceOfSuccessArgumentCaptor.getValue();

    assertThat(savedChanceOfSuccess)
        .extracting(
            ChanceOfSuccess::getGeophysicallyModified,
            ChanceOfSuccess::getGeophysicallyModifiedPercentage,
            ChanceOfSuccess::getGeophysicallyModifiedComments,
            ChanceOfSuccess::getRelatedToSpecificWellLocation,
            ChanceOfSuccess::getWellModifiedPercentage,
            ChanceOfSuccess::getWellModifiedComments
        )
        .containsExactly(
            form.getGeophysicallyModified(),
            null,
            null,
            form.getRelatedToSpecificWellLocation(),
            null,
            null
        );
  }
}