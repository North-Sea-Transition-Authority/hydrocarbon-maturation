package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;

@ExtendWith(MockitoExtension.class)
class RegulatorManagedInformationPersistenceServiceTest {

  @Mock
  private RegulatorManagedInformationRepository regulatorManagedInformationRepository;

  @Mock
  private ResourceSupportingDocumentService resourceSupportingDocumentService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  @InjectMocks
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Test
  void saveResponsibleTeamForResource_verifyMethodCall() {
    var resource = mock(Resource.class);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);

    regulatorManagedInformationPersistenceService.saveResponsibleTeamByResource(resource, ResponsibleTeamType.EXPLORATION);

    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());

    assertThat(argCaptor.getValue())
        .hasOnlyFields("id", "resource", "responsibleTeamType", "geographicalArea")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea)
        .containsExactly(
            resource,
            ResponsibleTeamType.EXPLORATION,
            null);
  }

  @Test
  void saveResponsibleTeamForResource_verifyItems() {
    var resource = mock(Resource.class);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);

    regulatorManagedInformationPersistenceService
        .saveResponsibleTeamByResource(resource, ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());

    assertThat(argCaptor.getValue())
        .hasOnlyFields("id", "resource", "responsibleTeamType", "geographicalArea")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea)
        .containsExactly(
            resource,
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
            null);
  }

  @Test
  void saveRegulatorManagedInformation_newRegulatorManagedInformation_verifySaved() {
    var resource = mock(Resource.class);
    var form = new RegulatorManagedInformationForm();
    form.setGeographicalArea(GeographicalArea.ONSHORE);
    var fileUploadForm = new FileUploadForm();
    var listOfUploads = List.of(fileUploadForm);
    form.setRelinquishmentReport(listOfUploads);
    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.empty());
    when(responsibleTeamService.canUpdateResponsibleTeam(resource)).thenReturn(true);

    regulatorManagedInformationPersistenceService.saveRegulatorManagedInformation(
        form, resource);

    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());
    verify(resourceSupportingDocumentService, times(1))
        .updateResourceSupportingDocumentFileDescriptions(form.getRelinquishmentReport());
    assertThat(argCaptor.getValue())
        .hasOnlyFields("id", "resource", "responsibleTeamType", "geographicalArea")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea)
        .containsExactly(
            resource,
            null,
            GeographicalArea.ONSHORE);
  }

  @Test
  void saveRegulatorManagedInformation_retrievedRegulatorManagedInformation_verifySaved() {
    var resource = mock(Resource.class);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    var form = new RegulatorManagedInformationForm();
    form.setGeographicalArea(GeographicalArea.CENTRAL_NORTH_SEA);
    form.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));
    when(responsibleTeamService.canUpdateResponsibleTeam(resource)).thenReturn(true);

    regulatorManagedInformationPersistenceService.saveRegulatorManagedInformation(
        form, resource);

    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());
    verify(resourceSupportingDocumentService, times(1))
        .updateResourceSupportingDocumentFileDescriptions(form.getRelinquishmentReport());
    assertThat(argCaptor.getValue())
        .hasOnlyFields("id", "resource", "responsibleTeamType", "geographicalArea")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea)
        .containsExactly(
            resource,
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
            GeographicalArea.CENTRAL_NORTH_SEA);
  }

  @Test
  void saveRegulatorManagedInformation_cantUpdateResponsibleTeamType_verifyResponsibleTeamTypeNotSaved() {
    var resource = mock(Resource.class);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    var form = new RegulatorManagedInformationForm();
    form.setGeographicalArea(GeographicalArea.CENTRAL_NORTH_SEA);
    form.setResponsibleTeamType(null);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));
    when(responsibleTeamService.canUpdateResponsibleTeam(resource)).thenReturn(false);

    regulatorManagedInformationPersistenceService.saveRegulatorManagedInformation(
        form, resource);

    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());
    verify(resourceSupportingDocumentService, times(1))
        .updateResourceSupportingDocumentFileDescriptions(form.getRelinquishmentReport());
    assertThat(argCaptor.getValue())
        .hasOnlyFields("id", "resource", "responsibleTeamType", "geographicalArea")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea)
        .containsExactly(
            resource,
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
            GeographicalArea.CENTRAL_NORTH_SEA);
  }

  @Test
  void getResponsibleTeamForResourceOrThrow_assertReturn() {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    var responsibleTeam = mock(RegulatorManagedInformation.class);
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(responsibleTeam));
    var result = regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource);
    assertThat(result).isEqualTo(responsibleTeam);
  }

  @Test
  void getRegulatorManagedInformationByResource_assertReturn() {
    var resource = new Resource();
    var regulatorManagedInformation = new RegulatorManagedInformation();
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));
    assertThat(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .contains(regulatorManagedInformation);
  }

  @Test
  void getResponsibleTeamForResourceOrThrow_assertThrows() {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResourceOrThrow(resource))
        .hasMessage("Could not find the responsible team for resource with ID %s"
            .formatted(resource.getId()))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class);
  }
}