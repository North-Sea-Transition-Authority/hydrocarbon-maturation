package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import org.apache.commons.lang3.BooleanUtils;
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
            DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(submissionDetail.getSubmission().getResource().getType())
                .toLowerCase())
        );
  }

  private ResourceDetailSummaryView createSummaryViewFromResourceDetail(ResourceDetail resourceDetail) {
    var orgNameAtTimeOfApproval = resourceDetailsPersistenceService.getOrganisationDisplayFromResourceDetails(resourceDetail);
    var currentOrganisationNameAndNumber = resourceDetailsPersistenceService.getOrganisationNameFromPortal(resourceDetail);
    var submissionDetail = resourceDetail.getSubmissionDetail();
    var draftOrSubmitted = submissionDetail.isDraftOrSubmittedSubmission();
    var isCorrection = BooleanUtils.isTrue(submissionDetail.isCorrection());
    var showNameAtTimeOfApproval = (!draftOrSubmitted || isCorrection)
        && !orgNameAtTimeOfApproval.equals(currentOrganisationNameAndNumber);
    return new ResourceDetailSummaryView(
        StringUtils.defaultIfBlank(resourceDetail.getResourceName(), ""),
        currentOrganisationNameAndNumber,
        showNameAtTimeOfApproval,
        orgNameAtTimeOfApproval,
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(resourceDetail.getOnshoreOffshore()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(
            resourceDetail.getSubmissionDetail().getSubmission().getResource().getType())
            .toLowerCase()
    );
  }
}

