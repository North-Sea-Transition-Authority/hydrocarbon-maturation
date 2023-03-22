package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class DryWellDetailsPersistenceService {

  private final DryWellDetailsRepository dryWellDetailsRepository;
  private final WellQueryService wellQueryService;

  @Autowired
  public DryWellDetailsPersistenceService(DryWellDetailsRepository dryWellDetailsRepository,
                                          WellQueryService wellQueryService) {
    this.dryWellDetailsRepository = dryWellDetailsRepository;
    this.wellQueryService = wellQueryService;
  }

  @Transactional
  public void saveDryWellDetails(DryWellDetailsForm form, SubmissionDetail submissionDetail) {
    var dryWellDetail = getDryWellDetailBySubmissionDetail(submissionDetail)
        .orElse(new DryWellDetail());

    var detailForm = form.getDrillDetailsForm();
    if (detailForm.getWell() != null) {
      dryWellDetail.setWellId(Integer.valueOf(detailForm.getWell()));
    }
    if (!StringUtils.isBlank(detailForm.getWell())) {
      dryWellDetail.setWellReference(wellQueryService
          .findWellById(Integer.valueOf(detailForm.getWell()))
          .map(WellView::registeredNumber)
          .orElse(null)
      );
    }
    dryWellDetail.setSubmissionDetail(submissionDetail);
    dryWellDetail.setDryHoleComments(form.getDryHoleComments());
    dryWellDetail.setDryHoleScope(form.getDryHoleScope());
    dryWellDetail.setTopReservoirDatum(detailForm.getReservoirDatum());
    dryWellDetail.setDryHoleDescription(form.getDryHoleDescription());
    dryWellDetail.setTopReservoirLatitudeDegrees(
        detailForm.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull());
    dryWellDetail.setTopReservoirLatitudeMinutes(
        detailForm.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull());
    dryWellDetail.setTopReservoirLatitudeSeconds(
        detailForm.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull());
    dryWellDetail.setTopReservoirLongitudeDegrees(
        detailForm.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull());
    dryWellDetail.setTopReservoirLongitudeMinutes(
        detailForm.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull());
    dryWellDetail.setTopReservoirLongitudeSeconds(
        detailForm.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull());
    dryWellDetail.setTopReservoirLongitudeHemisphere(detailForm.getCoordinateInputForm().getLongitudeHemisphere());
    dryWellDetail.setDataAcquisitionProgramme(detailForm.getDataAcquisitionProgramme());
    dryWellDetail.setGeophysicalUpdateCommentary(detailForm.getGeophysicalUpdate());
    dryWellDetail.setTotalDepthFormation(detailForm.getTotalDepthFormation());
    dryWellDetail.setDryHoleType(form.getDryHoleType());
    dryWellDetail.setDryHoleCost(form.getDryHoleCost().createBigDecimalOrNull());
    dryWellDetail.setTotalDepthFormationComments(detailForm.getTotalDepthFormationComments());
    dryWellDetail.setMigrationFailureCategory(form.getMigrationFailureCategory());
    dryWellDetail.setReservoirFailureCategory(form.getReservoirFailureCategory());
    dryWellDetail.setSealFailureCategory(form.getSealFailureCategory());
    dryWellDetail.setSourceFailureCategory(form.getSourceFailureCategory());
    dryWellDetail.setTrapFailureCategory(form.getTrapFailureCategory());
    dryWellDetailsRepository.save(dryWellDetail);
  }

  public Optional<DryWellDetail> getDryWellDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return dryWellDetailsRepository.findBySubmissionDetail(submissionDetail);
  }
}
