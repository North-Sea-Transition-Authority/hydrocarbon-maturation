package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.time.Clock;
import java.time.Instant;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class SubmissionDetailPersistenceService {

  private final SubmissionDetailRepository submissionDetailRepository;
  private final Clock clock;
  private final UserDetailService userDetailService;
  private final ResourceGroupService resourceGroupService;
  private final RegulatorTeamService regulatorTeamService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final AreaTeamService areaTeamService;

  @Autowired
  public SubmissionDetailPersistenceService(SubmissionDetailRepository submissionDetailRepository,
                                            Clock clock,
                                            UserDetailService userDetailService,
                                            ResourceGroupService resourceGroupService,
                                            RegulatorTeamService regulatorTeamService,
                                            ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                            AreaTeamService areaTeamService) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.clock = clock;
    this.userDetailService = userDetailService;
    this.resourceGroupService = resourceGroupService;
    this.regulatorTeamService = regulatorTeamService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.areaTeamService = areaTeamService;
  }

  public SubmissionDetail createInitialSubmissionDetail(Submission submission, SubmissionType submissionType) {
    var submissionDetail = new SubmissionDetail();
    var user = userDetailService.getUserDetail();
    submissionDetail.setCreatedByWuaId(user.wuaId().intValue());
    submissionDetail.setCreatedTimestamp(clock.instant());
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setSubmission(submission);
    submissionDetail.setVersionNumber(SubmissionDetailService.FIRST_VERSION);
    if (regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user)) {
      submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    } else {
      submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    }
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  SubmissionDetail createUpdateSubmissionDetail(SubmissionDetail previousSubmissionDetail) {
    var submissionDetail = createBaseSubmissionDetailFromPrevious(previousSubmissionDetail);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  SubmissionDetail createCorrectionSubmissionDetail(SubmissionDetail previousSubmissionDetail, ServiceUserDetail user) {
    var submissionDetail = createBaseSubmissionDetailFromPrevious(previousSubmissionDetail);
    submissionDetail.setCorrection(true);
    submissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    submissionDetail.setCorrectionTimestamp(Instant.now());
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  private SubmissionDetail createBaseSubmissionDetailFromPrevious(SubmissionDetail previousSubmissionDetail) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setCreatedByWuaId(userDetailService.getUserWuaId());
    submissionDetail.setCreatedTimestamp(clock.instant());
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmissionType(previousSubmissionDetail.getSubmissionType());
    submissionDetail.setSubmission(previousSubmissionDetail.getSubmission());
    submissionDetail.setVersionNumber(previousSubmissionDetail.getVersionNumber() + 1);
    return submissionDetail;
  }

  @Transactional
  void approveSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail.setAcceptedTimestamp(clock.instant());
    submissionDetail.setAcceptedByWuaId(userDetailService.getUserWuaId());

    resourceDetailsPersistenceService.updateOrganisationName(submissionDetail);
    submissionDetailRepository.save(submissionDetail);

    if (submissionDetail.isInitialResourceSubmission()) {
      resourceGroupService.createResourceGroupLink(submissionDetail);
    }
  }

  @Transactional
  void submitSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmittedTimestamp(clock.instant());
    submissionDetail.setSubmittedByWuaId(userDetailService.getUserWuaId());
    submissionDetailRepository.save(submissionDetail);
  }

  @Transactional
  public void deleteSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    if (submissionDetail.isUpdateSubmission()) {
      submissionDetail.setVersionNumber(null);
    }
    submissionDetailRepository.save(submissionDetail);
  }

  @Transactional
  public void saveSubmissionDetail(SubmissionDetail submissionDetail) {
    submissionDetailRepository.save(submissionDetail);
  }
}