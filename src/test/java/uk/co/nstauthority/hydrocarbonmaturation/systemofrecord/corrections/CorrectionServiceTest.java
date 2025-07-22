package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@ExtendWith(MockitoExtension.class)
class CorrectionServiceTest {

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private CorrectionCreateService correctionCreateService;

  @InjectMocks
  private CorrectionService correctionService;

  private Resource resource;
  private User user;

  @BeforeEach
  void setUp() {
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

    correctionService.startCorrectionForTypeAndRedirect(form, user, resource);

    verify(correctionCreateService).createSubmissionCorrectionAndRedirect(previousSubmissionDetail.getId(), user);
  }

  @Test
  void startCorrectionForType_historicUpdate() {
    var previousSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);

    var form = new CorrectionForm();
    var correctionType = CorrectionType.HISTORIC_UPDATE;
    form.setCorrectionType(correctionType);
    form.setMissingUpdateDay("1");
    form.setMissingUpdateMonth("1");
    form.setMissingUpdateYear("2023");
    form.setSubmissionDetailIdForMissingUpdate(previousSubmissionDetail.getId().toString());

    correctionService.startCorrectionForTypeAndRedirect(form, user, resource);

    verify(correctionCreateService).createMissingUpdateAndRedirect(
        previousSubmissionDetail.getId(),
        form.getMissingUpdateDay(),
        form.getMissingUpdateMonth(),
        form.getMissingUpdateYear(),
        user
    );
  }

  @Test
  void startCorrectionForType_fieldHistoricUpdate() {
    var form = new CorrectionForm();
    var correctionType = CorrectionType.FIELD_HISTORIC_UPDATE;
    var submissionType = SubmissionType.CREATE_FDP;
    form.setCorrectionType(correctionType);
    form.setMissingUpdateDay("1");
    form.setMissingUpdateMonth("1");
    form.setMissingUpdateYear("2023");
    form.setSubmissionTypeForMissingUpdate(submissionType);

    correctionService.startCorrectionForTypeAndRedirect(form, user, resource);

    verify(correctionCreateService).createMissingFieldUpdateAndRedirect(
        resource,
        submissionType,
        form.getMissingUpdateDay(),
        form.getMissingUpdateMonth(),
        form.getMissingUpdateYear(),
        user
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

    correctionService.startCorrectionForTypeAndRedirect(form, user, resource);

    verify(correctionCreateService).updateApprovedDateAndRedirect(
        selectedSubmissionDetail.getId(),
        form.getApprovalDateChangeDay(),
        form.getApprovalDateChangeMonth(),
        form.getApprovalDateChangeYear(),
        user
    );
  }

  @Test
  void startCorrectionForType_removeSubmission() {
    var submissionDetailForRemoval = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.REMOVE_SUBMISSION);
    form.setSubmissionDetailIdForRemoval(submissionDetailForRemoval.getId().toString());

    correctionService.startCorrectionForTypeAndRedirect(form, user, resource);

    verify(correctionCreateService).removeSubmissionAndRedirect(submissionDetailForRemoval.getId());
  }

  @Test
  void canRemoveSubmissions() {
    var resource = new Resource();
    when(resourceLinkService.prospectIsLinkedToDiscovery(resource)).thenReturn(false);
    assertTrue(correctionService.canRemoveSubmissions(resource));
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
    resource.setType(ResourceType.PROSPECT);
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
  void getCorrectionType_forField_canRemoveSubmissions() {
    var resource = new Resource();
    resource.setType(ResourceType.FIELD);
    var correctionTypes = correctionService.getCorrectionTypes(resource);
    assertThat(correctionTypes).containsExactly(
        entry(CorrectionType.FIELD_HISTORIC_UPDATE.name(), CorrectionType.FIELD_HISTORIC_UPDATE.getDisplayName()),
        entry(CorrectionType.SUBMISSION_CORRECTION.name(), CorrectionType.SUBMISSION_CORRECTION.getDisplayName()),
        entry(CorrectionType.REMOVE_SUBMISSION.name(), CorrectionType.REMOVE_SUBMISSION.getDisplayName())
    );
  }

  @Test
  void getCorrectionType_cantRemoveSubmissions_prospectLinkedToDiscovery() {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);
    when(resourceLinkService.prospectIsLinkedToDiscovery(resource)).thenReturn(true);
    var correctionTypes = correctionService.getCorrectionTypes(resource);
    assertThat(correctionTypes).containsExactly(
        entry(CorrectionType.HISTORIC_UPDATE.name(), CorrectionType.HISTORIC_UPDATE.getDisplayName()),
        entry(CorrectionType.SUBMISSION_CORRECTION.name(), CorrectionType.SUBMISSION_CORRECTION.getDisplayName()),
        entry(CorrectionType.ACCEPTED_DATE_CHANGE.name(), CorrectionType.ACCEPTED_DATE_CHANGE.getDisplayName())
    );
  }
}
