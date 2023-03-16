package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

@Service
public class RegulatorManagedInformationPersistenceService {

  private final RegulatorManagedInformationRepository regulatorManagedInformationRepository;

  @Autowired
  public RegulatorManagedInformationPersistenceService(
      RegulatorManagedInformationRepository regulatorManagedInformationRepository) {
    this.regulatorManagedInformationRepository = regulatorManagedInformationRepository;
  }

  @Transactional
  public void saveResponsibleTeamByResource(Resource resource,
                                            ResponsibleTeamType responsibleTeamTypeEnum) {
    var regulatorManagedInformation = getRegulatorManagedInformationByResource(resource)
        .orElse(new RegulatorManagedInformation());
    regulatorManagedInformation.setResource(resource);
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamTypeEnum);
    regulatorManagedInformationRepository.save(regulatorManagedInformation);
  }

  @Transactional
  public void saveRegulatorManagedInformation(RegulatorManagedInformationForm regulatorManagedInformationForm,
                                              Resource resource) {
    var regulatorManagedInformation = getRegulatorManagedInformationByResource(resource)
        .orElse(new RegulatorManagedInformation());
    regulatorManagedInformation.setResource(resource);
    regulatorManagedInformation.setGeographicalArea(regulatorManagedInformationForm.getGeographicalArea());
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
}
