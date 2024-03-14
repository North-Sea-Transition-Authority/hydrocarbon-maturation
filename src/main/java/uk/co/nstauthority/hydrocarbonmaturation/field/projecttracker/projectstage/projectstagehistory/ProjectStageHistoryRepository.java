package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Repository
public interface ProjectStageHistoryRepository extends CrudRepository<ProjectStageHistory, UUID> {
  Collection<ProjectStageHistory> findAllByProject(Project project);
}
