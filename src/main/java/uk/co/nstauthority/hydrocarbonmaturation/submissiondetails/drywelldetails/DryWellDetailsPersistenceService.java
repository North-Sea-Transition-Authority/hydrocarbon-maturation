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
    dryWellDetail.setDryWellId(form.getDryWell());
    dryWellDetail.setDryWellName(form.getDryWell());
    dryWellDetail.setSubmissionDetail(submissionDetail);
    dryWellDetail.setDryHoleComments(form.getDryHoleComments());
    dryWellDetail.setDryHoleScope(form.getDryHoleScope());
    dryWellDetail.setDatum(form.getReservoirDatum());
    dryWellDetail.setDryHoleDescription(form.getDryHoleDescription());
    dryWellDetail.setLatitudeDegrees(
        form.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull());
    dryWellDetail.setLatitudeMinutes(
        form.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull());
    dryWellDetail.setLatitudeSeconds(
        form.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull());
    dryWellDetail.setLongitudeDegrees(
        form.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull());
    dryWellDetail.setLongitudeMinutes(
        form.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull());
    dryWellDetail.setLongitudeSeconds(
        form.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull());
    dryWellDetail.setLongitudeHemisphere(form.getCoordinateInputForm().getLongitudeHemisphere());
    dryWellDetail.setDataAcquisitionProgramme(form.getDataAcquisitionProgramme());
    dryWellDetail.setGeophysicalUpdateCommentary(form.getGeophysicalUpdate());
    dryWellDetail.setTotalDepthFormation(form.getTotalDepthFormation());
    dryWellDetail.setDryHoleType(form.getDryHoleType());
    dryWellDetail.setDryHoleCost(form.getDryHoleCost().createBigDecimalOrNull());
    dryWellDetail.setTotalDepthFormationComments(form.getTotalDepthFormationComments());

    dryWellDetailsRepository.save(dryWellDetail);
  }

  public Optional<DryWellDetail> getDryWellDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return dryWellDetailsRepository.findBySubmissionDetail(submissionDetail);
  }
}
