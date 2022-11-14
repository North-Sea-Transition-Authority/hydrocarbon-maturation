package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PermeabilityDryWellFormServiceTest {

  @Mock
  private PermeabilityDryWellFormValidator permeabilityDryWellFormValidator;

  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @InjectMocks
  private PermeabilityDryWellFormService permeabilityDryWellFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new PermeabilityDryWellForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    permeabilityDryWellFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(permeabilityDryWellFormValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void permeabilityDetailToForm_newEntity_thenEmptyForm() {
    when(permeabilityPersistenceService.getPermeabilityDetail(submissionDetail)).thenReturn(Optional.empty());

    assertThat(permeabilityDryWellFormService.getPermeabilityForm(submissionDetail))
        .extracting(
            form -> form.getPermeabilityForm().getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityForm().getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityForm().getPermeabilityAverage().asBigDecimal(),
            form -> form.getPermeabilityForm().getAverageMethodUsed(),
            form -> form.getPermeabilityForm().getPermeabilitySource(),
            form -> form.getPermeabilityForm().getOtherPermeabilitySource(),
            form -> form.getPermeabilityForm().getPermeabilityComments(),
            form -> form.getPermeabilityForm().getKvKhMin().asBigDecimal(),
            form -> form.getPermeabilityForm().getKvKhMax().asBigDecimal(),
            form -> form.getPermeabilityForm().getKvKhAverage().asBigDecimal(),
            form -> form.getPermeabilityForm().getKvKhAverageMethodUsed(),
            form -> form.getPermeabilityForm().getKvKhAverageMethodUsedOther(),
            form -> form.getPermeabilityForm().getKvKhSource(),
            form -> form.getPermeabilityForm().getOtherKvKhSource(),
            form -> form.getPermeabilityForm().getKvKhComments())
        .containsExactly(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            null,
            null,
            null,
            null,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            null,
            null,
            null,
            null,
            null
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
    filledEntity.setKvKhAverageMethodUsed(PermeabilitySource.OTHER);
    filledEntity.setKvKhComments("comments");
    filledEntity.setKvKhMax(BigDecimal.ONE);
    filledEntity.setKvKhMin(BigDecimal.ONE);
    filledEntity.setKvKhSource(PermeabilitySource.OTHER);
    filledEntity.setKvKhAverage(BigDecimal.ONE);
    filledEntity.setSourceOther("comments");
    filledEntity.setKvKhSourceOther("comments");
    filledEntity.setKvKhAverageMethodUsedOther("comments");

    when(permeabilityPersistenceService.getPermeabilityDetail(submissionDetail))
        .thenReturn(Optional.of(filledEntity));

    assertThat(permeabilityDryWellFormService.getPermeabilityForm(submissionDetail))
        .extracting(
            form -> form.getPermeabilityForm().getPermeabilityMin().asBigDecimal(),
            form -> form.getPermeabilityForm().getPermeabilityMax().asBigDecimal(),
            form -> form.getPermeabilityForm().getPermeabilityAverage().asBigDecimal(),
            form -> form.getPermeabilityForm().getAverageMethodUsed(),
            form -> form.getPermeabilityForm().getPermeabilitySource(),
            form -> form.getPermeabilityForm().getOtherPermeabilitySource(),
            form -> form.getPermeabilityForm().getPermeabilityComments(),
            form -> form.getPermeabilityForm().getKvKhMin().asBigDecimal(),
            form -> form.getPermeabilityForm().getKvKhMax().asBigDecimal(),
            form -> form.getPermeabilityForm().getKvKhAverage().asBigDecimal(),
            form -> form.getPermeabilityForm().getKvKhAverageMethodUsed(),
            form -> form.getPermeabilityForm().getKvKhAverageMethodUsedOther(),
            form -> form.getPermeabilityForm().getKvKhSource(),
            form -> form.getPermeabilityForm().getOtherKvKhSource(),
            form -> form.getPermeabilityForm().getKvKhComments())
        .containsExactly(
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            AverageMethod.ARITHMETIC,
            PermeabilitySource.OTHER,
            "comments",
            "comments",
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.ONE),
            PermeabilitySource.OTHER,
            "comments",
            PermeabilitySource.OTHER,
            "comments",
            "comments"
        );
  }
}