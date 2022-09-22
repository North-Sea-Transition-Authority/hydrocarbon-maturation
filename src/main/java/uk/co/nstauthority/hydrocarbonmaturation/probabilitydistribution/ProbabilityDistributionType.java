package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.Displayable;

public enum ProbabilityDistributionType implements Displayable {
  NORMAL(NormalBehaviour.class, "Normal", 1, "normal"),
  LOGNORMAL(LognormalBehaviour.class, "Lognormal", 2, "lognormal"),
  BETA(BetaBehaviour.class, "Beta", 3, "beta"),
  TRIANGULAR(TriangularBehaviour.class, "Triangular", 4, "triangular"),
  RECTANGULAR(RectangularBehaviour.class, "Rectangular", 5, "rectangular"),
  CONSTANT(ConstantBehaviour.class, "Constant", 6, "constant");

  private Class<? extends ProbabilityDistributionTypeBehaviour> presetBehaviour;
  private final boolean hasPresetBehaviour;
  private String displayName;
  private int displayOrder;
  private String path;

  ProbabilityDistributionType(
      Class<? extends ProbabilityDistributionTypeBehaviour> presetBehaviour,
      String displayName,
      int displayOrder,
      String path) {
    this.presetBehaviour = presetBehaviour;
    this.displayName = displayName;
    this.displayOrder = displayOrder;
    this.path = path;
    this.hasPresetBehaviour = true;
  }

  ProbabilityDistributionType() {
    this.hasPresetBehaviour = false;
  }

  public Class<? extends ProbabilityDistributionTypeBehaviour> getPresetBehaviour() {
    return presetBehaviour;
  }

  public boolean hasPresetBehaviour() {
    return hasPresetBehaviour;
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

  public ProbabilityDistributionTypeBehaviour newInstance() {
    try {
      return ReflectionUtils.accessibleConstructor(this.getPresetBehaviour()).newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new ProbabilityDistributionException(e);
    }
  }
}
