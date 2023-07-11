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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesDuplicationServiceTest {
  @Mock
  private ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private ReservoirPropertiesDuplicationService reservoirPropertiesDuplicationService;

  @Test
  void duplicateSubmissionSection_ReservoirPropertyExists_DoesDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);

    var reservoirProperty = mock(ReservoirProperty.class);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(oldSubmissionDetail))
        .thenReturn(Optional.of(reservoirProperty));

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, reservoirProperty,
        ReservoirProperty.class);
  }

  @Test
  void duplicateSubmissionSection_ReservoirPropertyDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);

    when(reservoirPropertiesPersistenceService.getReservoirProperty(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ReservoirProperty.class), eq(ReservoirProperty.class));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "RELINQUISHMENT_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void duplicateSubmissionSection_ProbabilityDistributionsExist_ProspectUpdate_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_PROSPECT);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(oldSubmissionDetail)
        .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution1,
        ProbabilityDistribution.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution2,
        ProbabilityDistribution.class);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "RELINQUISHMENT_PROSPECT", "CREATE_DISCOVERY", "CREATE_PROSPECT", "CREATE_DISCOVERY_FROM_PROSPECT", "CREATE_FIELD", "CREATE_CSR"},
      mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_ProbabilityDistributionsExist_NonProspectUpdate_DoesDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    var probabilityDistribution1 = mock(ProbabilityDistribution.class);
    var probabilityDistribution2 = mock(ProbabilityDistribution.class);
    if (submissionType == SubmissionType.UPDATE_DISCOVERY
        || submissionType == SubmissionType.RELINQUISHMENT_DISCOVERY) {
      whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(oldSubmissionDetail)
          .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));
    } else if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      whenGetAllProbabilityDistributionsForInstancesForDryWellUpdateAndSameType(oldSubmissionDetail)
          .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));
    } else {
      whenGetAllProbabilityDistributionsForInstancesForDryWellUpdate(oldSubmissionDetail)
          .thenReturn(List.of(probabilityDistribution1, probabilityDistribution2));
    }

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution1,
        ProbabilityDistribution.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, probabilityDistribution2,
        ProbabilityDistribution.class);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "RELINQUISHMENT_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void duplicateSubmissionSection_ProbabilityDistributionsDoNotExist_ProspectUpdate_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(oldSubmissionDetail)
        .thenReturn(List.of());

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "POST_DRILL_DRY_HOLE"}, mode = EnumSource.Mode.INCLUDE)
  void duplicateSubmissionSection_ProbabilityDistributionsDoNotExist_DryWellAndSameType_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    whenGetAllProbabilityDistributionsForInstancesForDryWellUpdateAndSameType(oldSubmissionDetail)
        .thenReturn(List.of());

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "UPDATE_DISCOVERY",
      "RELINQUISHMENT_DISCOVERY"})
  void duplicateSubmissionSection_ProbabilityDistributionsDoNotExist_Discovery_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(submissionType);

    whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(oldSubmissionDetail)
          .thenReturn(List.of());

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateSubmissionSection_averageNetPayExists_Discovery_duplicates() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.UPDATE_DISCOVERY);
    var averageNetPayProbDistribution = new ProbabilityDistribution();
    averageNetPayProbDistribution.setInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT);
    averageNetPayProbDistribution.setDistributionType(ProbabilityDistributionType.CONSTANT);
    whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(oldSubmissionDetail)
        .thenReturn(List.of(averageNetPayProbDistribution));
    var mockNetPayThickness = mock(ProbabilityDistribution.class);
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
        eq(newSubmissionDetail), any(), eq(ProbabilityDistribution.class)))
        .thenReturn(mockNetPayThickness);

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateSubmissionSection_averageNetPayExists_PostDrill_DoesNotSetAverageNetPayThickness() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.PROSPECT_UPDATE);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    var averageNetPayProbDistribution = new ProbabilityDistribution();
    averageNetPayProbDistribution.setInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT);
    averageNetPayProbDistribution.setDistributionType(ProbabilityDistributionType.CONSTANT);
    whenGetAllProbabilityDistributionsForInstancesForDryWellUpdate(oldSubmissionDetail)
        .thenReturn(List.of(averageNetPayProbDistribution));
    var mockNetPayThickness = mock(ProbabilityDistribution.class);
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
        eq(newSubmissionDetail), any(), eq(ProbabilityDistribution.class)))
        .thenReturn(mockNetPayThickness);

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(mockNetPayThickness, never()).setInstance(any(ProbabilityDistributionFieldInstance.class));
    verify(submissionDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateSubmissionSection_PostDrill_update_Duplicates() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    var averageNetPayProbDistribution = new ProbabilityDistribution();
    whenGetAllProbabilityDistributionsForInstancesForDryWellUpdateAndSameType(oldSubmissionDetail)
        .thenReturn(List.of(averageNetPayProbDistribution));

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, times(1))
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(ProbabilityDistribution.class), eq(ProbabilityDistribution.class));
  }

  @Test
  void duplicateSubmissionSection_averageNetPayExistsNotCalculated_Discovery_notDuplicates() {
    var oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.CREATE_DISCOVERY);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetailWithResourceAndSubmissionType(SubmissionType.UPDATE_DISCOVERY);
    var averageNetPayProbDistribution = new ProbabilityDistribution();
    averageNetPayProbDistribution.setInstance(ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT);
    averageNetPayProbDistribution.setDistributionType(ProbabilityDistributionType.NOT_CALCULATED);
    whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(oldSubmissionDetail)
        .thenReturn(List.of(averageNetPayProbDistribution));

    reservoirPropertiesDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForProspectUpdate(SubmissionDetail oldSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT
    )));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForDiscoveryUpdate(SubmissionDetail oldSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY,
        ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT
    )));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForDryWellUpdate(SubmissionDetail oldSubmissionDetail) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetail, Set.of(
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT
    )));
  }

  private OngoingStubbing<List<ProbabilityDistribution>> whenGetAllProbabilityDistributionsForInstancesForDryWellUpdateAndSameType(
      SubmissionDetail oldSubmissionDetails) {
    return when(probabilityDistributionPersistenceService.getAllProbabilityDistributionsForInstances(oldSubmissionDetails, Set.of(
        ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT,
        ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT
        )));
  }
}