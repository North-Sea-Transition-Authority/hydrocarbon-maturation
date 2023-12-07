package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

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
  void getSummarySection_noExistingFieldComplexity() {
    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var summarySection = fieldComplexitySummaryService.getSummarySection(submissionDetail);
    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), FieldComplexitySummaryService.FIELD_COMPLEXITY_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        FieldComplexitySummaryService.VISCOSITY, ""
    );
    summaryDataView.addKeyValue(FieldComplexitySummaryService.VERTICAL_HETEROGENEITY, "");
    summaryDataView.addKeyValue(FieldComplexitySummaryService.AREAL_DENSITY, "");
    summaryDataView.addKeyValue(FieldComplexitySummaryService.STRUCTURAL_COMPLEXITY, "");
    summaryDataView.addKeyValue(FieldComplexitySummaryService.FIELD_COMPLEXITY, "");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(FieldComplexitySummaryService.FIELD_COMPLEXITY_SUMMARY, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_existingFieldComplexity_completedSection_thenAllValuesShown() {
    var fieldComplexity = new FieldComplexity();
    fieldComplexity.setViscosityRange(ViscosityRange.ONE_TO_TEN );
    fieldComplexity.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.ONE_TO_TEN );
    fieldComplexity.setArealDensityRange(ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE );
    fieldComplexity.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION );
    fieldComplexity.setCalculatedFieldComplexity(BigDecimal.ONE);

    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(fieldComplexity));

    var summarySection = fieldComplexitySummaryService.getSummarySection(submissionDetail);
    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), FieldComplexitySummaryService.FIELD_COMPLEXITY_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        FieldComplexitySummaryService.VISCOSITY, ViscosityRange.ONE_TO_TEN.getDisplayName()
    );
    summaryDataView.addKeyValue(FieldComplexitySummaryService.VERTICAL_HETEROGENEITY,
        VerticalHeterogeneityRange.ONE_TO_TEN.getDisplayName());
    summaryDataView.addKeyValue(FieldComplexitySummaryService.AREAL_DENSITY,
        ArealDensityRange.GREATER_THAN_FOUR_POINT_FIVE.getDisplayName());
    summaryDataView.addKeyValue(FieldComplexitySummaryService.STRUCTURAL_COMPLEXITY,
        StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION.getDisplayName());
    summaryDataView.addKeyValue(FieldComplexitySummaryService.FIELD_COMPLEXITY,
        BigDecimal.ONE);

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(FieldComplexitySummaryService.FIELD_COMPLEXITY_SUMMARY, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_existingFieldComplexity_incompleteSection_thenEmptyStringsShown() {
    var fieldComplexity = new FieldComplexity();
    fieldComplexity.setViscosityRange(null);
    fieldComplexity.setVerticalHeterogeneityRange(null);
    fieldComplexity.setArealDensityRange(null);
    fieldComplexity.setStructuralComplexityRange(null);
    fieldComplexity.setCalculatedFieldComplexity(null);

    when(fieldComplexityRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(fieldComplexity));

    var summarySection = fieldComplexitySummaryService.getSummarySection(submissionDetail);
    assertThat(summarySection).isPresent();

    assertSummarySection(summarySection.get(), FieldComplexitySummaryService.FIELD_COMPLEXITY_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        FieldComplexitySummaryService.VISCOSITY, ""
    );
    summaryDataView.addKeyValue(FieldComplexitySummaryService.VERTICAL_HETEROGENEITY, "");
    summaryDataView.addKeyValue(FieldComplexitySummaryService.AREAL_DENSITY, "");
    summaryDataView.addKeyValue(FieldComplexitySummaryService.STRUCTURAL_COMPLEXITY, "");
    summaryDataView.addKeyValue(FieldComplexitySummaryService.FIELD_COMPLEXITY, "");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(FieldComplexitySummaryService.FIELD_COMPLEXITY_SUMMARY, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }
}