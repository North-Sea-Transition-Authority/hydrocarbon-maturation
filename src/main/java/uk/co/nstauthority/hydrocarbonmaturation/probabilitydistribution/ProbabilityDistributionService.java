package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProbabilityDistributionService {

  public ProbabilityDistributionTemplate getProbabilityDistributionTemplateForInstance(
      ProbabilityDistributionFieldInstance instance
  ) {
    var presetTypes = instance.getPresetTypes();

    List<ProbabilityDistributionTypeWithInputs> probabilityDistributionTypeWithInputsList = new ArrayList<>();

    presetTypes.forEach(type -> {

      var probabilityDistributionTypeWithInputs = new ProbabilityDistributionTypeWithInputs();

      probabilityDistributionTypeWithInputs.setProbabilityDistributionType(type);

      List<ProbabilityDistributionTypeInput> probabilityDistributionTypeInputs = new ArrayList<>();

      var behaviour = type.newInstance();

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isMinAvailable(),
          behaviour.isMinRequired(),
          ProbabilityOfExceedanceType.MIN,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isP99Available(),
          behaviour.isP99Required(),
          ProbabilityOfExceedanceType.P99,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isP90Available(),
          behaviour.isP90Required(),
          ProbabilityOfExceedanceType.P90,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isModeAvailable(),
          behaviour.isModeRequired(),
          ProbabilityOfExceedanceType.MODE,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isP50Available(),
          behaviour.isP50Required(),
          ProbabilityOfExceedanceType.P50,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isMeanAvailable(),
          behaviour.isMeanRequired(),
          ProbabilityOfExceedanceType.MEAN,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isP10Available(),
          behaviour.isP10Required(),
          ProbabilityOfExceedanceType.P10,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isP1Available(),
          behaviour.isP1Required(),
          ProbabilityOfExceedanceType.P1,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isMaxAvailable(),
          behaviour.isMaxRequired(),
          ProbabilityOfExceedanceType.MAX,
          probabilityDistributionTypeInputs
      );

      checkAvailabilityAndAddProbabilityInputs(
          behaviour.isConstantAvailable(),
          behaviour.isConstantRequired(),
          ProbabilityOfExceedanceType.CONSTANT,
          probabilityDistributionTypeInputs
      );

      probabilityDistributionTypeWithInputs.setProbabilityDistributionTypeInputs(probabilityDistributionTypeInputs);

      probabilityDistributionTypeWithInputsList.add(probabilityDistributionTypeWithInputs);
    });

    var probabilityDistributionTemplate = new ProbabilityDistributionTemplate();
    probabilityDistributionTemplate.setUnits(instance.getUnits());
    probabilityDistributionTemplate.setProbabilityDistributionTypeWithInputsList(probabilityDistributionTypeWithInputsList);
    return probabilityDistributionTemplate;

  }

  private void checkAvailabilityAndAddProbabilityInputs(
      boolean isAvailable,
      boolean isRequired,
      ProbabilityOfExceedanceType probabilityOfExceedanceType,
      List<ProbabilityDistributionTypeInput> probabilityDistributionTypeInputs
  ) {
    if (isAvailable) {
      var input = createProbabilityDistributionTypeInput(
          probabilityOfExceedanceType, isRequired
      );
      probabilityDistributionTypeInputs.add(input);
    }
  }

  private ProbabilityDistributionTypeInput createProbabilityDistributionTypeInput(
      ProbabilityOfExceedanceType probabilityOfExceedanceType,
      boolean isRequired
  ) {
    ProbabilityDistributionTypeInput input = new ProbabilityDistributionTypeInput();
    input.setProbabilityOfExceedanceType(probabilityOfExceedanceType);
    input.setRequired(isRequired);
    return input;
  }

}
