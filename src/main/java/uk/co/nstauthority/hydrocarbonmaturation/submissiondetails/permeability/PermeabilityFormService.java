package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class PermeabilityFormService {

  private final PermeabilityPersistenceService permeabilityPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  @Autowired
  public PermeabilityFormService(PermeabilityPersistenceService permeabilityPersistenceService,
                                 ProbabilityDistributionFormService probabilityDistributionFormService) {
    this.permeabilityPersistenceService = permeabilityPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
  }

  PermeabilityForm getPermeabilityForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail)
        .map(detail -> permeabilityDetailToForm(reservoirSubmissionDetail, detail))
        .orElse(setProbabilityDistributions(new PermeabilityForm(), reservoirSubmissionDetail));
  }

  private PermeabilityForm permeabilityDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                    PermeabilityDetail permeabilityDetail) {
    var form = new PermeabilityForm();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      var permeabilityForm = form.getPermeabilityValuesForm();
      form.setHasPermeability(permeabilityDetail.getHasPermeability());
      if (BooleanUtils.isTrue(permeabilityDetail.getHasPermeability())) {
        setPermeabilityForm(permeabilityForm, permeabilityDetail, reservoirSubmissionDetail);
      }
    } else {
      setPermeabilityForm(form.getPermeabilityValuesForm(), permeabilityDetail, reservoirSubmissionDetail);
    }

    return form;
  }

  private void setPermeabilityForm(PermeabilityValuesForm permeabilityValuesForm, PermeabilityDetail permeabilityDetail,
                                   ReservoirSubmissionDetail reservoirSubmissionDetail) {
    permeabilityValuesForm.setAverageMethodUsed(permeabilityDetail.getAverageMethodUsed());
    permeabilityValuesForm.setPermeabilityAverage(
        DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityAverage()));
    permeabilityValuesForm.setPermeabilityComments(permeabilityDetail.getPermeabilityComments());
    permeabilityValuesForm.setPermeabilityMax(
        DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityMax()));
    permeabilityValuesForm.setPermeabilityMin(
        DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityMin()));
    permeabilityValuesForm.setPermeabilityAverage(
        DecimalInput.createDecimalInput(permeabilityDetail.getPermeabilityAverage()));
    permeabilityValuesForm.setPermeabilitySource(permeabilityDetail.getPermeabilitySource());
    permeabilityValuesForm.setKvKhAverage(DecimalInput.createDecimalInput(permeabilityDetail.getKvKhAverage()));

    if (permeabilityDetail.getPermeabilitySource() == PermeabilitySource.OTHER) {
      permeabilityValuesForm.setOtherPermeabilitySource(permeabilityDetail.getSourceOther());
    }

    setProbabilityDistributions(permeabilityValuesForm, reservoirSubmissionDetail);
  }

  private PermeabilityForm setProbabilityDistributions(PermeabilityForm permeabilityForm,
                                                       ReservoirSubmissionDetail reservoirSubmissionDetail) {
    setProbabilityDistributions(permeabilityForm.getPermeabilityValuesForm(), reservoirSubmissionDetail);
    return permeabilityForm;
  }

  private void setProbabilityDistributions(PermeabilityValuesForm permeabilityValuesForm,
                                          ReservoirSubmissionDetail reservoirSubmissionDetail) {
    if (reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      permeabilityValuesForm.setPermeability(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.FIELD_PERMEABILITY
      ));

      permeabilityValuesForm.setHorizontalPermeability(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.FIELD_HORIZONTAL_PERMEABILITY
      ));

      permeabilityValuesForm.setVerticalPermeability(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.FIELD_VERTICAL_PERMEABILITY
      ));
    }
  }
}
