package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class PlannedSanctionedWellsFormService {

  private final PlannedSanctionedWellsRepository plannedSanctionedWellsRepository;
  private final PwsFaultRepository pwsFaultRepository;
  private final HoleCompletionTypesRepository holeCompletionTypesRepository;
  private final PlannedCompletionTypesRepository plannedCompletionTypesRepository;
  private final ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository;

  @Autowired
  public PlannedSanctionedWellsFormService(PlannedSanctionedWellsRepository plannedSanctionedWellsRepository,
                                           PwsFaultRepository pwsFaultRepository,
                                           HoleCompletionTypesRepository holeCompletionTypesRepository,
                                           PlannedCompletionTypesRepository plannedCompletionTypesRepository,
                                           ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository) {
    this.plannedSanctionedWellsRepository = plannedSanctionedWellsRepository;
    this.pwsFaultRepository = pwsFaultRepository;
    this.holeCompletionTypesRepository = holeCompletionTypesRepository;
    this.plannedCompletionTypesRepository = plannedCompletionTypesRepository;
    this.artificialLiftMechanismsRepository = artificialLiftMechanismsRepository;
  }

  public PlannedSanctionedWellsForm submissionDetailToForm(SubmissionDetail submissionDetail) {
    var form = new PlannedSanctionedWellsForm();
    plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail)
        .ifPresent(
            plannedSanctionedWells -> {
              form.setTotalCapex(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getTotalCapex())
              );
              form.setTotalNumberOfPlannedSanctionedWells(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfPlannedSanctionedWells())
              );
              form.setNumberOfInjectors(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfInjectors())
              );
              form.setNumberOfProducers(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfProducers())
              );
              form.setNumberOfMonitors(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfMonitors())
              );
              form.setAnyHorizontalWells(
                  plannedSanctionedWells.getAnyHorizontalWells()
              );
              form.setNumberOfHorizontalWells(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfHorizontalWells())
              );
              form.setNumberOfHorizontalWellsWithErd(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfHorizontalWellsWithErd())
              );
              form.setAnyHighPressureTemperatureWells(
                  plannedSanctionedWells.getAnyHighPressureTemperatureWells()
              );
              form.setNumberOfHighPressureTemperatureWells(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfHighPressureTemperatureWells())
              );
              form.setAnyWellsWithinNarrowOrTightPpfgWindow(
                  plannedSanctionedWells.getAnyWellsWithinNarrowOrTightPpfgWindow()
              );
              form.setNumberOfWellsWithinNarrowOrTightPpfgWindow(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfWellsWithinNarrowOrTightPpfgWindow())
              );
              form.setIsManagedPressureDrillingUtilized(
                  plannedSanctionedWells.getIsManagedPressureDrillingUtilized()
              );
              form.setAnyWellboreStabilityChallenges(
                  plannedSanctionedWells.getAnyWellboreStabilityChallenges()
              );
              form.setNumberOfWellboreStabilityChallenges(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfWellboreStabilityChallenges())
              );
              form.setStabilityChallengesInformation(
                  plannedSanctionedWells.getStabilityChallengesInformation()
              );
              form.setAnyWellsExpectedToCrossFaults(
                  plannedSanctionedWells.getAnyWellsExpectedToCrossFaults()
              );
              form.setNumberOfWellsExpectedToCrossFaults(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfWellsExpectedToCrossFaults())
              );
              form.setAnyExpectedLossesFromDrilling(
                  plannedSanctionedWells.getAnyExpectedLossesFromDrilling()
              );
              form.setWhyLossesExpected(
                  plannedSanctionedWells.getWhyLossesExpected()
              );
              form.setAnyExpectedDrillingChallenges(
                  plannedSanctionedWells.getAnyExpectedDrillingChallenges()
              );
              form.setDrillingChallengesMitigations(
                  plannedSanctionedWells.getDrillingChallengesMitigations()
              );
              form.setStimulationTypeRequired(
                  plannedSanctionedWells.getStimulationTypeRequired()
              );
              if (plannedSanctionedWells.getStimulationTypeRequired() != null) {
                form.getStimulationTypeDecimalInputHashMap()
                    .put(plannedSanctionedWells.getStimulationTypeRequired(),
                        DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfWellsRequiringStimulation()
                        )
                    );
              }
              form.setIsExpectingFormationDamage(
                  plannedSanctionedWells.getIsExpectingFormationDamage()
              );
              form.setExpectedFormationDamage(
                  plannedSanctionedWells.getExpectedFormationDamage()
              );
              form.setAnyHeavyOil(
                  plannedSanctionedWells.getAnyHeavyOil()
              );
              form.setAnySpecialisedDrillingEquipmentRequired(
                  plannedSanctionedWells.getAnySpecialisedDrillingEquipmentRequired()
              );
              form.setSpecialisedDrillingEquipmentRequired(
                  plannedSanctionedWells.getSpecialisedDrillingEquipmentRequired()
              );

              setPswFaults(form, plannedSanctionedWells);
              setHoleCompletionTypes(form, plannedSanctionedWells);
              setPlannedCompletionTypes(form, plannedSanctionedWells);
              setArtificialLiftMechanismsTypes(form, plannedSanctionedWells);
            }
        );


    return form;
  }

  private void setPswFaults(PlannedSanctionedWellsForm form, PlannedSanctionedWells plannedSanctionedWells) {
    Set<Fault> expectedFaults = pwsFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
        .stream().map(PswFaults::getExpectedFault)
        .collect(Collectors.toSet());

    form.setExpectedFaults(expectedFaults);
  }

  private void setHoleCompletionTypes(PlannedSanctionedWellsForm form, PlannedSanctionedWells plannedSanctionedWells) {
    Map<HoleCompletionType, Boolean> selectedHoleCompletionTypes =
        new EnumMap<>(HoleCompletionType.class);
    Map<HoleCompletionType, DecimalInput> numberOfHoleCompletionTypes =
        PlannedSanctionedWellsForm.emptyNumberOfHoleCompletionTypes();

    holeCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
        .forEach(
            holeCompletionTypes -> {
              var type = holeCompletionTypes.getHoleCompletionType();
              var isSelected = true;
              selectedHoleCompletionTypes.put(type, isSelected);

              var number = DecimalInput.createDecimalInput(holeCompletionTypes.getNumberOfWellsWithHoleCompletionType());
              numberOfHoleCompletionTypes.put(type, number);
            }
        );

    form.setSelectedHoleCompletionTypes(selectedHoleCompletionTypes);
    form.setNumberOfHoleCompletionTypes(numberOfHoleCompletionTypes);
  }

  private void setPlannedCompletionTypes(PlannedSanctionedWellsForm form, PlannedSanctionedWells plannedSanctionedWells) {
    Map<PlannedCompletionType, Boolean> selectedPlannedCompletionTypes =
        new EnumMap<>(PlannedCompletionType.class);
    Map<PlannedCompletionType, DecimalInput> numberOfPlannedCompletionTypes =
        PlannedSanctionedWellsForm.emptyNumberOfPlannedCompletionTypes();

    plannedCompletionTypesRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
        .forEach(
            plannedCompletionTypes -> {
              var type = plannedCompletionTypes.getPlannedCompletionType();
              var isSelected = true;
              selectedPlannedCompletionTypes.put(type, isSelected);

              var number = DecimalInput.createDecimalInput(
                  plannedCompletionTypes.getNumberOfWellsWithPlannedCompletionType());
              numberOfPlannedCompletionTypes.put(type, number);

              if (PlannedCompletionType.OTHER.equals(type)) {
                form.setOtherPlannedCompletionType(plannedCompletionTypes.getOtherPlannedCompletionType());
              }
            }
        );

    form.setSelectedPlannedCompletionTypes(selectedPlannedCompletionTypes);
    form.setNumberOfPlannedCompletionTypes(numberOfPlannedCompletionTypes);
  }

  private void setArtificialLiftMechanismsTypes(PlannedSanctionedWellsForm form,
                                                PlannedSanctionedWells plannedSanctionedWells) {
    Map<ArtificialLiftMechanism, Boolean> selectedArtificialLiftMechanisms =
        new EnumMap<>(ArtificialLiftMechanism.class);
    Map<ArtificialLiftMechanism, DecimalInput> numberOfArtificialLiftMechanisms =
        PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms();

    artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
        .forEach(
            plannedCompletionTypes -> {
              var type = plannedCompletionTypes.getArtificialLiftMechanism();
              var isSelected = true;
              selectedArtificialLiftMechanisms.put(type, isSelected);

              var number =
                  DecimalInput.createDecimalInput(plannedCompletionTypes.getNumberOfWellsWithArtificialLiftMechanism());
              numberOfArtificialLiftMechanisms.put(type, number);

              if (ArtificialLiftMechanism.OTHER.equals(type)) {
                form.setOtherArtificialLiftMechanism(plannedCompletionTypes.getOtherArtificialLiftMechanism());
              }
            }
        );

    form.setSelectedArtificialLiftMechanisms(selectedArtificialLiftMechanisms);
    form.setNumberOfArtificialLiftMechanisms(numberOfArtificialLiftMechanisms);
  }
}
