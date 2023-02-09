package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.updatediscovery.UpdateDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.postdrilldryhole.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.relinquishment.RelinquishmentForm;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@ExtendWith(MockitoExtension.class)
class SubmissionCreateServiceTest {

  private static final ResourceDetailsForm RESOURCE_DETAILS_FORM = new ResourceDetailsForm();

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
  private ResponsibleTeamPersistenceService responsibleTeamPersistenceService;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private PermissionService permissionService;

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @InjectMocks
  private SubmissionCreateService submissionCreateService;


  @Test
  void createInitialProspect() {
    var resource = new Resource();

    when(resourceService.createResource(ResourceType.PROSPECT)).thenReturn(resource);

    submissionCreateService.createInitialProspect(RESOURCE_DETAILS_FORM);

    verify(responsibleTeamPersistenceService, times(1))
        .saveResponsibleTeamForResource(resource, ResponsibleTeamType.EXPLORATION);
    verify(submissionDetailPersistenceService, times(1)).createInitialSubmissionDetail(resource, SubmissionType.CREATE_PROSPECT);
  }

  @Test
  void createUpdateProspect_successful_verifyUpdateGeneralDetails() {
    var form = new UpdateProspectForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submissionDetail.getResourceId(), SubmissionType.PROSPECT_UPDATE)).thenReturn(submissionDetailNew);
    var newGeneralDetail = new GeneralDetail();
    when(generalDetailsPersistenceService.getGeneralDetailOrThrow(submissionDetailNew))
        .thenReturn(newGeneralDetail);

    submissionCreateService.createUpdateProspect(form);

    verify(submissionDetailDuplicationService, times(1))
        .duplicateSubmissionDetailSections(submissionDetail, submissionDetailNew);
    verify(generalDetailsPersistenceService, times(1))
        .updateGeneralDetailProspect(form, newGeneralDetail);
  }

  @Test
  void createUpdateProspect_failure_assertThrown() {
    var form = new UpdateProspectForm();
    var generalDetailId = UUID.randomUUID();
    form.setProspectSelectedOption(generalDetailId.toString());

    doThrow(RemiEntityNotFoundException.class).when(submissionDetailService)
        .getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption()));

    assertThatExceptionOfType(RemiEntityNotFoundException.class)
        .isThrownBy(()->submissionCreateService.createUpdateProspect(form));

    verify(submissionDetailPersistenceService, never()).createInitialSubmissionDetail(any(), any());
    verify(generalDetailsPersistenceService, never()).updateGeneralDetailProspect(any(), any());
  }

  @Test
  void createPostDrillDryHoleSubmission_Successful_VerifyDuplicatesSubmissionDetailSections() {
    var form = new PostDrillDryHoleForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submissionDetail.getResourceId(), SubmissionType.POST_DRILL_DRY_HOLE))
        .thenReturn(submissionDetailNew);

    submissionCreateService.createPostDrillDryHoleSubmission(form);

    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, submissionDetailNew);
  }

  @Test
  void createNewDiscoveryFromProspect_Successful_verifySubmissionCreated() {
    var form = new StartDiscoveryForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    var discoveryResource = new Resource();
    discoveryResource.setType(ResourceType.DISCOVERY);
    var prospectResource = new Resource();
    prospectResource.setType(ResourceType.PROSPECT);
    submissionDetail.setResourceId(prospectResource);
    when(resourceService.createResource(ResourceType.DISCOVERY)).thenReturn(discoveryResource);
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(discoveryResource, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .thenReturn(submissionDetailNew);

    submissionCreateService.createNewDiscoveryFromProspect(form);

    verify(resourceLinkService).createNewResourceLink(prospectResource, discoveryResource);
    verify(responsibleTeamPersistenceService, times(1))
        .saveResponsibleTeamForResource(discoveryResource, ResponsibleTeamType.EXPLORATION);
    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, submissionDetailNew);
  }

  @Test
  void createUpdateDiscoverySubmission_Successful_VerifyDuplicatesSubmissionDetailSections() {
    var form = new UpdateDiscoveryForm();
    form.setDiscoverySelectedOption(UUID.randomUUID().toString());
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(UUID.fromString(form.getDiscoverySelectedOption())))
        .thenReturn(submissionDetail);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submissionDetail.getResourceId(), SubmissionType.UPDATE_DISCOVERY))
        .thenReturn(submissionDetailNew);

    submissionCreateService.createUpdateDiscovery(form);

    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, submissionDetailNew);
  }

  @Test
  void createDiscovery_verifyMethodCalls() {
    var captor = ArgumentCaptor.forClass(Resource.class);

    submissionCreateService.createDiscovery(RESOURCE_DETAILS_FORM);

    verify(resourceService, times(1)).createResource(ResourceType.DISCOVERY);
    verify(responsibleTeamPersistenceService, times(1))
        .saveResponsibleTeamForResource(captor.capture(), eq(ResponsibleTeamType.EXPLORATION));
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(captor.getValue(), SubmissionType.CREATE_DISCOVERY);
  }

  @Test
  void getSubmissionTypeOptionsForUser_whenExplorationTeam_assertAllSubmissionTypes() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var teamMember = TeamMemberTestUtil
        .Builder()
        .withRole(RegulatorTeamRole.EXPLORATION)
        .build();
    when(userDetailService.getUserDetail()).thenReturn(user);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(true);

    var result = submissionCreateService.getSubmissionTypeOptionsForUser();

    assertThat(result)
        .extracting(Map::keySet)
        .isEqualTo(
            Set.of("CREATE_PROSPECT", "PROSPECT_UPDATE",
                "POST_DRILL_DRY_HOLE", "CREATE_DISCOVERY_FROM_PROSPECT",
                "CREATE_DISCOVERY", "UPDATE_DISCOVERY",  "RELINQUISHMENT_PROSPECT")
        );
  }

  @Test
  void getSubmissionTypeOptionsForUser_whenNotExplorationTeam_assertAllSubmissionTypes() {
    var user = ServiceUserDetailTestUtil.Builder().build();
    var teamMember = TeamMemberTestUtil
        .Builder()
        .withRole(null)
        .build();
    when(userDetailService.getUserDetail()).thenReturn(user);
    when(permissionService.hasPermission(user, Set.of(RolePermission.EXPLORATION, RolePermission.INDUSTRY_SUBMIT)))
        .thenReturn(false);

    var result = submissionCreateService.getSubmissionTypeOptionsForUser();

    assertThat(result)
        .extracting(Map::keySet)
        .isEqualTo(
            Set.of("CREATE_PROSPECT", "PROSPECT_UPDATE",
                "POST_DRILL_DRY_HOLE", "CREATE_DISCOVERY_FROM_PROSPECT",
                "CREATE_DISCOVERY", "UPDATE_DISCOVERY")
        );
  }

  @Test
  void createRelinquishment_whenProspect_verifyMethodCalls() {
    var relinquishmentForm = new RelinquishmentForm();
    relinquishmentForm.setChosenResourceType(ResourceType.PROSPECT);
    relinquishmentForm.setProspectSelectedOption(UUID.randomUUID().toString());
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setResourceId(new Resource());
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(
        UUID.fromString(relinquishmentForm.getProspectSelectedOption())))
        .thenReturn(submissionDetail);

    var newSubmissionDetail = submissionCreateService.createRelinquishment(relinquishmentForm);

    verify(submissionDetailPersistenceService)
        .createInitialSubmissionDetail(submissionDetail.getResourceId(), SubmissionType.RELINQUISHMENT_PROSPECT);
    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, newSubmissionDetail);
  }

  @Test
  void createRelinquishment_whenDiscovery_verifyMethodCalls() {
    var relinquishmentForm = new RelinquishmentForm();
    relinquishmentForm.setChosenResourceType(ResourceType.DISCOVERY);
    relinquishmentForm.setDiscoverySelectedOption(UUID.randomUUID().toString());
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setResourceId(new Resource());
    when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(
        UUID.fromString(relinquishmentForm.getDiscoverySelectedOption())))
        .thenReturn(submissionDetail);

    var newSubmissionDetail = submissionCreateService.createRelinquishment(relinquishmentForm);

    verify(submissionDetailPersistenceService)
        .createInitialSubmissionDetail(submissionDetail.getResourceId(), SubmissionType.RELINQUISHMENT_DISCOVERY);
    verify(submissionDetailDuplicationService)
        .duplicateSubmissionDetailSections(submissionDetail, newSubmissionDetail);
  }

  @Test
  void createRelinquishment_whenNotDiscoveryOrProspect_assertThrow() {
    var relinquishmentForm = new RelinquishmentForm();
    relinquishmentForm.setChosenResourceType(ResourceType.FIELD);
    assertThatThrownBy(() -> submissionCreateService.createRelinquishment(relinquishmentForm))
        .isExactlyInstanceOf(ActionNotAllowedException.class)
        .hasMessage("You must create a relinquishment off of a discovery or prospect.");
  }
}