package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.complexity.ComplexitySummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;

@Service
public class FieldComplexitySummaryService {

  public static final String FIELD_COMPLEXITY_SUMMARY = "Field complexity";

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
              var summaryDataView = ComplexitySummaryUtil.getComplexitySummaryDataView(
                  fieldComplexity.getViscosityRange(),
                  fieldComplexity.getVerticalHeterogeneityRange(),
                  fieldComplexity.getArealDensityRange(),
                  fieldComplexity.getStructuralComplexityRange()
              );

              summaryDataView.addKeyValue(FIELD_COMPLEXITY, fieldComplexity.getCalculatedFieldComplexity() == null
                  ? ""
                  : fieldComplexity.getCalculatedFieldComplexity());

              return SummaryCard.simpleSummaryCardWithHeading(FIELD_COMPLEXITY, summaryDataView);
            }
        ).orElse(SummaryCard.emptySummaryCardWithHeading(FIELD_COMPLEXITY));
  }
}
