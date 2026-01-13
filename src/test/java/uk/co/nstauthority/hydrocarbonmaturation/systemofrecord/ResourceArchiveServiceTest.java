package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceArchiveService.ACTIVE_SUBMISSIONS_WARNING;
import static uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceArchiveService.DISCOVERY_LINKED_TO_FIELD_WARNING;
import static uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceArchiveService.OPEN_CORRECTION_WARNING;

import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
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
      Set.of(SubmissionDetailStatus.DRAFT, SubmissionDetailStatus.SUBMITTED);

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

  @Mock
  private ResourceGroupService resourceGroupService;

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
          .contains(OPEN_CORRECTION_WARNING.formatted(resource.getType().getDisplayName().toLowerCase()));
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
          .contains(OPEN_CORRECTION_WARNING.formatted(resource.getType().getDisplayName().toLowerCase()));
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
          .contains(ACTIVE_SUBMISSIONS_WARNING.formatted(resource.getType().getDisplayName().toLowerCase()));
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
          .contains(ACTIVE_SUBMISSIONS_WARNING.formatted(resource.getType().getDisplayName().toLowerCase()));
    }
  }

  @Test
  void archiveResource() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);

    resourceArchiveService.archiveResource(resource);

    verify(resourceRepository, times(1))
        .save(resourceArgumentCaptor.capture());
    assertThat(resourceArgumentCaptor.getValue().getActive())
        .isFalse();

    verify(fieldStatusSnsService, never()).publishFieldStatusUpdateSnsMessage(any(Integer.class), any(Integer.class));
  }

  @Test
  void archiveResource_field() {
    var resource = new Resource();
    resource.setType(ResourceType.FIELD);

    var field = new Field();
    field.setFieldIdentifier(1);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

    resourceArchiveService.archiveResource(resource);

    verify(resourceRepository, times(1))
        .save(resourceArgumentCaptor.capture());
    assertThat(resourceArgumentCaptor.getValue().getActive())
        .isFalse();

    verify(fieldStatusSnsService).publishFieldStatusUpdateSnsMessage(field.getFieldIdentifier(),9999);
  }

  @Test
  void unarchiveResource() {
    var resource = new Resource();
    resource.setActive(true);
    resource.setType(ResourceType.DISCOVERY);

    resourceArchiveService.unarchiveResource(resource);

    verify(resourceRepository, times(1))
        .save(resourceArgumentCaptor.capture());
    assertThat(resourceArgumentCaptor.getValue().getActive())
        .isTrue();

    verify(fieldStatusSnsService, never()).publishFieldStatusUpdateSnsMessage(any(Integer.class), any(Integer.class));
  }

  @Test
  void resourceCanBeUnarchived_inGroup_noActiveResources() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resource.setActive(false);
    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResource(resource);
    var resourceGroup = new ResourceGroup();
    resourceGroupLink.setResourceGroup(resourceGroup);
    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(resourceGroupLink);
    var otherResource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    otherResource.setActive(false);
    var otherResourceGroupLink = new ResourceGroupLink();
    otherResourceGroupLink.setResource(otherResource);
    otherResourceGroupLink.setResourceGroup(resourceGroup);
    when(resourceGroupService.getResourceGroupLinksByResourceGroup(resourceGroup))
        .thenReturn(List.of(resourceGroupLink, otherResourceGroupLink));
    assertThat(resourceArchiveService.resourceCannotBeUnarchivedWarning(resource)).isEmpty();
  }

  @Test
  void resourceCannotBeUnarchivedWarning_otherActiveResourceInGroup() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    resource.setActive(false);
    var resourceGroupLink = new ResourceGroupLink();
    resourceGroupLink.setResource(resource);
    var resourceGroup = new ResourceGroup();
    resourceGroupLink.setResourceGroup(resourceGroup);

    var otherResource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    otherResource.setActive(true);
    var otherResourceGroupLink = new ResourceGroupLink();
    otherResourceGroupLink.setResource(otherResource);
    otherResourceGroupLink.setResourceGroup(resourceGroup);

    when(resourceGroupService.getResourceGroupLinkByResource(resource))
        .thenReturn(resourceGroupLink);
    when(resourceGroupService.getResourceGroupLinksByResourceGroup(resourceGroup))
        .thenReturn(List.of(resourceGroupLink, otherResourceGroupLink));

    var warning = resourceArchiveService.resourceCannotBeUnarchivedWarning(resource);

    assertThat(warning).isNotEmpty();
    assertThat(warning.get()).contains(
        ResourceArchiveService.OTHER_ACTIVE_RESOURCE_IN_GROUP.formatted(resource.getType().getDisplayName().toLowerCase())
    );
  }

  @Test
  void unarchiveResource_field() {
    var resource = new Resource();
    resource.setType(ResourceType.FIELD);

    var field = new Field();
    field.setFieldIdentifier(1);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);
    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.of(FieldStatus.STATUS_200));

    resourceArchiveService.unarchiveResource(resource);

    verify(resourceRepository, times(1))
        .save(resourceArgumentCaptor.capture());
    assertThat(resourceArgumentCaptor.getValue().getActive())
        .isTrue();

    verify(fieldStatusSnsService).publishFieldStatusUpdateSnsMessage(1, FieldStatus.STATUS_200.getStatusValue());
  }

  @Test
  void unarchiveResource_field_statusNotFound_throwsException() {
    var resource = new Resource();
    resource.setType(ResourceType.FIELD);

    var field = new Field();
    field.setFieldIdentifier(1);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);
    when(fieldStatusService.getMostRecentFieldStatus(field)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> resourceArchiveService.unarchiveResource(resource))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("field status not found for field id: 1");
  }
}