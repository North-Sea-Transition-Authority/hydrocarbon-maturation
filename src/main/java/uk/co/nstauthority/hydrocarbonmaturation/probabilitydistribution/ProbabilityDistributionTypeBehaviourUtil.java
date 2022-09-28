package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;

public class ProbabilityDistributionTypeBehaviourInstanceUtil {

  private ProbabilityDistributionTypeBehaviourInstanceUtil() {
  }


  public static ProbabilityDistributionTypeBehaviour newInstance(
      Class<? extends ProbabilityDistributionTypeBehaviour> presetBehaviour) {
    try {
      return ReflectionUtils.accessibleConstructor(presetBehaviour).newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new ProbabilityDistributionException(e);
    }
  }

  public static ProbabilityDistributionTypeBehaviour newInstance(ProbabilityDistributionFieldInstance instance) {
    if (!instance.isCustomType()) {
      throw new IllegalStateException(
          "Cannot create instance of a ProbabilityDistributionFieldInstance that does not have custom behaviour");
    }

    return newInstance(instance.getCustomBehaviour());
  }
}
