package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class ResourceDetailSummaryService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  public ResourceDetailSummaryService(ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                      OrganisationUnitQueryService organisationUnitQueryService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  public ResourceDetailSummaryView getResourceDetailSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail);
    return resourceDetail.map(this::createSummaryViewFromResourceDetail)
        .orElse(ResourceDetailSummaryView.empty(
            DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(submissionDetail.getSubmission().getResource().getType())
                .toLowerCase())
        );
  }

  private ResourceDetailSummaryView createSummaryViewFromResourceDetail(ResourceDetail detail) {
    var orgNameAtTimeOfApproval =  getSavedOrganisationDisplay(detail);
    var currentOrganisationNameAndNumber = getCurrentOrganisationDisplay(detail);
    var draftOrSubmitted = detail.getSubmissionDetail().isDraftOrSubmittedSubmission();
    var showNameAtTimeOfApproval = !draftOrSubmitted && !orgNameAtTimeOfApproval.equals(currentOrganisationNameAndNumber);
    return new ResourceDetailSummaryView(
        StringUtils.defaultIfBlank(detail.getResourceName(), ""),
        currentOrganisationNameAndNumber,
        showNameAtTimeOfApproval,
        orgNameAtTimeOfApproval,
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(detail.getOnshoreOffshore()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(
            detail.getSubmissionDetail().getSubmission().getResource().getType())
            .toLowerCase()
    );
  }

  private String getSavedOrganisationDisplay(ResourceDetail detail) {
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

  private String getCurrentOrganisationDisplay(ResourceDetail detail) {
    return organisationUnitQueryService.getOrganisationUnitNameAndNumberByIdOrEmpty(detail.getOrganisationId());
  }
}

