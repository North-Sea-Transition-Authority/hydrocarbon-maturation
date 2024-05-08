package uk.co.nstauthority.hydrocarbonmaturation.complexity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

class ComplexitySummaryUtilTest {

  @Test
  void getComplexitySummaryDataView() {
    var generatedSummaryDataView = ComplexitySummaryUtil.getComplexitySummaryDataView(
        ViscosityRange.ONE_TO_TEN,
        VerticalHeterogeneityRange.GREATER_THAN_TEN_THOUSAND,
        ArealDensityRange.ONE_TO_TWO,
        StructuralComplexityRange.EXCELLENT_RESERVOIR_QUALITY
    );

    var expectedDataView = new SummaryDataView(List.of(
        new SummaryKeyValue(
            ComplexitySummaryUtil.VISCOSITY,
            ViscosityRange.ONE_TO_TEN.getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexitySummaryUtil.VERTICAL_HETEROGENEITY,
            VerticalHeterogeneityRange.GREATER_THAN_TEN_THOUSAND.getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexitySummaryUtil.AREAL_DENSITY,
            ArealDensityRange.ONE_TO_TWO.getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexitySummaryUtil.STRUCTURAL_COMPLEXITY,
            StructuralComplexityRange.EXCELLENT_RESERVOIR_QUALITY.getDisplayName()
        )
    ));

    assertThat(generatedSummaryDataView)
        .usingRecursiveComparison()
        .isEqualTo(expectedDataView);
  }

  @Test
  void getComplexitySummaryDataView_handlesNullValues() {
    var generatedSummaryDataView = ComplexitySummaryUtil.getComplexitySummaryDataView(null, null, null, null);

    var expectedDataView = new SummaryDataView(List.of(
        new SummaryKeyValue(
            ComplexitySummaryUtil.VISCOSITY,
            ""
        ),
        new SummaryKeyValue(
            ComplexitySummaryUtil.VERTICAL_HETEROGENEITY,
            ""
        ),
        new SummaryKeyValue(
            ComplexitySummaryUtil.AREAL_DENSITY,
            ""
        ),
        new SummaryKeyValue(
            ComplexitySummaryUtil.STRUCTURAL_COMPLEXITY,
            ""
        )
    ));

    assertThat(generatedSummaryDataView)
        .usingRecursiveComparison()
        .isEqualTo(expectedDataView);
  }
}
