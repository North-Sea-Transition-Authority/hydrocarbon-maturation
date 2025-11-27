package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@ExtendWith(MockitoExtension.class)
class RegulatorManagedInformationPersistenceServiceTest {

  @Mock
  private RegulatorManagedInformationRepository regulatorManagedInformationRepository;

  @Mock
  private ResourceSupportingDocumentService resourceSupportingDocumentService;

  @Mock
  private PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  @Mock
  private WellQueryService wellQueryService;

  @InjectMocks
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Test
  void saveResponsibleTeamForResource_verifyMethodCall() {
    var resource = mock(Resource.class);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);

    regulatorManagedInformationPersistenceService.saveResponsibleTeamAndGeoByResource(resource, ResponsibleTeamType.EXPLORATION);

    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());

    assertThat(argCaptor.getValue())
        .hasOnlyFields(
            "id",
            "resource",
            "responsibleTeamType",
            "geographicalArea",
            "resourceName",
            "hydrocarbonCase",
            "opportunityType",
            "otherTypeOfOpportunity",
            "linkedToShelvedCopField",
            "wellId",
            "wellReference")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea,
            RegulatorManagedInformation::getResourceName,
            RegulatorManagedInformation::getHydrocarbonCase,
            RegulatorManagedInformation::getOpportunityType,
            RegulatorManagedInformation::getOtherTypeOfOpportunity,
            RegulatorManagedInformation::getLinkedToShelvedCopField,
            RegulatorManagedInformation::getWellId,
            RegulatorManagedInformation::getWellReference)
        .containsExactly(
            resource,
            ResponsibleTeamType.EXPLORATION,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
  }

  @Test
  void saveResponsibleTeamForResource_verifyItems() {
    var resource = mock(Resource.class);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);

    regulatorManagedInformationPersistenceService
        .saveResponsibleTeamAndGeoByResource(resource, ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());

    assertThat(argCaptor.getValue())
        .hasOnlyFields(
            "id",
            "resource",
            "responsibleTeamType",
            "geographicalArea",
            "resourceName",
            "hydrocarbonCase",
            "opportunityType",
            "otherTypeOfOpportunity",
            "linkedToShelvedCopField",
            "wellId",
            "wellReference")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea,
            RegulatorManagedInformation::getResourceName,
            RegulatorManagedInformation::getHydrocarbonCase,
            RegulatorManagedInformation::getOpportunityType,
            RegulatorManagedInformation::getOtherTypeOfOpportunity,
            RegulatorManagedInformation::getLinkedToShelvedCopField,
            RegulatorManagedInformation::getWellId,
            RegulatorManagedInformation::getWellReference)
        .containsExactly(
            resource,
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
  }

  @Test
  void saveResponsibleTeamAndGeoForPostFieldResource() {
    var resource = mock(Resource.class);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);

    regulatorManagedInformationPersistenceService
        .saveResponsibleTeamAndGeoByPostFieldResource(resource, ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);

    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());

    assertThat(argCaptor.getValue())
        .hasOnlyFields(
            "id",
            "resource",
            "responsibleTeamType",
            "geographicalArea",
            "resourceName",
            "hydrocarbonCase",
            "opportunityType",
            "otherTypeOfOpportunity",
            "linkedToShelvedCopField",
            "wellId",
            "wellReference")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea,
            RegulatorManagedInformation::getResourceName,
            RegulatorManagedInformation::getHydrocarbonCase,
            RegulatorManagedInformation::getOpportunityType,
            RegulatorManagedInformation::getOtherTypeOfOpportunity,
            RegulatorManagedInformation::getLinkedToShelvedCopField,
            RegulatorManagedInformation::getWellId,
            RegulatorManagedInformation::getWellReference)
        .containsExactly(
            resource,
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
            null,
            null,
            null,
            null,
            null,
            true,
            null,
            null);
  }

  @Test
  void saveRegulatorManagedInformation_newRegulatorManagedInformation_verifySaved() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);
    var form = new RegulatorManagedInformationForm();
    form.setGeographicalArea(GeographicalArea.ONSHORE);
    var regulatorResourceName = "regulator resource name";
    form.setResourceName(regulatorResourceName);
    var hydrocarbonCase = HydrocarbonsDiscovered.GAS_ONLY;
    form.setHydrocarbonCase(hydrocarbonCase);
    var opportunityType = OpportunityType.OTHER;
    var otherTypeOfOpportunity = "other opportunity";
    form.setOpportunityType(opportunityType);
    form.setOtherTypeOfOpportunity(otherTypeOfOpportunity);
    form.setLinkedToShelvedCopField(true);
    var wellId = "1";
    form.setWell(wellId);
    var fileUploadForm = new UploadedFileForm();
    var listOfUploads = List.of(fileUploadForm);
    form.setRelinquishmentReport(listOfUploads);
    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.empty());
    var wellRef = "well-ref";
    var wellView = new WellView(wellId, wellRef);
    when(wellQueryService.findWellById(Integer.valueOf(form.getWell()))).thenReturn(Optional.of(wellView));

    regulatorManagedInformationPersistenceService.saveRegulatorManagedInformation(
        form, resource);

    verify(resourceSupportingDocumentService, times(1)).saveDocuments(resource, form.getRelinquishmentReport(), FileDocumentType.RELINQUISHMENT_REPORT);
    verify(postFieldDiscoveryLinkService, times(1)).createOrUpdatePostFieldDiscoveryLinkFromForm(form, resource);
    verify(postFieldDiscoveryLinkService, never()).deletePostFieldDiscoveryLinkIfExists(any(Resource.class));
    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());
    assertThat(argCaptor.getValue())
        .hasOnlyFields(
            "id",
            "resource",
            "responsibleTeamType",
            "geographicalArea",
            "resourceName",
            "hydrocarbonCase",
            "opportunityType",
            "otherTypeOfOpportunity",
            "linkedToShelvedCopField",
            "wellId",
            "wellReference"
        )
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea,
            RegulatorManagedInformation::getResourceName,
            RegulatorManagedInformation::getHydrocarbonCase,
            RegulatorManagedInformation::getOpportunityType,
            RegulatorManagedInformation::getOtherTypeOfOpportunity,
            RegulatorManagedInformation::getLinkedToShelvedCopField,
            RegulatorManagedInformation::getWellId,
            RegulatorManagedInformation::getWellReference)
        .containsExactly(
            resource,
            null,
            GeographicalArea.ONSHORE,
            regulatorResourceName,
            hydrocarbonCase,
            opportunityType,
            otherTypeOfOpportunity,
            true,
            Integer.valueOf(form.getWell()),
            wellView.reference()
        );
  }

  @Test
  void saveRegulatorManagedInformation_discoveryNotLinkedToShelvedCopField_verifyLinkRemoved() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);
    var form = new RegulatorManagedInformationForm();
    form.setGeographicalArea(GeographicalArea.ONSHORE);
    var regulatorResourceName = "regulator resource name";
    form.setResourceName(regulatorResourceName);
    var hydrocarbonCase = HydrocarbonsDiscovered.GAS_ONLY;
    form.setHydrocarbonCase(hydrocarbonCase);
    var opportunityType = OpportunityType.OTHER;
    var otherTypeOfOpportunity = "other opportunity";
    form.setOpportunityType(opportunityType);
    form.setOtherTypeOfOpportunity(otherTypeOfOpportunity);
    form.setLinkedToShelvedCopField(false);
    var wellId = "1";
    form.setWell(wellId);
    var fileUploadForm = new UploadedFileForm();
    var listOfUploads = List.of(fileUploadForm);
    form.setRelinquishmentReport(listOfUploads);
    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.empty());
    var wellRef = "well-ref";
    var wellView = new WellView(wellId, wellRef);
    when(wellQueryService.findWellById(Integer.valueOf(form.getWell()))).thenReturn(Optional.of(wellView));

    regulatorManagedInformationPersistenceService.saveRegulatorManagedInformation(
        form, resource);

    verify(resourceSupportingDocumentService, times(1)).saveDocuments(resource, form.getRelinquishmentReport(), FileDocumentType.RELINQUISHMENT_REPORT);
    verify(postFieldDiscoveryLinkService, never()).createOrUpdatePostFieldDiscoveryLinkFromForm(any(RegulatorManagedInformationForm.class), any(Resource.class));
    verify(postFieldDiscoveryLinkService, times(1)).deletePostFieldDiscoveryLinkIfExists(resource);
    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());
    assertThat(argCaptor.getValue())
        .hasOnlyFields(
            "id",
            "resource",
            "responsibleTeamType",
            "geographicalArea",
            "resourceName",
            "hydrocarbonCase",
            "opportunityType",
            "otherTypeOfOpportunity",
            "linkedToShelvedCopField",
            "wellId",
            "wellReference"
        )
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea,
            RegulatorManagedInformation::getResourceName,
            RegulatorManagedInformation::getHydrocarbonCase,
            RegulatorManagedInformation::getOpportunityType,
            RegulatorManagedInformation::getOtherTypeOfOpportunity,
            RegulatorManagedInformation::getLinkedToShelvedCopField,
            RegulatorManagedInformation::getWellId,
            RegulatorManagedInformation::getWellReference)
        .containsExactly(
            resource,
            null,
            GeographicalArea.ONSHORE,
            regulatorResourceName,
            hydrocarbonCase,
            opportunityType,
            otherTypeOfOpportunity,
            false,
            Integer.valueOf(form.getWell()),
            wellView.reference());
  }

  @Test
  void saveRegulatorManagedInformation_retrievedRegulatorManagedInformation_verifySaved() {
    var resource = new Resource();
    resource.setType(ResourceType.DISCOVERY);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    var form = new RegulatorManagedInformationForm();
    form.setGeographicalArea(GeographicalArea.CENTRAL_NORTH_SEA);
    form.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    var regulatorResourceName = "regulator resource name";
    form.setResourceName(regulatorResourceName);
    var hydrocarbonCase = HydrocarbonsDiscovered.GAS_ONLY;
    form.setHydrocarbonCase(hydrocarbonCase);
    var opportunityType = OpportunityType.OTHER;
    var otherTypeOfOpportunity = "other opportunity";
    form.setOpportunityType(opportunityType);
    form.setOtherTypeOfOpportunity(otherTypeOfOpportunity);
    form.setLinkedToShelvedCopField(false);
    var wellId = "1";
    form.setWell(wellId);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));
    var wellRef = "well-ref";
    var wellView = new WellView(wellId, wellRef);
    when(wellQueryService.findWellById(Integer.valueOf(form.getWell()))).thenReturn(Optional.of(wellView));

    regulatorManagedInformationPersistenceService.saveRegulatorManagedInformation(
        form, resource);

    verify(resourceSupportingDocumentService, times(1)).saveDocuments(resource, form.getRelinquishmentReport(), FileDocumentType.RELINQUISHMENT_REPORT);
    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());
    assertThat(argCaptor.getValue())
        .hasOnlyFields(
            "id",
            "resource",
            "responsibleTeamType",
            "geographicalArea",
            "resourceName",
            "hydrocarbonCase",
            "opportunityType",
            "otherTypeOfOpportunity",
            "linkedToShelvedCopField",
            "wellId",
            "wellReference")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea,
            RegulatorManagedInformation::getResourceName,
            RegulatorManagedInformation::getHydrocarbonCase,
            RegulatorManagedInformation::getOpportunityType,
            RegulatorManagedInformation::getOtherTypeOfOpportunity,
            RegulatorManagedInformation::getLinkedToShelvedCopField,
            RegulatorManagedInformation::getWellId,
            RegulatorManagedInformation::getWellReference)
        .containsExactly(
            resource,
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
            GeographicalArea.CENTRAL_NORTH_SEA,
            regulatorResourceName,
            hydrocarbonCase,
            opportunityType,
            otherTypeOfOpportunity,
            false,
            Integer.valueOf(form.getWell()),
            wellView.reference());
  }

  @Test
  void saveRegulatorManagedInformation_cantUpdateResponsibleTeamType_verifyResponsibleTeamTypeNotSaved() {
    var resource = new Resource();
    resource.setType(ResourceType.PROSPECT); // Can only update responsible team of discoveries
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    var form = new RegulatorManagedInformationForm();
    form.setGeographicalArea(GeographicalArea.CENTRAL_NORTH_SEA);
    form.setResponsibleTeamType(null);
    form.setHydrocarbonCase(null);
    form.setOpportunityType(null);
    form.setOtherTypeOfOpportunity(null);
    var regulatorResourceName = "regulator resource name";
    form.setResourceName(regulatorResourceName);
    form.setLinkedToShelvedCopField(false);
    var wellId = "1";
    form.setWell(wellId);

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);
    when(regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));
    var wellRef = "well-ref";
    var wellView = new WellView(wellId, wellRef);
    when(wellQueryService.findWellById(Integer.valueOf(form.getWell()))).thenReturn(Optional.of(wellView));

    regulatorManagedInformationPersistenceService.saveRegulatorManagedInformation(
        form, resource);

    verify(resourceSupportingDocumentService, times(1)).saveDocuments(resource, form.getRelinquishmentReport(), FileDocumentType.RELINQUISHMENT_REPORT);
    verify(regulatorManagedInformationRepository, times(1)).save(argCaptor.capture());
    assertThat(argCaptor.getValue())
        .hasOnlyFields(
            "id",
            "resource",
            "responsibleTeamType",
            "geographicalArea",
            "resourceName",
            "hydrocarbonCase",
            "opportunityType",
            "otherTypeOfOpportunity",
            "linkedToShelvedCopField",
            "wellId",
            "wellReference")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea,
            RegulatorManagedInformation::getResourceName,
            RegulatorManagedInformation::getHydrocarbonCase,
            RegulatorManagedInformation::getOpportunityType,
            RegulatorManagedInformation::getOtherTypeOfOpportunity,
            RegulatorManagedInformation::getLinkedToShelvedCopField,
            RegulatorManagedInformation::getWellId,
            RegulatorManagedInformation::getWellReference)
        .containsExactly(
            resource,
            ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA,
            GeographicalArea.CENTRAL_NORTH_SEA,
            regulatorResourceName,
            null,
            null,
            null,
            false,
            Integer.valueOf(form.getWell()),
            wellView.reference());
  }

  @Test
  void saveResponsibleTeamAndGeographicAreaByResource_verifyCalls() {
    var resource = mock(Resource.class);
    var responsibleTeamType = ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND;
    var geographicalArea = GeographicalArea.NORTHERN_NORTH_SEA;

    var argCaptor = ArgumentCaptor.forClass(RegulatorManagedInformation.class);
    regulatorManagedInformationPersistenceService.saveResponsibleTeamAndGeographicAreaByResource(resource, responsibleTeamType, geographicalArea);

    verify(regulatorManagedInformationRepository).save(argCaptor.capture());

    assertThat(argCaptor.getValue())
        .hasOnlyFields(
            "id",
            "resource",
            "responsibleTeamType",
            "geographicalArea",
            "resourceName",
            "hydrocarbonCase",
            "opportunityType",
            "otherTypeOfOpportunity",
            "linkedToShelvedCopField",
            "wellId",
            "wellReference")
        .extracting(
            RegulatorManagedInformation::getResource,
            RegulatorManagedInformation::getResponsibleTeamType,
            RegulatorManagedInformation::getGeographicalArea,
            RegulatorManagedInformation::getResourceName,
            RegulatorManagedInformation::getHydrocarbonCase,
            RegulatorManagedInformation::getOpportunityType,
            RegulatorManagedInformation::getOtherTypeOfOpportunity,
            RegulatorManagedInformation::getLinkedToShelvedCopField,
            RegulatorManagedInformation::getWellId,
            RegulatorManagedInformation::getWellReference)
        .containsExactly(
            resource,
            responsibleTeamType,
            geographicalArea,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
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