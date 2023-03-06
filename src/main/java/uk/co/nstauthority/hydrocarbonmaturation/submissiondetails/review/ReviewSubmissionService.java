package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import java.util.List;
import java.util.UUID;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.LicencePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
class ReviewSubmissionService {

  private final SubmissionRecordSummaryQueryService submissionRecordSummaryQueryService;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final RegulatorTeamService regulatorTeamService;
  private final IndustryTeamMemberService industryTeamMemberService;
  private final LicenceQueryService licenceQueryService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final LicencePersistenceService licencePersistenceService;

  @Autowired
  ReviewSubmissionService(SubmissionRecordSummaryQueryService submissionRecordSummaryQueryService,
                          SubmissionDetailDisplayService submissionDetailDisplayService,
                          RegulatorTeamService regulatorTeamService,
                          IndustryTeamMemberService industryTeamMemberService,
                          LicenceQueryService licenceQueryService,
                          SubmissionDetailService submissionDetailService,
                          ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                          GeneralDetailsPersistenceService generalDetailsPersistenceService,
                          LicencePersistenceService licencePersistenceService) {
    this.submissionRecordSummaryQueryService = submissionRecordSummaryQueryService;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.regulatorTeamService = regulatorTeamService;
    this.industryTeamMemberService = industryTeamMemberService;
    this.licenceQueryService = licenceQueryService;
    this.submissionDetailService = submissionDetailService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.licencePersistenceService = licencePersistenceService;
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
      var memberOfOrganisationUnit = industryTeamMemberService.isMemberOfOrganisationUnit(user, organisationUnitId);
      if (!memberOfOrganisationUnit) {
        return false;
      }
      return isSubmissionRecordAssociatedWithAnExtantLicence(submissionRecord);
    } else {
      return hasExtantLicenceAndOrganisationMembershipOnLatestApprovedSubmissionOnResource(user, submissionDetail);
    }
  }

  boolean canReviewSubmission(SubmissionRecordSummaryView submissionRecord, ServiceUserDetail userDetail) {
    return ResponsibleTeamType.EXPLORATION.equals(submissionRecord.responsibleTeamType())
        && regulatorTeamService.isRegulatorTeamRole(userDetail, RegulatorTeamRole.EXPLORATION);
  }

  private boolean hasExtantLicenceAndOrganisationMembershipOnLatestApprovedSubmissionOnResource(
      ServiceUserDetail user, SubmissionDetail submissionDetail) {
    return submissionDetailService.getLatestApprovedSubmissionForSubmission(submissionDetail)
        .map(latestSubmissionDetail -> {
          var resourceIsAssociatedWithAnExtantLicence =
              generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(latestSubmissionDetail)
                  .map(licencePersistenceService::hasAnExtantLicence)
                  .orElse(false);
          if (!resourceIsAssociatedWithAnExtantLicence) {
            return false;
          }
          return resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(latestSubmissionDetail)
                  .map(resourceDetail -> industryTeamMemberService
                      .isMemberOfOrganisationUnit(user, resourceDetail.getOrganisationId()))
                  .orElse(false);
        })
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

  private boolean isSubmissionRecordAssociatedWithAnExtantLicence(SubmissionRecordSummaryView submissionRecord) {
    if (!CollectionUtils.isEmpty(submissionRecord.licenceIds())) {
      var extantLicences = licenceQueryService.getExtantLicencesByIds(
          NumberUtil.toListOfStrings(submissionRecord.licenceIds()));
      return !extantLicences.isEmpty();
    } else {
      return false;
    }
  }
}
