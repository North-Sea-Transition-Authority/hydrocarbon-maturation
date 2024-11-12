package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceArchiveService.ACTIVE_SUBMISSIONS_WARNING;
import static uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceArchiveService.DISCOVERY_LINKED_TO_FIELD_WARNING;
import static uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceArchiveService.OPEN_CORRECTION_WARNING;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRepository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.field.epmq.FieldStatusSnsService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@ExtendWith(MockitoExtension.class)
class ResourceArchiveServiceTest {

  private static final Set<SubmissionDetailStatus> OPEN_CORRECTION_STATUSES =
      Set.of(SubmissionDetailStatus.ACCEPTED, SubmissionDetailStatus.DRAFT, SubmissionDetailStatus.SUBMITTED);

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private ResourceRepository resourceRepository;

  @Mock
  private PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  @Mock
  private FieldStatusSnsService fieldStatusSnsService;

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  @Mock
  private FieldStatusService fieldStatusService;

  @InjectMocks
  private ResourceArchiveService resourceArchiveService;

  private ArgumentCaptor<Resource> resourceArgumentCaptor = ArgumentCaptor.forClass(Resource.class);

  @Nested
  class ResourceCannotBeArchivedWarning {

    @ParameterizedTest
    @EnumSource(value = ResourceType.class, names = "DISCOVERY", mode = EnumSource.Mode.EXCLUDE)
    void noWarnings(ResourceType resourceType) {
      var resource = new Resource();
      resource.setType(resourceType);

      when(submissionDetailService.openCorrectionExists(OPEN_CORRECTION_STATUSES, resource))
          .thenReturn(false);
      when(submissionDetailService.hasDraftOrSubmittedSubmission(resource))
          .thenReturn(false);

      assertThat(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
          .isEmpty();
    }

    @Test
    void discovery_noWarnings() {
      var resource = new Resource();
      resource.setType(ResourceType.DISCOVERY);

      when(resourceLinkService.discoveryIsLinkedToField(resource))
          .thenReturn(false);
      when(submissionDetailService.openCorrectionExists(OPEN_CORRECTION_STATUSES, resource))
          .thenReturn(false);
      when(submissionDetailService.hasDraftOrSubmittedSubmission(resource))
          .thenReturn(false);

      assertThat(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
          .isEmpty();
    }

    @Test
    void discoveryIsLinkedToField() {
      var resource = new Resource();
      resource.setType(ResourceType.DISCOVERY);

      when(resourceLinkService.discoveryIsLinkedToField(resource))
          .thenReturn(true);

      assertThat(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
          .isNotEmpty()
          .contains(DISCOVERY_LINKED_TO_FIELD_WARNING);
    }

    @Test
    void discoveryIsLinkedToShelvedCopField() {
      var resource = new Resource();
      resource.setType(ResourceType.DISCOVERY);

      when(resourceLinkService.discoveryIsLinkedToField(resource))
          .thenReturn(false);

      when(postFieldDiscoveryLinkService.postFieldDiscoveryLinkExists(resource))
          .thenReturn(true);

      assertThat(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
          .isNotEmpty()
          .contains(DISCOVERY_LINKED_TO_FIELD_WARNING);
    }

    @ParameterizedTest
    @EnumSource(value = ResourceType.class, names = "DISCOVERY", mode = EnumSource.Mode.EXCLUDE)
    void openCorrection(ResourceType resourceType) {
      var resource = new Resource();
      resource.setType(resourceType);

      when(submissionDetailService.openCorrectionExists(OPEN_CORRECTION_STATUSES, resource))
          .thenReturn(true);

      assertThat(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
          .isNotEmpty()
          .contains(OPEN_CORRECTION_WARNING.formatted(resource.getType().getDisplayName()));
    }

    @Test
    void openCorrection_discovery() {
      var resource = new Resource();
      resource.setType(ResourceType.DISCOVERY);

      when(resourceLinkService.discoveryIsLinkedToField(resource))
          .thenReturn(false);
      when(submissionDetailService.openCorrectionExists(OPEN_CORRECTION_STATUSES, resource))
          .thenReturn(true);

      assertThat(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
          .isNotEmpty()
          .contains(OPEN_CORRECTION_WARNING.formatted(resource.getType().getDisplayName()));
    }

    @ParameterizedTest
    @EnumSource(value = ResourceType.class, names = "DISCOVERY", mode = EnumSource.Mode.EXCLUDE)
    void draftOrSubmittedSubmissions(ResourceType resourceType) {
      var resource = new Resource();
      resource.setType(resourceType);

      when(submissionDetailService.openCorrectionExists(OPEN_CORRECTION_STATUSES, resource))
          .thenReturn(false);
      when(submissionDetailService.hasDraftOrSubmittedSubmission(resource))
          .thenReturn(true);

      assertThat(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
          .isNotEmpty()
          .contains(ACTIVE_SUBMISSIONS_WARNING.formatted(resource.getType().getDisplayName()));
    }

    @Test
    void draftOrSubmittedSubmissions_discovery() {
      var resource = new Resource();
      resource.setType(ResourceType.DISCOVERY);

      when(resourceLinkService.discoveryIsLinkedToField(resource))
          .thenReturn(false);
      when(submissionDetailService.openCorrectionExists(OPEN_CORRECTION_STATUSES, resource))
          .thenReturn(false);
      when(submissionDetailService.hasDraftOrSubmittedSubmission(resource))
          .thenReturn(true);

      assertThat(resourceArchiveService.resourceCannotBeArchivedWarning(resource))
          .isNotEmpty()
          .contains(ACTIVE_SUBMISSIONS_WARNING.formatted(resource.getType().getDisplayName()));
    }
  }

  @Test
  void archiveResource() {
    var resource = new Resource();
    resource.setArchived(false);
    resource.setType(ResourceType.DISCOVERY);

    resourceArchiveService.archiveResource(resource);

    verify(resourceRepository, times(1))
        .save(resourceArgumentCaptor.capture());
    assertThat(resourceArgumentCaptor.getValue().getArchived())
        .isTrue();

    verify(fieldStatusSnsService, never()).publishFieldStatusUpdateSnsMessage(any(Integer.class), any(Integer.class));
  }

  @Test
  void archiveResource_field() {
    var resource = new Resource();
    resource.setArchived(false);
    resource.setType(ResourceType.FIELD);

    var field = new Field();
    field.setFieldIdentifier(1);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

    resourceArchiveService.archiveResource(resource);

    verify(resourceRepository, times(1))
        .save(resourceArgumentCaptor.capture());
    assertThat(resourceArgumentCaptor.getValue().getArchived())
        .isTrue();

    verify(fieldStatusSnsService).publishFieldStatusUpdateSnsMessage(field.getFieldIdentifier(),9999);
  }

  @Test
  void unarchiveResource() {
    var resource = new Resource();
    resource.setArchived(true);
    resource.setType(ResourceType.DISCOVERY);

    resourceArchiveService.unarchiveResource(resource);

    verify(resourceRepository, times(1))
        .save(resourceArgumentCaptor.capture());
    assertThat(resourceArgumentCaptor.getValue().getArchived())
        .isFalse();

    verify(fieldStatusSnsService, never()).publishFieldStatusUpdateSnsMessage(any(Integer.class), any(Integer.class));
  }

  @Test
  void unarchiveResource_field() {
    var resource = new Resource();
    resource.setArchived(true);
    resource.setType(ResourceType.FIELD);

    var field = new Field();
    field.setFieldIdentifier(1);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);
    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_200));

    resourceArchiveService.unarchiveResource(resource);

    verify(resourceRepository, times(1))
        .save(resourceArgumentCaptor.capture());
    assertThat(resourceArgumentCaptor.getValue().getArchived())
        .isFalse();

    verify(fieldStatusSnsService).publishFieldStatusUpdateSnsMessage(1, FieldStatus.STATUS_200.getStatusValue());
  }
}