package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

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
class SourceAndSealServiceTest {

  private static SourceAndSealFormValidator sourceAndSealFormValidator;
  private static SourceAndSealService sourceAndSealService;
  private static SourceAndSealRepository sourceAndSealRepository;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;


  @BeforeAll
  static void setup() {
    sourceAndSealFormValidator = mock(SourceAndSealFormValidator.class);
    sourceAndSealRepository = mock(SourceAndSealRepository.class);
    sourceAndSealService = new SourceAndSealService(sourceAndSealFormValidator, sourceAndSealRepository);

    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new SourceAndSealForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    sourceAndSealService.validate(form, bindingResult);
    verify(sourceAndSealFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void saveSourceAndSealDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new SourceAndSealForm();
    form.setSourceFormationName("source formation name");
    form.setTopSealFormationName("top seal formation name");

    var sourceAndSealArgumentCaptor = ArgumentCaptor.forClass(SourceAndSeal.class);

    sourceAndSealService.saveSourceAndSealDetails(form, submissionDetail);

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
}