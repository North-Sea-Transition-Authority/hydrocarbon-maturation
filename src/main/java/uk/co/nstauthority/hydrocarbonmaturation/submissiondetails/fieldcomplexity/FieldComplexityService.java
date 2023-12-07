package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import java.math.BigDecimal;
import javax.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class FieldComplexityService {

  private final FieldComplexityRepository fieldComplexityRepository;

  @Autowired
  public FieldComplexityService(FieldComplexityRepository fieldComplexityRepository) {
    this.fieldComplexityRepository = fieldComplexityRepository;
  }

  public FieldComplexityForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new FieldComplexityForm();
    fieldComplexityRepository.findBySubmissionDetail(submissionDetail)
        .ifPresent(
            fieldComplexity -> {
              form.setViscosityRange(fieldComplexity.getViscosityRange());
              form.setVerticalHeterogeneityRange(fieldComplexity.getVerticalHeterogeneityRange());
              form.setArealDensityRange(fieldComplexity.getArealDensityRange());
              form.setStructuralComplexityRange(fieldComplexity.getStructuralComplexityRange());
            }
        );
    return form;
  }

  @Transactional
  public void saveFieldComplexity(FieldComplexityForm form, SubmissionDetail submissionDetail) {
    var fieldComplexity = fieldComplexityRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new FieldComplexity());
    fieldComplexity.setSubmissionDetail(submissionDetail);

    fieldComplexity.setViscosityRange(form.getViscosityRange());
    fieldComplexity.setVerticalHeterogeneityRange(form.getVerticalHeterogeneityRange());
    fieldComplexity.setArealDensityRange(form.getArealDensityRange());
    fieldComplexity.setStructuralComplexityRange(form.getStructuralComplexityRange());

    // calculate field complexity
    var calculatedFieldComplexity = calculateFieldComplexity(form);
    fieldComplexity.setCalculatedFieldComplexity(
        calculatedFieldComplexity == null
            ? null
            : BigDecimal.valueOf(calculatedFieldComplexity)
    );

    fieldComplexityRepository.save(fieldComplexity);
  }

  /*
   * calculates the field complexity or null if any required parameters are null
   * @param form with viscosity, verticalHeterogeneity, arealDensity, structuralComplexity as non-null
   * @return calculated field complexity, or null
   */
  private Integer calculateFieldComplexity(FieldComplexityForm form) {

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

    return (2 * viscosity) + verticalHeterogeneity + arealDensity + (2 * structuralComplexity);
  }
}
