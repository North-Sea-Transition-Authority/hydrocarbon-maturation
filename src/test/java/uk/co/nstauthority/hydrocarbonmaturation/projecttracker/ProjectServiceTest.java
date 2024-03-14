package uk.co.nstauthority.hydrocarbonmaturation.projecttracker;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectListView;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectRepository;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionQuerySearchRecord;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
  @Mock
  private ProjectRepository projectRepository;
  @Mock
  private SubmissionHistoryQueryService submissionHistoryQueryService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private Clock clock;
  @InjectMocks
  private ProjectService projectService;

  private final Instant FIXED_INSTANT = Instant.parse("2024-02-01T00:00:00.00Z");

  private Field field;

  @BeforeEach
  void setUp() {
    field = FieldTestUtils.createField(8000);
  }

  @Nested
  class GivenGettingAcceptedSubmissionsForField {
    @Test
    void whenSingleAcceptedSubmission_thenSubmissionInMap() {
      var searchRecord = new SubmissionQuerySearchRecord(
          SubmissionDetailStatus.ACCEPTED,
          Timestamp.from(Instant.now()),
          UUID.randomUUID(),
          SubmissionType.CREATE_CSR
      );
      var submissionQuerySearchRecords = List.of(searchRecord);
      when(submissionHistoryQueryService.getAcceptedSubmissionsOnResource(field.getResource().getId()))
          .thenReturn(submissionQuerySearchRecords);

      assertThat(projectService.getAcceptedSubmissionsMapForField(field))
          .isEqualTo(SubmissionHistorySelectorUtil.getSubmissionSelectorItems(submissionQuerySearchRecords));

      verify(submissionHistoryQueryService).getAcceptedSubmissionsOnResource(field.getResource().getId());
      verifyNoMoreInteractions(submissionHistoryQueryService);
    }

    @Test
    void whenMultipleAcceptedSubmission_thenAllSubmissionsInMap() {
      var searchRecord1 = new SubmissionQuerySearchRecord(
          SubmissionDetailStatus.ACCEPTED,
          Timestamp.valueOf(LocalDateTime.of(2024, 1, 15, 0, 0, 0)),
          UUID.randomUUID(),
          SubmissionType.CREATE_CSR
      );
      var searchRecord2 = new SubmissionQuerySearchRecord(
          SubmissionDetailStatus.ACCEPTED,
          Timestamp.valueOf(LocalDateTime.of(2024, 2, 27, 0, 0, 0)),
          UUID.randomUUID(),
          SubmissionType.CREATE_FDP
      );
      var submissionQuerySearchRecords = List.of(searchRecord1, searchRecord2);
      when(submissionHistoryQueryService.getAcceptedSubmissionsOnResource(field.getResource().getId()))
          .thenReturn(submissionQuerySearchRecords);

      assertThat(projectService.getAcceptedSubmissionsMapForField(field))
          .usingRecursiveComparison()
          .isEqualTo(
              Map.of(
                  searchRecord2.submissionDetailId().toString(),
                  "Submission 1: %s (Accepted: 27 February 2024)"
                      .formatted(searchRecord2.submissionType().getTypeName()),

                  searchRecord1.submissionDetailId().toString(),
                  "Submission 2: %s (Accepted: 15 January 2024)"
                      .formatted(searchRecord1.submissionType().getTypeName())
              )
          );

      verify(submissionHistoryQueryService).getAcceptedSubmissionsOnResource(field.getResource().getId());
      verifyNoMoreInteractions(submissionHistoryQueryService);
    }

    @Test
    void whenNoAcceptedSubmissions_thenEmptyMap() {
      assertThat(projectService.getAcceptedSubmissionsMapForField(field))
          .isEmpty();

      verify(submissionHistoryQueryService).getAcceptedSubmissionsOnResource(field.getResource().getId());
      verifyNoMoreInteractions(submissionHistoryQueryService);
    }
  }

  @Nested
  class GivenSavingNewProject {
    private SubmissionDetail submissionDetail;
    private Field field;
    private ProjectForm form;

    @BeforeEach
    void setUp() {
      submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      field = FieldTestUtils.createField(8000);
      form = getPopulatedProjectForm();
    }

    @Test
    void whenSelectedSubmissionDetailDoesNotExist_thenAssertThrows() {
      var randomUuid = UUID.randomUUID().toString();
      form.setSelectedSubmissionId(randomUuid);

      when(submissionDetailService.getSubmissionDetailForIdOrError(UUID.fromString(randomUuid)))
          .thenThrow(RemiEntityNotFoundException.class);

      assertThatThrownBy(() -> projectService.saveNewProject(field, form))
          .isInstanceOf(RemiEntityNotFoundException.class);

      verifyNoInteractions(projectRepository);
    }

    @Test
    void whenNoSelectedSubmissionDetail_thenAssertSavedWithoutSubmissionDetail() {
      form.setSelectedSubmissionId(null);

      when(clock.instant())
          .thenReturn(FIXED_INSTANT);

      var savedProject = new Project();
      when(projectRepository.save(any(Project.class)))
          .thenReturn(savedProject);

      assertThat(projectService.saveNewProject(field, form))
          .isEqualTo(savedProject);

      var captor = ArgumentCaptor.forClass(Project.class);
      verify(projectRepository).save(captor.capture());
      verifyNoMoreInteractions(projectRepository);

      verifyNoInteractions(submissionDetailService);

      assertThat(captor.getValue())
          .extracting(
              Project::getField,
              Project::getProjectName,
              Project::getProjectType,
              Project::getProjectStatus,
              Project::getPartOfPipeline,
              Project::getCreatedTimestamp
          ).containsExactly(
              field,
              form.getProjectName(),
              form.getProjectType(),
              form.getProjectStatus(),
              form.getIsPartOfPipeline(),
              FIXED_INSTANT
          );
    }

    @Test
    void whenSelectedSubmissionDetail_thenAssertSavedWithSubmissionDetail() {
      when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
          .thenReturn(submissionDetail);

      when(clock.instant())
          .thenReturn(FIXED_INSTANT);
      var savedProject = new Project();
      when(projectRepository.save(any(Project.class)))
          .thenReturn(savedProject);

      assertThat(projectService.saveNewProject(field, form))
          .isEqualTo(savedProject);

      var captor = ArgumentCaptor.forClass(Project.class);
      verify(projectRepository).save(captor.capture());
      verifyNoMoreInteractions(projectRepository);

      verify(submissionDetailService).getSubmissionDetailForIdOrError(submissionDetail.getId());
      verifyNoMoreInteractions(submissionDetailService);

      assertThat(captor.getValue())
          .extracting(
              Project::getField,
              Project::getProjectName,
              Project::getProjectType,
              Project::getProjectStatus,
              Project::getPartOfPipeline,
              Project::getSubmissionDetail,
              Project::getCreatedTimestamp
          ).containsExactly(
              field,
              form.getProjectName(),
              form.getProjectType(),
              form.getProjectStatus(),
              form.getIsPartOfPipeline(),
              submissionDetail,
              FIXED_INSTANT
          );
    }

    private ProjectForm getPopulatedProjectForm() {
      var form = new ProjectForm();
      form.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN);
      form.setProjectStatus(ProjectStatus.ASSESSMENT_PHASE);
      form.setIsPartOfPipeline(true);
      form.setSelectedSubmissionId(submissionDetail.getId().toString());
      return form;
    }
  }

  @Nested
  class GivenSaveProjectUpdate {
    private SubmissionDetail submissionDetail;
    private Project project;
    private ProjectForm form;

    @BeforeEach
    void setUp() {
      submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_CSR);
      project = ProjectTestUtils.createPopulatedProject(
          "Project name",
          ProjectType.FIELD_DEVELOPMENT_PLAN,
          ProjectStatus.ON_HOLD,
          true,
          Instant.now()
      );
      form = getPopulatedProjectForm();
    }

    @Test
    void whenSelectedSubmissionDetailDoesNotExist_thenAssertThrows() {
      var randomUuid = UUID.randomUUID().toString();
      form.setSelectedSubmissionId(randomUuid);

      when(submissionDetailService.getSubmissionDetailForIdOrError(UUID.fromString(randomUuid)))
          .thenThrow(RemiEntityNotFoundException.class);

      assertThatThrownBy(() -> projectService.saveProjectUpdate(project, form))
          .isInstanceOf(RemiEntityNotFoundException.class);

      verifyNoInteractions(projectRepository);
    }

    @Test
    void whenNoSelectedSubmissionDetail_thenAssertSavedWithoutSubmissionDetail() {
      form.setSelectedSubmissionId(null);

      var savedProject = new Project();
      when(projectRepository.save(any(Project.class)))
          .thenReturn(savedProject);

      assertThat(projectService.saveProjectUpdate(project, form))
          .isEqualTo(savedProject);

      var captor = ArgumentCaptor.forClass(Project.class);
      verify(projectRepository).save(captor.capture());
      verifyNoMoreInteractions(projectRepository);

      verifyNoInteractions(submissionDetailService);

      assertThat(captor.getValue())
          .extracting(
              Project::getProjectType,
              Project::getCreatedTimestamp,
              Project::getField,
              Project::getProjectName,
              Project::getProjectStatus,
              Project::getPartOfPipeline,
              Project::getSubmissionDetail
          ).containsExactly(
              project.getProjectType(),
              project.getCreatedTimestamp(),
              project.getField(),
              form.getProjectName(),
              form.getProjectStatus(),
              form.getIsPartOfPipeline(),
              null
          );
    }

    @Test
    void whenSelectedSubmissionDetail_thenAssertSavedWithSubmissionDetail() {
      when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetail.getId()))
          .thenReturn(submissionDetail);
      var savedProject = new Project();
      when(projectRepository.save(any(Project.class)))
          .thenReturn(savedProject);

      assertThat(projectService.saveProjectUpdate(project, form))
          .isEqualTo(savedProject);

      var captor = ArgumentCaptor.forClass(Project.class);
      verify(projectRepository).save(captor.capture());
      verifyNoMoreInteractions(projectRepository);

      verify(submissionDetailService).getSubmissionDetailForIdOrError(submissionDetail.getId());
      verifyNoMoreInteractions(submissionDetailService);

      assertThat(captor.getValue())
          .extracting(
              Project::getProjectType,
              Project::getCreatedTimestamp,
              Project::getField,
              Project::getProjectName,
              Project::getProjectStatus,
              Project::getPartOfPipeline,
              Project::getSubmissionDetail
          ).containsExactly(
              project.getProjectType(),
              project.getCreatedTimestamp(),
              project.getField(),
              form.getProjectName(),
              form.getProjectStatus(),
              form.getIsPartOfPipeline(),
              submissionDetail
          );
    }

    @Test
    void whenFdpaProject_thenSaveFdpaStartUpDate() {
      project.setProjectType(ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM);

      var savedProject = new Project();
      when(projectRepository.save(any(Project.class)))
          .thenReturn(savedProject);

      assertThat(projectService.saveProjectUpdate(project, form))
          .isEqualTo(savedProject);

      var captor = ArgumentCaptor.forClass(Project.class);
      verify(projectRepository).save(captor.capture());
      verifyNoMoreInteractions(projectRepository);

      assertThat(captor.getValue())
          .extracting(
              Project::getProjectType,
              Project::getCreatedTimestamp,
              Project::getField,
              Project::getProjectName,
              Project::getProjectStatus,
              Project::getPartOfPipeline,
              Project::getFdpaStartUpDate
          ).containsExactly(
              project.getProjectType(),
              project.getCreatedTimestamp(),
              project.getField(),
              form.getProjectName(),
              form.getProjectStatus(),
              form.getIsPartOfPipeline(),
              LocalDate.of(2002, 11, 3)
          );
    }

    @ParameterizedTest
    @EnumSource(
        value = ProjectType.class,
        names = "FIELD_DEVELOPMENT_PLAN_ADDENDUM",
        mode = EnumSource.Mode.EXCLUDE
    )
    void whenNonFdpaProject_thenStartUpDateNull(ProjectType projectType) {
      project.setProjectType(projectType);

      var savedProject = new Project();
      when(projectRepository.save(any(Project.class)))
          .thenReturn(savedProject);

      assertThat(projectService.saveProjectUpdate(project, form))
          .isEqualTo(savedProject);

      var captor = ArgumentCaptor.forClass(Project.class);
      verify(projectRepository).save(captor.capture());
      verifyNoMoreInteractions(projectRepository);

      assertThat(captor.getValue().getFdpaStartUpDate()).isNull();
    }

    private ProjectForm getPopulatedProjectForm() {
      var form = new ProjectForm();
      form.setProjectType(ProjectType.CESSATION_OF_PRODUCTION);
      form.setProjectStatus(ProjectStatus.ON_HOLD);
      form.setIsPartOfPipeline(true);
      form.setSelectedSubmissionId(submissionDetail.getId().toString());
      form.setFdpaStartUpDateDay("3");
      form.setFdpaStartUpDateMonth("11");
      form.setFdpaStartUpDateYear("2002");
      return form;
    }
  }

  @Nested
  class GivenGetProjectListViews {
    @Test
    void whenNoProjectsForField_thenEmptyList() {
      assertThat(projectService.getProjectListViews(field))
          .isEmpty();
    }

    @Test
    void whenOneProjectForField_thenProjectListViewCorrect() {
      var project = ProjectTestUtils.createPopulatedProject(
          "Project name",
          ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM,
          ProjectStatus.AUTHORISE_PHASE,
          true,
          FIXED_INSTANT
      );

      var expectedCaption = "%s created on 1 February 2024".formatted(project.getProjectType().getDisplayName());

      when(projectRepository.findAllByField(field))
          .thenReturn(List.of(project));
      assertThat(projectService.getProjectListViews(field))
          .extracting(
              ProjectListView::projectName,
              ProjectListView::projectCaption,
              ProjectListView::linkUrl
          ).containsExactly(
              Tuple.tuple(
                  project.getProjectName(),
                  expectedCaption,
                  ReverseRouter.route(on(ViewProjectController.class).getViewProject(
                      project.getField().getResource().getId(),
                      project.getId())
                  )
              )
          );
    }

    @Test
    void whenMultipleProjectsForField_thenOrderedList() {
      var firstCreatedProject = ProjectTestUtils.createPopulatedProject(
          "Project 1",
          ProjectType.FIELD_DEVELOPMENT_PLAN,
          ProjectStatus.AUTHORISE_PHASE,
          false,
          FIXED_INSTANT);

      var secondCreatedProject = ProjectTestUtils.createPopulatedProject(
          "Project 2",
          ProjectType.CESSATION_OF_PRODUCTION,
          ProjectStatus.ASSESSMENT_PHASE,
          false,
          FIXED_INSTANT.plus(1, ChronoUnit.HOURS));

      var thirdCreatedProject = ProjectTestUtils.createPopulatedProject(
          "Project 3",
          ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM,
          ProjectStatus.ON_HOLD,
          true,
          FIXED_INSTANT.plus(10, ChronoUnit.DAYS));

      when(projectRepository.findAllByField(field))
          .thenReturn(List.of(thirdCreatedProject, secondCreatedProject, firstCreatedProject));
      assertThat(projectService.getProjectListViews(field))
          .extracting(
              ProjectListView::projectName,
              ProjectListView::linkUrl
          ).containsExactly(
              Tuple.tuple(
                  firstCreatedProject.getProjectName(),
                  ReverseRouter.route(on(ViewProjectController.class).getViewProject(
                      firstCreatedProject.getField().getResource().getId(),
                      firstCreatedProject.getId()
                  ))
              ),
              Tuple.tuple(
                  secondCreatedProject.getProjectName(),
                  ReverseRouter.route(on(ViewProjectController.class).getViewProject(
                      secondCreatedProject.getField().getResource().getId(),
                      secondCreatedProject.getId()
                  ))
              ),
              Tuple.tuple(
                  thirdCreatedProject.getProjectName(),
                  ReverseRouter.route(on(ViewProjectController.class).getViewProject(
                      thirdCreatedProject.getField().getResource().getId(),
                      thirdCreatedProject.getId()
                  ))
              )
          );
    }
  }

  @Nested
  class GivenProjectToForm {
    @ParameterizedTest
    @EnumSource(
        value = ProjectType.class,
        names = "FIELD_DEVELOPMENT_PLAN_ADDENDUM",
        mode = EnumSource.Mode.EXCLUDE
    )
    void whenProjectTypeNonFdpa_withSubmissionDetail_thenValuesOnForm(ProjectType projectType) {
      var project = ProjectTestUtils.createPopulatedProject(
          "Project name",
          projectType,
          ProjectStatus.ON_HOLD,
          true,
          Instant.now()
      );
      project.setSubmissionDetail(SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_FDP));

      assertThat(projectService.projectToForm(project))
          .extracting(
              ProjectForm::getProjectName,
              ProjectForm::getProjectType,
              ProjectForm::getProjectStatus,
              ProjectForm::getIsPartOfPipeline,
              ProjectForm::getSelectedSubmissionId,
              ProjectForm::getFdpaStartUpDateDay,
              ProjectForm::getFdpaStartUpDateMonth,
              ProjectForm::getFdpaStartUpDateYear
          ).containsExactly(
              project.getProjectName(),
              project.getProjectType(),
              project.getProjectStatus(),
              project.getPartOfPipeline(),
              project.getSubmissionDetail().getId().toString(),
              null,
              null,
              null
          );
    }

    @Test
    void whenProjectTypeFdpa_thenValuesOnForm() {
      var project = ProjectTestUtils.createPopulatedProject(
          "Project name",
          ProjectType.FIELD_DEVELOPMENT_PLAN_ADDENDUM,
          ProjectStatus.ON_HOLD,
          true,
          Instant.now()
      );
      var fdpaStartUpDate = LocalDate.of(2024, Month.MARCH, 6);
      project.setFdpaStartUpDate(fdpaStartUpDate);

      assertThat(projectService.projectToForm(project))
          .extracting(
              ProjectForm::getProjectName,
              ProjectForm::getProjectType,
              ProjectForm::getProjectStatus,
              ProjectForm::getIsPartOfPipeline,
              ProjectForm::getFdpaStartUpDateDay,
              ProjectForm::getFdpaStartUpDateMonth,
              ProjectForm::getFdpaStartUpDateYear
          ).containsExactly(
              project.getProjectName(),
              project.getProjectType(),
              project.getProjectStatus(),
              project.getPartOfPipeline(),
              String.valueOf(fdpaStartUpDate.getDayOfMonth()),
              String.valueOf(fdpaStartUpDate.getMonthValue()),
              String.valueOf(fdpaStartUpDate.getYear())
          );
    }

    @Test
    void whenNoSubmissionDetailSaved_thenNotOnForm() {
      var project = ProjectTestUtils.createPopulatedProject(
          "Project name",
          ProjectType.CESSATION_OF_PRODUCTION,
          ProjectStatus.ON_HOLD,
          true,
          Instant.now()
      );

      project.setSubmissionDetail(null);

      assertThat(projectService.projectToForm(project).getSelectedSubmissionId()).isNull();
    }
  }
}