package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.complexity.ComplexityUtil;
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
    var calculatedFieldComplexity = ComplexityUtil.calculateComplexity(form);
    fieldComplexity.setCalculatedFieldComplexity(calculatedFieldComplexity);

    fieldComplexityRepository.save(fieldComplexity);
  }
}
