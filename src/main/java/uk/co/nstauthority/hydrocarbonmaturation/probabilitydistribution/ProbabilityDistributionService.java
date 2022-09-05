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

      if (behaviour.isMinAvailable()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.MIN, behaviour.isMinRequired()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isP99Available()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.P99, behaviour.isP99Available()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isP90Available()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.P90, behaviour.isP90Required()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isModeAvailable()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.MODE, behaviour.isModeRequired()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isP50Available()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.P50, behaviour.isP50Required()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isMeanAvailable()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.MEAN, behaviour.isMeanRequired()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isP10Available()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.P10, behaviour.isP10Required()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isP1Available()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.P1, behaviour.isP1Required()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isMaxAvailable()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.MAX, behaviour.isMaxRequired()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      if (behaviour.isConstantAvailable()) {
        var input = createProbabilityDistributionTypeInput(
            ProbabilityOfExceedanceType.CONSTANT, behaviour.isConstantRequired()
        );
        probabilityDistributionTypeInputs.add(input);
      }

      probabilityDistributionTypeWithInputs.setProbabilityDistributionTypeInputs(probabilityDistributionTypeInputs);

      probabilityDistributionTypeWithInputsList.add(probabilityDistributionTypeWithInputs);
    });

    var probabilityDistributionTemplate = new ProbabilityDistributionTemplate();
    probabilityDistributionTemplate.setUnits(instance.getUnits());
    probabilityDistributionTemplate.setProbabilityDistributionTypeWithInputsList(probabilityDistributionTypeWithInputsList);
    return probabilityDistributionTemplate;

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
