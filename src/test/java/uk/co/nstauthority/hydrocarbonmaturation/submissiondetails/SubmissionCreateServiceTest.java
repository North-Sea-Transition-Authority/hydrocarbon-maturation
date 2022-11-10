package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.StartDiscoveryForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.PostDrillDryHoleForm;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionCreateServiceTest {

  @Mock
  private ResourceService resourceService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @InjectMocks
  private SubmissionCreateService submissionCreateService;

  @Test
  void createInitialProspect() {
    var resource = new Resource();

    when(resourceService.createResource(ResourceType.PROSPECT)).thenReturn(resource);

    submissionCreateService.createInitialProspect();

    verify(submissionDetailService, times(1)).createInitialSubmissionDetail(resource, SubmissionType.CREATE_PROSPECT);
  }

  @Test
  void createUpdateProspect_successful_verifyUpdateGeneralDetails() {
    var form = new UpdateProspectForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    when(submissionDetailService.createInitialSubmissionDetail(submissionDetail.getResourceId(), SubmissionType.PROSPECT_UPDATE)).thenReturn(submissionDetailNew);
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailByIdOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(generalDetail);

    submissionCreateService.createUpdateProspect(form);

    verify(generalDetailsPersistenceService, times(1))
        .updateGeneralDetailProspect(form, submissionDetailNew, generalDetail);
  }

  @Test
  void createUpdateProspect_failure_assertThrown() {
    var form = new UpdateProspectForm();
    var generalDetailId = UUID.randomUUID();
    form.setProspectSelectedOption(generalDetailId.toString());

    doThrow(RemiEntityNotFoundException.class).when(generalDetailsPersistenceService)
        .getGeneralDetailByIdOrThrow(UUID.fromString(form.getProspectSelectedOption()));

    assertThatExceptionOfType(RemiEntityNotFoundException.class)
        .isThrownBy(()->submissionCreateService.createUpdateProspect(form));

    verify(submissionDetailService, never()).createInitialSubmissionDetail(any(), any());
    verify(generalDetailsPersistenceService, never()).updateGeneralDetailProspect(any(), any(), any());
  }

  @Test
  void createPostDrillDryHoleSubmission_Successful_VerifyCreatesPostDrillDryHoleSubmissionAndNewGeneralDetail() {
    var form = new PostDrillDryHoleForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    var prospectName = "prospect-name";
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setProspectName(prospectName);
    when(generalDetailsPersistenceService.getGeneralDetailByIdOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(generalDetail);
    when(submissionDetailService.createInitialSubmissionDetail(submissionDetail.getResourceId(), SubmissionType.POST_DRILL_DRY_HOLE))
        .thenReturn(submissionDetailNew);

    submissionCreateService.createPostDrillDryHoleSubmission(form);

    verify(generalDetailsPersistenceService).createGeneralDetailWithProspectName(submissionDetailNew, prospectName);
  }

  @Test
  void createNewDiscovery_Successful_verifySubmissionCreated() {
    var form = new StartDiscoveryForm();
    form.setProspectSelectedOption(UUID.randomUUID().toString());

    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    var discoveryResource = new Resource();
    discoveryResource.setType(ResourceType.DISCOVERY);
    var prospectResource = new Resource();
    prospectResource.setType(ResourceType.PROSPECT);
    submissionDetail.setResourceId(prospectResource);
    when(resourceService.createResource(ResourceType.DISCOVERY)).thenReturn(discoveryResource);
    when(generalDetailsPersistenceService.getGeneralDetailByIdOrThrow(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(generalDetail);

    submissionCreateService.createNewDiscovery(form);

    verify(resourceLinkService).createNewResourceLink(prospectResource, discoveryResource);

    verify(submissionDetailService, times(1))
        .createInitialSubmissionDetail(discoveryResource, SubmissionType.CREATE_DISCOVERY);
  }
}