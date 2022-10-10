package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
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

  @InjectMocks
  private SubmissionCreateService submissionCreateService;

  @Test
  void createInitialProspect() {
    Resource resource = new Resource();

    when(resourceService.createResource(ResourceType.PROSPECT)).thenReturn(resource);

    submissionCreateService.createInitialProspect();

    verify(submissionDetailService, times(1)).createInitialSubmissionDetail(resource, SubmissionType.CREATE_PROSPECT);
  }

  @Test
  void createUpdateProspect_successful_verifyUpdateGeneralDetails() {
    var form = new UpdateProspectForm();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    var submissionDetailNew = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    Resource resource = new Resource();
    UUID submissionId = UUID.randomUUID();
    resource.setId(submissionId);
    form.setProspectSelectedOption(submissionId.toString());
    submissionDetail.setResourceId(resource);
    submissionDetail.setId(submissionId);

    when(resourceService.getResourceForId(submissionDetail.getId())).thenReturn(Optional.of(resource));
    when(submissionDetailService.createInitialSubmissionDetail(resource, SubmissionType.PROSPECT_UPDATE)).thenReturn(submissionDetailNew);
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    when(generalDetailsPersistenceService.getGeneralDetailById(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(Optional.of(generalDetail));

    submissionCreateService.createUpdateProspect(form);

    verify(generalDetailsPersistenceService, times(1))
        .updateGeneralDetailProspect(form, submissionDetailNew, generalDetail);
  }

  @Test
  void createUpdateProspect_failure_assertThrown() {
    var form = new UpdateProspectForm();
    UUID generalDetailId = UUID.randomUUID();
    form.setProspectSelectedOption(generalDetailId.toString());

    when(generalDetailsPersistenceService.getGeneralDetailById(UUID.fromString(form.getProspectSelectedOption())))
        .thenReturn(Optional.empty());

    assertThatThrownBy(()->submissionCreateService.createUpdateProspect(form))
        .hasMessage("The prospect with ID %s you want to update does not exist!"
            .formatted(form.getProspectSelectedOption()));

    verify(submissionDetailService, never()).createInitialSubmissionDetail(any(), any());
    verify(generalDetailsPersistenceService, never()).updateGeneralDetailProspect(any(), any(), any());
  }
}