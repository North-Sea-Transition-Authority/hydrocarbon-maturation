package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

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
class SubmissionEstimateServiceTest {
  @Mock
  private SubmissionEstimateRepository submissionEstimateRepository;
  @InjectMocks
  private SubmissionEstimateService submissionEstimateService;

  private final LocalDate FIXED_DATE = LocalDate.of(2023, 11, 3);
  private final UUID SUBMISSION_ESTIMATE_ID = UUID.randomUUID();

  private Project project;
  private SubmissionEstimate submissionEstimate;
  private SubmissionEstimateType submissionEstimateType;

  @BeforeEach
  void setUp() {
    submissionEstimateType = mock(SubmissionEstimateType.class);
  }

  @Test
  void saveSubmissionEstimate_allDatesProvided_verifyCall() {
    var form = getPopulatedForm();
    var submissionEstimateType = mock(SubmissionEstimateType.class);

    var captor = ArgumentCaptor.forClass(SubmissionEstimate.class);
    submissionEstimateService.saveSubmissionEstimateFromForm(form, project, submissionEstimateType);

    verify(submissionEstimateRepository).save(captor.capture());
    verifyNoMoreInteractions(submissionEstimateRepository);

    assertThat(captor.getValue())
        .extracting(
            SubmissionEstimate::getSubmissionEstimateStatus,
            SubmissionEstimate::getProject,
            SubmissionEstimate::getSubmissionEstimateType,

            SubmissionEstimate::getEarliestDate,
            SubmissionEstimate::getMostLikelyDate,
            SubmissionEstimate::getLatestDate,
            SubmissionEstimate::getDateWhenEstimated
        )
        .contains(
            SubmissionEstimateStatus.CREATED,
            project,
            submissionEstimateType,

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

    var submissionEstimateType = mock(SubmissionEstimateType.class);

    var captor = ArgumentCaptor.forClass(SubmissionEstimate.class);
    submissionEstimateService.saveSubmissionEstimateFromForm(form, project, submissionEstimateType);

    verify(submissionEstimateRepository).save(captor.capture());
    verifyNoMoreInteractions(submissionEstimateRepository);

    assertThat(captor.getValue())
        .extracting(
            SubmissionEstimate::getSubmissionEstimateStatus,
            SubmissionEstimate::getProject,
            SubmissionEstimate::getSubmissionEstimateType,

            SubmissionEstimate::getMostLikelyDate,
            SubmissionEstimate::getLatestDate,
            SubmissionEstimate::getDateWhenEstimated
        )
        .contains(
            SubmissionEstimateStatus.CREATED,
            project,
            submissionEstimateType,

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

    var submissionEstimateType = mock(SubmissionEstimateType.class);

    var captor = ArgumentCaptor.forClass(SubmissionEstimate.class);
    submissionEstimateService.saveSubmissionEstimateFromForm(form, project, submissionEstimateType);

    verify(submissionEstimateRepository).save(captor.capture());
    verifyNoMoreInteractions(submissionEstimateRepository);

    assertThat(captor.getValue())
        .extracting(
            SubmissionEstimate::getSubmissionEstimateStatus,
            SubmissionEstimate::getProject,
            SubmissionEstimate::getSubmissionEstimateType,

            SubmissionEstimate::getEarliestDate,
            SubmissionEstimate::getMostLikelyDate,
            SubmissionEstimate::getDateWhenEstimated
        )
        .contains(
            SubmissionEstimateStatus.CREATED,
            project,
            submissionEstimateType,
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
    var firstEstimates = new SubmissionEstimate();
    firstEstimates.setDateWhenEstimated(FIXED_DATE);
    firstEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.CREATED);

    var laterEstimates = new SubmissionEstimate();
    laterEstimates.setDateWhenEstimated(FIXED_DATE.plusDays(1));
    laterEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.CREATED);

    var removedEstimates = new SubmissionEstimate();
    removedEstimates.setDateWhenEstimated(FIXED_DATE.plusDays(5));
    removedEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.REMOVED);

    var latestEstimates = new SubmissionEstimate();
    latestEstimates.setDateWhenEstimated(FIXED_DATE.plusDays(10));
    latestEstimates.setSubmissionEstimateStatus(SubmissionEstimateStatus.CREATED);

    when(submissionEstimateRepository.getAllByProjectAndSubmissionEstimateType(project, submissionEstimateType))
        .thenReturn(List.of(latestEstimates, removedEstimates, firstEstimates, laterEstimates));

    assertThat(submissionEstimateService.getCreatedEstimates(project, submissionEstimateType))
        .isEqualTo(List.of(firstEstimates, laterEstimates, latestEstimates));
  }

  @Test
  void getSubmissionEstimate_present() {
    submissionEstimate = new SubmissionEstimate();
    when(submissionEstimateRepository.findById(SUBMISSION_ESTIMATE_ID))
        .thenReturn(Optional.of(submissionEstimate));

    assertThat(submissionEstimateService.getSubmissionEstimate(SUBMISSION_ESTIMATE_ID))
        .get()
        .isEqualTo(submissionEstimate);
  }

  @Test
  void getSubmissionEstimate_notPresent() {
    when(submissionEstimateRepository.findById(SUBMISSION_ESTIMATE_ID))
        .thenReturn(Optional.empty());

    assertThat(submissionEstimateService.getSubmissionEstimate(SUBMISSION_ESTIMATE_ID))
        .isEmpty();
  }

  private SubmissionEstimateForm getPopulatedForm() {
    var form = new SubmissionEstimateForm();
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