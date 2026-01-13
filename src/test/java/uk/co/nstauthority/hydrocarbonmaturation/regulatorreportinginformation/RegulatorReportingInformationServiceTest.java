package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroup;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class RegulatorReportingInformationServiceTest {

  @Mock
  private ResourceGroupService resourceGroupService;

  @Mock
  private RegulatorReportingInformationRepository regulatorReportingInformationRepository;

  @Mock
  private WellDetailsFormService wellDetailsFormService;

  @Mock
  private WellQueryService wellQueryService;

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Mock
  private ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;

  @Mock
  private OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @InjectMocks
  private RegulatorReportingInformationService regulatorReportingInformationService;

  @Test
  void getForm_whenReportingInformationExists_mapsFields() {
    var resource = new Resource();
    var resourceGroup = new ResourceGroup();
    var link = new ResourceGroupLink();
    link.setResourceGroup(resourceGroup);

    var stored = new RegulatorReportingInformation();
    stored.setResourceName("Res A");
    stored.setHydrocarbonCase(HydrocarbonsDiscovered.GAS_ONLY);
    stored.setOpportunityType(OpportunityType.OTHER);
    stored.setOtherTypeOfOpportunity("Other");
    stored.setWellId(42);

    when(resourceGroupService.getResourceGroupLinkByResource(resource)).thenReturn(link);
    when(regulatorReportingInformationRepository.findByResourceGroup(resourceGroup))
        .thenReturn(Optional.of(stored));

    var form = regulatorReportingInformationService.getForm(resource);

    assertThat(form.getResourceName()).isEqualTo("Res A");
    assertThat(form.getHydrocarbonCase()).isEqualTo(HydrocarbonsDiscovered.GAS_ONLY);
    assertThat(form.getOpportunityType()).isEqualTo(OpportunityType.OTHER);
    assertThat(form.getOtherTypeOfOpportunity()).isEqualTo("Other");
    assertThat(form.getWell()).isEqualTo("42");
  }

  @Test
  void getForm_whenNoReportingInformation_returnsEmptyForm() {
    var resource = new Resource();
    var resourceGroup = new ResourceGroup();
    var link = new ResourceGroupLink();
    link.setResourceGroup(resourceGroup);

    when(resourceGroupService.getResourceGroupLinkByResource(resource)).thenReturn(link);
    when(regulatorReportingInformationRepository.findByResourceGroup(resourceGroup))
        .thenReturn(Optional.empty());

    var form = regulatorReportingInformationService.getForm(resource);

    assertThat(form.getResourceName()).isNull();
    assertThat(form.getHydrocarbonCase()).isNull();
    assertThat(form.getOpportunityType()).isNull();
    assertThat(form.getOtherTypeOfOpportunity()).isNull();
    assertThat(form.getWell()).isNull();
  }

  @Test
  void saveRegulatorReportingInformationForm_withWell_setsWellIdAndReferenceAndSaves() {
    var resource = new Resource();
    var resourceGroup = new ResourceGroup();
    var link = new ResourceGroupLink();
    link.setResourceGroup(resourceGroup);

    var form = new RegulatorReportingInformationForm();
    form.setResourceName("R");
    form.setHydrocarbonCase(HydrocarbonsDiscovered.GAS_ONLY);
    form.setOpportunityType(OpportunityType.OTHER);
    form.setOtherTypeOfOpportunity("O");
    form.setWell("123");

    var wellView = mock(WellView.class);
    when(wellView.reference()).thenReturn("WELL-REF");

    when(resourceGroupService.getResourceGroupLinkByResource(resource)).thenReturn(link);
    when(regulatorReportingInformationRepository.findByResourceGroup(resourceGroup))
        .thenReturn(Optional.empty());
    when(wellQueryService.findWellById(123)).thenReturn(Optional.of(wellView));

    regulatorReportingInformationService.saveRegulatorReportingInformationForm(form, resource);

    var captor = ArgumentCaptor.forClass(RegulatorReportingInformation.class);
    verify(regulatorReportingInformationRepository).save(captor.capture());
    var saved = captor.getValue();

    assertThat(saved.getResourceGroup()).isEqualTo(resourceGroup);
    assertThat(saved.getResourceName()).isEqualTo("R");
    assertThat(saved.getHydrocarbonCase()).isEqualTo(HydrocarbonsDiscovered.GAS_ONLY);
    assertThat(saved.getOpportunityType()).isEqualTo(OpportunityType.OTHER);
    assertThat(saved.getOtherTypeOfOpportunity()).isEqualTo("O");
    assertThat(saved.getWellId()).isEqualTo(123);
    assertThat(saved.getWellReference()).isEqualTo("WELL-REF");
  }

  @Test
  void saveRegulatorReportingInformationForm_withNullWell_clearsWellIdAndReferenceAndSaves() {
    var resource = new Resource();
    var resourceGroup = new ResourceGroup();
    var link = new ResourceGroupLink();
    link.setResourceGroup(resourceGroup);

    var existing = new RegulatorReportingInformation();
    existing.setResourceGroup(resourceGroup);
    existing.setWellId(9);
    existing.setWellReference("OLD-REF");

    var form = new RegulatorReportingInformationForm();
    form.setResourceName("R2");
    form.setHydrocarbonCase(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    form.setOpportunityType(OpportunityType.OTHER);
    form.setOtherTypeOfOpportunity("O2");
    form.setWell(null);

    when(resourceGroupService.getResourceGroupLinkByResource(resource)).thenReturn(link);
    when(regulatorReportingInformationRepository.findByResourceGroup(resourceGroup))
        .thenReturn(Optional.of(existing));

    regulatorReportingInformationService.saveRegulatorReportingInformationForm(form, resource);

    var captor = ArgumentCaptor.forClass(RegulatorReportingInformation.class);
    verify(regulatorReportingInformationRepository).save(captor.capture());
    var saved = captor.getValue();

    assertThat(saved.getWellId()).isNull();
    assertThat(saved.getWellReference()).isNull();
    assertThat(saved.getResourceName()).isEqualTo("R2");
  }

  @Test
  void saveRegulatorReportingInformationForNewResource_prospect_populatesFields() {
    // arrange
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    var resourceGroup = new ResourceGroup();

    var resourceDetails = mock(ResourceDetail.class);
    when(resourceDetails.getResourceName()).thenReturn("Prospect X");
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail))
        .thenReturn(resourceDetails);

    when(hydrocarbonCasePersistenceService.getHydrocarbonsDiscoveredForReporting(submissionDetail))
        .thenReturn(HydrocarbonsDiscovered.GAS_ONLY);

    var opp = mock(OpportunityDetail.class);
    when(opp.getOpportunityType()).thenReturn(OpportunityType.OTHER);
    when(opp.getOtherTypeOfOpportunity()).thenReturn("OppOther");
    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail))
        .thenReturn(Optional.of(opp));

    // act
    regulatorReportingInformationService.saveRegulatorReportingInformationForNewResource(submissionDetail, resourceGroup);

    // assert
    var captor = ArgumentCaptor.forClass(RegulatorReportingInformation.class);
    verify(regulatorReportingInformationRepository).save(captor.capture());
    var saved = captor.getValue();

    assertThat(saved.getResourceGroup()).isEqualTo(resourceGroup);
    assertThat(saved.getResourceName()).isEqualTo("Prospect X");
    assertThat(saved.getHydrocarbonCase()).isEqualTo(HydrocarbonsDiscovered.GAS_ONLY);
    assertThat(saved.getOpportunityType()).isEqualTo(OpportunityType.OTHER);
    assertThat(saved.getOtherTypeOfOpportunity()).isEqualTo("OppOther");
    assertThat(saved.getWellId()).isNull();
    assertThat(saved.getWellReference()).isNull();
  }

  @Test
  void saveRegulatorReportingInformationForNewResource_discovery_populatesFieldsAndWell() {
    // arrange
    var submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var resourceGroup = new ResourceGroup();

    var resourceDetails = mock(ResourceDetail.class);
    when(resourceDetails.getResourceName()).thenReturn("Discovery Y");
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail))
        .thenReturn(resourceDetails);

    when(reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
        .thenReturn(HydrocarbonsDiscovered.OIL_ONLY);

    var opp = mock(OpportunityDetail.class);
    when(opp.getOpportunityType()).thenReturn(OpportunityType.OTHER);
    when(opp.getOtherTypeOfOpportunity()).thenReturn("OtherOpp");
    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail))
        .thenReturn(Optional.of(opp));

    var discoveryWell = mock(DiscoveryWellDetail.class);
    when(discoveryWell.getWellId()).thenReturn(55);
    when(discoveryWell.getWellReference()).thenReturn("W-55");
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWell));

    // act
    regulatorReportingInformationService.saveRegulatorReportingInformationForNewResource(submissionDetail, resourceGroup);

    // assert
    var captor = ArgumentCaptor.forClass(RegulatorReportingInformation.class);
    verify(regulatorReportingInformationRepository).save(captor.capture());
    var saved = captor.getValue();

    assertThat(saved.getResourceGroup()).isEqualTo(resourceGroup);
    assertThat(saved.getResourceName()).isEqualTo("Discovery Y");
    assertThat(saved.getHydrocarbonCase()).isEqualTo(HydrocarbonsDiscovered.OIL_ONLY);
    assertThat(saved.getOpportunityType()).isEqualTo(OpportunityType.OTHER);
    assertThat(saved.getOtherTypeOfOpportunity()).isEqualTo("OtherOpp");
    assertThat(saved.getWellId()).isEqualTo(55);
    assertThat(saved.getWellReference()).isEqualTo("W-55");
  }
}
