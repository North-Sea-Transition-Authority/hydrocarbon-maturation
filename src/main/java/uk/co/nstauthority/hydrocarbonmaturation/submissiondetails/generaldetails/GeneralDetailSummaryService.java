package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Comparator;
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
    var licenceApplication = "";
    var isLicenceApplicationInfoSource = false;
    var informationSource = detail.getInformationSource();
    if (informationSource == InformationSource.LICENCE_APPLICATION) {
      licenceApplication = detail.getLicenceApplicationReference() != null
          ? detail.getLicenceApplicationReference()
          : "";
      isLicenceApplicationInfoSource = true;
    }
    return new GeneralDetailSummaryView(
        getProspectNameDisplay(detail),
        getOnShoreOffShoreDisplay(detail),
        BooleanUtil.yesNoFromBoolean(detail.getProspectLocatedInUkcs(), ""),
        getPercentageLocatedInUkcs(detail),
        getInformationSourceDisplay(detail),
        getOrganisationDisplay(detail),
        isLicenceApplicationInfoSource,
        licenceApplication,
        getQuadBlocksDisplay(quadrantAndBlocks)
    );
  }

  private List<String> getQuadBlocksDisplay(List<QuadrantAndBlock> quadrantAndBlocks) {
    return quadrantAndBlocks.stream()
        .sorted(Comparator.comparing(QuadrantAndBlock::getSortKey))
        .map(QuadrantAndBlock::getQuadrantBlock)
        .toList();
  }

  private String getOrganisationDisplay(GeneralDetail detail) {
    var organisationName = detail.getOrganisationName();
    var organisationRegisteredNumber = detail.getOrganisationRegisteredNumber();
    var organisationDisplay = "";
    if (organisationName != null) {
      if (organisationRegisteredNumber != null) {
        organisationDisplay = "%s (%s)".formatted(organisationName,
            organisationRegisteredNumber);
      } else {
        organisationDisplay = organisationName;
      }
    }
    return organisationDisplay;
  }

  private String getInformationSourceDisplay(GeneralDetail detail) {
    var informationSourceDisplay = "";
    var informationSource = detail.getInformationSource();
    if (informationSource != null) {
      if (informationSource == InformationSource.OTHER) {
        informationSourceDisplay = detail.getOtherInformationSource();
      } else {
        informationSourceDisplay = informationSource.getDisplayName();
      }
    }
    return informationSourceDisplay;
  }

  private String getOnShoreOffShoreDisplay(GeneralDetail detail) {
    return detail.getOnshoreOffshore() != null
        ? detail.getOnshoreOffshore().getDisplayName()
        : "";
  }

  private String getProspectNameDisplay(GeneralDetail detail) {
    return detail.getProspectName() != null
        ? detail.getProspectName()
        : "";
  }

  private String getPercentageLocatedInUkcs(GeneralDetail detail) {
    return detail.getPercentageLocatedInUkcs() != null && !detail.getProspectLocatedInUkcs()
        ? detail.getPercentageLocatedInUkcs().toString() + "%"
        : "";
  }
}
