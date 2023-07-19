package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceType.ONE_C;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceType.P10;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceType.P50;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceType.P90;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceType.THREE_C;
import static uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceType.TWO_C;

import java.util.EnumMap;
import java.util.Map;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

public class ProbabilityOfExceedanceTypeUtil {

  private ProbabilityOfExceedanceTypeUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  public static Map<ProbabilityOfExceedanceType, String> getContingentUncertaintyCategoryPromptOverrides() {
    var map = new EnumMap<ProbabilityOfExceedanceType, String>(ProbabilityOfExceedanceType.class);

    putTypeAndPromptInMap(map, P90, ONE_C);
    putTypeAndPromptInMap(map, P50, TWO_C);
    putTypeAndPromptInMap(map, P10, THREE_C);

    return map;
  }

  private static void putTypeAndPromptInMap(EnumMap<ProbabilityOfExceedanceType, String> map,
                                            ProbabilityOfExceedanceType thisType,
                                            ProbabilityOfExceedanceType thatType) {
    map.put(thisType, getThisOrThatPrompt(thisType, thatType));
  }

  private static String getThisOrThatPrompt(ProbabilityOfExceedanceType thisType,
                                            ProbabilityOfExceedanceType thatType) {
    return "%s or %s".formatted(thisType.getDisplayName(), thatType.getDisplayName());
  }
}
