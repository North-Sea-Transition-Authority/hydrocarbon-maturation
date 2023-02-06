package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@Service
class PermeabilityPersistenceService {

  private final PermeabilityDetailRepository permeabilityDetailRepository;

  @Autowired
  PermeabilityPersistenceService(PermeabilityDetailRepository permeabilityDetailRepository) {
    this.permeabilityDetailRepository = permeabilityDetailRepository;
  }

  @Transactional
  public void savePermeabilityDetail(PermeabilityForm form, SubmissionDetail submissionDetail) {
    var permeabilityDetail = getPermeabilityDetail(submissionDetail)
        .orElse(new PermeabilityDetail());
    permeabilityDetail.setSubmissionDetail(submissionDetail);

    if (submissionDetail.getSubmissionType() == SubmissionType.POST_DRILL_DRY_HOLE) {
      permeabilityDetail.setHasPermeability(form.getHasPermeability());
      if (BooleanUtils.isTrue(form.getHasPermeability())) {
        setPermeabilityDetails(permeabilityDetail, form.getPermeabilityValuesForm());
      }
    } else {
      setPermeabilityDetails(permeabilityDetail, form.getPermeabilityValuesForm());
    }

    permeabilityDetailRepository.save(permeabilityDetail);
  }

  Optional<PermeabilityDetail> getPermeabilityDetail(SubmissionDetail submissionDetail) {
    return permeabilityDetailRepository.findBySubmissionDetail(submissionDetail);
  }

  private void setPermeabilityDetails(PermeabilityDetail permeabilityDetail, PermeabilityValuesForm permeabilityValuesForm) {
    permeabilityDetail.setPermeabilityAverage(
        permeabilityValuesForm.getPermeabilityAverage().createBigDecimalOrNull());
    permeabilityDetail.setPermeabilityComments(permeabilityValuesForm.getPermeabilityComments());
    permeabilityDetail.setPermeabilityMax(permeabilityValuesForm.getPermeabilityMax().createBigDecimalOrNull());
    permeabilityDetail.setPermeabilityMin(permeabilityValuesForm.getPermeabilityMin().createBigDecimalOrNull());
    permeabilityDetail.setPermeabilitySource(permeabilityValuesForm.getPermeabilitySource());
    permeabilityDetail.setAverageMethodUsed(permeabilityValuesForm.getAverageMethodUsed());
    permeabilityDetail.setKvKhAverage(permeabilityValuesForm.getKvKhAverage().createBigDecimalOrNull());

    if (permeabilityValuesForm.getPermeabilitySource() == PermeabilitySource.OTHER) {
      permeabilityDetail.setSourceOther(permeabilityValuesForm.getOtherPermeabilitySource());
    }
  }
}
