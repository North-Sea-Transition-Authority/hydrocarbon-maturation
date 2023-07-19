package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProbabilityOfExceedanceType implements Displayable {

  MIN("Min", "min"),
  P99("P99", "p99"),
  P90("P90", "p90"),
  ONE_C("1C", "oneC"),
  MODE("Mode", "mode"),
  P50("P50", "p50"),
  TWO_C("2C", "twoC"),
  MEAN("Mean", "mean"),
  P10("P10", "p10"),
  THREE_C("3C", "threeC"),
  P1("P1", "p1"),
  MAX("Max", "max"),
  MOST_LIKELY("Most likely", "mostLikely"),
  CONSTANT("Constant", "constant");

  private final String displayName;
  private final String path;

  ProbabilityOfExceedanceType(String displayName, String path) {
    this.displayName = displayName;
    this.path = path;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }
}
