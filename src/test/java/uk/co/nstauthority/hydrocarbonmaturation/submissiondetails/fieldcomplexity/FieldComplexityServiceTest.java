package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class FieldComplexityServiceTest {

  @Mock
  private FieldComplexityRepository fieldComplexityRepository;

  @InjectMocks
  private FieldComplexityService fieldComplexityService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void submissionDetailToForm_noExistingFieldComplexity() {
    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(fieldComplexityService.submissionDetailToForm(submissionDetail))
        .extracting(
            FieldComplexityForm::getViscosityRange,
            FieldComplexityForm::getVerticalHeterogeneityRange,
            FieldComplexityForm::getArealDensityRange,
            FieldComplexityForm::getStructuralComplexityRange
        ).containsOnlyNulls();
  }

  @Test
  void submissionDetailToForm_existingFieldComplexity() {
    var fieldComplexity = new FieldComplexity();
    fieldComplexity.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    fieldComplexity.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.TEN_TO_ONE_HUNDRED);
    fieldComplexity.setArealDensityRange(ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE);
    fieldComplexity.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);
    fieldComplexity.setCalculatedFieldComplexity(BigDecimal.valueOf(100));

    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(fieldComplexity));

    assertThat(fieldComplexityService.submissionDetailToForm(submissionDetail))
        .extracting(
            FieldComplexityForm::getViscosityRange,
            FieldComplexityForm::getVerticalHeterogeneityRange,
            FieldComplexityForm::getArealDensityRange,
            FieldComplexityForm::getStructuralComplexityRange
        ).containsExactly(
            fieldComplexity.getViscosityRange(),
            fieldComplexity.getVerticalHeterogeneityRange(),
            fieldComplexity.getArealDensityRange(),
            fieldComplexity.getStructuralComplexityRange()
        );
  }

  @Test
  void saveFieldComplexity_noExistingFieldComplexity() {
    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var form = new FieldComplexityForm();
    form.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    form.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.TEN_TO_ONE_HUNDRED);
    form.setArealDensityRange(ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE);
    form.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);

    var captor = ArgumentCaptor.forClass(FieldComplexity.class);
    fieldComplexityService.saveFieldComplexity(form, submissionDetail);

    verify(fieldComplexityRepository, times(1)).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            FieldComplexity::getViscosityRange,
            FieldComplexity::getVerticalHeterogeneityRange,
            FieldComplexity::getArealDensityRange,
            FieldComplexity::getStructuralComplexityRange
        ).containsExactly(
            form.getViscosityRange(),
            form.getVerticalHeterogeneityRange(),
            form.getArealDensityRange(),
            form.getStructuralComplexityRange()
        );
  }

  @Test
  void calculateFieldComplexity_incompleteForm_thenNotCalculated() {
    var fieldComplexity = new FieldComplexity();
    fieldComplexity.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    fieldComplexity.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.TEN_TO_ONE_HUNDRED);
    fieldComplexity.setArealDensityRange(ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE);
    fieldComplexity.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);
    fieldComplexity.setCalculatedFieldComplexity(BigDecimal.valueOf(100));

    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(fieldComplexity));

    var form = new FieldComplexityForm();

    var captor = ArgumentCaptor.forClass(FieldComplexity.class);
    fieldComplexityService.saveFieldComplexity(form, submissionDetail);

    verify(fieldComplexityRepository, times(1)).save(captor.capture());

    assertThat(captor.getValue().getCalculatedFieldComplexity()).isNull();
  }

  @Test
  void calculateFieldComplexity_completeForm_thenCalculated() {
    var fieldComplexity = new FieldComplexity();
    fieldComplexity.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    fieldComplexity.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.TEN_TO_ONE_HUNDRED);
    fieldComplexity.setArealDensityRange(ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE);
    fieldComplexity.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);
    fieldComplexity.setCalculatedFieldComplexity(BigDecimal.valueOf(100));

    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(fieldComplexity));

    var form = new FieldComplexityForm();
    form.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    form.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.TEN_TO_ONE_HUNDRED);
    form.setArealDensityRange(ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE);
    form.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);

    var captor = ArgumentCaptor.forClass(FieldComplexity.class);
    fieldComplexityService.saveFieldComplexity(form, submissionDetail);

    verify(fieldComplexityRepository, times(1)).save(captor.capture());

    var viscosity = form.getViscosityRange().getValue();
    var verticalHeterogeneity = form.getVerticalHeterogeneityRange().getValue();
    var arealDensity = form.getArealDensityRange().getValue();
    var structuralComplexity = form.getStructuralComplexityRange().getValue();

    var expectedFieldComplexity = (2 * viscosity) + verticalHeterogeneity + arealDensity + (2 * structuralComplexity);
    assertThat(captor.getValue().getCalculatedFieldComplexity().intValue()).isEqualTo(expectedFieldComplexity);
  }
}