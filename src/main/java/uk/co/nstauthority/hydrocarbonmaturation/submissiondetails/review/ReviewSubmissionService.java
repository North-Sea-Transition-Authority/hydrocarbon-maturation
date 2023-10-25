package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdate;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class ReviewSubmissionService {

  private final SubmissionRecordSummaryQueryService submissionRecordSummaryQueryService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final RegulatorTeamService regulatorTeamService;
  private final IndustryTeamMemberService industryTeamMemberService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final SubmissionUpdateService submissionUpdateService;
  private final ResourceLinkService resourceLinkService;
  private final ResponsibleTeamService responsibleTeamService;

  @Autowired
  ReviewSubmissionService(SubmissionRecordSummaryQueryService submissionRecordSummaryQueryService,
                          SubmissionDetailDisplayService submissionDetailDisplayService,
                          RegulatorTeamService regulatorTeamService,
                          IndustryTeamMemberService industryTeamMemberService,
                          SubmissionDetailService submissionDetailService,
                          ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                          SubmissionUpdateService submissionUpdateService,
                          ResourceLinkService resourceLinkService,
                          ResponsibleTeamService responsibleTeamService) {
    this.submissionRecordSummaryQueryService = submissionRecordSummaryQueryService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.regulatorTeamService = regulatorTeamService;
    this.industryTeamMemberService = industryTeamMemberService;
    this.submissionDetailService = submissionDetailService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.submissionUpdateService = submissionUpdateService;
    this.resourceLinkService = resourceLinkService;
    this.responsibleTeamService = responsibleTeamService;
  }

  public SubmissionRecordSummaryView getSubmissionRecordOrThrow(UUID resourceId) {
    return submissionRecordSummaryQueryService.getSubmissionRecord(resourceId)
        .map(submissionRecordSummaryQueryResult -> new SubmissionRecordSummaryView(
            submissionRecordSummaryQueryResult.getSubmissionDetailId(),
            submissionRecordSummaryQueryResult.getSubmissionType(),
            getResourceNameAndCentreOfInterest(submissionRecordSummaryQueryResult),
            submissionRecordSummaryQueryResult.getOrganisationResponsibleId(),
            getOrganisationResponsible(submissionRecordSummaryQueryResult),
            submissionRecordSummaryQueryResult.getLicencesTheResourceIsOn(),
            getLicences(submissionRecordSummaryQueryResult),
            getResponsibleTeam(submissionRecordSummaryQueryResult),
            submissionRecordSummaryQueryResult.getResponsibleTeam()
            )
        )
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission record not found for resource ID %s"
            .formatted(resourceId)));
  }

  boolean canViewSubmission(SubmissionRecordSummaryView submissionRecord,
                            ServiceUserDetail user,
                            SubmissionDetail submissionDetail) {
    if (!submissionDetail.isApprovedOrSubmittedSubmission()) {
      return false;
    }

    if (responsibleTeamService.isInResponsibleTeam(user, submissionRecord.responsibleTeamType())) {
      return true;
    }

    if (submissionRecord.submissionType() == SubmissionType.CREATE_PROSPECT
        || submissionRecord.submissionType() == SubmissionType.CREATE_DISCOVERY) {
      //when there is no latest approved submission on resource, checks the current submission
      var organisationUnitId = submissionRecord.responsibleOrganisationId();
      return industryTeamMemberService.isMemberOfOrganisationUnit(user, organisationUnitId);
    } else {
      return isResponsibleOrganisationMemberOnLatestApprovedSubmissionOnResource(user, submissionDetail);
    }
  }

  boolean canUpdateSubmission(SubmissionRecordSummaryView submissionRecord,
                              ServiceUserDetail userDetail,
                              SubmissionDetail submissionDetail) {
    if (submissionRecord.submissionType() == SubmissionType.CREATE_PROSPECT
        || submissionRecord.submissionType() == SubmissionType.CREATE_DISCOVERY) {
      //when there is no latest approved submission on resource, checks the current submission
      var organisationUnitId = submissionRecord.responsibleOrganisationId();
      return industryTeamMemberService.isSubmitterOfOrganisationUnit(userDetail, organisationUnitId);
    } else {
      return isSubmitterForResponsibleOrgOnLatestApprovedSubmissionOnResource(userDetail, submissionDetail);
    }
  }

  boolean canReviewSubmission(SubmissionRecordSummaryView submissionRecord,
                              ServiceUserDetail userDetail,
                              SubmissionDetail currentSubmission) {
    return currentSubmission.getStatus() == SubmissionDetailStatus.SUBMITTED
        && (ResponsibleTeamType.EXPLORATION.equals(submissionRecord.responsibleTeamType())
        && regulatorTeamService.isRegulatorTeamRole(userDetail, RegulatorTeamRole.EXPLORATION));
  }

  boolean canRequestUpdateOnSubmissionDetail(SubmissionDetail submissionDetail) {
    return submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail);
  }

  boolean hasUpdateBeenRequestedOnSubmission(Submission submission) {
    return submissionUpdateService.hasAnUnresolvedUpdateOnSubmission(submission);
  }

  Optional<SubmissionUpdate> getLatestUnresolvedSubmissionUpdateBySubmission(Submission submission) {
    return submissionUpdateService.getLatestUnresolvedUpdateOnSubmission(submission);
  }

  boolean canViewSelectedSubmissionDetail(SubmissionDetail selectedSubmissionDetail, Resource currentResource) {
    //can only view submitted or approved submissions on the same resource, or linked resource
    //the submission detail to be viewed also has to be the latest update on submission
    return selectedSubmissionDetail.isApprovedOrSubmittedSubmission()
        && BooleanUtils.isNotTrue(selectedSubmissionDetail.getSubmission().isWithdrawn())
        && resourceLinkService.isResourceLinkedOrSameResource(
            currentResource, selectedSubmissionDetail.getSubmission().getResource())
        && submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
            selectedSubmissionDetail, Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.SUBMITTED));
  }

  Optional<SubmissionDetail> getUpdateSubmissionDetailBySubmission(Submission submission) {
    return submissionUpdateService.getDraftUpdateSubmissionDetailBySubmission(submission);
  }

  private boolean isSubmitterForResponsibleOrgOnLatestApprovedSubmissionOnResource(
      ServiceUserDetail user, SubmissionDetail submissionDetail) {
    return submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail)
        .flatMap(resourceDetailsPersistenceService::getResourceDetailBySubmissionDetail)
        .map(resourceDetail -> industryTeamMemberService
                    .isSubmitterOfOrganisationUnit(user, resourceDetail.getOrganisationId()))
        .orElse(false);
  }

  private boolean isResponsibleOrganisationMemberOnLatestApprovedSubmissionOnResource(
      ServiceUserDetail user, SubmissionDetail submissionDetail) {
    return submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail)
        .map(latestSubmissionDetail ->
          resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestSubmissionDetail)
                  .map(resourceDetail -> industryTeamMemberService
                      .isMemberOfOrganisationUnit(user, resourceDetail.getOrganisationId()))
                  .orElse(false))
        .orElse(false);
  }

  private String getResourceNameAndCentreOfInterest(
      SubmissionRecordSummaryQueryResult submissionRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getResourceNameAndCentreOfInterestBlockDisplay(
        submissionRecordSummaryQueryResult.getResourceName(),
        submissionRecordSummaryQueryResult.getCentreOfInterestQuadBlock()
    );
  }

  private List<String> getLicences(SubmissionRecordSummaryQueryResult submissionRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getLicencesForDisplay(submissionRecordSummaryQueryResult.getLicencesTheResourceIsOn());
  }

  private String getOrganisationResponsible(SubmissionRecordSummaryQueryResult submissionRecordSummaryQueryResult) {
    return submissionDetailDisplayService.getOrganisationResponsibleDisplay(
        submissionRecordSummaryQueryResult.getOrganisationResponsibleId());
  }

  private static String getResponsibleTeam(SubmissionRecordSummaryQueryResult submissionRecordSummaryQueryResult) {
    return submissionRecordSummaryQueryResult.getResponsibleTeam().getDisplayName();
  }
}
