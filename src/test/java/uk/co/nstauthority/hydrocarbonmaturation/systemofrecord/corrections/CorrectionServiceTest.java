package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@ExtendWith(MockitoExtension.class)
class CorrectionServiceTest {

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private SubmissionCreateService submissionCreateService;

  @Mock
  private SubmissionPersistenceService submissionPersistenceService;

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @Mock
  private SubmissionDetailDuplicationService submissionDetailDuplicationService;

  private CorrectionService correctionService;

  private Resource resource;
  private ServiceUserDetail user;

  @BeforeEach
  void setUp() {
    correctionService = new CorrectionService(
        regulatorManagedInformationPersistenceService,
        responsibleTeamService,
        submissionCreateService,
        submissionDetailService,
        submissionPersistenceService,
        submissionDetailPersistenceService,
        submissionDetailDuplicationService);

    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    user = ServiceUserDetailTestUtil.Builder().build();
  }

  @Test
  void canStartCorrection() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.of(regulatorManagedInformation)
    );
    when(responsibleTeamService.isInResponsibleTeam(user, regulatorManagedInformation.getResponsibleTeamType())).thenReturn(true);
    when(submissionDetailService.openCorrectionExists(Set.of(SubmissionDetailStatus.DRAFT), resource)).thenReturn(false);
    assertTrue(correctionService.canStartCorrection(resource, user));
  }

  @Test
  void cantStartCorrect_whenNoRegulatorManagedInfo() {
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.empty()
    );

    assertFalse(correctionService.canStartCorrection(resource, user));
  }

  @Test
  void cantStartCorrection_whenNoResponsibleTeam() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.of(regulatorManagedInformation)
    );
    assertFalse(correctionService.canStartCorrection(resource, user));
  }

  @Test
  void cantStartCorrection_whenNotInResponsibleTeam() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.of(regulatorManagedInformation)
    );
    when(responsibleTeamService.isInResponsibleTeam(user, regulatorManagedInformation.getResponsibleTeamType())).thenReturn(false);
    assertFalse(correctionService.canStartCorrection(resource, user));
  }

  @Test
  void cantStartCorrection_whenDraftCorrectionExists() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.of(regulatorManagedInformation)
    );
    when(responsibleTeamService.isInResponsibleTeam(user, regulatorManagedInformation.getResponsibleTeamType())).thenReturn(true);
    when(submissionDetailService.openCorrectionExists(Set.of(SubmissionDetailStatus.DRAFT), resource)).thenReturn(true);
    assertFalse(correctionService.canStartCorrection(resource, user));
  }

  @Test
  void startCorrectionForType_submissionCorrection() {
    var previousSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.SUBMISSION_CORRECTION);
    form.setSubmissionIdForCorrectingInformation(previousSubmissionDetail.getId().toString());
    when(submissionDetailService.getSubmissionDetailForIdOrError(previousSubmissionDetail.getId())).thenReturn(previousSubmissionDetail);
    var correctionSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    when(submissionCreateService.createUpdateSubmissionDetail(previousSubmissionDetail)).thenReturn(correctionSubmissionDetail);
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);

    correctionService.startCorrectionForType(form, user);

    verify(submissionDetailPersistenceService, times(1)).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());
    assertThat(submissionDetailArgumentCaptor.getValue()).extracting(
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getSubmitterType
    ).containsExactly(
        true,
        user.wuaId().intValue(),
        SubmitterType.REGULATOR
    );
    assertNotNull(submissionDetailArgumentCaptor.getValue().getCorrectionTimestamp());
  }

  @Test
  void getCorrectionRedirectForType_submissionCorrection() {
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.SUBMISSION_CORRECTION);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var modelAndView = correctionService.getCorrectionRedirectForType(form, submissionDetail);
    assertEquals(
        ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetail.getId())).getView(),
        modelAndView.getView());
  }

  @Test
  @Disabled
  void startCorrectionForType_historicUpdate() {
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);

    var submissionDetail = correctionService.startCorrectionForType(form, user);

    assertNull(submissionDetail);

  }

  @Test
  @Disabled
  void getCorrectionRedirectForType_historicUpdate() {
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var modelAndView = correctionService.getCorrectionRedirectForType(form, submissionDetail);
    assertNull(modelAndView);
  }

  @Test
  void startCorrectionForType_approvalDateChange() {
    var selectedSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    form.setSubmissionIdForChangingApprovedDate(selectedSubmissionDetail.getId().toString());
    form.setApprovalDateChangeDay("1");
    form.setApprovalDateChangeMonth("1");
    form.setApprovalDateChangeYear("2023");
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionDetail.getId())).thenReturn(selectedSubmissionDetail);
    var correctionSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    when(submissionCreateService.createUpdateSubmissionDetail(selectedSubmissionDetail)).thenReturn(correctionSubmissionDetail);
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);

    correctionService.startCorrectionForType(form, user);

    verify(submissionDetailPersistenceService, times(1)).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());

    var approvedDate = LocalDate.of(
        Integer.parseInt(form.getApprovalDateChangeYear()),
        Integer.parseInt(form.getApprovalDateChangeMonth()),
        Integer.parseInt(form.getApprovalDateChangeDay())
    ).atStartOfDay(ZoneId.systemDefault()).toInstant();
    var wuaId = user.wuaId().intValue();

    assertThat(submissionDetailArgumentCaptor.getValue()).extracting(
        SubmissionDetail::getStatus,
        SubmissionDetail::getSubmittedByWuaId,
        SubmissionDetail::getSubmittedTimestamp,
        SubmissionDetail::getApprovedByWuaId,
        SubmissionDetail::getApprovedTimestamp,
        SubmissionDetail::isCorrection,
        SubmissionDetail::getCorrectionByWuaId,
        SubmissionDetail::getSubmitterType
    ).containsExactly(
        SubmissionDetailStatus.APPROVED,
        wuaId,
        approvedDate,
        wuaId,
        approvedDate,
        true,
        wuaId,
        SubmitterType.REGULATOR
    );
    assertNotNull(submissionDetailArgumentCaptor.getValue().getCorrectionTimestamp());

  }

  @Test
  void getCorrectionRedirectForType_approvalDateChange() {
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.APPROVAL_DATE_CHANGE);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var modelAndView = correctionService.getCorrectionRedirectForType(form, submissionDetail);
    assertEquals(
        ReverseRouter.redirect(on(ViewResourceRecordController.class)
            .showSubmissionSummary(submissionDetail.getSubmission().getResource().getId(), submissionDetail.getId(), null)).getView(),
        modelAndView.getView());
  }

  @Test
  void startCorrectionForType_removeSubmission() {
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.REMOVE_SUBMISSION);

    var submissionDetail = correctionService.startCorrectionForType(form, user);

    assertNull(submissionDetail);

  }

  @Test
  void getCorrectionRedirectForType_removeSubmission() {
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.REMOVE_SUBMISSION);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var modelAndView = correctionService.getCorrectionRedirectForType(form, submissionDetail);
    assertNull(modelAndView);
  }
}
