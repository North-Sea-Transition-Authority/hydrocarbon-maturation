package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class ProbabilityDistributionTemplateService {

  public ProbabilityDistributionTemplate getProbabilityDistributionTemplateForInstance(
      ProbabilityDistributionFieldInstance instance
  ) {
    return getProbabilityDistributionTemplateForInstanceAndPromptOverrides(instance, Collections.emptyMap());
  }

  public ProbabilityDistributionTemplate getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
      ProbabilityDistributionFieldInstance instance,
      Map<ProbabilityOfExceedanceType, String> promptOverrides
  ) {
    var presetTypes = instance.getPresetTypes();

    List<ProbabilityDistributionTypeWithInputs> probabilityDistributionTypeWithInputsList = new ArrayList<>();

    presetTypes.forEach(type -> {

      var probabilityDistributionTypeWithInputs = new ProbabilityDistributionTypeWithInputs();
      probabilityDistributionTypeWithInputs.setProbabilityDistributionType(type);

      var behaviour = determineBehaviour(instance, type);

      addAddProbabilityInputsToList(
          probabilityDistributionTypeWithInputsList,
          probabilityDistributionTypeWithInputs,
          behaviour,
          promptOverrides
      );
    });

    var probabilityDistributionTemplate = new ProbabilityDistributionTemplate();
    probabilityDistributionTemplate.setUnits(instance.getUnits());
    probabilityDistributionTemplate.setProbabilityDistributionTypeWithInputsList(probabilityDistributionTypeWithInputsList);

    return probabilityDistributionTemplate;
  }

  private ProbabilityDistributionTypeBehaviour determineBehaviour(ProbabilityDistributionFieldInstance instance,
                                                                  ProbabilityDistributionType type) {

    return instance.isCustomType()
        ? ProbabilityDistributionTypeBehaviourUtil.newCustomBehaviourInstance(instance)
        : type.newPresetInstance();
  }

  private void addAddProbabilityInputsToList(
      List<ProbabilityDistributionTypeWithInputs> probabilityDistributionTypeWithInputsList,
      ProbabilityDistributionTypeWithInputs probabilityDistributionTypeWithInputs,
      ProbabilityDistributionTypeBehaviour behaviour,
      Map<ProbabilityOfExceedanceType, String> promptOverrides) {
    List<ProbabilityDistributionTypeInput> probabilityDistributionTypeInputs = new ArrayList<>();

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isMinAvailable(),
        behaviour.isMinRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.MIN,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isP99Available(),
        behaviour.isP99Required(),
        promptOverrides,
        ProbabilityOfExceedanceType.P99,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isP90Available(),
        behaviour.isP90Required(),
        promptOverrides,
        ProbabilityOfExceedanceType.P90,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.is1CAvailable(),
        behaviour.is1CRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.ONE_C,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isModeAvailable(),
        behaviour.isModeRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.MODE,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isP50Available(),
        behaviour.isP50Required(),
        promptOverrides,
        ProbabilityOfExceedanceType.P50,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.is2CAvailable(),
        behaviour.is2CRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.TWO_C,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isMeanAvailable(),
        behaviour.isMeanRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.MEAN,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isP10Available(),
        behaviour.isP10Required(),
        promptOverrides,
        ProbabilityOfExceedanceType.P10,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.is3CAvailable(),
        behaviour.is3CRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.THREE_C,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isP1Available(),
        behaviour.isP1Required(),
        promptOverrides,
        ProbabilityOfExceedanceType.P1,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isMostLikelyAvailable(),
        behaviour.isMostLikelyRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.MOST_LIKELY,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isMaxAvailable(),
        behaviour.isMaxRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.MAX,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.isConstantAvailable(),
        behaviour.isConstantRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.CONSTANT,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.is1PAvailable(),
        behaviour.is1PRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.ONE_P,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.is2PAvailable(),
        behaviour.is2PRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.TWO_P,
        probabilityDistributionTypeInputs
    );

    checkAvailabilityAndAddProbabilityInputs(
        behaviour.is3PAvailable(),
        behaviour.is3PRequired(),
        promptOverrides,
        ProbabilityOfExceedanceType.THREE_P,
        probabilityDistributionTypeInputs
    );

    probabilityDistributionTypeWithInputs.setProbabilityDistributionTypeInputs(probabilityDistributionTypeInputs);

    probabilityDistributionTypeWithInputsList.add(probabilityDistributionTypeWithInputs);
  }

  public ProbabilityDistributionTypeError getProbabilityDistributionTypeError(
      BindingResult bindingResult,
      String formName,
      ProbabilityDistributionForm probabilityDistributionForm
  ) {

    if (Objects.isNull(probabilityDistributionForm.getProbabilityDistributionType())) {
      return ProbabilityDistributionTypeError.emptyError();
    }

    var typePath = probabilityDistributionForm.getProbabilityDistributionType().getPath();
    var errorPath = formName + "." + typePath + ".error";
    var errorField = bindingResult.getFieldError(errorPath);
    var errorId = formName.replace(".", "-") + "-" + typePath;

    if (Objects.isNull(errorField)) {
      return ProbabilityDistributionTypeError.emptyError();
    }

    var hasError = true;
    return new ProbabilityDistributionTypeError(
        hasError,
        "form." + errorPath,
        probabilityDistributionForm.getProbabilityDistributionType(),
        errorId
    );
  }

  private void checkAvailabilityAndAddProbabilityInputs(
      boolean isAvailable,
      boolean isRequired,
      Map<ProbabilityOfExceedanceType, String> promptOverrides,
      ProbabilityOfExceedanceType probabilityOfExceedanceType,
      List<ProbabilityDistributionTypeInput> probabilityDistributionTypeInputs
  ) {
    if (isAvailable) {
      var prompt = getPromptOrOverride(promptOverrides, probabilityOfExceedanceType);
      var input = createProbabilityDistributionTypeInput(probabilityOfExceedanceType, prompt, isRequired);
      probabilityDistributionTypeInputs.add(input);
    }
  }

  private String getPromptOrOverride(Map<ProbabilityOfExceedanceType, String> promptOverrides,
                                     ProbabilityOfExceedanceType probabilityOfExceedanceType) {
    return promptOverrides.getOrDefault(probabilityOfExceedanceType, probabilityOfExceedanceType.getDisplayName());
  }

  private ProbabilityDistributionTypeInput createProbabilityDistributionTypeInput(
      ProbabilityOfExceedanceType probabilityOfExceedanceType,
      String prompt,
      boolean isRequired
  ) {
    var input = new ProbabilityDistributionTypeInput();
    input.setProbabilityOfExceedanceType(probabilityOfExceedanceType);
    input.setPrompt(prompt);
    input.setRequired(isRequired);
    return input;
  }

}
