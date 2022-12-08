package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class GeneralDetailSummaryService {

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final LicencePersistenceService licencePersistenceService;

  @Autowired
  public GeneralDetailSummaryService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                                     QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                     LicencePersistenceService licencePersistenceService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var generalDetail = generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail);
    return generalDetail.map(detail -> {
      var quadrantAndBlocks = quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(detail);
      var licences = licencePersistenceService.getLicencesByGeneralDetail(detail)
          .stream()
          .sorted(Comparator.comparing(Licence::getLicenceReference))
          .toList();
      return createSummaryViewFromGeneralDetail(detail, quadrantAndBlocks, licences);
    })
    .orElse(GeneralDetailSummaryView.empty(getResourceTypeDisplay(submissionDetail)));
  }

  private GeneralDetailSummaryView createSummaryViewFromGeneralDetail(GeneralDetail detail,
                                                                      List<QuadrantAndBlock> quadrantAndBlocks,
                                                                      List<Licence> licences) {
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
        getResourceNameDisplay(detail),
        getOnShoreOffShoreDisplay(detail),
        BooleanUtil.yesNoFromBoolean(detail.getResourceOnALicence(), ""),
        getIsResourceOnALicenceBoolean(detail),
        BooleanUtil.yesNoFromBoolean(detail.getResourceLocatedInUkcs(), ""),
        getPercentageLocatedInUkcs(detail),
        getInformationSourceDisplay(detail),
        getOrganisationDisplay(detail),
        isLicenceApplicationInfoSource,
        licenceApplication,
        getQuadBlocksDisplay(quadrantAndBlocks),
        getLicenceDisplay(licences),
        getResourceTypeDisplay(detail.getSubmissionDetail())
    );
  }

  private String getResourceTypeDisplay(SubmissionDetail submissionDetail) {
    return submissionDetail.getResourceId().getType().getDisplayName().toLowerCase();
  }

  private boolean getIsResourceOnALicenceBoolean(GeneralDetail detail) {
    return BooleanUtils.isTrue(detail.getResourceOnALicence())
        || detail.getResourceOnALicence() == null;
  }

  private List<String> getQuadBlocksDisplay(List<QuadrantAndBlock> quadrantAndBlocks) {
    return quadrantAndBlocks.stream()
        .sorted(Comparator.comparing(QuadrantAndBlock::getSortKey))
        .map(QuadrantAndBlock::getQuadrantBlock)
        .toList();
  }

  private List<String> getLicenceDisplay(List<Licence> licences) {
    return licences.stream()
        .sorted(Comparator.comparing(Licence::getLicenceReference))
        .map(Licence::getLicenceReference)
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

  private String getResourceNameDisplay(GeneralDetail detail) {
    return detail.getResourceName() != null
        ? detail.getResourceName()
        : "";
  }

  private String getPercentageLocatedInUkcs(GeneralDetail detail) {
    return detail.getPercentageLocatedInUkcs() != null && !detail.getResourceLocatedInUkcs()
        ? detail.getPercentageLocatedInUkcs().toString() + "%"
        : "";
  }
}
