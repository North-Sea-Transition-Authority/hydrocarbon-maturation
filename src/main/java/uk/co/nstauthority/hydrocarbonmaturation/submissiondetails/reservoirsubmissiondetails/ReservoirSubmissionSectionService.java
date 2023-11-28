package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirSectionSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class ReservoirSubmissionSectionService {
  private final ReservoirSubmissionSectionRepository reservoirSubmissionSectionRepository;
  private final ReservoirFutureWorksDetailRepository reservoirFutureWorksDetailRepository;

  @Autowired
  public ReservoirSubmissionSectionService(ReservoirSubmissionSectionRepository reservoirSubmissionSectionRepository,
                                           ReservoirFutureWorksDetailRepository reservoirFutureWorksDetailRepository) {
    this.reservoirSubmissionSectionRepository = reservoirSubmissionSectionRepository;
    this.reservoirFutureWorksDetailRepository = reservoirFutureWorksDetailRepository;
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
}
