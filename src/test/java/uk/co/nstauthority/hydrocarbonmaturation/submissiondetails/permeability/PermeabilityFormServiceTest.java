package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PermeabilityFormServiceTest {

  @Mock
  private PermeabilityFormValidator permeabilityFormValidator;

  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @InjectMocks
  private PermeabilityFormService permeabilityFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new PermeabilityForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    permeabilityFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(permeabilityFormValidator, times(1)).validate(eq(form), eq(bindingResult),
       captor.capture());

    assertThat(captor.getValue())
        .extracting(
            SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void permeabilityDetailToForm_newEntity_thenEmptyForm() {
    when(permeabilityPersistenceService.getPermeabilityDetail(submissionDetail)).thenReturn(Optional.empty());

    assertThat(permeabilityFormService.getPermeabilityForm(submissionDetail))
        .extracting(
            form -> form.getPermeabilityValuesForm().getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getPermeabilityAverage().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getAverageMethodUsed(),
            form -> form.getPermeabilityValuesForm().getPermeabilitySource(),
            form -> form.getPermeabilityValuesForm().getOtherPermeabilitySource(),
            form -> form.getPermeabilityValuesForm().getPermeabilityComments(),
            form -> form.getPermeabilityValuesForm().getKvKhAverage().asBigDecimal())
        .containsExactly(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            null,
            null,
            null,
            null,
            Optional.empty()
        );
  }

  @Test
  void permeabilityDetailToForm_filledEntity_thenEmptyForm() {
    var filledEntity = new PermeabilityDetail();
    filledEntity.setHasPermeability(true);
    filledEntity.setAverageMethodUsed(AverageMethod.ARITHMETIC);
    filledEntity.setPermeabilityAverage(BigDecimal.ONE);
    filledEntity.setPermeabilityComments("comments");
    filledEntity.setPermeabilityMax(BigDecimal.ONE);
    filledEntity.setPermeabilityMin(BigDecimal.ONE);
    filledEntity.setPermeabilityAverage(BigDecimal.ONE);
    filledEntity.setPermeabilitySource(PermeabilitySource.OTHER);
    filledEntity.setKvKhAverage(BigDecimal.ONE);
    filledEntity.setSourceOther("comments");

    when(permeabilityPersistenceService.getPermeabilityDetail(submissionDetail))
        .thenReturn(Optional.of(filledEntity));

    assertThat(permeabilityFormService.getPermeabilityForm(submissionDetail))
        .extracting(
            form -> form.getPermeabilityValuesForm().getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getPermeabilityAverage().asBigDecimal(),
            form -> form.getPermeabilityValuesForm().getAverageMethodUsed(),
            form -> form.getPermeabilityValuesForm().getPermeabilitySource(),
            form -> form.getPermeabilityValuesForm().getOtherPermeabilitySource(),
            form -> form.getPermeabilityValuesForm().getPermeabilityComments(),
            form -> form.getPermeabilityValuesForm().getKvKhAverage().asBigDecimal())
        .containsExactly(
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            AverageMethod.ARITHMETIC,
            PermeabilitySource.OTHER,
            "comments",
            "comments",
            Optional.of(BigDecimal.ONE)
        );
  }
}