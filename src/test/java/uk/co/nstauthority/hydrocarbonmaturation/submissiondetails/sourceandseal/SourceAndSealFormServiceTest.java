package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class SourceAndSealFormServiceTest {

  private static SourceAndSealFormValidator sourceAndSealFormValidator;
  private static SourceAndSealFormService sourceAndSealFormService;
  private static SourceAndSealPersistenceService sourceAndSealPersistenceService;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;


  @BeforeAll
  static void setup() {
    sourceAndSealFormValidator = mock(SourceAndSealFormValidator.class);
    sourceAndSealPersistenceService = mock(SourceAndSealPersistenceService.class);
    sourceAndSealFormService = new SourceAndSealFormService(sourceAndSealFormValidator, sourceAndSealPersistenceService);

    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new SourceAndSealForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    sourceAndSealFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(sourceAndSealFormValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void sourceAndSealEntityToForm_newEntity_thenEmptyForm() {
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)).thenReturn(Optional.empty());

    assertThat(sourceAndSealFormService.getSourceAndSealForm(submissionDetail))
        .extracting(
            SourceAndSealForm::getSourceFormationName,
            SourceAndSealForm::getSourceAgeStage,
            SourceAndSealForm::getSourceLithology,
            SourceAndSealForm::getTopSealFormationName,
            SourceAndSealForm::getTopSealAgeStage,
            SourceAndSealForm::getTopSealLithology
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void sourceAndSealEntityToForm_existingEntity_thenPopulatedForm() {
    var sourceAndSeal = getSourceAndSeal(submissionDetail);
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail)).thenReturn(Optional.of(sourceAndSeal));

    assertThat(sourceAndSealFormService.getSourceAndSealForm(submissionDetail))
        .extracting(
            SourceAndSealForm::getSourceFormationName,
            SourceAndSealForm::getSourceAgeStage,
            SourceAndSealForm::getSourceLithology,
            SourceAndSealForm::getTopSealFormationName,
            SourceAndSealForm::getTopSealAgeStage,
            SourceAndSealForm::getTopSealLithology
        )
        .containsExactly(
            sourceAndSeal.getSourceFormationName(),
            sourceAndSeal.getSourceAgeStage(),
            sourceAndSeal.getSourceLithology(),
            sourceAndSeal.getTopSealFormationName(),
            sourceAndSeal.getTopSealAgeStage(),
            sourceAndSeal.getTopSealLithology()
        );
  }

  private SourceAndSeal getSourceAndSeal(SubmissionDetail submissionDetail) {
    var sourceAndSeal = new SourceAndSeal();
    sourceAndSeal.setSubmissionDetail(submissionDetail);
    sourceAndSeal.setSourceFormationName("formation name");
    sourceAndSeal.setTopSealFormationName("formation name");

    return sourceAndSeal;
  }
}