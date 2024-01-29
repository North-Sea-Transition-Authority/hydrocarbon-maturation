package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import jakarta.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import org.springframework.util.ReflectionUtils;

public class ProbabilityDistributionTypeBehaviourUtil {

  private ProbabilityDistributionTypeBehaviourUtil() {
  }


  public static ProbabilityDistributionTypeBehaviour newBehaviourInstance(
      Class<? extends ProbabilityDistributionTypeBehaviour> behaviour) {
    try {
      return ReflectionUtils.accessibleConstructor(behaviour).newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      throw new ProbabilityDistributionException(e);
    }
  }

  public static ProbabilityDistributionTypeBehaviour newCustomBehaviourInstance(ProbabilityDistributionFieldInstance instance) {
    if (!instance.isCustomType()) {
      throw new IllegalStateException(
          "Cannot create instance of a ProbabilityDistributionFieldInstance that does not have custom behaviour");
    }

    return newBehaviourInstance(instance.getCustomBehaviour());
  }

  public static ProbabilityDistributionTypeBehaviour getBehaviour(
      @Nullable ProbabilityDistributionFieldInstance instance, ProbabilityDistributionType probabilityDistributionType) {
    if (instance != null && instance.isCustomType()) {
      return newCustomBehaviourInstance(instance);
    }

    return probabilityDistributionType.newPresetInstance();
  }
}
