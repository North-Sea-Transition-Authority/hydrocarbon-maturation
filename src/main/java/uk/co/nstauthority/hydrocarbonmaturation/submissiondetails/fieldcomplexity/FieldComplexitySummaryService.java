package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;

@Service
public class FieldComplexitySummaryService {

  public static final int FIELD_COMPLEXITY_DISPLAY_ORDER = 15;
  public static final String FIELD_COMPLEXITY_SUMMARY = "Field complexity";

  public static final String VISCOSITY = "Viscosity";
  public static final String VERTICAL_HETEROGENEITY = "Vertical heterogeneity";
  public static final String AREAL_DENSITY = "Areal density";
  public static final String STRUCTURAL_COMPLEXITY = "Structural complexity";
  public static final String FIELD_COMPLEXITY = "Field complexity";

  private final FieldComplexityRepository fieldComplexityRepository;

  @Autowired
  public FieldComplexitySummaryService(FieldComplexityRepository fieldComplexityRepository) {
    this.fieldComplexityRepository = fieldComplexityRepository;
  }

  public SummaryCard getFieldComplexitySummary(SubmissionDetail submissionDetail) {
    return fieldComplexityRepository.findBySubmissionDetail(submissionDetail)
        .map(
            fieldComplexity -> {
              var summaryDataView = new SummaryDataView(new ArrayList<>());

              var viscosityRange = fieldComplexity.getViscosityRange();
              summaryDataView.addKeyValue(VISCOSITY, viscosityRange == null
                  ? ""
                  : viscosityRange.getDisplayName());

              var verticalHeterogeneityRange = fieldComplexity.getVerticalHeterogeneityRange();
              summaryDataView.addKeyValue(VERTICAL_HETEROGENEITY, verticalHeterogeneityRange == null
                  ? ""
                  : verticalHeterogeneityRange.getDisplayName());

              var arealDensityRange = fieldComplexity.getArealDensityRange();
              summaryDataView.addKeyValue(AREAL_DENSITY, arealDensityRange == null
                  ? ""
                  : arealDensityRange.getDisplayName());

              var structuralComplexityRange = fieldComplexity.getStructuralComplexityRange();
              summaryDataView.addKeyValue(STRUCTURAL_COMPLEXITY, structuralComplexityRange == null
                  ? ""
                  : structuralComplexityRange.getDisplayName());

              summaryDataView.addKeyValue(FIELD_COMPLEXITY, fieldComplexity.getCalculatedFieldComplexity() == null
                  ? ""
                  : fieldComplexity.getCalculatedFieldComplexity());

              return SummaryCard.simpleSummaryCardWithHeading(FIELD_COMPLEXITY,  summaryDataView);
            }
        ).orElse(SummaryCard.emptySummaryCardWithHeading(FIELD_COMPLEXITY));
  }
}
