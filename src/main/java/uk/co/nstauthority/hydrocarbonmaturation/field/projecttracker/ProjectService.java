package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import jakarta.validation.constraints.NotNull;
import java.time.Clock;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
  public void saveNewProject(Field field, ProjectForm form) {
    var project = new Project();

    project.setField(field);
    project.setProjectName(form.getProjectName());
    project.setProjectType(form.getProjectType());
    project.setProjectStatus(form.getProjectStatus());
    project.setPartOfPipeline(form.getIsPartOfPipeline());
    project.setCreatedTimestamp(clock.instant());

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
    projectRepository.save(project);
  }

  public List<ProjectListView> getProjectListViews(Field field) {
    return projectRepository.findAllByField(field)
        .stream()
        .map(project -> new ProjectListView(
            project.getProjectName(),
            "%s created on %s".formatted(
                project.getProjectType().getDisplayName(),
                DateUtil.formatLongDate(project.getCreatedTimestamp())
            ),
            ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(project.getField().getResource().getId(), project.getId()))
            )
        )
        .sorted(Comparator.comparing(ProjectListView::projectName))
        .toList();
  }

  public Project getProjectOrThrow(@NotNull UUID projectId) {
    return projectRepository.findById(projectId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no project with id %s".formatted(projectId)));
  }
}
