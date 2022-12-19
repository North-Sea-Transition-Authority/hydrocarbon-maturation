package uk.co.nstauthority.hydrocarbonmaturation.responsibleteam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Service
public class ResponsibleTeamPersistenceService {

  private final ResponsibleTeamRepository responsibleTeamRepository;

  @Autowired
  public ResponsibleTeamPersistenceService(
      ResponsibleTeamRepository responsibleTeamRepository) {
    this.responsibleTeamRepository = responsibleTeamRepository;
  }

  @Transactional
  public void saveResponsibleTeamForResource(Resource resource,
                                             ResponsibleTeamType responsibleTeamTypeEnum) {
    var responsibleTeam = new ResponsibleTeam();
    responsibleTeam.setResource(resource);
    responsibleTeam.setResponsibleTeamType(responsibleTeamTypeEnum);
    responsibleTeamRepository.save(responsibleTeam);
  }
}
