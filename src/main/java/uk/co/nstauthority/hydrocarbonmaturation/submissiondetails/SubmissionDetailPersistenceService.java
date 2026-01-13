package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import jakarta.transaction.Transactional;
import java.time.Clock;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation.RegulatorReportingInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Service
public class SubmissionDetailPersistenceService {

  private final SubmissionDetailRepository submissionDetailRepository;
  private final Clock clock;
  private final ResourceGroupService resourceGroupService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final InternalTeamService internalTeamService;
  private final RegulatorReportingInformationService regulatorReportingInformationService;

  @Autowired
  public SubmissionDetailPersistenceService(SubmissionDetailRepository submissionDetailRepository,
                                            Clock clock,
                                            ResourceGroupService resourceGroupService,
                                            ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                                            InternalTeamService internalTeamService,
                                            RegulatorReportingInformationService regulatorReportingInformationService) {
    this.submissionDetailRepository = submissionDetailRepository;
    this.clock = clock;
    this.resourceGroupService = resourceGroupService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.internalTeamService = internalTeamService;
    this.regulatorReportingInformationService = regulatorReportingInformationService;
  }

  public SubmissionDetail createInitialSubmissionDetail(Submission submission, SubmissionType submissionType, User user) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setCreatedByWuaId(user.wuaId().intValue());
    submissionDetail.setCreatedTimestamp(clock.instant());
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setSubmission(submission);
    submissionDetail.setVersionNumber(SubmissionDetailService.FIRST_VERSION);
    if (internalTeamService.isRegulatorOrAreaTeamUser(user)) {
      submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    } else {
      submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    }
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  SubmissionDetail createUpdateSubmissionDetail(SubmissionDetail previousSubmissionDetail, User user) {
    var submissionDetail = createBaseSubmissionDetailFromPrevious(previousSubmissionDetail, user);
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  SubmissionDetail createCorrectionSubmissionDetail(SubmissionDetail previousSubmissionDetail,
                                                    User user,
                                                    CorrectionType correctionType) {
    var submissionDetail = createBaseSubmissionDetailFromPrevious(previousSubmissionDetail, user);
    submissionDetail.setCorrection(true);
    submissionDetail.setCorrectionType(correctionType);
    submissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    submissionDetail.setCorrectionTimestamp(Instant.now());
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    submissionDetailRepository.save(submissionDetail);
    return submissionDetail;
  }

  private SubmissionDetail createBaseSubmissionDetailFromPrevious(SubmissionDetail previousSubmissionDetail, User user) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setCreatedByWuaId(user.wuaId().intValue());
    submissionDetail.setCreatedTimestamp(clock.instant());
    submissionDetail.setStatus(SubmissionDetailStatus.DRAFT);
    submissionDetail.setSubmissionType(previousSubmissionDetail.getSubmissionType());
    submissionDetail.setSubmission(previousSubmissionDetail.getSubmission());
    submissionDetail.setVersionNumber(previousSubmissionDetail.getVersionNumber() + 1);
    return submissionDetail;
  }

  @Transactional
  void approveSubmission(SubmissionDetail submissionDetail, User user) {
    submissionDetail.setStatus(SubmissionDetailStatus.ACCEPTED);
    submissionDetail.setAcceptedTimestamp(clock.instant());
    submissionDetail.setAcceptedByWuaId(user.wuaId().intValue());

    resourceDetailsPersistenceService.updateOrganisationName(submissionDetail);
    submissionDetailRepository.save(submissionDetail);

    if (submissionDetail.isInitialResourceSubmission()) {
      resourceGroupService.createResourceGroupLink(submissionDetail, user);
      var resourceGroup = resourceGroupService.getResourceGroupLinkByResource(submissionDetail.getSubmission().getResource())
          .getResourceGroup();
      regulatorReportingInformationService.saveRegulatorReportingInformationForNewResource(submissionDetail, resourceGroup);
    }

  }

  @Transactional
  void submitSubmission(SubmissionDetail submissionDetail, User user) {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    submissionDetail.setSubmittedTimestamp(clock.instant());
    submissionDetail.setSubmittedByWuaId(user.wuaId().intValue());
    submissionDetailRepository.save(submissionDetail);
  }

  @Transactional
  public void deleteSubmission(SubmissionDetail submissionDetail) {
    submissionDetail.setStatus(SubmissionDetailStatus.DELETED);
    if (submissionDetail.isUpdateToSubmission()) {
      submissionDetail.setVersionNumber(null);
    }
    submissionDetailRepository.save(submissionDetail);
  }

  @Transactional
  public void saveSubmissionDetail(SubmissionDetail submissionDetail) {
    submissionDetailRepository.save(submissionDetail);
  }
}