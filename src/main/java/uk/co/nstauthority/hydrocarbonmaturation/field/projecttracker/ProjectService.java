package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.constraints.NotNull;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionQuerySearchRecord;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.StringUtil;

@Service
public class ProjectService {
  private final ProjectRepository projectRepository;
  private final SubmissionHistoryQueryService submissionHistoryQueryService;
  private final SubmissionDetailService submissionDetailService;
  private final Clock clock;

  @Autowired
  public ProjectService(ProjectRepository projectRepository,
                        SubmissionHistoryQueryService submissionHistoryQueryService,
                        SubmissionDetailService submissionDetailService,
                        Clock clock) {
    this.projectRepository = projectRepository;
    this.submissionHistoryQueryService = submissionHistoryQueryService;
    this.submissionDetailService = submissionDetailService;
    this.clock = clock;
  }

  public Map<String, String> getAcceptedSubmissionsMapForField(Field field) {
    List<SubmissionQuerySearchRecord> acceptedSubmissions =
        submissionHistoryQueryService.getAcceptedSubmissionsOnResource(field.getResource().getId());

    return SubmissionHistorySelectorUtil.getSubmissionSelectorItems(acceptedSubmissions);
  }

  @Transactional
  public Project saveNewProject(Field field, ProjectForm form) {
    var project = new Project();
    project.setProjectStatus(ProjectStatus.EXTANT);

    if (isFdpProject(form.getProjectType()) || isFdpaProject(form.getProjectType())) {
      // set default value that can be changed on an update
      project.setProjectPhase(ProjectPhase.ASSESSMENT_PHASE);
    }

    project.setField(field);
    project.setCreatedTimestamp(clock.instant());
    project.setProjectType(form.getProjectType());

    setCommonProjectInformation(project, form);

    return projectRepository.save(project);
  }

  @Transactional
  public Project saveProjectUpdate(Project project, ProjectForm form) {
    setCommonProjectInformation(project, form);

    if (isFdpaProject(project.getProjectType())) {
      project.setFdpaStartUpDate(LocalDate.of(
          Integer.parseInt(form.getFdpaStartUpDateYear()),
          Integer.parseInt(form.getFdpaStartUpDateMonth()),
          Integer.parseInt(form.getFdpaStartUpDateDay())
      ));
    }

    if (isFdpProject(project.getProjectType()) || isFdpaProject(project.getProjectType())) {
      project.setProjectPhase(form.getProjectPhase());
    }

    return projectRepository.save(project);
  }

  public List<ProjectListView> getCreatedProjectListViews(Field field) {
    return projectRepository.findAllByField(field)
        .stream()
        .filter(project -> ProjectStatus.EXTANT.equals(project.getProjectStatus()))
        .map(this::createProjectListView)
        .sorted(Comparator.comparing(ProjectListView::projectName))
        .toList();
  }

  public Project getProjectOrThrow(@NotNull UUID projectId) {
    return projectRepository.findById(projectId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no project with id %s".formatted(projectId)));
  }

  public Optional<Project> getProject(UUID projectId) {
    return projectRepository.findById(projectId);
  }

  public ProjectForm projectToForm(Project project) {
    var form = new ProjectForm();

    form.setProjectName(project.getProjectName());
    form.setProjectPhase(project.getProjectPhase());
    form.setProjectType(project.getProjectType());
    form.setIsPartOfPipeline(project.getPartOfPipeline());

    if (project.getSubmissionDetail() != null) {
      form.setSelectedSubmissionId(project.getSubmissionDetail().getId().toString());
    }

    var fdpaStartUpDate = project.getFdpaStartUpDate();
    if (fdpaStartUpDate != null && isFdpaProject(project.getProjectType())) {
      form.setFdpaStartUpDateDay(String.valueOf(fdpaStartUpDate.getDayOfMonth()));
      form.setFdpaStartUpDateMonth(String.valueOf(fdpaStartUpDate.getMonthValue()));
      form.setFdpaStartUpDateYear(String.valueOf(fdpaStartUpDate.getYear()));
    }

    return form;
  }

  private ProjectListView createProjectListView(Project project) {
    var displayString = "";

    if (project.getCreatedTimestamp() != null) {
      displayString = "%s created on %s".formatted(
          project.getProjectType().getDisplayName(),
          DateUtil.formatLongDate(project.getCreatedTimestamp())
      );
    }

    return new ProjectListView(
        project.getProjectName(),
        displayString,
        ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null))
    );
  }

  private void setSubmissionDetailIfSelected(ProjectForm form, Project project) {
    if (form.getSelectedSubmissionId() != null) {
      StringUtil.toUuid(form.getSelectedSubmissionId())
          .ifPresent(
              selectedSubmissionDetailId -> {
                var selectedSubmissionDetail =
                    submissionDetailService.getSubmissionDetailForIdOrError(selectedSubmissionDetailId);
                project.setSubmissionDetail(selectedSubmissionDetail);
              }
          );
    }
  }

  private void setCommonProjectInformation(Project project, ProjectForm form) {
    project.setProjectName(form.getProjectName());
    project.setPartOfPipeline(form.getIsPartOfPipeline());
    setSubmissionDetailIfSelected(form, project);
  }

  @Transactional
  public void deleteProject(Project project) {
    project.setProjectStatus(ProjectStatus.DELETED);
    projectRepository.save(project);
  }

  private static boolean isFdpProject(ProjectType projectType) {
    return ProjectType.FIELD_DEVELOPMENT_PLAN.equals(projectType);
  }

  private static boolean isFdpaProject(ProjectType projectType) {
    return ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM.equals(projectType);
  }
}
