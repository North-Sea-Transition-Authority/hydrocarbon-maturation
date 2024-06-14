package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class PlannedSanctionedWellsService {

  private final PlannedSanctionedWellsRepository plannedSanctionedWellsRepository;
  private final PswFaultRepository pswFaultRepository;
  private final HoleCompletionTypesRepository holeCompletionTypesRepository;
  private final PlannedCompletionTypesRepository plannedCompletionTypesRepository;
  private final ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository;

  @Autowired
  public PlannedSanctionedWellsService(PlannedSanctionedWellsRepository plannedSanctionedWellsRepository,
                                       PswFaultRepository pswFaultRepository,
                                       HoleCompletionTypesRepository holeCompletionTypesRepository,
                                       PlannedCompletionTypesRepository plannedCompletionTypesRepository,
                                       ArtificialLiftMechanismsRepository artificialLiftMechanismsRepository) {
    this.plannedSanctionedWellsRepository = plannedSanctionedWellsRepository;
    this.pswFaultRepository = pswFaultRepository;
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
              form.setAnyMultilateralWells(
                  plannedSanctionedWells.getAnyMultilateralWells()
              );
              form.setNumberOfMultilateralWells(
                  DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfMultilateralWells())
              );

              setStimulationTypeRequiredForm(form, plannedSanctionedWells);
              setPswFaultsForm(form, plannedSanctionedWells);
              setHoleCompletionTypesForm(form, plannedSanctionedWells);
              setPlannedCompletionTypesForm(form, plannedSanctionedWells);
              setArtificialLiftMechanismsTypesForm(form, plannedSanctionedWells);
            }
        );


    return form;
  }

  @Transactional
  public void savePlannedSanctionedWells(PlannedSanctionedWellsForm form,
                                         SubmissionDetail submissionDetail) {
    var plannedSanctionedWells = plannedSanctionedWellsRepository.findBySubmissionDetail(submissionDetail)
        .orElse(new PlannedSanctionedWells());

    plannedSanctionedWells.setSubmissionDetail(submissionDetail);

    plannedSanctionedWells.setTotalCapex(
        form.getTotalCapex().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setNumberOfPlannedSanctionedWells(
        form.getTotalNumberOfPlannedSanctionedWells().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setNumberOfInjectors(
        form.getNumberOfInjectors().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setNumberOfProducers(
        form.getNumberOfProducers().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setNumberOfMonitors(
        form.getNumberOfMonitors().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setAnyHorizontalWells(
        form.getAnyHorizontalWells()
    );
    plannedSanctionedWells.setNumberOfHorizontalWells(
        form.getNumberOfHorizontalWells().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setNumberOfHorizontalWellsWithErd(
        form.getNumberOfHorizontalWellsWithErd().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setAnyHighPressureTemperatureWells(
        form.getAnyHighPressureTemperatureWells()
    );
    plannedSanctionedWells.setNumberOfHighPressureTemperatureWells(
        form.getNumberOfHighPressureTemperatureWells().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setAnyWellsWithinNarrowOrTightPpfgWindow(
        form.getAnyWellsWithinNarrowOrTightPpfgWindow()
    );
    plannedSanctionedWells.setNumberOfWellsWithinNarrowOrTightPpfgWindow(
        form.getNumberOfWellsWithinNarrowOrTightPpfgWindow().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setIsManagedPressureDrillingUtilized(
        form.getIsManagedPressureDrillingUtilized()
    );
    plannedSanctionedWells.setAnyWellboreStabilityChallenges(
        form.getAnyWellboreStabilityChallenges()
    );
    plannedSanctionedWells.setNumberOfWellboreStabilityChallenges(
        form.getNumberOfWellboreStabilityChallenges().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setStabilityChallengesInformation(
        form.getStabilityChallengesInformation()
    );
    plannedSanctionedWells.setAnyWellsExpectedToCrossFaults(
        form.getAnyWellsExpectedToCrossFaults()
    );
    plannedSanctionedWells.setNumberOfWellsExpectedToCrossFaults(
        form.getNumberOfWellsExpectedToCrossFaults().createBigDecimalOrNull()
    );
    plannedSanctionedWells.setAnyExpectedLossesFromDrilling(
        form.getAnyExpectedLossesFromDrilling()
    );
    plannedSanctionedWells.setWhyLossesExpected(
        form.getWhyLossesExpected()
    );
    plannedSanctionedWells.setAnyExpectedDrillingChallenges(
        form.getAnyExpectedDrillingChallenges()
    );
    plannedSanctionedWells.setDrillingChallengesMitigations(
        form.getDrillingChallengesMitigations()
    );

    var stimulationType = form.getStimulationTypeRequired();
    plannedSanctionedWells.setStimulationTypeRequired(
        stimulationType
    );

    if (stimulationType != null) {
      plannedSanctionedWells.setNumberOfWellsRequiringStimulation(
          form.getStimulationTypeDecimalInputHashMap()
              .get(stimulationType)
              .createBigDecimalOrNull()
      );
    }

    plannedSanctionedWells.setExpectingFormationDamage(
        form.getIsExpectingFormationDamage()
    );
    plannedSanctionedWells.setExpectedFormationDamage(
        form.getExpectedFormationDamage()
    );
    plannedSanctionedWells.setAnyHeavyOil(
        form.getAnyHeavyOil()
    );
    plannedSanctionedWells.setAnySpecialisedDrillingEquipmentRequired(
        form.getAnySpecialisedDrillingEquipmentRequired()
    );
    plannedSanctionedWells.setSpecialisedDrillingEquipmentRequired(
        form.getSpecialisedDrillingEquipmentRequired()
    );
    plannedSanctionedWells.setAnyMultilateralWells(
        form.getAnyMultilateralWells()
    );
    plannedSanctionedWells.setNumberOfMultilateralWells(
        form.getNumberOfMultilateralWells().createBigDecimalOrNull()
    );

    var savedPlannedSanctionedWells = plannedSanctionedWellsRepository.save(plannedSanctionedWells);

    /*
     * save to other tables related to planned sanctioned wells below.
     * Note these are not in separate methods to remain in the same spring transaction.
     */

    /* save PswFaults */
    // clear any existing faults
    pswFaultRepository.deleteAllByPlannedSanctionedWells(savedPlannedSanctionedWells);

    // save new faults
    var pswFaultsToSave = new ArrayList<PswFaults>();
    for (var faultType : form.getExpectedFaults()) {
      var pswFaults = new PswFaults();
      pswFaults.setPlannedSanctionedWells(savedPlannedSanctionedWells);
      pswFaults.setExpectedFault(faultType);

      pswFaultsToSave.add(pswFaults);
    }

    pswFaultRepository.saveAll(pswFaultsToSave);

    /* save HoleCompletionTypes */
    // clear any existing hole completion types
    holeCompletionTypesRepository.deleteAllByPlannedSanctionedWells(savedPlannedSanctionedWells);

    // save new hole completion types
    var holeCompletionTypesToSave = new ArrayList<HoleCompletionTypes>();
    for (var type : form.getSelectedHoleCompletionTypes().keySet()) {
      var holeCompletionTypes = new HoleCompletionTypes();
      holeCompletionTypes.setPlannedSanctionedWells(savedPlannedSanctionedWells);
      holeCompletionTypes.setHoleCompletionType(type);

      var numberOfWells = form.getNumberOfHoleCompletionTypes().get(type);
      holeCompletionTypes.setNumberOfWellsWithHoleCompletionType(numberOfWells.createBigDecimalOrNull());

      holeCompletionTypesToSave.add(holeCompletionTypes);
    }
    holeCompletionTypesRepository.saveAll(holeCompletionTypesToSave);

    /* save PlannedCompletionTypes */
    // clear any existing hole completion types
    plannedCompletionTypesRepository.deleteAllByPlannedSanctionedWells(savedPlannedSanctionedWells);

    // save new planned completion types
    var plannedCompletionTypesToSave = new ArrayList<PlannedCompletionTypes>();
    for (var type : form.getSelectedPlannedCompletionTypes().keySet()) {
      var plannedCompletionTypes = new PlannedCompletionTypes();
      plannedCompletionTypes.setPlannedSanctionedWells(savedPlannedSanctionedWells);
      plannedCompletionTypes.setPlannedCompletionType(type);

      var numberOfWells = form.getNumberOfPlannedCompletionTypes().get(type);
      plannedCompletionTypes.setNumberOfWellsWithPlannedCompletionType(numberOfWells.createBigDecimalOrNull());

      if (PlannedCompletionType.OTHER.equals(type)) {
        plannedCompletionTypes.setOtherPlannedCompletionType(form.getOtherPlannedCompletionType());
      }
      plannedCompletionTypesToSave.add(plannedCompletionTypes);
    }
    plannedCompletionTypesRepository.saveAll(plannedCompletionTypesToSave);

    /* save ArtificialLiftMechanisms */
    // clear any existing artificial lift mechanisms
    artificialLiftMechanismsRepository.deleteAllByPlannedSanctionedWells(savedPlannedSanctionedWells);

    // save new artificial lift mechanisms
    var artificialLiftMechanismsToSave = new ArrayList<ArtificialLiftMechanisms>();
    for (var type : form.getSelectedArtificialLiftMechanisms().keySet()) {
      var artificialLiftMechanisms = new ArtificialLiftMechanisms();
      artificialLiftMechanisms.setPlannedSanctionedWells(savedPlannedSanctionedWells);
      artificialLiftMechanisms.setArtificialLiftMechanism(type);

      var numberOfWells = form.getNumberOfArtificialLiftMechanisms().get(type);
      artificialLiftMechanisms.setNumberOfWellsWithArtificialLiftMechanism(numberOfWells.createBigDecimalOrNull());

      if (ArtificialLiftMechanism.OTHER.equals(type)) {
        artificialLiftMechanisms.setOtherArtificialLiftMechanism(form.getOtherArtificialLiftMechanism());
      }
      artificialLiftMechanismsToSave.add(artificialLiftMechanisms);
    }
    artificialLiftMechanismsRepository.saveAll(artificialLiftMechanismsToSave);
  }

  private void setStimulationTypeRequiredForm(PlannedSanctionedWellsForm form, PlannedSanctionedWells plannedSanctionedWells) {
    var stimulationTypeRequired = plannedSanctionedWells.getStimulationTypeRequired();
    form.setStimulationTypeRequired(
        stimulationTypeRequired
    );
    if (stimulationTypeRequired != null) {
      form.getStimulationTypeDecimalInputHashMap().put(
          stimulationTypeRequired,
          DecimalInput.createDecimalInput(plannedSanctionedWells.getNumberOfWellsRequiringStimulation())
      );
    }
  }

  private void setPswFaultsForm(PlannedSanctionedWellsForm form, PlannedSanctionedWells plannedSanctionedWells) {
    Set<Fault> expectedFaults = pswFaultRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
        .stream().map(PswFaults::getExpectedFault)
        .collect(Collectors.toSet());

    form.setExpectedFaults(expectedFaults);
  }

  private void setHoleCompletionTypesForm(PlannedSanctionedWellsForm form, PlannedSanctionedWells plannedSanctionedWells) {
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

  private void setPlannedCompletionTypesForm(PlannedSanctionedWellsForm form, PlannedSanctionedWells plannedSanctionedWells) {
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

  private void setArtificialLiftMechanismsTypesForm(PlannedSanctionedWellsForm form,
                                                    PlannedSanctionedWells plannedSanctionedWells) {
    Map<ArtificialLiftMechanism, Boolean> selectedArtificialLiftMechanisms =
        new EnumMap<>(ArtificialLiftMechanism.class);
    Map<ArtificialLiftMechanism, DecimalInput> numberOfArtificialLiftMechanisms =
        PlannedSanctionedWellsForm.emptyNumberOfArtificialLiftMechanisms();

    artificialLiftMechanismsRepository.findAllByPlannedSanctionedWells(plannedSanctionedWells)
        .forEach(
            artificialLiftMechanisms -> {
              var type = artificialLiftMechanisms.getArtificialLiftMechanism();
              var isSelected = true;
              selectedArtificialLiftMechanisms.put(type, isSelected);

              var number =
                  DecimalInput.createDecimalInput(artificialLiftMechanisms.getNumberOfWellsWithArtificialLiftMechanism());
              numberOfArtificialLiftMechanisms.put(type, number);

              if (ArtificialLiftMechanism.OTHER.equals(type)) {
                form.setOtherArtificialLiftMechanism(artificialLiftMechanisms.getOtherArtificialLiftMechanism());
              }
            }
        );

    form.setSelectedArtificialLiftMechanisms(selectedArtificialLiftMechanisms);
    form.setNumberOfArtificialLiftMechanisms(numberOfArtificialLiftMechanisms);
  }
}
