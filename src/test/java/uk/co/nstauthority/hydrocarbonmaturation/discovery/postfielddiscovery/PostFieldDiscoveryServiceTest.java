package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication.PostFieldDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.SubmissionPersistenceService;

@ExtendWith(MockitoExtension.class)
class PostFieldDiscoveryServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  ResourceService resourceService;

  @Mock
  PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  @Mock
  RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  SubmissionPersistenceService submissionPersistenceService;

  @Mock
  SubmissionDetailPersistenceService submissionDetailPersistenceService;

  @Mock
  SubmissionDetailService submissionDetailService;

  @Mock
  ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  PostFieldDuplicationService postFieldDuplicationService;

  @InjectMocks
  PostFieldDiscoveryService postFieldDiscoveryService;

  @Captor
  private ArgumentCaptor<PostFieldDiscoveryLink> linkArgumentCaptor;

  @Test
  void createNewDiscoveryFromField() {
    var field = new Resource();
    field.setType(ResourceType.FIELD);

    var discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);

    var submission = new Submission();
    submission.setResource(discovery);

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmission(submission);
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var fieldSubmissionDetail = new SubmissionDetail();

    var form = new ResourceDetailsForm();

    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);

    when(resourceService.createResource(ResourceType.DISCOVERY)).thenReturn(discovery);
    when(submissionPersistenceService.createSubmission(discovery)).thenReturn(submission);
    when(submissionDetailPersistenceService.createInitialSubmissionDetail(submission, SubmissionType.CREATE_DISCOVERY, USER))
        .thenReturn(submissionDetail);
    when(submissionDetailService.getLatestApprovedSubmissionDetailForResource(field)).thenReturn(Optional.of(fieldSubmissionDetail));
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(field))
        .thenReturn(Optional.of(regulatorManagedInformation));

    var result = postFieldDiscoveryService.createNewDiscoveryFromField(field, USER, form);
    assertThat(result).extracting(
        SubmissionDetail::getSubmission,
        SubmissionDetail::getSubmissionType
    ).contains(
        submission,
        SubmissionType.CREATE_DISCOVERY
    );

    verify(regulatorManagedInformationPersistenceService, times(1))
        .saveResponsibleTeamAndGeoByPostFieldResource(discovery, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
    verify(postFieldDuplicationService, times(1)).duplicateSubmissionDetails(fieldSubmissionDetail, submissionDetail);
    verify(resourceDetailsPersistenceService, times(1)).createInitialResourceDetail(form, submissionDetail);
    verify(postFieldDiscoveryLinkService, times(1)).createPostFieldDiscoveryLink(field, discovery);
  }
}
