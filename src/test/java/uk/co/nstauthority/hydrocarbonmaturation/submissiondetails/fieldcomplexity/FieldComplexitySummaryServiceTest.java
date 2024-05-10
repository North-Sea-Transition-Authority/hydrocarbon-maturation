package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.complexity.ComplexityUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;

@ExtendWith(MockitoExtension.class)
class FieldComplexitySummaryServiceTest {
  @Mock
  private FieldComplexityRepository fieldComplexityRepository;

  @InjectMocks
  private FieldComplexitySummaryService fieldComplexitySummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void getFieldComplexitySummary_noExistingFieldComplexity_thenEmptySummaryCard() {
    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(fieldComplexitySummaryService.getFieldComplexitySummary(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(SummaryCard.emptySummaryCardWithHeading(FieldComplexitySummaryService.FIELD_COMPLEXITY));
  }

  @Test
  void getFieldComplexitySummary_existingFieldComplexity_completedSection_thenAllValuesShown() {
    var fieldComplexity = new FieldComplexity();
    fieldComplexity.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    fieldComplexity.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.ONE_TO_TEN);
    fieldComplexity.setArealDensityRange(ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE);
    fieldComplexity.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);
    fieldComplexity.setCalculatedFieldComplexity(BigDecimal.ONE);

    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(fieldComplexity));

    var summaryDataView = SummaryDataView.newWithKeyValue(
        ComplexityUtil.VISCOSITY, ViscosityRange.ONE_TO_TEN.getDisplayName()
    );
    summaryDataView.addKeyValue(ComplexityUtil.VERTICAL_HETEROGENEITY,
        VerticalHeterogeneityRange.ONE_TO_TEN.getDisplayName());
    summaryDataView.addKeyValue(ComplexityUtil.AREAL_DENSITY,
        ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE.getDisplayName());
    summaryDataView.addKeyValue(ComplexityUtil.STRUCTURAL_COMPLEXITY,
        StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION.getDisplayName());
    summaryDataView.addKeyValue(FieldComplexitySummaryService.FIELD_COMPLEXITY,
        BigDecimal.ONE);

    var expectedSummaryCard = SummaryCard.simpleSummaryCardWithHeading(
        FieldComplexitySummaryService.FIELD_COMPLEXITY_SUMMARY,
        summaryDataView
    );

    assertThat(fieldComplexitySummaryService.getFieldComplexitySummary(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard);
  }

  @Test
  void getFieldComplexitySummary_existingFieldComplexity_incompleteSection_thenEmptyStringsShown() {
    var fieldComplexity = new FieldComplexity();
    fieldComplexity.setViscosityRange(null);
    fieldComplexity.setVerticalHeterogeneityRange(null);
    fieldComplexity.setArealDensityRange(null);
    fieldComplexity.setStructuralComplexityRange(null);
    fieldComplexity.setCalculatedFieldComplexity(null);

    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(fieldComplexity));

    var summaryDataView = SummaryDataView.newWithKeyValue(
        ComplexityUtil.VISCOSITY, ""
    );
    summaryDataView.addKeyValue(ComplexityUtil.VERTICAL_HETEROGENEITY, "");
    summaryDataView.addKeyValue(ComplexityUtil.AREAL_DENSITY, "");
    summaryDataView.addKeyValue(ComplexityUtil.STRUCTURAL_COMPLEXITY, "");
    summaryDataView.addKeyValue(FieldComplexitySummaryService.FIELD_COMPLEXITY, "");

    var expectedSummaryCard = SummaryCard.simpleSummaryCardWithHeading(
        FieldComplexitySummaryService.FIELD_COMPLEXITY_SUMMARY,
        summaryDataView
    );

    assertThat(fieldComplexitySummaryService.getFieldComplexitySummary(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard);
  }
}