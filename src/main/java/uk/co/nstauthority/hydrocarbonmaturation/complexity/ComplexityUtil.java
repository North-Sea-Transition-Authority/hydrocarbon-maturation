package uk.co.nstauthority.hydrocarbonmaturation.complexity;

import java.math.BigDecimal;
import java.util.ArrayList;
import org.apache.commons.lang3.ObjectUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;

public class ComplexityUtil {

  private ComplexityUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static final String VISCOSITY = "Viscosity";
  public static final String VERTICAL_HETEROGENEITY = "Vertical heterogeneity";
  public static final String AREAL_DENSITY = "Areal density";
  public static final String STRUCTURAL_COMPLEXITY = "Structural complexity";

  public static SummaryDataView getComplexitySummaryDataView(ViscosityRange viscosityRange,
                                                             VerticalHeterogeneityRange verticalHeterogeneityRange,
                                                             ArealDensityRange arealDensityRange,
                                                             StructuralComplexityRange structuralComplexityRange) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());

    summaryDataView.addKeyValue(VISCOSITY, viscosityRange == null
        ? ""
        : viscosityRange.getDisplayName());

    summaryDataView.addKeyValue(VERTICAL_HETEROGENEITY, verticalHeterogeneityRange == null
        ? ""
        : verticalHeterogeneityRange.getDisplayName());

    summaryDataView.addKeyValue(AREAL_DENSITY, arealDensityRange == null
        ? ""
        : arealDensityRange.getDisplayName());

    summaryDataView.addKeyValue(STRUCTURAL_COMPLEXITY, structuralComplexityRange == null
        ? ""
        : structuralComplexityRange.getDisplayName());

    return summaryDataView;
  }

  /*
   * calculates the complexity or null if any required parameters are null
   * @param form with viscosity, verticalHeterogeneity, arealDensity, structuralComplexity as non-null
   * @return calculated complexity, or null
   */
  public static BigDecimal calculateComplexity(BaseComplexityForm form) {

    // if any required params are null then return null
    if (ObjectUtils.anyNull(
        form.getViscosityRange(),
        form.getVerticalHeterogeneityRange(),
        form.getArealDensityRange(),
        form.getStructuralComplexityRange())) {
      return null;
    }

    var viscosity = form.getViscosityRange().getValue();
    var verticalHeterogeneity = form.getVerticalHeterogeneityRange().getValue();
    var arealDensity = form.getArealDensityRange().getValue();
    var structuralComplexity = form.getStructuralComplexityRange().getValue();

    var calculatedComplexity = (2 * viscosity) + verticalHeterogeneity + arealDensity + (2 * structuralComplexity);
    return BigDecimal.valueOf(calculatedComplexity);
  }
}
