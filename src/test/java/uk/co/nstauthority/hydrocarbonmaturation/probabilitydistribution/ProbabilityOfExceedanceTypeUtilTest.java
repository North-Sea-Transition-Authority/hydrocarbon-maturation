package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ProbabilityOfExceedanceTypeUtilTest {

  @Test
  void getContingentUncertaintyCategoryPromptOverrides() {
    var map = ProbabilityOfExceedanceTypeUtil.getContingentUncertaintyCategoryPromptOverrides();

    assertThat(map.entrySet())
        .extracting(Map.Entry::getKey, Map.Entry::getValue)
        .containsExactly(
            tuple(ProbabilityOfExceedanceType.P90, "P90 or 1C"),
            tuple(ProbabilityOfExceedanceType.P50, "P50 or 2C"),
            tuple(ProbabilityOfExceedanceType.P10, "P10 or 3C")
        );
  }
}