package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ResourceDetailSummaryService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Autowired
  public ResourceDetailSummaryService(ResourceDetailsPersistenceService resourceDetailsPersistenceService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
  }

  public ResourceDetailSummaryView getResourceDetailSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail);
    return resourceDetail.map(this::createSummaryViewFromResourceDetail)
        .orElse(ResourceDetailSummaryView.empty(
            DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(submissionDetail.getResourceId().getType())
                .toLowerCase())
        );
  }

  private ResourceDetailSummaryView createSummaryViewFromResourceDetail(ResourceDetail detail) {
    return new ResourceDetailSummaryView(
        StringUtils.defaultIfBlank(detail.getResourceName(), ""),
        getOrganisationDisplay(detail),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(detail.getOnshoreOffshore()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(detail.getSubmissionDetail().getResourceId().getType())
            .toLowerCase()
    );
  }

  private String getOrganisationDisplay(ResourceDetail detail) {
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
}

