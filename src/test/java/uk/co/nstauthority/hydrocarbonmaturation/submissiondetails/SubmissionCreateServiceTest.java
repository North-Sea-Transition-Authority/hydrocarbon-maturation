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

import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
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
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionCreateServiceTest {

  private static final ResourceDetailsForm RESOURCE_DETAILS_FORM = new ResourceDetailsForm();
  private final static ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

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
  private UserDetailService userDetailService;

  @Mock
  private OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private AreaTeamService areaTeamService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private ReservoirPersistenceService reservoirPersistenceService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

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
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_PROSPECT))
        .thenReturn(submissionDetail);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setResourceName("resource name");
    var reservoir = new Reservoir();
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail))
        .thenReturn(resourceDetail);
    when(reservoirPersistenceService.createReservoir(resourceDetail.getResourceName()))
        .thenReturn(reservoir);

    submissionCreateService.createInitialProspect(RESOURCE_DETAILS_FORM);

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
        SubmissionType.PROSPECT_UPDATE)).thenReturn(submissionDetailNew);
    var newGeneralDetail = new GeneralDetail();
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetailNew))
        .thenReturn(Optional.of(newGeneralDetail));
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createUpdateProspect(form);

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
        .isThrownBy(()->submissionCreateService.createUpdateProspect(form));

    verify(submissionDetailPersistenceService, never()).createInitialSubmissionDetail(any(), any());
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
        SubmissionType.POST_DRILL_DRY_HOLE))
        .thenReturn(submissionDetailNew);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createPostDrillDryHoleSubmission(form);

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
        SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT))
        .thenReturn(submissionDetailNew);

    submissionCreateService.createNewDiscoveryFromProspect(form);

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
        SubmissionType.UPDATE_DISCOVERY))
        .thenReturn(submissionDetailNew);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createUpdateDiscovery(form);

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
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_DISCOVERY))
        .thenReturn(submissionDetail);

    submissionCreateService.createDiscovery(RESOURCE_DETAILS_FORM);

    verify(resourceService, times(1)).createResource(ResourceType.DISCOVERY);
    verify(regulatorManagedInformationPersistenceService, times(1))
        .saveResponsibleTeamAndGeoByResource(captor.capture(), eq(ResponsibleTeamType.EXPLORATION));
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_DISCOVERY);
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
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(newSubmission, SubmissionType.RELINQUISHMENT_PROSPECT))
        .thenReturn(newSubmissionDetail);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createProspectRelinquishment(prospectRelinquishmentForm);

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
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(newSubmission, SubmissionType.RELINQUISHMENT_DISCOVERY))
        .thenReturn(newSubmissionDetail);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, submissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    submissionCreateService.createDiscoveryRelinquishment(discoveryRelinquishmentForm);

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

    when(submissionDetailPersistenceService.createUpdateSubmissionDetail(oldSubmissionDetail))
        .thenReturn(updateSubmissionDetail);
    var reservoir = new Reservoir();
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    assertThat(submissionCreateService.createUpdateSubmissionDetail(oldSubmissionDetail))
        .isEqualTo(updateSubmissionDetail);

    verify(submissionDetailDuplicationService, times(1))
        .duplicateSubmissionDetailSections(oldSubmissionDetail, updateSubmissionDetail);
    verify(reservoirSubmissionDetailPersistenceService, times(1))
        .saveReservoirSubmissionDetail(reservoir, updateSubmissionDetail);
  }

  @ParameterizedTest
  @MethodSource("calculateGetResourceTypeOptionsForUserConditionArguments")
  void getResourceTypeOptionsForUser(boolean isSubmitter, boolean isExploration, boolean isAreaTeamMember) {
    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(industryTeamService.isSubmitter(USER)).thenReturn(isSubmitter);
    when(regulatorTeamService.isExploration(USER)).thenReturn(isExploration);
    when(areaTeamService.isAreaTeamMember(USER)).thenReturn(isAreaTeamMember);

    var resourceTypeOptions = new HashMap<>();
    if (isSubmitter || isExploration) {
      resourceTypeOptions.put(ResourceType.PROSPECT.name(), ResourceType.PROSPECT.getDisplayName());
    }
    if (isSubmitter || isExploration || isAreaTeamMember) {
      resourceTypeOptions.put(ResourceType.DISCOVERY.name(), ResourceType.DISCOVERY.getDisplayName());
    }
    if (isSubmitter || isAreaTeamMember) {
      resourceTypeOptions.put(ResourceType.FIELD.name(), ResourceType.FIELD.getDisplayName());
    }

    assertThat(submissionCreateService.getResourceTypeOptionsForUser()).isEqualTo(resourceTypeOptions);
  }

  @Test
  void createCsrFieldSubmission_oldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR))
        .thenReturn(newSubmissionDetail);

    var oldSubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(oldSubmissionDetail));
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail));

    submissionCreateService.createCsrFieldSubmissionDetail(resource);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR);
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
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR))
        .thenReturn(newSubmissionDetail);

    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    submissionCreateService.createCsrFieldSubmissionDetail(resource);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_CSR);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(submissionDetailDuplicationService);
  }

  @Test
  void createFdpFieldSubmission_oldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP))
        .thenReturn(newSubmissionDetail);

    var oldSubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(oldSubmissionDetail));
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail));

    submissionCreateService.createFdpFieldSubmissionDetail(resource);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP);
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
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP))
        .thenReturn(newSubmissionDetail);

    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    submissionCreateService.createFdpFieldSubmissionDetail(resource);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDP);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(submissionDetailDuplicationService);
  }

  @Test
  void createFdpaFieldSubmission_oldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA))
        .thenReturn(newSubmissionDetail);

    var oldSubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(oldSubmissionDetail));
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail));

    submissionCreateService.createFdpaFieldSubmissionDetail(resource);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA);
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
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA))
        .thenReturn(newSubmissionDetail);

    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    submissionCreateService.createFdpaFieldSubmissionDetail(resource);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_FDPA);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(submissionDetailDuplicationService);
  }

  @Test
  void createCopFieldSubmission_oldSubmissionDetail() {
    var resource = new Resource();
    var submission = new Submission();
    var newSubmissionDetail = new SubmissionDetail();
    when(submissionPersistenceService.createSubmission(resource)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP))
        .thenReturn(newSubmissionDetail);

    var oldSubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.of(oldSubmissionDetail));
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllReservoirSubmissionDetailsForSubmissionDetail(oldSubmissionDetail))
        .thenReturn(List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail));

    submissionCreateService.createCoPFieldSubmissionDetail(resource);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP);
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
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP))
        .thenReturn(newSubmissionDetail);

    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(resource))
        .thenReturn(Optional.empty());

    submissionCreateService.createCoPFieldSubmissionDetail(resource);

    verify(submissionPersistenceService, times(1)).createSubmission(resource);
    verify(submissionDetailPersistenceService, times(1))
        .createInitialSubmissionDetail(submission, SubmissionType.CREATE_COP);
    verifyNoInteractions(reservoirSubmissionDetailPersistenceService);
    verifyNoInteractions(submissionDetailDuplicationService);
  }

  private static Stream<Arguments> calculateGetResourceTypeOptionsForUserConditionArguments() {
    return Sets.cartesianProduct(Set.of(true, false), Set.of(true, false), Set.of(true, false)).stream()
        .map(booleans -> Arguments.of(booleans.get(0), booleans.get(1), booleans.get(2)));
  }
}