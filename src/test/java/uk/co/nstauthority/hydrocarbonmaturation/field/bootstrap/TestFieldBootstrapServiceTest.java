package uk.co.nstauthority.hydrocarbonmaturation.field.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldRepository;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatusService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class TestFieldBootstrapServiceTest {

  @Mock
  private FieldRepository fieldRepository;
  @Mock
  private NewFieldService newFieldService;
  @Mock
  private FieldStatusService fieldStatusService;
  @Mock
  private SubmissionCreateService submissionCreateService;
  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @InjectMocks
  private TestFieldBootstrapService service;

  private SubmissionDetail mockSubmissionDetail;
  private ResourceDetail mockResourceDetail;
  private Field mockField;

  @BeforeEach
  void setUp() {
    mockSubmissionDetail = mock(SubmissionDetail.class);
    mockResourceDetail = mock(ResourceDetail.class);
    mockField = FieldTestUtils.createField(1);
  }

  // Not in @BeforeEach: early-exit tests don't reach field creation and would cause unused-stubbing failures.
  private void givenFlowThroughFieldCreation() {
    when(fieldRepository.findByCorrectedLegalFieldNameIgnoreCase(any(String.class)))
        .thenReturn(Optional.empty());
    when(mockResourceDetail.getId()).thenReturn(UUID.randomUUID());
    when(submissionCreateService.createDiscovery(any(ResourceDetailsForm.class), any(User.class)))
        .thenReturn(mockSubmissionDetail);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(any(SubmissionDetail.class)))
        .thenReturn(mockResourceDetail);
    when(newFieldService.createNewField(any(FieldForm.class))).thenReturn(mockField);
  }

  // Excludes createNewField stub — validation throws before it's reached.
  private void givenFlowThroughValidation() {
    when(fieldRepository.findByCorrectedLegalFieldNameIgnoreCase(any(String.class)))
        .thenReturn(Optional.empty());
    when(mockResourceDetail.getId()).thenReturn(UUID.randomUUID());
    when(submissionCreateService.createDiscovery(any(ResourceDetailsForm.class), any(User.class)))
        .thenReturn(mockSubmissionDetail);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(any(SubmissionDetail.class)))
        .thenReturn(mockResourceDetail);
  }

  @Test
  void bootstrapField_whenFieldAlreadyExists_returnsEmpty() {
    when(fieldRepository.findByCorrectedLegalFieldNameIgnoreCase("Existing Field"))
        .thenReturn(Optional.of(mock(Field.class)));

    var request = new BootstrapFieldRequest("Existing Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA, null, null, null);

    var result = service.bootstrapField(request);

    assertThat(result).isEmpty();
    verify(submissionCreateService, never())
        .createDiscovery(any(ResourceDetailsForm.class), any(User.class));
  }

  @Test
  void bootstrapField_offshore_createsFieldAndReturnsName() {
    givenFlowThroughFieldCreation();
    var request = new BootstrapFieldRequest("Test Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA, null, null, null);

    var result = service.bootstrapField(request);

    assertThat(result).contains("Test Field");
    verify(submissionCreateService).createDiscovery(any(ResourceDetailsForm.class), any(User.class));
    verify(submissionDetailPersistenceService).approveSubmission(mockSubmissionDetail,
        TestFieldBootstrapService.SYSTEM_USER);
    verify(resourceDetailsPersistenceService).getResourceDetailOrThrow(mockSubmissionDetail);
    verify(newFieldService).validate(any(FieldForm.class), any(BindingResult.class));
    verify(newFieldService).createNewField(any(FieldForm.class));
  }

  @Test
  void bootstrapField_onshore_createsFieldAndReturnsName() {
    givenFlowThroughFieldCreation();
    var request = new BootstrapFieldRequest("Onshore Field", OnshoreOffshore.ONSHORE,
        null, null, null, null);

    var result = service.bootstrapField(request);

    assertThat(result).contains("Onshore Field");
    verify(submissionCreateService).createDiscovery(any(ResourceDetailsForm.class), any(User.class));
    verify(newFieldService).createNewField(any(FieldForm.class));
  }

  @Test
  void bootstrapField_whenValidationFails_throwsException() {
    givenFlowThroughValidation();
    var request = new BootstrapFieldRequest("Bad Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA, null, null, null);

    doAnswer(invocation -> {
      BindingResult bindingResult = invocation.getArgument(1);
      bindingResult.rejectValue("correctedLegalFieldName", "required", "Required");
      return bindingResult;
    }).when(newFieldService).validate(any(FieldForm.class), any(BindingResult.class));

    assertThatThrownBy(() -> service.bootstrapField(request))
        .isInstanceOf(BootstrapFieldValidationException.class);

    verify(newFieldService, never()).createNewField(any(FieldForm.class));
  }

  @Test
  void bootstrapField_whenFieldStatusProvided_setsStatus() {
    givenFlowThroughFieldCreation();
    var request = new BootstrapFieldRequest("Test Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA, null, null, FieldStatus.STATUS_400);

    service.bootstrapField(request);

    verify(fieldStatusService).setMostRecentFieldStatus(mockField, FieldStatus.STATUS_400);
  }

  @Test
  void bootstrapField_whenFieldStatusNotProvided_doesNotSetStatus() {
    givenFlowThroughFieldCreation();
    var request = new BootstrapFieldRequest("Test Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA, null, null, null);

    service.bootstrapField(request);

    verify(fieldStatusService, never()).setMostRecentFieldStatus(any(Field.class), any(FieldStatus.class));
  }

  @Test
  void bootstrapField_whenDeterminationStatusNotProvided_defaultsToDetermined() {
    givenFlowThroughFieldCreation();
    var request = new BootstrapFieldRequest("Test Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA, null, null, null);

    var formCaptor = ArgumentCaptor.forClass(FieldForm.class);

    service.bootstrapField(request);

    verify(newFieldService).createNewField(formCaptor.capture());
    assertThat(formCaptor.getValue().getDeterminationStatus()).isEqualTo(DeterminationStatus.DETERMINED);
  }

  @Test
  void bootstrapField_whenDeterminationStatusProvided_usesProvidedValue() {
    givenFlowThroughFieldCreation();
    var request = new BootstrapFieldRequest("Test Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA, null, DeterminationStatus.UNDETERMINED, null);

    var formCaptor = ArgumentCaptor.forClass(FieldForm.class);

    service.bootstrapField(request);

    verify(newFieldService).createNewField(formCaptor.capture());
    assertThat(formCaptor.getValue().getDeterminationStatus()).isEqualTo(DeterminationStatus.UNDETERMINED);
  }

  @Test
  void bootstrapField_offshoreWithNorthernNorthSea_infersNnsTeam() {
    givenFlowThroughFieldCreation();
    var request = new BootstrapFieldRequest("Test Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.NORTHERN_NORTH_SEA, null, null, null);

    var formCaptor = ArgumentCaptor.forClass(FieldForm.class);

    service.bootstrapField(request);

    verify(newFieldService).createNewField(formCaptor.capture());
    assertThat(formCaptor.getValue().getResponsibleTeamType())
        .isEqualTo(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);
  }

  @Test
  void bootstrapField_offshoreWithCentralNorthSea_infersSnsTeam() {
    givenFlowThroughFieldCreation();
    var request = new BootstrapFieldRequest("Test Field", OnshoreOffshore.OFFSHORE,
        GeographicalArea.CENTRAL_NORTH_SEA, null, null, null);

    var formCaptor = ArgumentCaptor.forClass(FieldForm.class);

    service.bootstrapField(request);

    verify(newFieldService).createNewField(formCaptor.capture());
    assertThat(formCaptor.getValue().getResponsibleTeamType())
        .isEqualTo(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
  }
}
