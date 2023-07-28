package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class PermeabilityPersistenceService {

  private final PermeabilityDetailRepository permeabilityDetailRepository;

  @Autowired
  PermeabilityPersistenceService(PermeabilityDetailRepository permeabilityDetailRepository) {
    this.permeabilityDetailRepository = permeabilityDetailRepository;
  }

  @Transactional
  public void savePermeabilityDetail(PermeabilityForm form, ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var permeabilityDetail = getPermeabilityDetail(reservoirSubmissionDetail)
        .orElse(new PermeabilityDetail());
    permeabilityDetail.setReservoirSubmissionDetail(reservoirSubmissionDetail);

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

  Optional<PermeabilityDetail> getPermeabilityDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return permeabilityDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail);
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
