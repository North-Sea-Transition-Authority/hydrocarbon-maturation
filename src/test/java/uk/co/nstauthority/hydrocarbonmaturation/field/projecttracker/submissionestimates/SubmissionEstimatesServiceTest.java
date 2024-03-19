package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.time.LocalDate;
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

  private Project project;

  @Test
  void saveSubmissionEstimate_verifyCall() {
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