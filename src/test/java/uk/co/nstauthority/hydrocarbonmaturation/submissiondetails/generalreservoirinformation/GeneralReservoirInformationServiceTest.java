package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirLink;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertyPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertyPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirCurrentWorksDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirCurrentWorksDetailRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class GeneralReservoirInformationServiceTest {

  @Mock
  private ReservoirHydrocarbonsRepository reservoirHydrocarbonsRepository;

  @Mock
  private ReservoirPersistenceService reservoirPersistenceService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private ReservoirLinkService reservoirLinkService;

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  @Mock
  private ResourceLinkService resourceLinkService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  @Mock
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @Mock
  private ReservoirSubmissionSectionService reservoirSubmissionSectionService;

  @Mock
  private ReservoirCurrentWorksDetailRepository reservoirCurrentWorksDetailRepository;

  @Captor
  private ArgumentCaptor<ReservoirHydrocarbons> reservoirHydrocarbonsArgumentCaptor;

  @InjectMocks
  private GeneralReservoirInformationService generalReservoirInformationService;

  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

  @Test
  void saveGeneralReservoirInformation() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);

    var reservoirHydrocarbons =  new ReservoirHydrocarbons();
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    verify(reservoirPersistenceService, times(1))
        .saveReservoir(reservoirSubmissionDetail.getReservoir(), form.getReservoirName());
    verify(reservoirHydrocarbonsRepository, times(1)).save(reservoirHydrocarbonsArgumentCaptor.capture());
    assertThat(reservoirHydrocarbonsArgumentCaptor.getValue())
        .extracting(
            ReservoirHydrocarbons::getId,
            ReservoirHydrocarbons::getReservoirSubmissionDetail,
            ReservoirHydrocarbons::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirHydrocarbons.getId(),
            reservoirSubmissionDetail,
            form.getHydrocarbonsDiscovered()
        );
  }

  @Test
  void saveGeneralReservoirInformation_field() {
    var firstReservoir = new Reservoir();
    firstReservoir.setId(UUID.randomUUID());
    var secondReservoir = new Reservoir();
    secondReservoir.setId(UUID.randomUUID());
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setAssociatedDiscoveryReservoirs(Set.of(firstReservoir.getId().toString(), secondReservoir.getId().toString()));
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);

    var fieldSubmissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    var fieldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(fieldSubmissionDetail);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(fieldReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    var firstReservoirLink = new ReservoirLink();
    var secondReservoirLink = new ReservoirLink();
    var reservoirLinks = List.of(firstReservoirLink, secondReservoirLink);
    when(reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(fieldReservoirSubmissionDetail))
        .thenReturn(reservoirLinks);

    var reservoirs = List.of(firstReservoir, secondReservoir);
    when(reservoirPersistenceService.getReservoirs((List<UUID>) argThat(containsInAnyOrder(firstReservoir.getId(), secondReservoir.getId()))))
        .thenReturn(reservoirs);
    when(reservoirLinkService.constructReservoirLinks(fieldReservoirSubmissionDetail, reservoirs))
        .thenReturn(reservoirLinks);

    generalReservoirInformationService.saveGeneralReservoirInformation(form, fieldReservoirSubmissionDetail);

    verify(reservoirPersistenceService, times(1))
        .saveReservoir(fieldReservoirSubmissionDetail.getReservoir(), form.getReservoirName());
    verify(reservoirLinkService, times(1)).deleteReservoirLinks(reservoirLinks);
    verify(reservoirLinkService, times(1)).saveReservoirLinks(reservoirLinks);
    verify(reservoirHydrocarbonsRepository, times(1)).save(reservoirHydrocarbonsArgumentCaptor.capture());
    assertThat(reservoirHydrocarbonsArgumentCaptor.getValue())
        .extracting(
            ReservoirHydrocarbons::getId,
            ReservoirHydrocarbons::getReservoirSubmissionDetail,
            ReservoirHydrocarbons::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirHydrocarbons.getId(),
            fieldReservoirSubmissionDetail,
            form.getHydrocarbonsDiscovered()
        );
  }

  @Test
  void saveGeneralReservoirInformation_noReservoirHydrocarbons() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);

    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    verify(reservoirPersistenceService, times(1))
        .saveReservoir(reservoirSubmissionDetail.getReservoir(), form.getReservoirName());
    verify(reservoirHydrocarbonsRepository, times(1)).save(reservoirHydrocarbonsArgumentCaptor.capture());
    assertThat(reservoirHydrocarbonsArgumentCaptor.getValue())
        .extracting(
            ReservoirHydrocarbons::getReservoirSubmissionDetail,
            ReservoirHydrocarbons::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirSubmissionDetail,
            form.getHydrocarbonsDiscovered()
        );
  }

  @Test
  void saveGeneralReservoirInformation_formGasOnly() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY,
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD,
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD,
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD,
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY
        ),
        reservoirSubmissionDetail
    );

    verify(oilFluidPropertyPersistenceService, times(1)).clearAllOilFluidProperties(reservoirSubmissionDetail);
  }

  @Test
  void saveGeneralReservoirInformation_formOilOnly() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY,
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR,
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD,
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD,
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT,
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD,
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR_FIELD,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_IN_PLACE_DISCOVERY,
            ProbabilityDistributionFieldInstance.GAS_OUTPUTS_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
            ProbabilityDistributionFieldInstance.OUTPUTS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_DISCOVERY
        ),
        reservoirSubmissionDetail
    );

    verify(gasFluidPropertyPersistenceService, times(1)).clearAllGasFluidProperties(reservoirSubmissionDetail);
  }

  @Test
  void saveGeneralReservoirInformation_formAnyHydrocarbons() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);
  }

  @Test
  void saveGeneralReservoirInformation_copSubmission_thenNoFutureWorksDetail() {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(SubmissionType.CREATE_COP);

    var form = new GeneralReservoirInformationForm();
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    form.setAssociatedDiscoveryReservoirs(Set.of());

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    // check saving
    verifyNoInteractions(reservoirCurrentWorksDetailRepository);
    verifyNoInteractions(reservoirSubmissionSectionService);
  }

  @ParameterizedTest
  @MethodSource("allNonCopFieldSubmissionTypes")
  void saveGeneralReservoirInformation_fieldNonCop_noExistingFutureWorksDetail_answerNo(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);

    var form = new GeneralReservoirInformationForm();
    form.setIsReservoirBeingDevelopedUnderFuturePhase(false);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    form.setAssociatedDiscoveryReservoirs(Set.of());

    when(reservoirCurrentWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());
    var reservoirCurrentWorksDetailCaptor = ArgumentCaptor.forClass(ReservoirCurrentWorksDetail.class);

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    // check yes no saved
    verify(reservoirCurrentWorksDetailRepository, times(1))
        .save(reservoirCurrentWorksDetailCaptor.capture());
    assertThat(reservoirCurrentWorksDetailCaptor.getValue())
        .extracting(
            ReservoirCurrentWorksDetail::getReservoirSubmissionDetail,
            ReservoirCurrentWorksDetail::getIsReservoirBeingDevelopedUnderThisPhase
        ).containsExactly(
            reservoirSubmissionDetail,
            form.getIsReservoirBeingDevelopedUnderFuturePhase()
        );
    verifyNoMoreInteractions(reservoirCurrentWorksDetailRepository);

    // check sections saved
    verify(reservoirSubmissionSectionService, times(1))
        .deleteSelectedReservoirSubmissionSectionOptions(reservoirSubmissionDetail);
    verify(reservoirSubmissionSectionService, times(1))
        .saveReservoirSubmissionSections(form.getReservoirSubmissionSections(), reservoirSubmissionDetail);
    verifyNoMoreInteractions(reservoirSubmissionSectionService);
  }

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void saveGeneralReservoirInformation_nonField_noExistingFutureWorksDetail(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);

    var form = new GeneralReservoirInformationForm();
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    form.setAssociatedDiscoveryReservoirs(Set.of());

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    // check saving
    verifyNoInteractions(reservoirCurrentWorksDetailRepository);
    verifyNoInteractions(reservoirSubmissionSectionService);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA"}
  )
  void saveGeneralReservoirInformation_existingFutureWorksDetail_answerNo(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);
    var form = new GeneralReservoirInformationForm();
    form.setIsReservoirBeingDevelopedUnderFuturePhase(false);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    form.setAssociatedDiscoveryReservoirs(Set.of());

    var reservoirCurrentWorksDetail = new ReservoirCurrentWorksDetail();
    reservoirCurrentWorksDetail.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirCurrentWorksDetail.setIsReservoirBeingDevelopedUnderThisPhase(false);

    when(reservoirCurrentWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirCurrentWorksDetail));
    var reservoirCurrentWorksDetailCaptor = ArgumentCaptor.forClass(ReservoirCurrentWorksDetail.class);

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    // check yes no saved
    verify(reservoirCurrentWorksDetailRepository, times(1))
        .save(reservoirCurrentWorksDetailCaptor.capture());
    assertThat(reservoirCurrentWorksDetailCaptor.getValue())
        .extracting(
            ReservoirCurrentWorksDetail::getReservoirSubmissionDetail,
            ReservoirCurrentWorksDetail::getIsReservoirBeingDevelopedUnderThisPhase
        ).containsExactly(
            reservoirSubmissionDetail,
            form.getIsReservoirBeingDevelopedUnderFuturePhase()
        );
    verifyNoMoreInteractions(reservoirCurrentWorksDetailRepository);

    // check sections saved
    verify(reservoirSubmissionSectionService, times(1))
        .deleteSelectedReservoirSubmissionSectionOptions(reservoirSubmissionDetail);
    verify(reservoirSubmissionSectionService, times(1))
        .saveReservoirSubmissionSections(form.getReservoirSubmissionSections(), reservoirSubmissionDetail);
    verifyNoMoreInteractions(reservoirSubmissionSectionService);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA"}
  )
  void saveGeneralReservoirInformation_existingFutureWorksDetail_answerYes(SubmissionType submissionType) {
    reservoirSubmissionDetail.getSubmissionDetail().setSubmissionType(submissionType);
    var form = new GeneralReservoirInformationForm();
    form.setIsReservoirBeingDevelopedUnderFuturePhase(true);
    form.setReservoirSubmissionSections(Set.of(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES));
    form.setAssociatedDiscoveryReservoirs(Set.of());

    var reservoirCurrentWorksDetail = new ReservoirCurrentWorksDetail();
    reservoirCurrentWorksDetail.setReservoirSubmissionDetail(reservoirSubmissionDetail);

    when(reservoirCurrentWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirCurrentWorksDetail));
    var reservoirCurrentWorksDetailCaptor = ArgumentCaptor.forClass(ReservoirCurrentWorksDetail.class);

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    // check yes no saved
    verify(reservoirCurrentWorksDetailRepository, times(1))
        .save(reservoirCurrentWorksDetailCaptor.capture());
    assertThat(reservoirCurrentWorksDetailCaptor.getValue())
        .extracting(
            ReservoirCurrentWorksDetail::getReservoirSubmissionDetail,
            ReservoirCurrentWorksDetail::getIsReservoirBeingDevelopedUnderThisPhase
        ).containsExactly(
            reservoirSubmissionDetail,
            form.getIsReservoirBeingDevelopedUnderFuturePhase()
        );
    verifyNoMoreInteractions(reservoirCurrentWorksDetailRepository);

    // check sections saved
    verify(reservoirSubmissionSectionService, times(1))
        .deleteSelectedReservoirSubmissionSectionOptions(reservoirSubmissionDetail);
    verifyNoMoreInteractions(reservoirSubmissionSectionService);
  }

  @Test
  void getGeneralReservoirInformationForm_hydrocarbonsDiscovered() {
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    assertThat(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .extracting(
            GeneralReservoirInformationForm::getReservoirName,
            GeneralReservoirInformationForm::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirSubmissionDetail.getReservoir().getName(),
            reservoirHydrocarbons.getHydrocarbonsDiscovered()
        );
  }

  @Test
  void getGeneralReservoirInformationForm_hydrocarbonsDiscovered_field() {
    var fieldSubmissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    var fieldReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(fieldSubmissionDetail);

    var firstReservoir = new Reservoir();
    firstReservoir.setId(UUID.randomUUID());
    var firstReservoirLink = new ReservoirLink();
    firstReservoirLink.setLinkedReservoir(firstReservoir);
    var secondReservoir = new Reservoir();
    secondReservoir.setId(UUID.randomUUID());
    var secondReservoirLink = new ReservoirLink();
    secondReservoirLink.setLinkedReservoir(secondReservoir);
    when(reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(fieldReservoirSubmissionDetail))
        .thenReturn(List.of(firstReservoirLink, secondReservoirLink));

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(fieldReservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    assertThat(generalReservoirInformationService.getGeneralReservoirInformationForm(fieldReservoirSubmissionDetail))
        .extracting(
            GeneralReservoirInformationForm::getReservoirName,
            GeneralReservoirInformationForm::getAssociatedDiscoveryReservoirs,
            GeneralReservoirInformationForm::getHydrocarbonsDiscovered)
        .containsExactly(
            fieldReservoirSubmissionDetail.getReservoir().getName(),
            Set.of(firstReservoir.getId().toString(), secondReservoir.getId().toString()),
            reservoirHydrocarbons.getHydrocarbonsDiscovered()
        );
  }

  @Test
  void getGeneralReservoirInformationForm_noHydrocarbonsDiscovered() {
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    assertThat(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .extracting(
            GeneralReservoirInformationForm::getReservoirName,
            GeneralReservoirInformationForm::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirSubmissionDetail.getReservoir().getName(),
            null
        );
  }

  @Test
  void getGeneralReservoirInformationForm_noFutureWorkDetails() {
    when(reservoirCurrentWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    assertThat(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .extracting(
            GeneralReservoirInformationForm::getIsReservoirBeingDevelopedUnderFuturePhase,
            GeneralReservoirInformationForm::getReservoirSubmissionSections)
        .containsExactly(
            null,
            emptySet()
        );
  }

  @Test
  void getGeneralReservoirInformationForm_futureWorkDetails_answeredYes() {
    var reservoirCurrentWorksDetail = new ReservoirCurrentWorksDetail();
    reservoirCurrentWorksDetail.setIsReservoirBeingDevelopedUnderThisPhase(true);

    when(reservoirCurrentWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirCurrentWorksDetail));

    assertThat(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .extracting(
            GeneralReservoirInformationForm::getIsReservoirBeingDevelopedUnderFuturePhase,
            GeneralReservoirInformationForm::getReservoirSubmissionSections)
        .containsExactly(
            reservoirCurrentWorksDetail.getIsReservoirBeingDevelopedUnderThisPhase(),
            emptySet()
        );
  }

  @Test
  void getGeneralReservoirInformationForm_futureWorkDetails_answeredNo() {
    var reservoirCurrentWorksDetail = new ReservoirCurrentWorksDetail();
    reservoirCurrentWorksDetail.setIsReservoirBeingDevelopedUnderThisPhase(false);

    when(reservoirCurrentWorksDetailRepository.findByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirCurrentWorksDetail));

    var reservoirSection = new ReservoirSubmissionSection();
    reservoirSection.setReservoirSectionType(ReservoirSubmissionSectionType.RESERVOIR_PROPERTIES);

    when(reservoirSubmissionSectionService.getAllReservoirSubmissionSections(reservoirSubmissionDetail))
        .thenReturn(Set.of(reservoirSection));

    assertThat(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .extracting(
            GeneralReservoirInformationForm::getIsReservoirBeingDevelopedUnderFuturePhase,
            GeneralReservoirInformationForm::getReservoirSubmissionSections)
        .containsExactly(
            reservoirCurrentWorksDetail.getIsReservoirBeingDevelopedUnderThisPhase(),
            Set.of(reservoirSection.getReservoirSectionType())
        );
  }

  @Test
  void getPreselectedAssociatedDiscoveryReservoirs() {
    var firstReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var secondReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    var firstReservoir = firstReservoirSubmissionDetail.getReservoir();
    firstReservoir.setName("zzzz");
    var secondReservoir = secondReservoirSubmissionDetail.getReservoir();
    secondReservoir.setName("aaaa");

    var thirdReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(firstReservoir);
    var fourthReservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(secondReservoir);

    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailsForReservoirIds(
        Set.of(firstReservoir.getId().toString(), secondReservoir.getId().toString())))
        .thenReturn(
            List.of(firstReservoirSubmissionDetail, secondReservoirSubmissionDetail, thirdReservoirSubmissionDetail, fourthReservoirSubmissionDetail));

    var firstReservoirView = new ReservoirView(firstReservoir.getId(), firstReservoir.getName());
    var secondReservoirView = new ReservoirView(secondReservoir.getId(), secondReservoir.getName());
    when(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(firstReservoirSubmissionDetail))
        .thenReturn(firstReservoirView);
    when(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(secondReservoirSubmissionDetail))
        .thenReturn(secondReservoirView);
    when(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(thirdReservoirSubmissionDetail))
        .thenReturn(firstReservoirView);
    when(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(fourthReservoirSubmissionDetail))
        .thenReturn(secondReservoirView);

    assertThat(generalReservoirInformationService
        .getPreselectedAssociatedDiscoveryReservoirs(Set.of(firstReservoir.getId().toString(), secondReservoir.getId().toString())))
        .containsExactly(
            secondReservoirView, firstReservoirView
        );
  }

  @Test
  void getPreselectedAssociatedDiscoveryReservoirs_emptyList() {
    assertThat(generalReservoirInformationService.getPreselectedAssociatedDiscoveryReservoirs(Set.of())).isEmpty();
  }

  @Test
  void getReservoirHydrocarbons_verifyCalls() {
    generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail);

    verify(reservoirHydrocarbonsRepository, times(1))
        .findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  @Test
  void getResourceNamesFromSubmissionDetail() {
    var fieldSubmissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    var fieldResource = fieldSubmissionDetail.getSubmission().getResource();
    var field = new Field();
    field.setResource(fieldResource);
    when(fieldPersistenceService.getFieldOrThrow(fieldResource)).thenReturn(field);

    var firstDiscoveryResource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var firstResourceLink = new ResourceLink();
    firstResourceLink.setDiscoveryResource(firstDiscoveryResource);
    var secondDiscoveryResource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var secondResourceLink = new ResourceLink();
    secondResourceLink.setDiscoveryResource(secondDiscoveryResource);
    when(resourceLinkService.getResourceLinksByField(fieldResource))
        .thenReturn(List.of(firstResourceLink, secondResourceLink));

    var firstDiscoverySubmissionDetail = new SubmissionDetail();
    var secondDiscoverySubmissionDetail = new SubmissionDetail();
    when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(firstDiscoveryResource))
        .thenReturn(Optional.of(firstDiscoverySubmissionDetail));
    when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(secondDiscoveryResource))
        .thenReturn(Optional.of(secondDiscoverySubmissionDetail));

    var firstResourceDetail = new ResourceDetail();
    firstResourceDetail.setResourceName("first resource detail");
    var secondResourceDetail = new ResourceDetail();
    secondResourceDetail.setResourceName("second resource detail");
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(firstDiscoverySubmissionDetail))
        .thenReturn(firstResourceDetail);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(secondDiscoverySubmissionDetail))
        .thenReturn(secondResourceDetail);

    assertThat(generalReservoirInformationService.getDiscoveryResourceNamesForFieldResourceLinkFromSubmissionDetail(fieldSubmissionDetail))
        .containsExactly(firstResourceDetail.getResourceName(), secondResourceDetail.getResourceName());
  }

  @Test
  void getLinkedDiscoveryReservoirViewsForField() {
    var reservoirLink1 = new ReservoirLink();
    var reservoirLinkId1 = UUID.randomUUID();
    reservoirLink1.setId(reservoirLinkId1);
    reservoirLink1.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    var reservoir1 = new Reservoir();
    var reservoirId1 = UUID.randomUUID();
    reservoir1.setId(reservoirId1);
    var reservoirName1 = "Res 1";
    reservoir1.setName(reservoirName1);
    reservoirLink1.setLinkedReservoir(reservoir1);
    var reservoirLink2 = new ReservoirLink();
    var reservoirLinkId2 = UUID.randomUUID();
    reservoirLink2.setId(reservoirLinkId2);
    reservoirLink2.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    var reservoir2 = new Reservoir();
    var reservoirId2 = UUID.randomUUID();
    reservoir2.setId(reservoirId2);
    var reservoirName2 = "Res 2";
    reservoir2.setName(reservoirName2);
    reservoirLink2.setLinkedReservoir(reservoir2);
    when(reservoirLinkService.getReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(List.of(reservoirLink1, reservoirLink2));
    var discoveryReservoirSubmissionDetail1 = new ReservoirSubmissionDetail();
    var discoveryReservoirSubmissionDetail2 = new ReservoirSubmissionDetail();
    when(reservoirSubmissionDetailPersistenceService.getReservoirSubmissionDetailsForReservoirIds(Set.of(reservoir1.getId().toString(), reservoir2.getId().toString())))
        .thenReturn(List.of(discoveryReservoirSubmissionDetail1, discoveryReservoirSubmissionDetail2));
    var reservoirView1 = new ReservoirView(reservoirId1, reservoirName1);
    var reservoirView2 = new ReservoirView(reservoirId2, reservoirName2);
    when(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(discoveryReservoirSubmissionDetail1)).thenReturn(reservoirView1);
    when(reservoirLinkService.getReservoirViewFromReservoirSubmissionDetail(discoveryReservoirSubmissionDetail2)).thenReturn(reservoirView2);
    var linkedReservoirViews = generalReservoirInformationService.getLinkedDiscoveryReservoirViewsForField(reservoirSubmissionDetail);
    assertThat(linkedReservoirViews).containsExactly(reservoirView1, reservoirView2);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonCopFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .filter(submissionType -> !submissionType.equals(SubmissionType.CREATE_COP))
        .map(Arguments::of);
  }
}