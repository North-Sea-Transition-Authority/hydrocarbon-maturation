package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
class ReviewSubmissionService {

  private final SubmissionRecordSummaryQueryService submissionRecordSummaryQueryService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final RegulatorTeamService regulatorTeamService;
  private final IndustryTeamMemberService industryTeamMemberService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Autowired
  ReviewSubmissionService(SubmissionRecordSummaryQueryService submissionRecordSummaryQueryService,
                          SubmissionDetailDisplayService submissionDetailDisplayService,
                          RegulatorTeamService regulatorTeamService,
                          IndustryTeamMemberService industryTeamMemberService,
                          SubmissionDetailService submissionDetailService,
                          ResourceDetailsPersistenceService resourceDetailsPersistenceService) {
    this.submissionRecordSummaryQueryService = submissionRecordSummaryQueryService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.regulatorTeamService = regulatorTeamService;
    this.industryTeamMemberService = industryTeamMemberService;
    this.submissionDetailService = submissionDetailService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
  }

  SubmissionRecordSummaryView getSubmissionRecordOrThrow(UUID submissionDetailId) {
    return submissionRecordSummaryQueryService.getSubmissionRecord(submissionDetailId)
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
        .orElseThrow(() -> new RemiEntityNotFoundException("Submission record not found for submission ID %s"
            .formatted(submissionDetailId)));
  }

  boolean canViewSubmission(SubmissionRecordSummaryView submissionRecord,
                            ServiceUserDetail user,
                            SubmissionDetail submissionDetail) {
    if (ResponsibleTeamType.EXPLORATION.equals(submissionRecord.responsibleTeamType())
        && regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION)) {
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

  boolean canReviewSubmission(SubmissionRecordSummaryView submissionRecord, ServiceUserDetail userDetail) {
    return ResponsibleTeamType.EXPLORATION.equals(submissionRecord.responsibleTeamType())
        && regulatorTeamService.isRegulatorTeamRole(userDetail, RegulatorTeamRole.EXPLORATION);
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
        submissionRecordSummaryQueryResult.getCentreOfInterestQuadBlock(),
        submissionRecordSummaryQueryResult.getCreatedOn()
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
