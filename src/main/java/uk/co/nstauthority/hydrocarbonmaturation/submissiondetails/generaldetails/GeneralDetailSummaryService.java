package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class GeneralDetailSummaryService {

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @Autowired
  public GeneralDetailSummaryService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                     QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var generalDetail = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail);
    return generalDetail.map(detail -> {
      var quadrantAndBlocks = quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(detail);
      return createSummaryViewFromGeneralDetail(detail, quadrantAndBlocks);
    })
    .orElse(GeneralDetailSummaryView.empty());
  }

  private GeneralDetailSummaryView createSummaryViewFromGeneralDetail(GeneralDetail detail,
                                                                      List<QuadrantAndBlock> quadrantAndBlocks) {
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
    var quadAndBlockDisplayList = quadrantAndBlocks.stream()
        .map(QuadrantAndBlock::getQuadrantBlock)
        .toList();

    return new GeneralDetailSummaryView(
        prospectName,
        onshoreOrOffshore,
        locatedWithinUkcs,
        percentageLocatedInUkcs,
        informationSourceDisplay,
        isLicenceApplicationInfoSource,
        licenceApplication,
        quadAndBlockDisplayList);
  }
}
