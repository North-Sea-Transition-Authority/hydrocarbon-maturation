package uk.co.nstauthority.hydrocarbonmaturation.complexity;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

class ComplexityUtilTest {

  @Test
  void getComplexitySummaryDataView() {
    var generatedSummaryDataView = ComplexityUtil.getComplexitySummaryDataView(
        ViscosityRange.ONE_TO_TEN,
        VerticalHeterogeneityRange.GREATER_THAN_TEN_THOUSAND,
        ArealDensityRange.ONE_TO_TWO,
        StructuralComplexityRange.EXCELLENT_RESERVOIR_QUALITY
    );

    var expectedDataView = new SummaryDataView(List.of(
        new SummaryKeyValue(
            ComplexityUtil.VISCOSITY,
            ViscosityRange.ONE_TO_TEN.getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexityUtil.VERTICAL_HETEROGENEITY,
            VerticalHeterogeneityRange.GREATER_THAN_TEN_THOUSAND.getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexityUtil.AREAL_DENSITY,
            ArealDensityRange.ONE_TO_TWO.getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexityUtil.STRUCTURAL_COMPLEXITY,
            StructuralComplexityRange.EXCELLENT_RESERVOIR_QUALITY.getDisplayName()
        )
    ));

    assertThat(generatedSummaryDataView)
        .usingRecursiveComparison()
        .isEqualTo(expectedDataView);
  }

  @Test
  void getComplexitySummaryDataView_handlesNullValues() {
    var generatedSummaryDataView = ComplexityUtil.getComplexitySummaryDataView(null, null, null, null);

    var expectedDataView = new SummaryDataView(List.of(
        new SummaryKeyValue(
            ComplexityUtil.VISCOSITY,
            ""
        ),
        new SummaryKeyValue(
            ComplexityUtil.VERTICAL_HETEROGENEITY,
            ""
        ),
        new SummaryKeyValue(
            ComplexityUtil.AREAL_DENSITY,
            ""
        ),
        new SummaryKeyValue(
            ComplexityUtil.STRUCTURAL_COMPLEXITY,
            ""
        )
    ));

    assertThat(generatedSummaryDataView)
        .usingRecursiveComparison()
        .isEqualTo(expectedDataView);
  }

  @Test
  void calculateComplexity() {
    var form = new BaseComplexityForm();
    form.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);
    form.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    form.setArealDensityRange(ArealDensityRange.ONE_TO_TWO);
    form.setVerticalHeterogeneityRange(VerticalHeterogeneityRange.GREATER_THAN_TEN_THOUSAND);

    assertThat(ComplexityUtil.calculateComplexity(form))
        .isEqualTo(new BigDecimal(18));
  }

  @Test
  void calculateComplexity_formHasNullValue() {
    var form = new BaseComplexityForm();
    form.setStructuralComplexityRange(StructuralComplexityRange.COMPLEX_GEOMETRIES_COMPARTMENTALISATION);
    form.setViscosityRange(ViscosityRange.ONE_TO_TEN);
    form.setArealDensityRange(ArealDensityRange.ONE_TO_TWO);

    assertThat(ComplexityUtil.calculateComplexity(form))
        .isNull();
  }
}
