package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertyRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertyRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertyRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesRepository;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class ReservoirSubmissionSectionService {
  private final ReservoirSubmissionSectionRepository reservoirSubmissionSectionRepository;
  private final ReservoirFutureWorksDetailRepository reservoirFutureWorksDetailRepository;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  private final PermeabilityDetailRepository permeabilityDetailRepository;
  private final ReservoirPropertiesRepository reservoirPropertiesRepository;
  private final GeneralFluidPropertyRepository generalFluidPropertyRepository;
  private final GasFluidPropertyRepository gasFluidPropertyRepository;
  private final OilFluidPropertyRepository oilFluidPropertyRepository;
  private final DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

  @Autowired
  public ReservoirSubmissionSectionService(
      ReservoirSubmissionSectionRepository reservoirSubmissionSectionRepository,
      ReservoirFutureWorksDetailRepository reservoirFutureWorksDetailRepository,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      PermeabilityDetailRepository permeabilityDetailRepository,
      ReservoirPropertiesRepository reservoirPropertiesRepository,
      GeneralFluidPropertyRepository generalFluidPropertyRepository,
      GasFluidPropertyRepository gasFluidPropertyRepository,
      OilFluidPropertyRepository oilFluidPropertyRepository,
      DrillStemTestResultPersistenceService drillStemTestResultPersistenceService) {
    this.reservoirSubmissionSectionRepository = reservoirSubmissionSectionRepository;
    this.reservoirFutureWorksDetailRepository = reservoirFutureWorksDetailRepository;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.permeabilityDetailRepository = permeabilityDetailRepository;
    this.reservoirPropertiesRepository = reservoirPropertiesRepository;
    this.generalFluidPropertyRepository = generalFluidPropertyRepository;
    this.gasFluidPropertyRepository = gasFluidPropertyRepository;
    this.oilFluidPropertyRepository = oilFluidPropertyRepository;
    this.drillStemTestResultPersistenceService = drillStemTestResultPersistenceService;
  }

  public Set<ReservoirSubmissionSection> getAllReservoirSubmissionSections(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Transactional
  public void deleteSelectedReservoirSubmissionSectionOptions(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    reservoirSubmissionSectionRepository.deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Transactional
  public void saveReservoirSubmissionSections(Set<ReservoirSubmissionSectionType> reservoirSubmissionSections,
                                              ReservoirSubmissionDetail reservoirSubmissionDetail) {
    reservoirSubmissionSections.forEach(reservoirSubmissionSectionType ->
        saveNewReservoirSubmissionSection(reservoirSubmissionSectionType, reservoirSubmissionDetail));
  }

  @Transactional
  public void saveNewReservoirSubmissionSection(
      ReservoirSubmissionSectionType reservoirSubmissionSectionType,
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirSubmissionSection = new ReservoirSubmissionSection();
    reservoirSubmissionSection.setReservoirSectionType(reservoirSubmissionSectionType);
    reservoirSubmissionSection.setReservoirSubmissionDetail(reservoirSubmissionDetail);

    reservoirSubmissionSectionRepository.save(reservoirSubmissionSection);
  }

  /*
   * Gets the reservoir sections saved for reservoir submission detail or all sections if question was answered no
   * @return set of selected or all sections
   */
  public Set<ReservoirSubmissionSectionType> getRequiredReservoirSubmissionSections(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoirFutureWorksDetail =
        reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail)
            .orElse(new ReservoirFutureWorksDetail());

    if (Boolean.TRUE.equals(reservoirFutureWorksDetail.isReservoirBeingDevelopedUnderFuturePhase())) {
      return getAllReservoirSubmissionSections(reservoirSubmissionDetail)
          .stream().map(ReservoirSubmissionSection::getReservoirSectionType)
          .collect(Collectors.toSet());
    }

    return Arrays.stream(ReservoirSubmissionSectionType.values()).collect(Collectors.toSet());
  }

  /*
   * Get all the summary services that are not relevant for the selection
   * @param reservoirSubmissionDetail the reservoirSubmissionDetail which has some sections selected for
   * @return a list of all the summary service classes we are not interested in
   */
  public List<Class<? extends SummarySectionService<ReservoirSubmissionDetail>>> getSummarySectionClassesNotForSelection(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var requiredSections = getRequiredReservoirSubmissionSections(reservoirSubmissionDetail);
    return Arrays.stream(ReservoirSubmissionSectionType.values())
            .filter(sectionType -> !requiredSections.contains(sectionType))
            .map(ReservoirSubmissionSectionType::getSummaryClass)
            .toList();
  }

  /*
   * Get all the summary services that are not relevant for the selection
   * @param reservoirSubmissionDetail the reservoirSubmissionDetail which has some sections selected for
   * @return a list of all the summary service classes we are not interested in
   */
  public List<Class<? extends ReservoirSectionSubmissionService>> getSubmissionServiceClassesNotForSelection(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var requiredSections = getRequiredReservoirSubmissionSections(reservoirSubmissionDetail);
    return Arrays.stream(ReservoirSubmissionSectionType.values())
        .filter(sectionType -> !requiredSections.contains(sectionType))
        .map(ReservoirSubmissionSectionType::getSubmissionClass)
        .toList();
  }

  @Transactional
  public void cleanReservoirSubmissionSections(SubmissionDetail submissionDetail) {
    reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail)
        .forEach(
            reservoirSubmissionDetail -> {
              var selectedSections = getRequiredReservoirSubmissionSections(reservoirSubmissionDetail);
              cleanAllNonSelectedSections(selectedSections, reservoirSubmissionDetail);
            }
        );
  }

  private void cleanAllNonSelectedSections(Set<ReservoirSubmissionSectionType> selectedSections,
                                           ReservoirSubmissionDetail reservoirSubmissionDetail) {

    if (!selectedSections.contains(ReservoirSubmissionSectionType.PERMEABILITY)) {
      permeabilityDetailRepository.deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    }
    if (!selectedSections.contains(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES)) {
      reservoirPropertiesRepository.deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    }
    if (!selectedSections.contains(ReservoirSubmissionSectionType.GENERAL_FLUID_PROPERTIES)) {
      generalFluidPropertyRepository.deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    }
    if (!selectedSections.contains(ReservoirSubmissionSectionType.GAS_PROPERTIES)) {
      gasFluidPropertyRepository.deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    }
    if (!selectedSections.contains(ReservoirSubmissionSectionType.OIL_PROPERTIES)) {
      oilFluidPropertyRepository.deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    }
    if (!selectedSections.contains(ReservoirSubmissionSectionType.DRILL_STEM_TEST)) {
      drillStemTestResultPersistenceService.deleteDrillStemTestAndAllResults(reservoirSubmissionDetail);
    }
  }
}
