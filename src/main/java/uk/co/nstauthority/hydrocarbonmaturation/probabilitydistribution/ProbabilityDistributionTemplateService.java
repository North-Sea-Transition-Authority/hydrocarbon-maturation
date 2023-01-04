package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

@Service
public class ProbabilityDistributionTemplateService {

  public ProbabilityDistributionTemplate getProbabilityDistributionTemplateForInstance(
      ProbabilityDistributionFieldInstance instance
  ) {
    var presetTypes = instance.getPresetTypes();

    List<ProbabilityDistributionTypeWithInputs> probabilityDistributionTypeWithInputsList = new ArrayList<>();

    presetTypes.forEach(type -> {

      var probabilityDistributionTypeWithInputs = new ProbabilityDistributionTypeWithInputs();
      probabilityDistributionTypeWithInputs.setProbabilityDistributionType(type);

      var behaviour = determineBehaviour(instance, type);

      addAddProbabilityInputsToList(probabilityDistributionTypeWithInputsList, probabilityDistributionTypeWithInputs, behaviour);
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
      ProbabilityDistributionTypeBehaviour behaviour) {
    List<ProbabilityDistributionTypeInput> probabilityDistributionTypeInputs = new ArrayList<>();

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
        behaviour.isMostLikelyAvailable(),
        behaviour.isMostLikelyRequired(),
        ProbabilityOfExceedanceType.MOST_LIKELY,
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

    if (Objects.isNull(errorField)) {
      return ProbabilityDistributionTypeError.emptyError();
    }

    var hasError = true;
    return new ProbabilityDistributionTypeError(
        hasError,
        "form." + errorPath,
        probabilityDistributionForm.getProbabilityDistributionType(),
        formName + "-" + typePath
    );
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
    var input = new ProbabilityDistributionTypeInput();
    input.setProbabilityOfExceedanceType(probabilityOfExceedanceType);
    input.setRequired(isRequired);
    return input;
  }

}
