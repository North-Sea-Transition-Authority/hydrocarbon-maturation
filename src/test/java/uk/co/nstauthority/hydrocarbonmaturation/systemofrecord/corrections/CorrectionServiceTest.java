package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
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
  private ResourceGroupService resourceGroupService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private OrganisationCorrectionService organisationCorrectionService;

  @Mock
  private PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  private Clock clock;

  private CorrectionService correctionService;

  private Resource resource;
  private User user;

  @BeforeEach
  void setUp() {
    correctionService = new CorrectionService(
        regulatorManagedInformationPersistenceService,
        responsibleTeamService,
        submissionCreateService,
        submissionDetailService,
        submissionPersistenceService,
        submissionDetailPersistenceService,
        resourceGroupService,
        resourceLinkService,
        organisationCorrectionService,
        clock = Clock.fixed(Instant.now(), ZoneId.systemDefault()),
        postFieldDiscoveryLinkService);

    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    user = UserTestUtil.Builder().build();
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
    var correctionType = CorrectionType.SUBMISSION_CORRECTION;
    form.setCorrectionType(correctionType);
    form.setSubmissionDetailIdForCorrectingInformation(previousSubmissionDetail.getId().toString());
    when(submissionDetailService.getSubmissionDetailForIdOrError(previousSubmissionDetail.getId())).thenReturn(previousSubmissionDetail);
    var correctionSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    correctionSubmissionDetail.setCorrection(true);
    correctionSubmissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    correctionSubmissionDetail.setCorrectionTimestamp(clock.instant());
    correctionSubmissionDetail.setCorrectionType(correctionType);
    when(submissionCreateService.createCorrectionSubmissionDetail(previousSubmissionDetail, user, correctionType))
        .thenReturn(correctionSubmissionDetail);
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);

    var modelAndView = correctionService.startCorrectionForTypeAndRedirect(form, user);

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

    var form = new CorrectionForm();
    var correctionType = CorrectionType.HISTORIC_UPDATE;
    form.setCorrectionType(correctionType);
    form.setMissingUpdateDay("1");
    form.setMissingUpdateMonth("1");
    form.setMissingUpdateYear("2023");
    form.setSubmissionDetailIdForMissingUpdate(previousSubmissionDetail.getId().toString());

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

    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);

    var modelAndView = correctionService.startCorrectionForTypeAndRedirect(form, user);

    historicUpdateAssertions(submissionDetailArgumentCaptor, form, modelAndView, correctionType);
  }

  @Test
  void startCorrectionForType_historicUpdate_forDiscovery() {
    var previousSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    var form = new CorrectionForm();
    var correctionType = CorrectionType.HISTORIC_UPDATE;
    form.setCorrectionType(correctionType);
    form.setMissingUpdateDay("1");
    form.setMissingUpdateMonth("1");
    form.setMissingUpdateYear("2023");
    form.setSubmissionDetailIdForMissingUpdate(previousSubmissionDetail.getId().toString());

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

    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);

    var modelAndView = correctionService.startCorrectionForTypeAndRedirect(form, user);

    historicUpdateAssertions(submissionDetailArgumentCaptor, form, modelAndView, correctionType);
  }

  @Test
  void startCorrectionForType_historicUpdate_forField() {
    var previousSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
    when(submissionDetailService.getSubmissionDetailForIdOrError(previousSubmissionDetail.getId())).thenReturn(previousSubmissionDetail);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.HISTORIC_UPDATE);
    form.setSubmissionDetailIdForMissingUpdate(previousSubmissionDetail.getId().toString());

    assertThatThrownBy(() -> correctionService.startCorrectionForTypeAndRedirect(form, user))
        .hasMessage("Corrections can only be done for prospects and discoveries")
        .isExactlyInstanceOf(ActionNotAllowedException.class);;
  }

  private void historicUpdateAssertions(ArgumentCaptor<SubmissionDetail> submissionDetailArgumentCaptor,
                                        CorrectionForm form,
                                        ModelAndView modelAndView,
                                        CorrectionType correctionType) {
    verify(submissionDetailPersistenceService, times(1)).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());
    var submissionDetail = submissionDetailArgumentCaptor.getValue();
    var approvedDate = LocalDate.of(
        Integer.parseInt(form.getMissingUpdateYear()),
        Integer.parseInt(form.getMissingUpdateMonth()),
        Integer.parseInt(form.getMissingUpdateDay())
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
    var form = new CorrectionForm();
    var correctionType = CorrectionType.ACCEPTED_DATE_CHANGE;
    form.setCorrectionType(correctionType);
    form.setSubmissionDetailIdForChangingApprovedDate(selectedSubmissionDetail.getId().toString());
    form.setApprovalDateChangeDay("1");
    form.setApprovalDateChangeMonth("1");
    form.setApprovalDateChangeYear("2023");
    when(submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionDetail.getId())).thenReturn(selectedSubmissionDetail);
    var correctionSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    correctionSubmissionDetail.setCorrection(true);
    correctionSubmissionDetail.setCorrectionByWuaId(user.wuaId().intValue());
    correctionSubmissionDetail.setCorrectionTimestamp(Instant.now());
    correctionSubmissionDetail.setCorrectionType(correctionType);
    when(submissionCreateService.createCorrectionSubmissionDetail(selectedSubmissionDetail, user, correctionType)).thenReturn(correctionSubmissionDetail);
    var submissionDetailArgumentCaptor = ArgumentCaptor.forClass(SubmissionDetail.class);

    var modelAndView = correctionService.startCorrectionForTypeAndRedirect(form, user);

    verify(submissionDetailPersistenceService, times(1)).saveSubmissionDetail(submissionDetailArgumentCaptor.capture());

    var submissionDetail = submissionDetailArgumentCaptor.getValue();
    var approvedDate = LocalDate.of(
        Integer.parseInt(form.getApprovalDateChangeYear()),
        Integer.parseInt(form.getApprovalDateChangeMonth()),
        Integer.parseInt(form.getApprovalDateChangeDay())
    ).atStartOfDay(ZoneId.systemDefault()).toInstant();
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
  void startCorrectionForType_removeSubmission() {
    var submissionDetailForRemoval = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.REMOVE_SUBMISSION);
    form.setSubmissionDetailIdForRemoval(submissionDetailForRemoval.getId().toString());
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailForRemoval.getId()))
        .thenReturn(submissionDetailForRemoval);
    var modelAndView = correctionService.startCorrectionForTypeAndRedirect(form, user);

    assertEquals(
        ReverseRouter.redirect(on(DeleteSubmissionFromCorrectionController.class)
            .showDeleteConfirmationPage(submissionDetailForRemoval.getId(), null)).getView(),
        modelAndView.getView()
    );

  }

  @Test
  void canRemoveSubmissions() {
    var resource = new Resource();
    when(resourceGroupService.isPreferredView(resource)).thenReturn(false);
    when(resourceLinkService.prospectIsLinkedToDiscovery(resource)).thenReturn(false);
    assertTrue(correctionService.canRemoveSubmissions(resource));
  }

  @Test
  void cantRemoveSubmissions_whenResourceIsPreferredViewInGroup() {
    var resource = new Resource();
    when(resourceGroupService.isPreferredView(resource)).thenReturn(true);
    assertFalse(correctionService.canRemoveSubmissions(resource));
  }

  @Test
  void cantRemoveSubmissions_whenProspectIsLinkedToDiscovery() {
    var resource = new Resource();
    when(resourceLinkService.prospectIsLinkedToDiscovery(resource)).thenReturn(true);
    assertFalse(correctionService.canRemoveSubmissions(resource));
  }

  @Test
  void getCorrectionType_canRemoveSubmissions() {
    var resource = new Resource();
    when(resourceGroupService.isPreferredView(resource)).thenReturn(false);
    when(resourceLinkService.prospectIsLinkedToDiscovery(resource)).thenReturn(false);
    var correctionTypes = correctionService.getCorrectionTypes(resource);
    assertThat(correctionTypes).containsExactly(
        entry(CorrectionType.HISTORIC_UPDATE.name(), CorrectionType.HISTORIC_UPDATE.getDisplayName()),
        entry(CorrectionType.SUBMISSION_CORRECTION.name(), CorrectionType.SUBMISSION_CORRECTION.getDisplayName()),
        entry(CorrectionType.ACCEPTED_DATE_CHANGE.name(), CorrectionType.ACCEPTED_DATE_CHANGE.getDisplayName()),
        entry(CorrectionType.REMOVE_SUBMISSION.name(), CorrectionType.REMOVE_SUBMISSION.getDisplayName())
    );
  }

  @Test
  void getCorrectionType_cantRemoveSubmissions_preferredView() {
    var resource = new Resource();
    when(resourceGroupService.isPreferredView(resource)).thenReturn(true);
    var correctionTypes = correctionService.getCorrectionTypes(resource);
    assertThat(correctionTypes).containsExactly(
        entry(CorrectionType.HISTORIC_UPDATE.name(), CorrectionType.HISTORIC_UPDATE.getDisplayName()),
        entry(CorrectionType.SUBMISSION_CORRECTION.name(), CorrectionType.SUBMISSION_CORRECTION.getDisplayName()),
        entry(CorrectionType.ACCEPTED_DATE_CHANGE.name(), CorrectionType.ACCEPTED_DATE_CHANGE.getDisplayName())
    );
  }

  @Test
  void getCorrectionType_cantRemoveSubmissions_prospectLinkedToDiscovery() {
    var resource = new Resource();
    when(resourceLinkService.prospectIsLinkedToDiscovery(resource)).thenReturn(true);
    var correctionTypes = correctionService.getCorrectionTypes(resource);
    assertThat(correctionTypes).containsExactly(
        entry(CorrectionType.HISTORIC_UPDATE.name(), CorrectionType.HISTORIC_UPDATE.getDisplayName()),
        entry(CorrectionType.SUBMISSION_CORRECTION.name(), CorrectionType.SUBMISSION_CORRECTION.getDisplayName()),
        entry(CorrectionType.ACCEPTED_DATE_CHANGE.name(), CorrectionType.ACCEPTED_DATE_CHANGE.getDisplayName())
    );
  }

  @Test
  void deleteSubmission() {
    var firstSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    var secondSubmissionDetail = SubmissionDetailTestUtils.createUpdateToSubmission(firstSubmissionDetail);
    when(submissionDetailService.getSubmissionDetailsBySubmission(secondSubmissionDetail.getSubmission())).thenReturn(
        List.of(firstSubmissionDetail, secondSubmissionDetail)
    );

    correctionService.deleteSubmission(secondSubmissionDetail, user);
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

    correctionService.deleteSubmission(secondSubmissionDetail, user);
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
