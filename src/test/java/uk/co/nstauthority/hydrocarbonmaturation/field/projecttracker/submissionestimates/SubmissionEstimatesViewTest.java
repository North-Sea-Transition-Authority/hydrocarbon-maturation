package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class SubmissionEstimatesViewTest {
  private SubmissionEstimates submissionEstimates;

  @BeforeEach
  void setUp() {
    submissionEstimates = new SubmissionEstimates();
    submissionEstimates.setDateWhenEstimated(LocalDate.of(2024, 3, 20));
    submissionEstimates.setEarliestDate(LocalDate.of(2024, 3, 21));
    submissionEstimates.setMostLikelyDate(LocalDate.of(2024, 4, 20));
    submissionEstimates.setLatestDate(LocalDate.of(2025, 3, 20));
  }

  @Test
  void from_allDatesProvided() {
    assertThat(SubmissionEstimatesView.from(submissionEstimates))
        .isEqualTo(
          new SubmissionEstimatesView(
            "20 March 2024",
              "21 March 2024",
              "20 April 2024",
              "20 March 2025"
          )
        );
  }

  @Test
  void from_mandatoryOnlyDatesProvided() {
    submissionEstimates.setEarliestDate(null);
    submissionEstimates.setLatestDate(null);

    assertThat(SubmissionEstimatesView.from(submissionEstimates))
        .isEqualTo(
            new SubmissionEstimatesView(
                "20 March 2024",
                "",
                "20 April 2024",
                ""
            )
        );
  }
}