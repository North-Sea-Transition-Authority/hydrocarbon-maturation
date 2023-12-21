package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestResultsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertyRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertyRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertyRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilitySummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryService;

@ExtendWith(MockitoExtension.class)
class ReservoirSubmissionSectionServiceTest {

  @Mock
  private ReservoirSubmissionSectionRepository reservoirSubmissionSectionRepository;
  @Mock
  private ReservoirFutureWorksDetailRepository reservoirFutureWorksDetailRepository;
  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  @Mock
  private PermeabilityDetailRepository permeabilityDetailRepository;
  @Mock
  private ReservoirPropertiesRepository reservoirPropertiesRepository;
  @Mock
  private GeneralFluidPropertyRepository generalFluidPropertyRepository;
  @Mock
  private GasFluidPropertyRepository gasFluidPropertyRepository;
  @Mock
  private OilFluidPropertyRepository oilFluidPropertyRepository;
  @Mock
  private DrillStemTestResultPersistenceService drillStemTestResultPersistenceService;

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

  @Test
  void cleanReservoirSubmissionSections_reservoirsUnderFuturePhaseOfWork_mixedSelection_clearNotSelected() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    var isBeingDevelopedUnderFuturePhase = true;
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(isBeingDevelopedUnderFuturePhase);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    var reservoirSubmissionSection1 = new ReservoirSubmissionSection();
    reservoirSubmissionSection1.setReservoirSectionType(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES);
    var reservoirSubmissionSection2 = new ReservoirSubmissionSection();
    reservoirSubmissionSection2.setReservoirSectionType(ReservoirSubmissionSectionType.GAS_PROPERTIES);

    var selectedSections = Set.of(reservoirSubmissionSection1, reservoirSubmissionSection2);
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(selectedSections);

    reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);

    verify(permeabilityDetailRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(generalFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(oilFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(drillStemTestResultPersistenceService, times(1))
        .deleteDrillStemTestAndAllResults(reservoirSubmissionDetail);
    verifyNoInteractions(reservoirPropertiesRepository);
    verifyNoInteractions(gasFluidPropertyRepository);
  }

  @Test
  void cleanReservoirSubmissionSections_reservoirsUnderFuturePhaseOfWork_selectedPermeability_clearOthers() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    var isBeingDevelopedUnderFuturePhase = true;
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(isBeingDevelopedUnderFuturePhase);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    var permeabilitySubmissionSection = new ReservoirSubmissionSection();
    permeabilitySubmissionSection.setReservoirSectionType(ReservoirSubmissionSectionType.PERMEABILITY);

    var selectedSections = Set.of(permeabilitySubmissionSection);
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(selectedSections);

    reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);

    verify(generalFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(oilFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(gasFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirPropertiesRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(drillStemTestResultPersistenceService, times(1))
        .deleteDrillStemTestAndAllResults(reservoirSubmissionDetail);

    verifyNoInteractions(permeabilityDetailRepository);
  }

  @Test
  void cleanReservoirSubmissionSections_reservoirsUnderFuturePhaseOfWork_selectedGeneralFluidProperties_clearOthers() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    var isBeingDevelopedUnderFuturePhase = true;
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(isBeingDevelopedUnderFuturePhase);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    var generalFluidPropertiesSubmissionSection = new ReservoirSubmissionSection();
    generalFluidPropertiesSubmissionSection.setReservoirSectionType(ReservoirSubmissionSectionType.GENERAL_FLUID_PROPERTIES);

    var selectedSections = Set.of(generalFluidPropertiesSubmissionSection);
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(selectedSections);

    reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);

    verify(permeabilityDetailRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(oilFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(gasFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirPropertiesRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(drillStemTestResultPersistenceService, times(1))
        .deleteDrillStemTestAndAllResults(reservoirSubmissionDetail);

    verifyNoInteractions(generalFluidPropertyRepository);
  }

  @Test
  void cleanReservoirSubmissionSections_reservoirsUnderFuturePhaseOfWork_selectedOilProperties_clearOthers() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    var isBeingDevelopedUnderFuturePhase = true;
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(isBeingDevelopedUnderFuturePhase);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    var oilPropertiesSubmissionSection = new ReservoirSubmissionSection();
    oilPropertiesSubmissionSection.setReservoirSectionType(ReservoirSubmissionSectionType.OIL_PROPERTIES);

    var selectedSections = Set.of(oilPropertiesSubmissionSection);
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(selectedSections);

    reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);

    verify(permeabilityDetailRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(generalFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(gasFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirPropertiesRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(drillStemTestResultPersistenceService, times(1))
        .deleteDrillStemTestAndAllResults(reservoirSubmissionDetail);

    verifyNoInteractions(oilFluidPropertyRepository);
  }

  @Test
  void cleanReservoirSubmissionSections_reservoirsUnderFuturePhaseOfWork_selectedGasProperties_clearOthers() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    var isBeingDevelopedUnderFuturePhase = true;
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(isBeingDevelopedUnderFuturePhase);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    var gasPropertiesSubmissionSection = new ReservoirSubmissionSection();
    gasPropertiesSubmissionSection.setReservoirSectionType(ReservoirSubmissionSectionType.GAS_PROPERTIES);

    var selectedSections = Set.of(gasPropertiesSubmissionSection);
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(selectedSections);

    reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);

    verify(permeabilityDetailRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(generalFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(oilFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirPropertiesRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(drillStemTestResultPersistenceService, times(1))
        .deleteDrillStemTestAndAllResults(reservoirSubmissionDetail);

    verifyNoInteractions(gasFluidPropertyRepository);
  }

  @Test
  void cleanReservoirSubmissionSections_reservoirsUnderFuturePhaseOfWork_selectedReservoirProperties_clearOthers() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    var isBeingDevelopedUnderFuturePhase = true;
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(isBeingDevelopedUnderFuturePhase);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    var reservoirPropertiesSubmissionSection = new ReservoirSubmissionSection();
    reservoirPropertiesSubmissionSection.setReservoirSectionType(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES);

    var selectedSections = Set.of(reservoirPropertiesSubmissionSection);
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(selectedSections);

    reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);

    verify(permeabilityDetailRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(generalFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(oilFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(gasFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(drillStemTestResultPersistenceService, times(1))
        .deleteDrillStemTestAndAllResults(reservoirSubmissionDetail);

    verifyNoInteractions(reservoirPropertiesRepository);
  }

  @Test
  void cleanReservoirSubmissionSections_reservoirsUnderFuturePhaseOfWork_selectedDrillStemTestResults_clearOthers() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    var isBeingDevelopedUnderFuturePhase = true;
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(isBeingDevelopedUnderFuturePhase);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    var drillStemTestSubmissionSection = new ReservoirSubmissionSection();
    drillStemTestSubmissionSection.setReservoirSectionType(ReservoirSubmissionSectionType.DRILL_STEM_TEST);

    var selectedSections = Set.of(drillStemTestSubmissionSection);
    when(reservoirSubmissionSectionRepository.findAllByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(selectedSections);

    reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);

    verify(permeabilityDetailRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(generalFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(oilFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(gasFluidPropertyRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);
    verify(reservoirPropertiesRepository, times(1))
        .deleteAllByReservoirSubmissionDetail(reservoirSubmissionDetail);

    verifyNoInteractions(drillStemTestResultPersistenceService);
  }

  @Test
  void cleanReservoirSubmissionSections_noReservoirsUnderFuturePhaseOfWork_thenNoSectionsCleared() {
    var submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();

    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail));

    var reservoirFutureWorksDetail = new ReservoirFutureWorksDetail();
    var isBeingDevelopedUnderFuturePhase = false;
    reservoirFutureWorksDetail.setReservoirBeingDevelopedUnderFuturePhase(isBeingDevelopedUnderFuturePhase);
    when(reservoirFutureWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirFutureWorksDetail));

    reservoirSubmissionSectionService.cleanReservoirSubmissionSections(submissionDetail);

    verifyNoInteractions(
        permeabilityDetailRepository,
        generalFluidPropertyRepository,
        oilFluidPropertyRepository,
        drillStemTestResultPersistenceService,
        reservoirPropertiesRepository,
        gasFluidPropertyRepository
    );
  }
}