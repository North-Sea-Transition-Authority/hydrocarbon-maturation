package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.time.Clock;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcegroups.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Service
public class SubmissionDetailPersistenceService {

  private final SubmissionDetailRepository submissionDetailRepository;
  private final Clock clock;
  private final UserDetailService userDetailService;
  private final ResourceGroupService resourceGroupService;
  private final RegulatorTeamService regulatorTeamService;

  @Autowired
  public SubmissionDetailPersistenceService(SubmissionDetailRepository submissionDetailRepository,
                                            Clock clock,
                                            UserDetailService userDetailService,
                                            ResourceGroupService resourceGroupService,
                                            RegulatorTeamService regulatorTeamService) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.clock = clock;
    this.userDetailService = userDetailService;
    this.resourceGroupService = resourceGroupService;
    this.regulatorTeamService = regulatorTeamService;
  }

  SubmissionDetail createInitialSubmissionDetail(Resource resource, SubmissionType submissionType) {
    var submissionDetail = new SubmissionDetail();
    var user = userDetailService.getUserDetail();
    submissionDetail.setResourceId(resource);
    submissionDetail.setCreatedByWuaId(user.wuaId().intValue());
    submissionDetail.setCreatedTimestamp(clock.instant());
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setVersionNumber(SubmissionDetailService.FIRST_VERSION);
    if (regulatorTeamService.isRegulatorUser(user)) {
      submissionDetail.setSubmitterType(SubmitterType.NSTA);
    } else {
      submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    }
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  @Transactional
  void approveSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.APPROVED);
    submissionDetail.setApprovedTimestamp(clock.instant());
    submissionDetail.setApprovedByWuaId(userDetailService.getUserWuaId());
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
    submissionDetailRepository.save(submissionDetail);
  }

  @Transactional
  public void withdrawSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.WITHDRAWN);
    submissionDetail.setWithdrawnTimestamp(clock.instant());
    submissionDetail.setWithdrawnByWuaId(userDetailService.getUserWuaId());
    submissionDetailRepository.save(submissionDetail);
  }
}