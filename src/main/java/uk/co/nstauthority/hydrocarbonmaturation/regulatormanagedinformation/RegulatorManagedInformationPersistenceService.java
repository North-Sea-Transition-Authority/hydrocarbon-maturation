package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;

@Service
public class RegulatorManagedInformationPersistenceService {

  private final RegulatorManagedInformationRepository regulatorManagedInformationRepository;
  private final ResourceSupportingDocumentService resourceSupportingDocumentService;
  private final PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;
  private final WellQueryService wellQueryService;

  @Autowired
  public RegulatorManagedInformationPersistenceService(
      RegulatorManagedInformationRepository regulatorManagedInformationRepository,
      ResourceSupportingDocumentService resourceSupportingDocumentService,
      PostFieldDiscoveryLinkService postFieldDiscoveryLinkService, WellQueryService wellQueryService) {
    this.regulatorManagedInformationRepository = regulatorManagedInformationRepository;
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
    this.postFieldDiscoveryLinkService = postFieldDiscoveryLinkService;
    this.wellQueryService = wellQueryService;
  }

  @Transactional
  public void saveResponsibleTeamAndGeoByResource(Resource resource,
                                                  ResponsibleTeamType responsibleTeamTypeEnum) {
    var regulatorManagedInformation = getRegulatorManagedInformationByResource(resource)
        .orElse(new RegulatorManagedInformation());
    regulatorManagedInformation.setResource(resource);
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamTypeEnum);
    regulatorManagedInformationRepository.save(regulatorManagedInformation);
  }

  @Transactional
  public void saveResponsibleTeamAndGeoByPostFieldResource(Resource resource,
                                                           ResponsibleTeamType responsibleTeamTypeEnum) {
    var regulatorManagedInformation = getRegulatorManagedInformationByResource(resource)
        .orElse(new RegulatorManagedInformation());
    regulatorManagedInformation.setResource(resource);
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamTypeEnum);
    regulatorManagedInformation.setLinkedToShelvedCopField(true);
    regulatorManagedInformationRepository.save(regulatorManagedInformation);
  }

  @Transactional
  public void saveRegulatorManagedInformation(RegulatorManagedInformationForm regulatorManagedInformationForm,
                                              Resource resource) {
    var regulatorManagedInformation = getRegulatorManagedInformationByResource(resource)
        .orElse(new RegulatorManagedInformation());
    regulatorManagedInformation.setResource(resource);
    regulatorManagedInformation.setGeographicalArea(regulatorManagedInformationForm.getGeographicalArea());
    regulatorManagedInformation.setResourceName(regulatorManagedInformationForm.getResourceName());
    regulatorManagedInformation.setLinkedToShelvedCopField(regulatorManagedInformationForm.getLinkedToShelvedCopField());

    if (canUpdateResponsibleTeam(resource)) {
      regulatorManagedInformation.setResponsibleTeamType(regulatorManagedInformationForm.getResponsibleTeamType());
    }

    if (BooleanUtils.isTrue(regulatorManagedInformationForm.getLinkedToShelvedCopField())) {
      postFieldDiscoveryLinkService.createOrUpdatePostFieldDiscoveryLinkFromForm(regulatorManagedInformationForm, resource);
    } else {
      postFieldDiscoveryLinkService.deletePostFieldDiscoveryLinkIfExists(resource);
    }

    resourceSupportingDocumentService.saveDocuments(
        resource, regulatorManagedInformationForm.getRelinquishmentReport(), FileDocumentType.RELINQUISHMENT_REPORT);

    if (regulatorManagedInformationForm.getWell() != null) {
      regulatorManagedInformation.setWellId(Integer.valueOf(regulatorManagedInformationForm.getWell()));
      regulatorManagedInformation.setWellReference(wellQueryService
          .findWellById(Integer.valueOf(regulatorManagedInformationForm.getWell()))
          .map(WellView::reference)
          .orElse(null)
      );
    } else {
      regulatorManagedInformation.setWellId(null);
      regulatorManagedInformation.setWellReference(null);
    }

    regulatorManagedInformation.setHydrocarbonCase(regulatorManagedInformationForm.getHydrocarbonCase());
    regulatorManagedInformation.setOpportunityType(regulatorManagedInformationForm.getOpportunityType());
    regulatorManagedInformation.setOtherTypeOfOpportunity(regulatorManagedInformationForm.getOtherTypeOfOpportunity());

    regulatorManagedInformationRepository.save(regulatorManagedInformation);
  }

  @Transactional
  public void saveResponsibleTeamAndGeographicAreaByResource(Resource resource,
                                                             ResponsibleTeamType responsibleTeamType,
                                                             GeographicalArea geographicalArea) {
    var regulatorManagedInformation = getRegulatorManagedInformationByResource(resource)
        .orElse(new RegulatorManagedInformation());

    regulatorManagedInformation.setResource(resource);
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
    regulatorManagedInformation.setGeographicalArea(geographicalArea);

    regulatorManagedInformationRepository.save(regulatorManagedInformation);
  }

  public RegulatorManagedInformation getRegulatorManagedInformationByResourceOrThrow(Resource resource) {
    return regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find the responsible team for resource with ID %s"
                .formatted(resource.getId().toString())));
  }

  public Optional<RegulatorManagedInformation> getRegulatorManagedInformationByResource(Resource resource) {
    return regulatorManagedInformationRepository.findRegulatorManagedInformationByResource(resource);
  }

  public boolean canUpdateResponsibleTeam(Resource resource) {
    return resource.getType() == ResourceType.DISCOVERY;
  }
}
