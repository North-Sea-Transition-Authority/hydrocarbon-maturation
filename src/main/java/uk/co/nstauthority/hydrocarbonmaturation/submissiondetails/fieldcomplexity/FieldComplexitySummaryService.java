package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class FieldComplexitySummaryService implements SummarySectionService<SubmissionDetail> {

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

  @Override
  public Optional<SummarySection> getSummarySection(SubmissionDetail submissionDetail) {
    if (!submissionDetail.isFieldSubmission()) {
      return Optional.empty();
    }
    var summaryItems = List.of(getFieldComplexitySummary(submissionDetail));
    return Optional.of(new SummarySection(FIELD_COMPLEXITY_DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryItem getFieldComplexitySummary(SubmissionDetail submissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());
    fieldComplexityRepository.findBySubmissionDetail(submissionDetail)
        .ifPresentOrElse(
            fieldComplexity -> {
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
            },

            () -> {
              summaryDataView.addKeyValue(VISCOSITY, "");
              summaryDataView.addKeyValue(VERTICAL_HETEROGENEITY, "");
              summaryDataView.addKeyValue(AREAL_DENSITY, "");
              summaryDataView.addKeyValue(STRUCTURAL_COMPLEXITY, "");
              summaryDataView.addKeyValue(FIELD_COMPLEXITY, "");
            }
        );

    var summaryCard = SummaryCard.simpleSummaryCard(
        summaryDataView
    );

    return SummaryItem.withCard(FIELD_COMPLEXITY_SUMMARY, summaryCard);
  }
}
