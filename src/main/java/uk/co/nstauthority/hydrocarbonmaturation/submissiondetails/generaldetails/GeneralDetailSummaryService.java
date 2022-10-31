package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class GeneralDetailSummaryService {

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Autowired
  public GeneralDetailSummaryService(GeneralDetailsPersistenceService generalDetailsPersistenceService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var generalDetail = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail);
    return generalDetail.map(this::createSummaryViewFromGeneralDetail)
        .orElse(GeneralDetailSummaryView.empty());
  }

  private GeneralDetailSummaryView createSummaryViewFromGeneralDetail(GeneralDetail detail) {
    var prospectName = detail.getProspectName() != null ? detail.getProspectName() : "";
    var onshoreOrOffshore = detail.getOnshoreOffshore() != null ? detail.getOnshoreOffshore().getDisplayName() : "";
    var locatedWithinUkcs = BooleanUtil.yesNoFromBoolean(detail.getProspectLocatedInUkcs(), "");

    var percentageLocatedInUkcs =
        detail.getPercentageLocatedInUkcs() != null
            && !detail.getProspectLocatedInUkcs() ? detail.getPercentageLocatedInUkcs().toString() + "%" : "";

    var informationSourceDisplay = "";
    var licenceApplication = "";
    var isLicenceApplicationInfoSource = false;
    var informationSource = detail.getInformationSource();
    if (informationSource != null) {
      if (informationSource == InformationSource.LICENCE_APPLICATION) {
        licenceApplication = detail.getLicenceApplicationReference() != null
            ? detail.getLicenceApplicationReference()
            : "";
        isLicenceApplicationInfoSource = true;
      }
      if (informationSource == InformationSource.OTHER) {
        informationSourceDisplay = detail.getOtherInformationSource();
      } else {
        informationSourceDisplay = informationSource.getDisplayName();
      }
    }

    return new GeneralDetailSummaryView(
        prospectName,
        onshoreOrOffshore,
        locatedWithinUkcs,
        percentageLocatedInUkcs,
        informationSourceDisplay,
        isLicenceApplicationInfoSource,
        licenceApplication);
  }
}
