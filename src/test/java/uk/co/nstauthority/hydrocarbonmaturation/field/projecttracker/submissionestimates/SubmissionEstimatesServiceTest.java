package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

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
class SubmissionEstimatesServiceTest {
  @Mock
  private SubmissionEstimatesRepository submissionEstimatesRepository;
  @InjectMocks
  private SubmissionEstimatesService submissionEstimatesService;

  private final LocalDate FIXED_DATE = LocalDate.of(2023, 11, 3);
  private final UUID SUBMISSION_ESTIMATES_ID = UUID.randomUUID();

  private Project project;
  private SubmissionEstimates submissionEstimates;
  private SubmissionEstimatesType submissionEstimatesType;

  @BeforeEach
  void setUp() {
    submissionEstimatesType = mock(SubmissionEstimatesType.class);
  }

  @Test
  void saveSubmissionEstimate_allDatesProvided_verifyCall() {
    var form = getPopulatedForm();
    var submissionEstimatesType = mock(SubmissionEstimatesType.class);

    var captor = ArgumentCaptor.forClass(SubmissionEstimates.class);
    submissionEstimatesService.saveSubmissionEstimateFromForm(form, project, submissionEstimatesType);

    verify(submissionEstimatesRepository).save(captor.capture());
    verifyNoMoreInteractions(submissionEstimatesRepository);

    assertThat(captor.getValue())
        .extracting(
            SubmissionEstimates::getSubmissionEstimateStatus,
            SubmissionEstimates::getProject,
            SubmissionEstimates::getSubmissionEstimateType,

            SubmissionEstimates::getEarliestDate,
            SubmissionEstimates::getMostLikelyDate,
            SubmissionEstimates::getLatestDate,
            SubmissionEstimates::getDateWhenEstimated
        )
        .contains(
            SubmissionEstimateStatus.CREATED,
            project,
            submissionEstimatesType,

            LocalDate.of(
                Integer.parseInt(form.getEarliestDateYear()),
                Integer.parseInt(form.getEarliestDateMonth()),
                Integer.parseInt(form.getEarliestDateDay())
            ),
            LocalDate.of(
                Integer.parseInt(form.getMostLikelyDateYear()),
                Integer.parseInt(form.getMostLikelyDateMonth()),
                Integer.parseInt(form.getMostLikelyDateDay())
            ),
            LocalDate.of(
                Integer.parseInt(form.getLatestDateYear()),
                Integer.parseInt(form.getLatestDateMonth()),
                Integer.parseInt(form.getLatestDateDay())
            ),
            LocalDate.of(
                Integer.parseInt(form.getEstimatedDateYear()),
                Integer.parseInt(form.getEstimatedDateMonth()),
                Integer.parseInt(form.getEstimatedDateDay())
            )
        );
  }

  @Test
  void saveSubmissionEstimate_emptyEarliestProvided_verifyCall() {
    var form = getPopulatedForm();

    form.setEarliestDateDay(null);
    form.setEarliestDateMonth(null);
    form.setEarliestDateYear(null);

    var submissionEstimatesType = mock(SubmissionEstimatesType.class);

    var captor = ArgumentCaptor.forClass(SubmissionEstimates.class);
    submissionEstimatesService.saveSubmissionEstimateFromForm(form, project, submissionEstimatesType);

    verify(submissionEstimatesRepository).save(captor.capture());
    verifyNoMoreInteractions(submissionEstimatesRepository);

    assertThat(captor.getValue())
        .extracting(
            SubmissionEstimates::getSubmissionEstimateStatus,
            SubmissionEstimates::getProject,
            SubmissionEstimates::getSubmissionEstimateType,

            SubmissionEstimates::getMostLikelyDate,
            SubmissionEstimates::getLatestDate,
            SubmissionEstimates::getDateWhenEstimated
        )
        .contains(
            SubmissionEstimateStatus.CREATED,
            project,
            submissionEstimatesType,

            LocalDate.of(
                Integer.parseInt(form.getMostLikelyDateYear()),
                Integer.parseInt(form.getMostLikelyDateMonth()),
                Integer.parseInt(form.getMostLikelyDateDay())
            ),
            LocalDate.of(
                Integer.parseInt(form.getLatestDateYear()),
                Integer.parseInt(form.getLatestDateMonth()),
                Integer.parseInt(form.getLatestDateDay())
            ),
            LocalDate.of(
                Integer.parseInt(form.getEstimatedDateYear()),
                Integer.parseInt(form.getEstimatedDateMonth()),
                Integer.parseInt(form.getEstimatedDateDay())
            )
        );

    assertThat(captor.getValue().getEarliestDate()).isNull();
  }

  @Test
  void saveSubmissionEstimate_emptyLatestProvided_verifyCall() {
    var form = getPopulatedForm();

    form.setLatestDateDay(null);
    form.setLatestDateMonth(null);
    form.setLatestDateYear(null);

    var submissionEstimatesType = mock(SubmissionEstimatesType.class);

    var captor = ArgumentCaptor.forClass(SubmissionEstimates.class);
    submissionEstimatesService.saveSubmissionEstimateFromForm(form, project, submissionEstimatesType);

    verify(submissionEstimatesRepository).save(captor.capture());
    verifyNoMoreInteractions(submissionEstimatesRepository);

    assertThat(captor.getValue())
        .extracting(
            SubmissionEstimates::getSubmissionEstimateStatus,
            SubmissionEstimates::getProject,
            SubmissionEstimates::getSubmissionEstimateType,

            SubmissionEstimates::getEarliestDate,
            SubmissionEstimates::getMostLikelyDate,
            SubmissionEstimates::getDateWhenEstimated
        )
        .contains(
            SubmissionEstimateStatus.CREATED,
            project,
            submissionEstimatesType,
            LocalDate.of(
                Integer.parseInt(form.getEarliestDateYear()),
                Integer.parseInt(form.getEarliestDateMonth()),
                Integer.parseInt(form.getEarliestDateDay()))
            ,
            LocalDate.of(
                Integer.parseInt(form.getMostLikelyDateYear()),
                Integer.parseInt(form.getMostLikelyDateMonth()),
                Integer.parseInt(form.getMostLikelyDateDay())
            ),
            LocalDate.of(
                Integer.parseInt(form.getEstimatedDateYear()),
                Integer.parseInt(form.getEstimatedDateMonth()),
                Integer.parseInt(form.getEstimatedDateDay())
            )
        );

    assertThat(captor.getValue().getLatestDate()).isNull();
  }

  @Test
  void getCreatedEstimates_assertOrderedOnlyCreatedEstimatesReturned() {
    var firstEstimates = new SubmissionEstimates();
    firstEstimates.setDateWhenEstimated(FIXED_DATE);
    firstEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.CREATED);

    var laterEstimates = new SubmissionEstimates();
    laterEstimates.setDateWhenEstimated(FIXED_DATE.plusDays(1));
    laterEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.CREATED);

    var removedEstimates = new SubmissionEstimates();
    removedEstimates.setDateWhenEstimated(FIXED_DATE.plusDays(5));
    removedEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.REMOVED);

    var latestEstimates = new SubmissionEstimates();
    latestEstimates.setDateWhenEstimated(FIXED_DATE.plusDays(10));
    latestEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.CREATED);

    when(submissionEstimatesRepository.getAllByProjectAndSubmissionEstimateType(project, submissionEstimatesType))
        .thenReturn(List.of(latestEstimates, removedEstimates, firstEstimates, laterEstimates));

    assertThat(submissionEstimatesService.getCreatedEstimates(project, submissionEstimatesType))
        .isEqualTo(List.of(firstEstimates, laterEstimates, latestEstimates));
  }

  @Test
  void getSubmissionEstimate_present() {
    submissionEstimates = new SubmissionEstimates();
    when(submissionEstimatesRepository.findById(SUBMISSION_ESTIMATES_ID))
        .thenReturn(Optional.of(submissionEstimates));

    assertThat(submissionEstimatesService.getSubmissionEstimates(SUBMISSION_ESTIMATES_ID))
        .get()
        .isEqualTo(submissionEstimates);
  }

  @Test
  void getSubmissionEstimate_notPresent() {
    when(submissionEstimatesRepository.findById(SUBMISSION_ESTIMATES_ID))
        .thenReturn(Optional.empty());

    assertThat(submissionEstimatesService.getSubmissionEstimates(SUBMISSION_ESTIMATES_ID))
        .isEmpty();
  }

  private SubmissionEstimatesForm getPopulatedForm() {
    var form = new SubmissionEstimatesForm();
    form.setEarliestDateDay("3");
    form.setEarliestDateMonth("11");
    form.setEarliestDateYear("2023");

    form.setMostLikelyDateDay("10");
    form.setMostLikelyDateMonth("11");
    form.setMostLikelyDateYear("2023");

    form.setLatestDateDay("17");
    form.setLatestDateMonth("11");
    form.setLatestDateYear("2023");

    form.setEstimatedDateDay("3");
    form.setEstimatedDateMonth("11");
    form.setEstimatedDateYear("2022");
    return form;
  }
}