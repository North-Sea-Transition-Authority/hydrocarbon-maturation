package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class DiscoveryAssociationServiceTest {
  
  @Mock
  ResourceLinkService resourceLinkService;
  @Mock
  FieldPersistenceService fieldPersistenceService;
  @Mock
  FieldStatusService fieldStatusService;
  @Mock
  SubmissionUpdateService submissionUpdateService;
  
  @InjectMocks
  DiscoveryAssociationService discoveryAssociationService;

  @Test
  void associatedWithStatus300PlusField_isAssociatedWithStatus300PlusField() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);

    var submission = new Submission();
    submission.setResource(resource);

    var fieldResource = new Resource();

    var field = new Field();

    when(resourceLinkService.getAssociatedFieldResources(resource)).thenReturn(List.of(fieldResource));

    when(fieldPersistenceService.getFieldOrThrow(fieldResource)).thenReturn(field);

    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_300));

    assertThat(discoveryAssociationService.blockedByAssociatedFieldStatus(submission)).isTrue();
  }

  @Test
  void associatedWithStatus300PlusField_isNotAssociatedWithStatus300PlusField() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);

    var submission = new Submission();
    submission.setResource(resource);

    var fieldResource = new Resource();

    var field = new Field();

    when(resourceLinkService.getAssociatedFieldResources(resource)).thenReturn(List.of(fieldResource));

    when(fieldPersistenceService.getFieldOrThrow(fieldResource)).thenReturn(field);

    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_200));

    assertThat(discoveryAssociationService.associatedWithStatus300PlusField(submission)).isFalse();
  }

  @Test
  void associatedWithStatus300PlusField_isNotDiscovery() {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT);

    var submission = new Submission();
    submission.setResource(resource);

    assertThat(discoveryAssociationService.associatedWithStatus300PlusField(submission)).isFalse();
  }

  @Test
  void blockedByAssociatedFieldStatus_isCorrection() {
    var submission = new Submission();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setCorrection(true);

    when(submissionUpdateService.getDraftUpdateSubmissionDetailBySubmission(submission))
        .thenReturn(Optional.of(submissionDetail));

    assertThat(discoveryAssociationService.blockedByAssociatedFieldStatus(submission)).isFalse();
  }

  @Test
  void blockedByAssociatedFieldStatus_isNotCorrection_isNotAssociatedWithStatus300PlusField() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);

    var submission = new Submission();
    submission.setResource(resource);

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setCorrection(false);

    when(submissionUpdateService.getDraftUpdateSubmissionDetailBySubmission(submission))
        .thenReturn(Optional.of(submissionDetail));

    when(resourceLinkService.getAssociatedFieldResources(resource)).thenReturn(List.of());

    assertThat(discoveryAssociationService.blockedByAssociatedFieldStatus(submission)).isFalse();
  }

  @Test
  void blockedByAssociatedFieldStatus_isNotCorrection_isAssociatedWithStatus300PlusField() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);

    var submission = new Submission();
    submission.setResource(resource);

    var submissionDetail = new SubmissionDetail();
    submissionDetail.setCorrection(false);

    var fieldResource = new Resource();

    var field = new Field();

    when(submissionUpdateService.getDraftUpdateSubmissionDetailBySubmission(submission))
        .thenReturn(Optional.of(submissionDetail));

    when(resourceLinkService.getAssociatedFieldResources(resource)).thenReturn(List.of(fieldResource));

    when(fieldPersistenceService.getFieldOrThrow(fieldResource)).thenReturn(field);

    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_300));

    assertThat(discoveryAssociationService.blockedByAssociatedFieldStatus(submission)).isTrue();
  }
  
}
