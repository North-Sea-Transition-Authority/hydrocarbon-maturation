package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
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

    var fieldSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
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
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT
        ),
        reservoirSubmissionDetail
    );
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
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
        ),
        reservoirSubmissionDetail
    );
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

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(
        List.of(ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR),
        reservoirSubmissionDetail
    );
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
    var fieldSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
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
    var fieldSubmissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
    var fieldResource = fieldSubmissionDetail.getSubmission().getResource();
    var field = new Field();
    field.setResource(fieldResource);
    when(fieldPersistenceService.getFieldByResourceOrThrow(fieldResource)).thenReturn(field);

    var firstDiscoveryResource = new Resource();
    var firstResourceLink = new ResourceLink();
    firstResourceLink.setDiscoveryResource(firstDiscoveryResource);
    var secondDiscoveryResource = new Resource();
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
}