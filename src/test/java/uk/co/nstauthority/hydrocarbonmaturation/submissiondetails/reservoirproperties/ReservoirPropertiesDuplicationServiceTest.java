package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistribution;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.ReservoirDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesDuplicationServiceTest {
  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private ReservoirDuplicationPersistenceService reservoirDuplicationPersistenceService;

  @InjectMocks
  private ReservoirPropertiesDuplicationService reservoirPropertiesDuplicationService;

  @Test
  void duplicateReservoirSection_ReservoirPropertyExists_DoesDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var reservoirProperty = mock(ReservoirProperty.class);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(oldReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, reservoirProperty,
        ReservoirProperty.class);
  }

  @Test
  void duplicateReservoirSection_ReservoirPropertyDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(oldReservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ReservoirProperty.class), eq(ReservoirProperty.class));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "RELINQUISHMENT_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void duplicateReservoirSection_ProbabilityDistributionsExist_ProspectUpdate_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(oldReservoirSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution1,
        ProbabilityDistribution.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution2,
        ProbabilityDistribution.class);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "RELINQUISHMENT_PROSPECT", "CREATE_DISCOVERY", "CREATE_PROSPECT", "CREATE_DISCOVERY_FROM_PROSPECT", "CREATE_FIELD", "CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"},
      mode = EnumSource.Mode.EXCLUDE)
  void duplicateReservoirSection_ProbabilityDistributionsExist_NonProspectUpdate_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    if (submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
      whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(oldReservoirSubmissionDetail)
          .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));
    } else if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      whenGetAllProbabilityDistributionsForInstancesForDryWellUpdateAndSameType(oldReservoirSubmissionDetail)
          .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));
    } else {
      whenGetAllProbabilityDistributionsForInstancesForDryWellUpdate(oldReservoirSubmissionDetail)
          .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));
    }

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution1,
        ProbabilityDistribution.class);
    verify(reservoirDuplicationPersistenceService).duplicateChildAndReplaceParent(newReservoirSubmissionDetail, probabilityDistribution2,
        ProbabilityDistribution.class);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "RELINQUISHMENT_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void duplicateReservoirSection_ProbabilityDistributionsDoNotExist_ProspectUpdate_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(oldReservoirSubmissionDetail)
        .thenReturn(List.of());

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "POST_DRILL_DRY_HOLE"}, mode = EnumSource.Mode.INCLUDE)
  void duplicateReservoirSection_ProbabilityDistributionsDoNotExist_DryWellAndSameType_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    whenGetAllProbabilityDistributionsForInstancesForDryWellUpdateAndSameType(oldReservoirSubmissionDetail)
        .thenReturn(List.of());

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "UPDATE_DISCOVERY",
      "RELINQUISHMENT_DISCOVERY"})
  void duplicateReservoirSection_ProbabilityDistributionsDoNotExist_Discovery_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(oldReservoirSubmissionDetail)
        .thenReturn(List.of());

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "CREATE_FDP",
      "CREATE_FDPA",
      "CREATE_CSR",
      "CREATE_COP"})
  void duplicateReservoirSection_ProbabilityDistributionsDoNotExist_Field_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    whenGetAllProbabilityDistributionsForInstancesForFieldUpdate(oldReservoirSubmissionDetail)
        .thenReturn(List.of());

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateReservoirSection_averageNetPayExists_Discovery_duplicates() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.UPDATE_DISCOVERY);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var averageNetPayProbDistribution = new ProbabilityDistribution();
    averageNetPayProbDistribution.setInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT);
    averageNetPayProbDistribution.setDistributionType(ProbabilityDistributionType.CONSTANT);
    whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(oldReservoirSubmissionDetail)
        .thenReturn(List.of(averageNetPayProbDistribution));
    var mockNetPayThickness = mock(ProbabilityDistribution.class);
    when(reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
        eq(newReservoirSubmissionDetail), any(), eq(ProbabilityDistribution.class)))
        .thenReturn(mockNetPayThickness);

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateReservoirSection_averageNetPayExists_PostDrill_DoesNotSetAverageNetPayThickness() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.PROSPECT_UPDATE);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var averageNetPayProbDistribution = new ProbabilityDistribution();
    averageNetPayProbDistribution.setInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT);
    averageNetPayProbDistribution.setDistributionType(ProbabilityDistributionType.CONSTANT);
    whenGetAllProbabilityDistributionsForInstancesForDryWellUpdate(oldReservoirSubmissionDetail)
        .thenReturn(List.of(averageNetPayProbDistribution));
    var mockNetPayThickness = mock(ProbabilityDistribution.class);
    when(reservoirDuplicationPersistenceService.duplicateChildAndReplaceParent(
        eq(newReservoirSubmissionDetail), any(), eq(ProbabilityDistribution.class)))
        .thenReturn(mockNetPayThickness);

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(mockNetPayThickness, never()).setInstance(any(ProbabilityDistributionFieldInstance.class));
    verify(reservoirDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateReservoirSection_PostDrill_update_Duplicates() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var averageNetPayProbDistribution = new ProbabilityDistribution();
    whenGetAllProbabilityDistributionsForInstancesForDryWellUpdateAndSameType(oldReservoirSubmissionDetail)
        .thenReturn(List.of(averageNetPayProbDistribution));

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "CREATE_FDP",
      "CREATE_FDPA",
      "CREATE_CSR",
      "CREATE_COP"})
  void duplicateReservoirSection_field_update_Duplicates(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var probabilityDistribution = new ProbabilityDistribution();
    whenGetAllProbabilityDistributionsForInstancesForFieldUpdate(oldReservoirSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution));

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verify(reservoirDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(eq(newReservoirSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateReservoirSection_averageNetPayExistsNotCalculated_Discovery_notDuplicates() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.UPDATE_DISCOVERY);

    var reservoir = new Reservoir();
    var oldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, oldSubmissionDetail);
    var newReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(reservoir, newSubmissionDetail);

    var averageNetPayProbDistribution = new ProbabilityDistribution();
    averageNetPayProbDistribution.setInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT);
    averageNetPayProbDistribution.setDistributionType(ProbabilityDistributionType.NOT_CALCULATED);
    whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(oldReservoirSubmissionDetail)
        .thenReturn(List.of(averageNetPayProbDistribution));

    reservoirPropertiesDuplicationService.duplicateReservoirSection(oldReservoirSubmissionDetail, newReservoirSubmissionDetail);

    verifyNoInteractions(reservoirDuplicationPersistenceService);
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(reservoirSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT
    )));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForFieldUpdate(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(reservoirSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD,
        ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD,
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD,
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD,
        ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD,
        ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD
    )));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(reservoirSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY,
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY
    )));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForDryWellUpdate(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(reservoirSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT
    )));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForDryWellUpdateAndSameType(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(reservoirSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY
        )));
  }
}