package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ExtendWith(MockitoExtension.class)
class CorrectionCreateServiceTest {

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private SubmissionPersistenceService submissionPersistenceService;

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @Mock
  private OrganisationCorrectionService organisationCorrectionService;

  @Mock
  private ResourceGroupService resourceGroupService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  @Captor
  private ArgumentCaptor<SubmissionDetail> submissionDetailArgumentCaptor;

  private Clock clock;

  private CorrectionCreateService correctionCreateService;

  private Resource resource;
  private User user;

  @BeforeEach
  void setUp() {
    correctionCreateService = new CorrectionCreateService(
        submissionDetailService,
        submissionCreateService,
        submissionPersistenceService,
        submissionDetailPersistenceService,
        organisationCorrectionService,
        postFieldDiscoveryLinkService,
        resourceLinkService,
        resourceGroupService,
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
    );

    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    user = UserTestUtil.Builder().build();
  }

  @Test
  void createSubmissionCorrectionAndRedirect() {
    var previousSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var correctionType = CorrectionType.SUBMISSION_CORRECTION;

    when(submissionDetailService.getSubmissionDetailForIdOrError(previousSubmissionDetail.getId())).thenReturn(previousSubmissionDetail);

    var correctionSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    correctionSubmissionDetail.setCorrection(true);
    correctionSubmissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    correctionSubmissionDetail.setCorrectionTimestamp(clock.instant());
    correctionSubmissionDetail.setCorrectionType(correctionType);
    when(submissionCreateService.createCorrectionSubmissionDetail(previousSubmissionDetail, user, correctionType))
        .thenReturn(correctionSubmissionDetail);

    var modelAndView = correctionCreateService.createSubmissionCorrectionAndRedirect(previousSubmissionDetail.getId(), user);

    verify(submissionDetailPersistenceService, times(1)).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());
    var submissionDetail = submissionDetailArgumentCaptor.getValue();
    assertThat(submissionDetail).extracting(
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getCorrectionType,
        SubmissionDetail::getSubmitterType
    ).containsExactly(
        true,
        user.wuaId().intValue(),
        correctionType,
        SubmitterType.REGULATOR
    );
    assertNotNull(submissionDetailArgumentCaptor.getValue().getCorrectionTimestamp());
    assertEquals(
        ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null)).getView(),
        modelAndView.getView()
    );
  }

  @Test
  void startCorrectionForType_historicUpdate_forProspect() {
    var previousSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);

    var correctionType = CorrectionType.HISTORIC_UPDATE;

    when(submissionDetailService.getSubmissionDetailForIdOrError(previousSubmissionDetail.getId())).thenReturn(previousSubmissionDetail);

    var newSubmission = new Submission();
    newSubmission.setResource(previousSubmissionDetail.getSubmission().getResource());
    when(submissionPersistenceService.createSubmission(previousSubmissionDetail.getSubmission().getResource())).thenReturn(newSubmission);

    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.PROSPECT_UPDATE);
    newSubmissionDetail.setCorrection(true);
    newSubmissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    newSubmissionDetail.setCorrectionTimestamp(Instant.now());
    newSubmissionDetail.setCorrectionType(correctionType);
    when(submissionCreateService.createCorrectionSubmissionDetail(previousSubmissionDetail, user, correctionType)).thenReturn(newSubmissionDetail);

    var modelAndView = correctionCreateService.createMissingUpdateAndRedirect(
        previousSubmissionDetail.getId(),
        "1",
        "1",
        "2023",
        user
    );

    historicUpdateAssertions(1, 1, 2023, modelAndView, correctionType);
  }

  @Test
  void startCorrectionForType_historicUpdate_forDiscovery() {
    var previousSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    var correctionType = CorrectionType.HISTORIC_UPDATE;

    when(submissionDetailService.getSubmissionDetailForIdOrError(previousSubmissionDetail.getId())).thenReturn(previousSubmissionDetail);

    var newSubmission = new Submission();
    newSubmission.setResource(previousSubmissionDetail.getSubmission().getResource());
    when(submissionPersistenceService.createSubmission(previousSubmissionDetail.getSubmission().getResource())).thenReturn(newSubmission);

    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.UPDATE_DISCOVERY);
    newSubmissionDetail.setCorrection(true);
    newSubmissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    newSubmissionDetail.setCorrectionTimestamp(Instant.now());
    newSubmissionDetail.setCorrectionType(correctionType);
    when(submissionCreateService.createCorrectionSubmissionDetail(previousSubmissionDetail, user, correctionType))
        .thenReturn(newSubmissionDetail);

    var modelAndView = correctionCreateService.createMissingUpdateAndRedirect(
        previousSubmissionDetail.getId(),
        "1",
        "1",
        "2023",
        user
    );

    historicUpdateAssertions(1, 1, 2023, modelAndView, correctionType);
  }

  @Test
  void startCorrectionForType_historicUpdate_forField() {
    var previousSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
    previousSubmissionDetail.setAcceptedTimestamp(LocalDate.of(2022, 1, 1)
        .atStartOfDay(ZoneId.systemDefault()).toInstant());

    when(submissionDetailService.getExtantSubmissionDetailsForResource(resource)).thenReturn(List.of(previousSubmissionDetail));

    var newSubmission = new Submission();
    newSubmission.setResource(previousSubmissionDetail.getSubmission().getResource());
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(newSubmission);

    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP);
    newSubmissionDetail.setCorrection(true);
    newSubmissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    newSubmissionDetail.setCorrectionTimestamp(Instant.now());
    newSubmissionDetail.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    when(submissionCreateService.createCorrectionSubmissionDetail(previousSubmissionDetail, user, CorrectionType.HISTORIC_UPDATE))
        .thenReturn(newSubmissionDetail);

    var modelAndView = correctionCreateService.createMissingFieldUpdateAndRedirect(
        resource,
        SubmissionType.CREATE_FDP,
        "1",
        "1",
        "2023",
        user
    );

    historicUpdateAssertions(1, 1, 2023, modelAndView, CorrectionType.HISTORIC_UPDATE);
  }

  private void historicUpdateAssertions(int day,
                                        int month,
                                        int year,
                                        ModelAndView modelAndView,
                                        CorrectionType correctionType) {
    verify(submissionDetailPersistenceService, times(1)).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());
    var submissionDetail = submissionDetailArgumentCaptor.getValue();
    var approvedDate = LocalDate.of(
        year,
        month,
        day
    ).atStartOfDay(ZoneId.systemDefault()).toInstant();
    var wuaId = user.wuaId().intValue();

    assertThat(submissionDetail).extracting(
        SubmissionDetail::getStatus,
        SubmissionDetail::getVersionNumber,
        SubmissionDetail::getSubmittedByWuaId,
        SubmissionDetail::getSubmittedTimestamp,
        SubmissionDetail::getAcceptedByWuaId,
        SubmissionDetail::getAcceptedTimestamp,
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getCorrectionType,
        SubmissionDetail::getSubmitterType
    ).containsExactly(
        SubmissionDetailStatus.DRAFT,
        1,
        wuaId,
        approvedDate,
        wuaId,
        approvedDate,
        true,
        wuaId,
        correctionType,
        SubmitterType.REGULATOR
    );
    assertNotNull(submissionDetail.getCorrectionTimestamp());
    assertEquals(
        ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId(), null)).getView(),
        modelAndView.getView()
    );
  }

  @Test
  void startCorrectionForType_approvalDateChange() {
    var selectedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);

    var correctionType = CorrectionType.ACCEPTED_DATE_CHANGE;

    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionDetail.getId())).thenReturn(selectedSubmissionDetail);

    var correctionSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    correctionSubmissionDetail.setCorrection(true);
    correctionSubmissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    correctionSubmissionDetail.setCorrectionTimestamp(Instant.now());
    correctionSubmissionDetail.setCorrectionType(correctionType);

    when(submissionCreateService.createCorrectionSubmissionDetail(selectedSubmissionDetail, user, correctionType)).thenReturn(correctionSubmissionDetail);

    var modelAndView = correctionCreateService.updateApprovedDateAndRedirect(
        selectedSubmissionDetail.getId(),
        "1",
        "1",
        "2023",
        user
    );

    verify(submissionDetailPersistenceService, times(1)).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());

    var submissionDetail = submissionDetailArgumentCaptor.getValue();
    var approvedDate = LocalDate.of(2023,1,1).atStartOfDay(ZoneId.systemDefault()).toInstant();
    var wuaId = user.wuaId().intValue();

    assertThat(submissionDetail).extracting(
        SubmissionDetail::getStatus,
        SubmissionDetail::getSubmittedByWuaId,
        SubmissionDetail::getSubmittedTimestamp,
        SubmissionDetail::getAcceptedByWuaId,
        SubmissionDetail::getAcceptedTimestamp,
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getCorrectionType,
        SubmissionDetail::getSubmitterType
    ).containsExactly(
        SubmissionDetailStatus.ACCEPTED,
        wuaId,
        approvedDate,
        wuaId,
        approvedDate,
        true,
        wuaId,
        correctionType,
        SubmitterType.REGULATOR
    );
    assertNotNull(submissionDetail.getCorrectionTimestamp());
    assertEquals(
        ReverseRouter.redirect(on(ViewResourceRecordController.class)
            .showSubmissionSummary(submissionDetail.getSubmission().getResource().getId(), submissionDetail.getId(), null, null)).getView(),
        modelAndView.getView()
    );
  }

  @Test
  void deleteSubmission() {
    var firstSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var secondSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(firstSubmissionDetail);
    when(submissionDetailService.getSubmissionDetailsBySubmission(secondSubmissionDetail.getSubmission())).thenReturn(
        List.of(firstSubmissionDetail, secondSubmissionDetail)
    );

    correctionCreateService.deleteSubmission(secondSubmissionDetail, user);
    assertThat(firstSubmissionDetail).extracting(
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getCorrectionTimestamp,
        SubmissionDetail::getCorrectionType,
        SubmissionDetail::getSubmitterType,
        SubmissionDetail::getStatus
    ).containsExactly(
        true,
        user.wuaId().intValue(),
        clock.instant(),
        CorrectionType.REMOVE_SUBMISSION,
        SubmitterType.REGULATOR,
        SubmissionDetailStatus.DELETED
    );
    assertThat(secondSubmissionDetail).extracting(
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getCorrectionTimestamp,
        SubmissionDetail::getSubmitterType,
        SubmissionDetail::getStatus
    ).containsExactly(
        true,
        user.wuaId().intValue(),
        clock.instant(),
        SubmitterType.REGULATOR,
        SubmissionDetailStatus.DELETED
    );
    verify(resourceLinkService).deleteDiscoveryToProspectLink(secondSubmissionDetail.getSubmission().getResource());
    verify(resourceGroupService).removeResourceFromGroup(secondSubmissionDetail.getSubmission().getResource());
    verify(postFieldDiscoveryLinkService).deletePostFieldDiscoveryLinkIfExists(secondSubmissionDetail.getSubmission().getResource());
  }

  @Test
  void deleteSubmission_notInitialSubmission() {
    var firstSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.UPDATE_DISCOVERY);
    var secondSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(firstSubmissionDetail);
    when(submissionDetailService.getSubmissionDetailsBySubmission(secondSubmissionDetail.getSubmission())).thenReturn(
        List.of(firstSubmissionDetail, secondSubmissionDetail)
    );

    correctionCreateService.deleteSubmission(secondSubmissionDetail, user);
    assertThat(firstSubmissionDetail).extracting(
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getCorrectionTimestamp,
        SubmissionDetail::getCorrectionType,
        SubmissionDetail::getSubmitterType,
        SubmissionDetail::getStatus
    ).containsExactly(
        true,
        user.wuaId().intValue(),
        clock.instant(),
        CorrectionType.REMOVE_SUBMISSION,
        SubmitterType.REGULATOR,
        SubmissionDetailStatus.DELETED
    );
    assertThat(secondSubmissionDetail).extracting(
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getCorrectionTimestamp,
        SubmissionDetail::getSubmitterType,
        SubmissionDetail::getStatus
    ).containsExactly(
        true,
        user.wuaId().intValue(),
        clock.instant(),
        SubmitterType.REGULATOR,
        SubmissionDetailStatus.DELETED
    );
    verify(resourceLinkService, never()).deleteDiscoveryToProspectLink(any(Resource.class));
    verify(resourceGroupService, never()).removeResourceFromGroup(any(Resource.class));
    verify(postFieldDiscoveryLinkService, never()).deletePostFieldDiscoveryLinkIfExists(any(Resource.class));
  }

}
