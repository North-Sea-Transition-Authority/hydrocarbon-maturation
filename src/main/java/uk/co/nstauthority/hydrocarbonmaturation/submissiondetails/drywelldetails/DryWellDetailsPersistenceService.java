package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class DryWellDetailsPersistenceService {

  private final DryWellDetailsRepository dryWellDetailsRepository;

  @Autowired
  public DryWellDetailsPersistenceService(DryWellDetailsRepository dryWellDetailsRepository) {
    this.dryWellDetailsRepository = dryWellDetailsRepository;
  }

  @Transactional
  public void saveDryWellDetails(DryWellDetailsForm form, SubmissionDetail submissionDetail) {
    var dryWellDetail = getDryWellDetailBySubmissionDetail(submissionDetail)
        .orElse(new DryWellDetail());

    var detailForm = form.getDrillDetailsForm();
    dryWellDetail.setDryWellId(detailForm.getDryWell());
    dryWellDetail.setDryWellName(detailForm.getDryWell());
    dryWellDetail.setSubmissionDetail(submissionDetail);
    dryWellDetail.setDryHoleComments(form.getDryHoleComments());
    dryWellDetail.setDryHoleScope(form.getDryHoleScope());
    dryWellDetail.setDatum(detailForm.getReservoirDatum());
    dryWellDetail.setDryHoleDescription(form.getDryHoleDescription());
    dryWellDetail.setLatitudeDegrees(
        detailForm.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull());
    dryWellDetail.setLatitudeMinutes(
        detailForm.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull());
    dryWellDetail.setLatitudeSeconds(
        detailForm.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull());
    dryWellDetail.setLongitudeDegrees(
        detailForm.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull());
    dryWellDetail.setLongitudeMinutes(
        detailForm.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull());
    dryWellDetail.setLongitudeSeconds(
        detailForm.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull());
    dryWellDetail.setLongitudeHemisphere(detailForm.getCoordinateInputForm().getLongitudeHemisphere());
    dryWellDetail.setDataAcquisitionProgramme(detailForm.getDataAcquisitionProgramme());
    dryWellDetail.setGeophysicalUpdateCommentary(detailForm.getGeophysicalUpdate());
    dryWellDetail.setTotalDepthFormation(detailForm.getTotalDepthFormation());
    dryWellDetail.setDryHoleType(form.getDryHoleType());
    dryWellDetail.setDryHoleCost(form.getDryHoleCost().createBigDecimalOrNull());
    dryWellDetail.setTotalDepthFormationComments(detailForm.getTotalDepthFormationComments());

    dryWellDetailsRepository.save(dryWellDetail);
  }

  public Optional<DryWellDetail> getDryWellDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return dryWellDetailsRepository.findBySubmissionDetail(submissionDetail);
  }
}
