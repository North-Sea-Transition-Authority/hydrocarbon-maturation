package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class PermeabilityPersistenceService {

  private final PermeabilityDetailRepository permeabilityDetailRepository;

  @Autowired
  PermeabilityPersistenceService(PermeabilityDetailRepository permeabilityDetailRepository) {
    this.permeabilityDetailRepository = permeabilityDetailRepository;
  }

  @Transactional
  public void savePermeabilityDetail(PermeabilityDryWellForm form, SubmissionDetail submissionDetail) {
    var permeabilityDetail = getPermeabilityDetail(submissionDetail)
        .orElse(new PermeabilityDetail());
    var permeabilityForm = form.getPermeabilityForm();
    permeabilityDetail.setSubmissionDetail(submissionDetail);
    permeabilityDetail.setHasPermeability(form.getHasPermeability());
    if (BooleanUtils.isTrue(form.getHasPermeability())) {
      permeabilityDetail.setPermeabilityAverage(
          permeabilityForm.getPermeabilityAverage().createBigDecimalOrNull());
      permeabilityDetail.setPermeabilityComments(permeabilityForm.getPermeabilityComments());
      permeabilityDetail.setPermeabilityMax(permeabilityForm.getPermeabilityMax().createBigDecimalOrNull());
      permeabilityDetail.setPermeabilityMin(permeabilityForm.getPermeabilityMin().createBigDecimalOrNull());
      permeabilityDetail.setPermeabilitySource(permeabilityForm.getPermeabilitySource());
      permeabilityDetail.setAverageMethodUsed(permeabilityForm.getAverageMethodUsed());
      permeabilityDetail.setKvKhAverage(permeabilityForm.getKvKhAverage().createBigDecimalOrNull());
      permeabilityDetail.setKvKhComments(permeabilityForm.getKvKhComments());
      permeabilityDetail.setKvKhMax(permeabilityForm.getKvKhMax().createBigDecimalOrNull());
      permeabilityDetail.setKvKhMin(permeabilityForm.getKvKhMin().createBigDecimalOrNull());
      permeabilityDetail.setKvKhAverageMethodUsed(permeabilityForm.getKvKhAverageMethodUsed());
      permeabilityDetail.setKvKhSource(permeabilityForm.getKvKhSource());

      if (permeabilityForm.getKvKhAverageMethodUsed() == PermeabilitySource.OTHER) {
        permeabilityDetail.setKvKhAverageMethodUsedOther(permeabilityForm.getKvKhAverageMethodUsedOther());
      }

      if (permeabilityForm.getKvKhSource() == PermeabilitySource.OTHER) {
        permeabilityDetail.setKvKhSourceOther(permeabilityForm.getOtherKvKhSource());
      }

      if (permeabilityForm.getPermeabilitySource() == PermeabilitySource.OTHER) {
        permeabilityDetail.setSourceOther(permeabilityForm.getOtherPermeabilitySource());
      }
    }

    permeabilityDetailRepository.save(permeabilityDetail);
  }

  Optional<PermeabilityDetail> getPermeabilityDetail(SubmissionDetail submissionDetail) {
    return permeabilityDetailRepository.findBySubmissionDetail(submissionDetail);
  }
}
