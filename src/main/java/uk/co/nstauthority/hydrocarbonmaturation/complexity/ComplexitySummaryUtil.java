package uk.co.nstauthority.hydrocarbonmaturation.complexity;

import java.util.ArrayList;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;

public class ComplexitySummaryUtil {

  private ComplexitySummaryUtil() {
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
}
