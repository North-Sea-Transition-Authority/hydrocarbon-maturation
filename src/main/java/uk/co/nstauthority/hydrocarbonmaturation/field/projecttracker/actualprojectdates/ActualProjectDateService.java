package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@Service
class ActualProjectDateService {
  private final ActualProjectDateRepository actualProjectDateRepository;

  @Autowired
  ActualProjectDateService(ActualProjectDateRepository actualProjectDateRepository) {
    this.actualProjectDateRepository = actualProjectDateRepository;
  }

  @Transactional
  public void saveDateFromForm(
      ActualProjectDateForm form,
      Project project,
      ActualProjectDateType actualProjectDateType
  ) {

    var date = LocalDate.of(
        Integer.parseInt(form.getDateYear()),
        Integer.parseInt(form.getDateMonth()),
        Integer.parseInt(form.getDateDay())
    );

    var projectDate = new ActualProjectDate();
    projectDate.setProjectDateStatus(ActualProjectDateStatus.CREATED);

    projectDate.setProject(project);
    projectDate.setProjectDateType(actualProjectDateType);
    projectDate.setDate(date);

    actualProjectDateRepository.save(projectDate);
  }

  List<ActualProjectDate> getDates(Project project, ActualProjectDateType type) {
    return actualProjectDateRepository.getAllByProjectAndActualProjectDateType(project, type)
        .stream()
        .filter(projectDate -> ActualProjectDateStatus.CREATED.equals(projectDate.getProjectDateStatus()))
        .sorted(Comparator.comparing(ActualProjectDate::getDate))
        .toList();
  }

  public Optional<ActualProjectDate> getDate(UUID projectDateId) {
    return actualProjectDateRepository.findById(projectDateId);
  }

  @Transactional
  public void removeDate(ActualProjectDate date) {
    date.setProjectDateStatus(ActualProjectDateStatus.REMOVED);

    actualProjectDateRepository.save(date);
  }
}
