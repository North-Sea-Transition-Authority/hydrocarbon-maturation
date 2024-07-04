package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import jakarta.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
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

  public static ProbabilityDistributionTypeBehaviour newCustomBehaviourInstance(
      ProbabilityDistributionFieldInstance instance) {
    if (!instance.isCustomType()) {
      throw new IllegalStateException(
          "Cannot create instance of a ProbabilityDistributionFieldInstance that does not have custom behaviour");
    }

    return newBehaviourInstance(instance.getCustomBehaviour());
  }

  public static ProbabilityDistributionTypeBehaviour getBehaviour(
      @Nullable ProbabilityDistributionFieldInstance instance,
      ProbabilityDistributionType probabilityDistributionType) {
    if (instance != null && instance.isCustomType()) {
      return newCustomBehaviourInstance(instance);
    }

    return probabilityDistributionType.newPresetInstance();
  }

  public static List<ProbabilityDistributionTypeBehaviour> getBehaviours(
      @Nullable ProbabilityDistributionFieldInstance instance) {
    if (instance == null) {
      return List.of();
    }

    if (instance.isCustomType()) {
      return List.of(newCustomBehaviourInstance(instance));
    }

    return instance.getPresetTypes()
        .stream()
        .map(probabilityDistributionType -> getBehaviour(instance, probabilityDistributionType))
        .toList();
  }
}
