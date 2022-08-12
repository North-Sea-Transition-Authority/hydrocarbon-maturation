package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

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
class SourceAndSealPersistenceServiceTest {

  private static SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @Mock
  private static SourceAndSealRepository sourceAndSealRepository;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    sourceAndSealPersistenceService = new SourceAndSealPersistenceService(sourceAndSealRepository);

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveSourceAndSealDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new SourceAndSealForm();
    form.setSourceFormationName("source formation name");
    form.setTopSealFormationName("top seal formation name");

    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealPersistenceService.saveSourceAndSealDetails(form, submissionDetail);

    verify(sourceAndSealRepository, times(1)).save(sourceAndSealArgumentCaptor.capture());
    var savedSourceAndSeal = (SourceAndSeal) sourceAndSealArgumentCaptor.getValue();

    assertThat(savedSourceAndSeal)
        .extracting(
            SourceAndSeal::getSourceFormationName,
            SourceAndSeal::getSourceAgeStage,
            SourceAndSeal::getSourceLithology,
            SourceAndSeal::getTopSealFormationName,
            SourceAndSeal::getTopSealAgeStage,
            SourceAndSeal::getTopSealLithology
        )
        .containsExactly(
            form.getSourceFormationName(),
            form.getSourceAgeStage(),
            form.getSourceLithology(),
            form.getTopSealFormationName(),
            form.getTopSealAgeStage(),
            form.getTopSealLithology()
        );
  }

  @Test
  void getSourceAndSeal_verifyMethodCall() {
    sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail);
    verify(sourceAndSealRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}