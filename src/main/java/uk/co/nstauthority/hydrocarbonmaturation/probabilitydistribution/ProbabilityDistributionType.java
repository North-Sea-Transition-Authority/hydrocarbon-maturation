package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProbabilityDistributionType implements Displayable {
  NORMAL(NormalBehaviour.class, "Normal", 1, "normal"),
  LOGNORMAL(LognormalBehaviour.class, "Lognormal", 2, "lognormal"),
  BETA(BetaBehaviour.class, "Beta", 3, "beta"),
  TRIANGULAR(TriangularBehaviour.class, "Triangular", 4, "triangular"),
  RECTANGULAR(RectangularBehaviour.class, "Rectangular", 5, "rectangular"),
  CONSTANT(ConstantBehaviour.class, "Constant", 6, "constant"),
  NOT_CALCULATED(NotCalculatedBehaviour.class, "Not calculated", 7, "notCalculated"),
  CUSTOM("custom");

  private Class<? extends ProbabilityDistributionTypeBehaviour> presetBehaviour;
  private final boolean hasCustomBehaviour;
  private String displayName;
  private int displayOrder;
  private final String path;

  ProbabilityDistributionType(
      Class<? extends ProbabilityDistributionTypeBehaviour> presetBehaviour,
      String displayName,
      int displayOrder,
      String path) {
    this.presetBehaviour = presetBehaviour;
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.path = path;
    this.hasCustomBehaviour = false;
  }

  ProbabilityDistributionType(String path) {
    this.path = path;
    this.hasCustomBehaviour = true;
  }

  public Class<? extends ProbabilityDistributionTypeBehaviour> getPresetBehaviour() {
    return presetBehaviour;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }

  @Override
  public int getDisplayOrder() {
    return this.displayOrder;
  }

  public String getPath() {
    return path;
  }

  @Override
  public String getEnumName() {
    return this.name();
  }

  public ProbabilityDistributionTypeBehaviour newPresetInstance() {
    if (hasCustomBehaviour) {
      throw new IllegalStateException(
          "Cannot create instance of a ProbabilityDistributionTypeBehaviour that has custom behaviour");
    }

    return ProbabilityDistributionTypeBehaviourUtil.newBehaviourInstance(this.getPresetBehaviour());
  }
}
