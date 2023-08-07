package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class OnshoreOffshoreServiceTest {

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  private OnshoreOffshoreService onshoreOffshoreService;

  @BeforeEach
  void setUp() {
    onshoreOffshoreService = new OnshoreOffshoreService(
        resourceDetailsPersistenceService,
        fieldPersistenceService);
  }

  @Test
  void getOnshoreOffshore_whenNotAField() {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var resourceDetail = new ResourceDetail();
    var onshoreOffshore = OnshoreOffshore.OFFSHORE;
    resourceDetail.setOnshoreOffshore(onshoreOffshore);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail)).thenReturn(resourceDetail);

    assertEquals(onshoreOffshoreService.getOnshoreOffshore(submissionDetail), onshoreOffshore);
  }

  @Test
  void getOnshoreOffshore_whenResourceIsAField() {
    var resource = new Resource();
    resource.setType(ResourceType.FIELD);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR, resource);
    var onshoreOffshore = OnshoreOffshore.OFFSHORE;
    var field = new Field();
    field.setResource(submissionDetail.getSubmission().getResource());
    field.setOnshoreOffshore(onshoreOffshore);
    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

    assertEquals(onshoreOffshoreService.getOnshoreOffshore(submissionDetail), onshoreOffshore);
  }
}
