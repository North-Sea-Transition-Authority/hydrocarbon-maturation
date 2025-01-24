package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Repository
interface ActualProjectDateRepository extends CrudRepository<ActualProjectDate, UUID> {
  List<ActualProjectDate> getAllByProjectAndActualProjectDateType(Project project, ActualProjectDateType type);
}