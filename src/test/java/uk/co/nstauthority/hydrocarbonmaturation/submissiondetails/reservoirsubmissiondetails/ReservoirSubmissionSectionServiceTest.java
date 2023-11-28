package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;

@ExtendWith(MockitoExtension.class)
class ReservoirSubmissionSectionServiceTest {

  @Mock
  private ReservoirSubmissionSectionRepository reservoirSubmissionSectionRepository;

  @Mock
  private ReservoirFutureWorksDetailRepository reservoirFutureWorksDetailRepository;

  @InjectMocks
  private ReservoirSubmissionSectionService reservoirSubmissionSectionService;

  @Test
  void saveReservoirSubmissionSections() {
    var reservoirSubmissionSectionCaptor = ArgumentCaptor.forClass(ReservoirSubmissionSection.class);

    var sections = Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES);
    var reservoirSubmissionDetail = mock(ReservoirSubmissionDetail.class);
    reservoirSubmissionSectionService.saveReservoirSubmissionSections(sections, reservoirSubmissionDetail);

    verify(reservoirSubmissionSectionRepository, times(1))
        .save(reservoirSubmissionSectionCaptor.capture());

    assertThat(reservoirSubmissionSectionCaptor.getAllValues())
        .extracting(ReservoirSubmissionSection::getReservoirSubmissionDetail)
        .containsOnly(reservoirSubmissionDetail);

    assertThat(reservoirSubmissionSectionCaptor.getAllValues())
        .extracting(ReservoirSubmissionSection::getReservoirSectionType)
        .containsExactlyElementsOf(sections);

    verifyNoMoreInteractions(reservoirSubmissionSectionRepository);
  }

  @Test
  void getRequiredReservoirSubmissionSections_noFutureWorksExists() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    assertThat(reservoirSubmissionSectionService.getRequiredReservoirSubmissionSections(reservoirSubmissionDetail))
        .containsExactlyInAnyOrder(ReservoirSubmissionSectionType.values());
  }

  @Test
  void getRequiredReservoirSubmissionSections_futureWorksExists_answeredNo() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    reservoirFutureWorksDetail.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(false);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    assertThat(reservoirSubmissionSectionService.getRequiredReservoirSubmissionSections(reservoirSubmissionDetail))
        .containsExactlyInAnyOrder(ReservoirSubmissionSectionType.values());
  }

  @Test
  void getRequiredReservoirSubmissionSections_futureWorksExists() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    reservoirFutureWorksDetail.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(true);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    var reservoirSubmissionSection = new ReservoirSubmissionSection();
    reservoirSubmissionSection.setReservoirSectionType(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES);
    var sections = Set.of(reservoirSubmissionSection);
    when(reservoirSubmissionSectionService.getAllReservoirSubmissionSections(reservoirSubmissionDetail))
        .thenReturn(sections);

    assertThat(reservoirSubmissionSectionService.getRequiredReservoirSubmissionSections(reservoirSubmissionDetail))
        .containsExactly(reservoirSubmissionSection.getReservoirSectionType());
  }

  @Test
  void getSummarySectionClassesNotForSelection_noSelection() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    // no selection so all sections are relevant
    assertThat(reservoirSubmissionSectionService.getSummarySectionClassesNotForSelection(reservoirSubmissionDetail))
        .isEmpty();
  }

  @Test
  void getSummarySectionClassesNotForSelection_isBeingDevelopedUnderFuturePhase_someSelected_selectedNotInList() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    var workDetail = new ReservoirFutureWorksDetail();
    workDetail.setReservoirBeingDevelopedUnderFuturePhase(true);

    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(workDetail));
    var selectedSubmissionSection = new ReservoirSubmissionSection();
    selectedSubmissionSection.setReservoirSectionType(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES);
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Set.of(selectedSubmissionSection));

    // all selected so none are irrelevant
    assertThat(reservoirSubmissionSectionService.getSummarySectionClassesNotForSelection(reservoirSubmissionDetail))
        .containsExactly(
            PermeabilitySummaryService.class,
            GeneralFluidPropertySummaryService.class,
            GasFluidPropertySummaryService.class,
            OilFluidPropertySummaryService.class,
            DrillStemTestResultsSummaryService.class
        );
  }

  @Test
  void getSummarySectionClassesNotForSelection_isBeingDevelopedUnderFuturePhase_noneSelected_allInList() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    var workDetail = new ReservoirFutureWorksDetail();
    workDetail.setReservoirBeingDevelopedUnderFuturePhase(true);

    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(workDetail));
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Set.of());

    // all selected so none are irrelevant
    assertThat(reservoirSubmissionSectionService.getSummarySectionClassesNotForSelection(reservoirSubmissionDetail))
        .containsExactly(
            ReservoirPropertiesSummaryService.class,
            PermeabilitySummaryService.class,
            GeneralFluidPropertySummaryService.class,
            GasFluidPropertySummaryService.class,
            OilFluidPropertySummaryService.class,
            DrillStemTestResultsSummaryService.class
        );
  }
}