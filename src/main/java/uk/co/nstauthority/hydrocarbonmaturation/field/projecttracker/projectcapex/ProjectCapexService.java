package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.StringUtil;

@Service
class ProjectCapexService {
  private final ProjectCapexRepository projectCapexRepository;

  @Autowired
  ProjectCapexService(ProjectCapexRepository projectCapexRepository) {
    this.projectCapexRepository = projectCapexRepository;
  }

  @Transactional
  public void saveNewProjectCapex(ProjectCapexForm form, Project project) {
    var projectCapex = new ProjectCapex();
    projectCapex.setProject(project);
    projectCapex.setStatus(ProjectCapexStatus.CREATED);

    projectCapex.setDateProvided(
        DateUtil.createLocalDate(
            form.getDateProvidedYear(),
            form.getDateProvidedMonth(),
            form.getDateProvidedDay()
        ).orElse(null));
    projectCapex.setTotalCapex(form.getTotalCapex().createBigDecimalOrNull());
    projectCapex.setSourceOfInformation(form.getSourceOfInformation());

    projectCapexRepository.save(projectCapex);
  }

  List<ProjectCapex> getOrderedCreatedProjectCapexs(Project project) {
    return projectCapexRepository.findAllByProject(project)
        .stream()
        .filter(projectCapex -> projectCapex.getStatus().equals(ProjectCapexStatus.CREATED))
        .sorted(Comparator.comparing(ProjectCapex::getDateProvided))
        .toList();
  }

  Optional<ProjectCapex> getProjectCapex(UUID projectCapexId) {
    return projectCapexRepository.findById(projectCapexId);
  }

  Optional<ProjectCapexView> getProjectCapexView(UUID projectCapexId) {
    return getProjectCapex(projectCapexId).map(this::toProjectCapexView);
  }

  private ProjectCapexView toProjectCapexView(ProjectCapex projectCapex) {
    return new ProjectCapexView(
        DateUtil.formatLongDate(projectCapex.getDateProvided()),
        StringUtil.formatMoneyInMillions(projectCapex.getTotalCapex()),
        projectCapex.getSourceOfInformation().getDisplayName()
    );
  }

  @Transactional
  public void removeProjectCapex(ProjectCapex projectCapex) {
    projectCapex.setStatus(ProjectCapexStatus.REMOVED);
    projectCapexRepository.save(projectCapex);
  }
}
