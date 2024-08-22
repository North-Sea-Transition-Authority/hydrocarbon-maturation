package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.DiscoveryRelinquishmentForm;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.ProspectRelinquishmentForm;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamRoleTestUtil;

@ExtendWith(MockitoExtension.class)
class SubmissionCreateServiceTest {

  private static final ResourceDetailsForm RESOURCE_DETAILS_FORM = new ResourceDetailsForm();
  private final static User USER = UserTestUtil.Builder().build();

  @Mock
  private ResourceService resourceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private SubmissionDetailDuplicationService submissionDetailDuplicationService;

  @Mock
  private SubmissionPersistenceService submissionPersistenceService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @Mock
  private ReservoirPersistenceService reservoirPersistenceService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private TeamQueryService teamQueryService;

  @InjectMocks
  private SubmissionCreateService submissionCreateService;

  @Test
  void createInitialProspect() {
    var resource = new Resource();
    var submission = new Submission();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    when(resourceService.createResource(ResourceType.PROSPECT)).thenReturn(resource);
    when(submissionPersistenceService.createSubmission(resource))
        .thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_PROSPECT, USER))
        .thenReturn(submissionDetail);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setResourceName("resource name");
    var reservoir = new Reservoir();
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail))
        .thenReturn(resourceDetail);
    when(reservoirPersistenceService.createReservoir(resourceDetail.getResourceName()))
        .thenReturn(reservoir);

    submissionCreateService.createInitialProspect(RESOURCE_DETAILS_FORM, USER);

    verify(regulatorManagedInformationPersistenceService, times(1))
        .saveResponsibleTeamAndGeoByResource(resource, ResponsibleTeamType.EXPLORATION);
    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(reservoir, submissionDetail);
  }

  @Test
  void createUpdateProspect_successful_verifyUpdateGeneralDetails() {
    var form = new UpdateProspectForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());
    var submission = new Submission();

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    when(submissionPersistenceService.createSubmission(submissionDetail.getSubmission().getResource()))
        .thenReturn(submission);
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(
        submission,
        SubmissionType.PROSPECT_UPDATE,
        USER)).thenReturn(submissionDetailNew);
    var newGeneralDetail = new GeneralDetail();
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetailNew))
        .thenReturn(Optional.of(newGeneralDetail));
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createUpdateProspect(form, USER);

    verify(submissionDetailDuplicationService, times(1))
        .duplicateSubmissionDetailSections(submissionDetail, submissionDetailNew);
    verify(generalDetailsPersistenceService, times(1))
        .updateGeneralDetailProspect(form, newGeneralDetail);
    verify(submissionPersistenceService).createSubmission(submissionDetail.getSubmission().getResource());
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(reservoir, submissionDetailNew);
  }
  @Test
  void createUpdateProspect_failure_assertThrown() {
    var form = new UpdateProspectForm();
    var generalDetailId = UUID.randomUUID();
    form.setProspectSelectedOption(generalDetailId.toString());

    doThrow(RemiEntityNotFoundException.class).when(submissionDetailService)
        .getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));

    assertThatExceptionOfType(RemiEntityNotFoundException.class)
        .isThrownBy(()->submissionCreateService.createUpdateProspect(form, USER));

    verify(submissionDetailPersistenceService, never()).createInitialSubmissionDetail(any(), any(), any());
    verify(generalDetailsPersistenceService, never()).updateGeneralDetailProspect(any(), any());
    verifyNoInteractions(submissionPersistenceService);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
  }

  @Test
  void createPostDrillDryHoleSubmission_Successful_VerifyDuplicatesSubmissionDetailSections() {
    var form = new PostDrillDryHoleForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submission = new Submission();
    when(submissionPersistenceService.createSubmission(submissionDetail.getSubmission().getResource()))
        .thenReturn(submission);
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(
        submission,
        SubmissionType.POST_DRILL_DRY_HOLE,
        USER))
        .thenReturn(submissionDetailNew);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createPostDrillDryHoleSubmission(form, USER);

    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, submissionDetailNew);
    verify(opportunityDetailsPersistenceService, times(1))
        .createOrUpdateOpportunityDetail(submissionDetailNew);
    verify(submissionPersistenceService, times(1))
        .createSubmission(submissionDetail.getSubmission().getResource());
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(reservoir, submissionDetailNew);
  }

  @Test
  void createNewDiscoveryFromProspect_Successful_verifySubmissionCreated() {
    var form = new StartDiscoveryForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    var discoveryResource = new Resource();
    discoveryResource.setType(ResourceType.DISCOVERY);
    submissionDetailNew.getSubmission().setResource(discoveryResource);
    when(submissionPersistenceService.createSubmission(discoveryResource))
        .thenReturn(submissionDetailNew.getSubmission());
    var prospectResource = new Resource();
    prospectResource.setType(ResourceType.PROSPECT);
    submissionDetail.getSubmission().setResource(prospectResource);
    when(resourceService.createResource(ResourceType.DISCOVERY)).thenReturn(discoveryResource);
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(
        submissionDetailNew.getSubmission(),
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT, USER))
        .thenReturn(submissionDetailNew);

    submissionCreateService.createNewDiscoveryFromProspect(form, USER);

    verify(resourceLinkService).createNewProspectDiscoveryResourceLink(prospectResource, discoveryResource);
    verify(regulatorManagedInformationPersistenceService, times(1))
        .saveResponsibleTeamAndGeoByResource(discoveryResource, ResponsibleTeamType.EXPLORATION);
    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, submissionDetailNew);
    verify(submissionPersistenceService, times(1))
        .createSubmission(discoveryResource);
    verify(opportunityDetailsPersistenceService, times(1))
        .createOrUpdateOpportunityDetail(submissionDetailNew);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
  }

  @Test
  void createUpdateDiscoverySubmission_Successful_VerifyDuplicatesSubmissionDetailSections() {
    var form = new UpdateDiscoveryForm();
    form.setDiscoverySelectedOption(UUID.randomUUID().toString());
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submission = new Submission();
    when(submissionPersistenceService.createSubmission(submissionDetail.getSubmission().getResource()))
        .thenReturn(submission);
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getDiscoverySelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(
        submission,
        SubmissionType.UPDATE_DISCOVERY, USER))
        .thenReturn(submissionDetailNew);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createUpdateDiscovery(form, USER);

    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, submissionDetailNew);
    verify(submissionPersistenceService, times(1))
        .createSubmission(submissionDetail.getSubmission().getResource());
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(reservoir, submissionDetailNew);
  }

  @Test
  void createDiscovery_verifyMethodCalls() {
    var captor = ArgumentCaptor.forClass(Resource.class);
    var submission = new Submission();
    var submissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(any()))
        .thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_DISCOVERY, USER))
        .thenReturn(submissionDetail);

    submissionCreateService.createDiscovery(RESOURCE_DETAILS_FORM, USER);

    verify(resourceService, times(1)).createResource(ResourceType.DISCOVERY);
    verify(regulatorManagedInformationPersistenceService, times(1))
        .saveResponsibleTeamAndGeoByResource(captor.capture(), eq(ResponsibleTeamType.EXPLORATION));
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_DISCOVERY, USER);
    verify(submissionPersistenceService, times(1)).createSubmission(captor.getValue());
    verify(opportunityDetailsPersistenceService, times(1))
        .createOrUpdateOpportunityDetail(any());
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
  }

  @Test
  void createProspectRelinquishment_verifyMethodCalls() {
    var prospectRelinquishmentForm = new ProspectRelinquishmentForm();
    prospectRelinquishmentForm.setProspectSelectedOption(UUID.randomUUID().toString());
    var submissionDetail = new SubmissionDetail();
    var submission = SubmissionTestUtils.createSubmission(new Resource());
    submissionDetail.setSubmission(submission);
    var newSubmission = SubmissionTestUtils.createSubmission(submission.getResource());
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(submissionDetail.getSubmission().getResource()))
        .thenReturn(newSubmission);
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(
        UUID.fromString(prospectRelinquishmentForm.getProspectSelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(newSubmission, SubmissionType.RELINQUISHMENT_PROSPECT, USER))
        .thenReturn(newSubmissionDetail);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createProspectRelinquishment(prospectRelinquishmentForm, USER);

    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, newSubmissionDetail);
    verify(generalDetailsPersistenceService)
        .setRelinquishmentInformationSource(newSubmissionDetail);
    verify(submissionPersistenceService, times(1))
        .createSubmission(submissionDetail.getSubmission().getResource());
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(reservoir, newSubmissionDetail);
  }

  @Test
  void createDiscoveryRelinquishment_verifyMethodCalls() {
    var discoveryRelinquishmentForm = new DiscoveryRelinquishmentForm();
    discoveryRelinquishmentForm.setDiscoverySelectedOption(UUID.randomUUID().toString());
    var submissionDetail = new SubmissionDetail();
    var submission = SubmissionTestUtils.createSubmission(new Resource());
    submissionDetail.setSubmission(submission);
    var newSubmission = SubmissionTestUtils.createSubmission(submission.getResource());
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(submissionDetail.getSubmission().getResource()))
        .thenReturn(newSubmission);
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(
        UUID.fromString(discoveryRelinquishmentForm.getDiscoverySelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(newSubmission, SubmissionType.RELINQUISHMENT_DISCOVERY, USER))
        .thenReturn(newSubmissionDetail);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createDiscoveryRelinquishment(discoveryRelinquishmentForm, USER);

    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, newSubmissionDetail);
    verify(generalDetailsPersistenceService)
        .setRelinquishmentInformationSource(newSubmissionDetail);
    verify(submissionPersistenceService, times(1))
        .createSubmission(submissionDetail.getSubmission().getResource());
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(reservoir, newSubmissionDetail);
  }

  @Test
  void createUpdateSubmissionDetail() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var updateSubmissionDetail = mock(SubmissionDetail.class);

    when(submissionDetailPersistenceService.createUpdateSubmissionDetail(oldSubmissionDetail, USER))
        .thenReturn(updateSubmissionDetail);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertThat(submissionCreateService.createUpdateSubmissionDetail(oldSubmissionDetail, USER))
        .isEqualTo(updateSubmissionDetail);

    verify(submissionDetailDuplicationService, times(1))
        .duplicateSubmissionDetailSections(oldSubmissionDetail, updateSubmissionDetail);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(reservoir, updateSubmissionDetail);
  }

  @Test
  void getResourceTypeOptionsForSubmitterUser() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.INDUSTRY_SUBMIT).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    var resourceTypeOptions = new HashMap<>();
    resourceTypeOptions.put(ResourceType.PROSPECT.name(), ResourceType.PROSPECT.getDisplayName());
    resourceTypeOptions.put(ResourceType.DISCOVERY.name(), ResourceType.DISCOVERY.getDisplayName());
    resourceTypeOptions.put(ResourceType.FIELD.name(), ResourceType.FIELD.getDisplayName());

    assertThat(submissionCreateService.getResourceTypeOptionsForUser(USER)).isEqualTo(resourceTypeOptions);
  }

  @Test
  void getResourceTypeOptionsForExplorationUser() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.EXPLORATION).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    var resourceTypeOptions = new HashMap<>();
    resourceTypeOptions.put(ResourceType.PROSPECT.name(), ResourceType.PROSPECT.getDisplayName());
    resourceTypeOptions.put(ResourceType.DISCOVERY.name(), ResourceType.DISCOVERY.getDisplayName());

    assertThat(submissionCreateService.getResourceTypeOptionsForUser(USER)).isEqualTo(resourceTypeOptions);
  }

  @Test
  void getResourceTypeOptionsForAreaTeamUser() {
    var teamRole = TeamRoleTestUtil.Builder().withRole(Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    var resourceTypeOptions = new HashMap<>();
    resourceTypeOptions.put(ResourceType.DISCOVERY.name(), ResourceType.DISCOVERY.getDisplayName());
    resourceTypeOptions.put(ResourceType.FIELD.name(), ResourceType.FIELD.getDisplayName());

    assertThat(submissionCreateService.getResourceTypeOptionsForUser(USER)).isEqualTo(resourceTypeOptions);
  }

  @ParameterizedTest
  @EnumSource(value = Role.class, mode = EnumSource.Mode.EXCLUDE, names = {"INDUSTRY_SUBMIT", "EXPLORATION", "MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM"})
  void getResourceTypeOptionsForOtherUsers(Role role) {
    var teamRole = TeamRoleTestUtil.Builder().withRole(role).build();
    when(teamQueryService.getTeamRolesForUser(USER.wuaId())).thenReturn(Set.of(teamRole));

    assertThat(submissionCreateService.getResourceTypeOptionsForUser(USER)).isEmpty();
  }

  @Test
  void createCsrFieldSubmission_oldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR, USER))
        .thenReturn(newSubmissionDetail);

    var oldSubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(oldSubmissionDetail));
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail));

    submissionCreateService.createCsrFieldSubmissionDetail(resource, USER);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR, USER);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(firstReservoirSubmissionDetail.getReservoir(), newSubmissionDetail);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(secondReservoirSubmissionDetail.getReservoir(), newSubmissionDetail);
    verify(submissionDetailDuplicationService, times(1))
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
  }

  @Test
  void createCsrFieldSubmission_noOldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR, USER))
        .thenReturn(newSubmissionDetail);

    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    submissionCreateService.createCsrFieldSubmissionDetail(resource, USER);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR, USER);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(submissionDetailDuplicationService);
  }

  @Test
  void createFdpFieldSubmission_oldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP, USER))
        .thenReturn(newSubmissionDetail);

    var oldSubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(oldSubmissionDetail));
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail));

    submissionCreateService.createFdpFieldSubmissionDetail(resource, USER);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP, USER);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(firstReservoirSubmissionDetail.getReservoir(), newSubmissionDetail);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(secondReservoirSubmissionDetail.getReservoir(), newSubmissionDetail);
    verify(submissionDetailDuplicationService, times(1))
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
  }

  @Test
  void createFdpFieldSubmission_noOldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP, USER))
        .thenReturn(newSubmissionDetail);

    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    submissionCreateService.createFdpFieldSubmissionDetail(resource, USER);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP, USER);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(submissionDetailDuplicationService);
  }

  @Test
  void createFdpaFieldSubmission_oldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA, USER))
        .thenReturn(newSubmissionDetail);

    var oldSubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(oldSubmissionDetail));
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail));

    submissionCreateService.createFdpaFieldSubmissionDetail(resource, USER);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA, USER);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(firstReservoirSubmissionDetail.getReservoir(), newSubmissionDetail);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(secondReservoirSubmissionDetail.getReservoir(), newSubmissionDetail);
    verify(submissionDetailDuplicationService, times(1))
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
  }

  @Test
  void createFdpaFieldSubmission_noOldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA, USER))
        .thenReturn(newSubmissionDetail);

    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    submissionCreateService.createFdpaFieldSubmissionDetail(resource, USER);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA, USER);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(submissionDetailDuplicationService);
  }

  @Test
  void createCopFieldSubmission_oldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP, USER))
        .thenReturn(newSubmissionDetail);

    var oldSubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(oldSubmissionDetail));
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail));

    submissionCreateService.createCoPFieldSubmissionDetail(resource, USER);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP, USER);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(firstReservoirSubmissionDetail.getReservoir(), newSubmissionDetail);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(secondReservoirSubmissionDetail.getReservoir(), newSubmissionDetail);
    verify(submissionDetailDuplicationService, times(1))
        .duplicateSubmissionDetailSections(oldSubmissionDetail, newSubmissionDetail);
  }

  @Test
  void createCopFieldSubmission_noOldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP, USER))
        .thenReturn(newSubmissionDetail);

    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    submissionCreateService.createCoPFieldSubmissionDetail(resource, USER);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP, USER);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(submissionDetailDuplicationService);
  }
}