package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class SubmissionEstimateViewTest {
  private SubmissionEstimate submissionEstimate;

  @BeforeEach
  void setUp() {
    submissionEstimate = new SubmissionEstimate();
    submissionEstimate.setDateWhenEstimated(LocalDate.of(2024, 3, 20));
    submissionEstimate.setEarliestDate(LocalDate.of(2024, 3, 21));
    submissionEstimate.setMostLikelyDate(LocalDate.of(2024, 4, 20));
    submissionEstimate.setLatestDate(LocalDate.of(2025, 3, 20));
  }

  @Test
  void from_allDatesProvided() {
    assertThat(SubmissionEstimateView.from(submissionEstimate))
        .isEqualTo(
          new SubmissionEstimateView(
            "20 March 2024",
              "21 March 2024",
              "20 April 2024",
              "20 March 2025"
          )
        );
  }

  @Test
  void from_mandatoryOnlyDatesProvided() {
    submissionEstimate.setEarliestDate(null);
    submissionEstimate.setLatestDate(null);

    assertThat(SubmissionEstimateView.from(submissionEstimate))
        .isEqualTo(
            new SubmissionEstimateView(
                "20 March 2024",
                "",
                "20 April 2024",
                ""
            )
        );
  }
}