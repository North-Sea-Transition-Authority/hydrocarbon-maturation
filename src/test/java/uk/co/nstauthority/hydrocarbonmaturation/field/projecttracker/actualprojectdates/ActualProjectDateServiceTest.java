package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@ExtendWith(MockitoExtension.class)
class ActualProjectDateServiceTest {
  @Mock
  private ActualProjectDateRepository actualProjectDateRepository;
  @InjectMocks
  private ActualProjectDateService actualProjectDateService;

  private static final LocalDate FIXED_DATE = LocalDate.of(2023, 11, 3);
  private static final UUID PROJECT_DATE_ID = UUID.randomUUID();

  private Project project;
  private ActualProjectDateType actualProjectDateType;

  @BeforeEach
  void setUp() {
    project = new Project();

    actualProjectDateType = mock(ActualProjectDateType.class);
  }

  @Test
  void saveProjectDate() {
    var form = getPopulatedForm();
    var captor = ArgumentCaptor.forClass(ActualProjectDate.class);
    actualProjectDateService.saveDateFromForm(form, project, actualProjectDateType);

    verify(actualProjectDateRepository).save(captor.capture());
    verifyNoMoreInteractions(actualProjectDateRepository);

    assertThat(captor.getValue())
        .extracting(
            ActualProjectDate::getProjectDateStatus,
            ActualProjectDate::getProject,
            ActualProjectDate::getProjectDateType,
            ActualProjectDate::getDate
        )
        .contains(
            ActualProjectDateStatus.CREATED,
            project,
            actualProjectDateType,
            LocalDate.of(
                Integer.parseInt(form.getDateYear()),
                Integer.parseInt(form.getDateMonth()),
                Integer.parseInt(form.getDateDay())
            )
        );
  }

  @Test
  void getDates() {
    var firstDates = new ActualProjectDate();
    firstDates.setDate(FIXED_DATE);
    firstDates.setProjectDateStatus(ActualProjectDateStatus.CREATED);

    var laterDates = new ActualProjectDate();
    laterDates.setDate(FIXED_DATE.plusDays(1));
    laterDates.setProjectDateStatus(ActualProjectDateStatus.CREATED);

    var removedDates = new ActualProjectDate();
    removedDates.setDate(FIXED_DATE.plusDays(5));
    removedDates.setProjectDateStatus(ActualProjectDateStatus.REMOVED);

    var latestDates = new ActualProjectDate();
    latestDates.setDate(FIXED_DATE.plusDays(10));
    latestDates.setProjectDateStatus(ActualProjectDateStatus.CREATED);

    when(actualProjectDateRepository.getAllByProjectAndActualProjectDateType(project, actualProjectDateType))
        .thenReturn(List.of(latestDates, removedDates, firstDates, laterDates));

    assertThat(actualProjectDateService.getDates(project, actualProjectDateType))
        .isEqualTo(List.of(firstDates, laterDates, latestDates));
  }

  @Test
  void getProjectDate_present() {
    var projectDate = new ActualProjectDate();

    when(actualProjectDateRepository.findById(PROJECT_DATE_ID))
        .thenReturn(Optional.of(projectDate));

    assertThat(actualProjectDateService.getDate(PROJECT_DATE_ID))
        .isEqualTo(Optional.of(projectDate));
  }

  @Test
  void getProjectDate_notPresent() {
    when(actualProjectDateRepository.findById(PROJECT_DATE_ID))
        .thenReturn(Optional.empty());

    assertThat(actualProjectDateService.getDate(PROJECT_DATE_ID))
        .isEmpty();
  }

  private ActualProjectDateForm getPopulatedForm() {
    var form = new ActualProjectDateForm();
    form.setDateDay("3");
    form.setDateMonth("11");
    form.setDateYear("2022");
    return form;
  }
}