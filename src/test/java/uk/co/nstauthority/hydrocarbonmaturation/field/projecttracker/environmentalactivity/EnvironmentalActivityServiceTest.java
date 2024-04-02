package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;

@ExtendWith(MockitoExtension.class)
class EnvironmentalActivityServiceTest {
  @Mock
  private EnvironmentalActivityRepository environmentalActivityRepository;
  @InjectMocks
  private EnvironmentalActivityService environmentalActivityService;

  private Project project;

  @Test
  void getEnvironmentalActivityForm_noActivityAlreadyExists() {
    when(environmentalActivityRepository.findByProject(project))
        .thenReturn(Optional.empty());

    assertThat(environmentalActivityService.getEnvironmentalActivityForm(project))
        .usingRecursiveComparison()
        .isEqualTo(new EnvironmentalActivityForm());
  }

  @Test
  void getEnvironmentalActivityForm_activityAlreadyExistsAndAllPopulated() {
    var activity = new EnvironmentalActivity();
    activity.setProject(project);

    final var FIXED_DATE = LocalDate.of(2020, 3, 27);
    activity.setSubmissionDate(FIXED_DATE);
    activity.setConsultationStartDate(FIXED_DATE.plusDays(1));
    activity.setConsultationClosingDate(FIXED_DATE.plusDays(2));

    when(environmentalActivityRepository.findByProject(project))
        .thenReturn(Optional.of(activity));

    assertThat(environmentalActivityService.getEnvironmentalActivityForm(project))
        .extracting(
            EnvironmentalActivityForm::getSubmissionDateYear,
            EnvironmentalActivityForm::getSubmissionDateMonth,
            EnvironmentalActivityForm::getSubmissionDateDay,

            EnvironmentalActivityForm::getConsultationStartDateYear,
            EnvironmentalActivityForm::getConsultationStartDateMonth,
            EnvironmentalActivityForm::getConsultationStartDateDay,

            EnvironmentalActivityForm::getConsultationClosingDateYear,
            EnvironmentalActivityForm::getConsultationClosingDateMonth,
            EnvironmentalActivityForm::getConsultationClosingDateDay
        )
        .contains(
                "2020",
                "3",
                "27",

                "2020",
                "3",
                "28",

                "2020",
                "3",
                "29"
        );
  }

  @Test
  void getEnvironmentalActivityForm_activityAlreadyExistsAndNonePopulated() {
    var activity = new EnvironmentalActivity();
    activity.setProject(project);

    when(environmentalActivityRepository.findByProject(project))
        .thenReturn(Optional.of(activity));

    assertThat(environmentalActivityService.getEnvironmentalActivityForm(project))
        .extracting(
            EnvironmentalActivityForm::getSubmissionDateYear,
            EnvironmentalActivityForm::getSubmissionDateMonth,
            EnvironmentalActivityForm::getSubmissionDateDay,

            EnvironmentalActivityForm::getConsultationStartDateYear,
            EnvironmentalActivityForm::getConsultationStartDateMonth,
            EnvironmentalActivityForm::getConsultationStartDateDay,

            EnvironmentalActivityForm::getConsultationClosingDateYear,
            EnvironmentalActivityForm::getConsultationClosingDateMonth,
            EnvironmentalActivityForm::getConsultationClosingDateDay
        )
        .containsOnlyNulls();
  }

  @Test
  void saveEnvironmentalActivity_existingProject_thenOverwrites() {
    var environmentalActivity = new EnvironmentalActivity(UUID.randomUUID());
    when(environmentalActivityRepository.findByProject(project))
        .thenReturn(Optional.of(environmentalActivity));

    environmentalActivityService.saveEnvironmentalActivity(new EnvironmentalActivityForm(), project);

    verify(environmentalActivityRepository).save(environmentalActivity);
  }

  @Test
  void saveEnvironmentalActivity_noExistingProject_thenSavesNew() {
    when(environmentalActivityRepository.findByProject(project))
        .thenReturn(Optional.empty());

    environmentalActivityService.saveEnvironmentalActivity(new EnvironmentalActivityForm(), project);

    var captor = ArgumentCaptor.forClass(EnvironmentalActivity.class);
    verify(environmentalActivityRepository).save(any(EnvironmentalActivity.class));
  }

  @Test
  void saveEnvironmentalActivity_allDatesProvided() {
    when(environmentalActivityRepository.findByProject(project))
        .thenReturn(Optional.empty());

    var form = new EnvironmentalActivityForm();
    form.setSubmissionDateYear("2022");
    form.setSubmissionDateMonth("1");
    form.setSubmissionDateDay("25");

    form.setConsultationStartDateYear("2023");
    form.setConsultationStartDateMonth("2");
    form.setConsultationStartDateDay("26");

    form.setConsultationClosingDateYear("2024");
    form.setConsultationClosingDateMonth("3");
    form.setConsultationClosingDateDay("27");

    environmentalActivityService.saveEnvironmentalActivity(form, project);

    var captor = ArgumentCaptor.forClass(EnvironmentalActivity.class);
    verify(environmentalActivityRepository).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            EnvironmentalActivity::getSubmissionDate,
            EnvironmentalActivity::getConsultationStartDate,
            EnvironmentalActivity::getConsultationClosingDate
        )
        .containsExactly(
            LocalDate.of(2022, 1, 25),
            LocalDate.of(2023, 2, 26),
            LocalDate.of(2024, 3, 27)
        );
  }
}