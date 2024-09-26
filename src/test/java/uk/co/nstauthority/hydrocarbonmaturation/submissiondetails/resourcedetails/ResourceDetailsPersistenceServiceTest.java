package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockPersistenceService;

@ExtendWith(MockitoExtension.class)
class ResourceDetailsPersistenceServiceTest {

  @Mock
  private ResourceDetailsRepository resourceDetailsRepository;

  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;

  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @InjectMocks
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.PROSPECT_UPDATE);
  }


  @Test
  void createInitialResourceDetail_verifyMethodCall() {
    var submissionDetail = new SubmissionDetail();
    var resourceForm = new ResourceDetailsForm();
    var captor = ArgumentCaptor.forClass(ResourceDetail.class);
    var organisationUnit = new OrganisationUnitDto(null, null, null, Collections.emptyList());
    when(organisationUnitQueryService.findOrganisationUnit(1))
        .thenReturn(Optional.of(organisationUnit));
    resourceForm.setResourceName("name");
    resourceForm.setResponsibleOrganisationId("1");
    resourceForm.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetailsPersistenceService.createInitialResourceDetail(resourceForm, submissionDetail);

    verify(resourceDetailsRepository).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
           ResourceDetail::getSubmissionDetail,
           ResourceDetail::getResourceName,
           ResourceDetail::getOrganisationId,
           ResourceDetail::getOrganisationName,
           ResourceDetail::getOrganisationRegisteredNumber,
           ResourceDetail::getOnshoreOffshore)
        .containsExactly(
            submissionDetail,
            resourceForm.getResourceName(),
            organisationUnit.organisationUnitId(),
            organisationUnit.organisationUnitName(),
            organisationUnit.companyHouseNumber(),
            resourceForm.getOnshoreOffshore());
  }

  @Test
  void saveResourceDetails_whenOrganisationUnitPresent_verifyMethodCall() {
    var submissionDetail = new SubmissionDetail();
    var resourceForm = new ResourceDetailsForm();
    var captor = ArgumentCaptor.forClass(ResourceDetail.class);
    var organisationUnit = new OrganisationUnitDto(null, null, null, Collections.emptyList());
    when(organisationUnitQueryService.findOrganisationUnit(1))
        .thenReturn(Optional.of(organisationUnit));
    resourceForm.setResourceName("name");
    resourceForm.setResponsibleOrganisationId("1");
    resourceForm.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetailsPersistenceService.saveResourceDetails(resourceForm, submissionDetail);

    verify(resourceDetailsRepository).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ResourceDetail::getSubmissionDetail,
            ResourceDetail::getResourceName,
            ResourceDetail::getOrganisationId,
            ResourceDetail::getOrganisationName,
            ResourceDetail::getOrganisationRegisteredNumber,
            ResourceDetail::getOnshoreOffshore)
        .containsExactly(
            submissionDetail,
            resourceForm.getResourceName(),
            organisationUnit.organisationUnitId(),
            organisationUnit.organisationUnitName(),
            organisationUnit.companyHouseNumber(),
            resourceForm.getOnshoreOffshore());
  }

  @Test
  void saveResourceDetails_whenOrganisationUnitIdNull_verifyMethodCall() {
    var submissionDetail = new SubmissionDetail();
    var resourceForm = new ResourceDetailsForm();
    var captor = ArgumentCaptor.forClass(ResourceDetail.class);
    resourceForm.setResourceName("name3");
    resourceForm.setResponsibleOrganisationId("2");
    when(organisationUnitQueryService.findOrganisationUnit(2))
        .thenReturn(Optional.empty());
    resourceForm.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetailsPersistenceService.saveResourceDetails(resourceForm, submissionDetail);

    verify(resourceDetailsRepository).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ResourceDetail::getSubmissionDetail,
            ResourceDetail::getResourceName,
            ResourceDetail::getOrganisationId,
            ResourceDetail::getOrganisationName,
            ResourceDetail::getOrganisationRegisteredNumber,
            ResourceDetail::getOnshoreOffshore)
        .containsExactly(
            submissionDetail,
            resourceForm.getResourceName(),
            null,
            null,
            null,
            resourceForm.getOnshoreOffshore());
  }

  @Test
  void saveResourceDetails_whenOrganisationUnitIdNotInEPA_verifyMethodCall() {
    var submissionDetail = new SubmissionDetail();
    var resourceForm = new ResourceDetailsForm();
    var captor = ArgumentCaptor.forClass(ResourceDetail.class);
    resourceForm.setResourceName("name");
    resourceForm.setResponsibleOrganisationId(null);
    resourceForm.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    resourceDetailsPersistenceService.saveResourceDetails(resourceForm, submissionDetail);

    verify(resourceDetailsRepository).save(captor.capture());

    assertThat(captor.getValue())
        .extracting(
            ResourceDetail::getSubmissionDetail,
            ResourceDetail::getResourceName,
            ResourceDetail::getOrganisationId,
            ResourceDetail::getOrganisationName,
            ResourceDetail::getOrganisationRegisteredNumber,
            ResourceDetail::getOnshoreOffshore)
        .containsExactly(
            submissionDetail,
            resourceForm.getResourceName(),
            null,
            null,
            null,
            resourceForm.getOnshoreOffshore());
  }

  @Test
  void saveResourceDetails_whenOrganisationUnitIdNull_updatingOnshoreOffshore_verifyMethodCall() {
    var submissionDetail = new SubmissionDetail();
    var resourceForm = new ResourceDetailsForm();
    var captor = ArgumentCaptor.forClass(ResourceDetail.class);
    resourceForm.setResourceName("name3");
    resourceForm.setResponsibleOrganisationId("2");
    when(organisationUnitQueryService.findOrganisationUnit(2))
        .thenReturn(Optional.empty());
    resourceForm.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);

    var resourceDetail = new ResourceDetail();
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    var generalDetail = mock(GeneralDetail.class);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.ofNullable(generalDetail));
    when(resourceDetailsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    resourceDetailsPersistenceService.saveResourceDetails(resourceForm, submissionDetail);

    verify(resourceDetailsRepository).save(captor.capture());
    verify(quadrantAndBlockPersistenceService).removeCurrentQuadsAndBlocks(generalDetail);

    assertThat(captor.getValue())
        .extracting(
            ResourceDetail::getSubmissionDetail,
            ResourceDetail::getResourceName,
            ResourceDetail::getOrganisationId,
            ResourceDetail::getOrganisationName,
            ResourceDetail::getOrganisationRegisteredNumber,
            ResourceDetail::getOnshoreOffshore)
        .containsExactly(
            submissionDetail,
            resourceForm.getResourceName(),
            null,
            null,
            null,
            resourceForm.getOnshoreOffshore());
  }

  @Test
  void saveResourceDetails_whenOrganisationUnitIdNull_notUpdatingOnshoreOffshore_verifyMethodCall() {
    var submissionDetail = new SubmissionDetail();
    var resourceForm = new ResourceDetailsForm();
    var captor = ArgumentCaptor.forClass(ResourceDetail.class);
    resourceForm.setResourceName("name3");
    resourceForm.setResponsibleOrganisationId("2");
    when(organisationUnitQueryService.findOrganisationUnit(2))
        .thenReturn(Optional.empty());
    resourceForm.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);

    var resourceDetail = new ResourceDetail();
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var generalDetail = mock(GeneralDetail.class);
    when(resourceDetailsRepository.findBySubmissionDetail(submissionDetail)).thenReturn(Optional.of(resourceDetail));
    resourceDetailsPersistenceService.saveResourceDetails(resourceForm, submissionDetail);

    verify(resourceDetailsRepository).save(captor.capture());
    verify(quadrantAndBlockPersistenceService, never()).removeCurrentQuadsAndBlocks(generalDetail);

    assertThat(captor.getValue())
        .extracting(
            ResourceDetail::getSubmissionDetail,
            ResourceDetail::getResourceName,
            ResourceDetail::getOrganisationId,
            ResourceDetail::getOrganisationName,
            ResourceDetail::getOrganisationRegisteredNumber,
            ResourceDetail::getOnshoreOffshore)
        .containsExactly(
            submissionDetail,
            resourceForm.getResourceName(),
            null,
            null,
            null,
            resourceForm.getOnshoreOffshore());
  }

  @Test
  void getResourceDetailOrThrow_verifyMethodCall() {
    var resourceDetail = new ResourceDetail();

    when(resourceDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetail));
    assertThat(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail))
        .isEqualTo(resourceDetail);
  }

  @Test
  void getResourceDetailOrThrow_assertThrow() {
    when(resourceDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());
    assertThatThrownBy(() -> resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Could not find resource detail for submission detail %s".formatted(submissionDetail.getId()));
  }

  @Test
  void getResourceDetailById_assertReturn() {
    var id = UUID.randomUUID();
    var resourceDetails = new ResourceDetail();
    when(resourceDetailsRepository.findById(id))
        .thenReturn(Optional.of(resourceDetails));
    var result = resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(id);
    assertThat(result).isEqualTo(resourceDetails);
  }

  @Test
  void getResourceDetailById_assertThrown() {
    var id = UUID.randomUUID();
    when(resourceDetailsRepository.findById(id))
        .thenReturn(Optional.empty());
    assertThatThrownBy(() -> resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(id))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("Could not find resource detail by ID %s".formatted(id));
  }

  @Test
  void getGeneralDetailBySubmissionId_verifyMethodCall() {
    resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail);
    verify(resourceDetailsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void getResourceDetailsByName_verifyMethodCall() {
    resourceDetailsPersistenceService.getResourceDetailsByName("prospect");
    verify(resourceDetailsRepository, times(1))
        .findByResourceNameContainingIgnoreCase("prospect");
  }

  @Test
  void updateOrganisationName_ifResourceDetailAndOrgUnitPresent_assertUpdated() {
    var resourceDetails = new ResourceDetail();
    resourceDetails.setOrganisationId(1);
    resourceDetails.setOrganisationName("Old name");
    resourceDetails.setOrganisationRegisteredNumber("25");
    var captor = ArgumentCaptor.forClass(ResourceDetail.class);
    when(resourceDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetails));
    var organisationUnitDto = new OrganisationUnitDto(
        1,
        "New name",
        "25",
        Collections.emptyList()
    );

    when(organisationUnitQueryService.findOrganisationUnit(resourceDetails.getOrganisationId()))
        .thenReturn(Optional.of(organisationUnitDto));
    resourceDetailsPersistenceService.updateOrganisationName(submissionDetail);
    verify(resourceDetailsRepository, times(1))
        .save(captor.capture());
    var result = captor.getValue();
    assertThat(result)
        .extracting(
            ResourceDetail::getOrganisationId,
            ResourceDetail::getOrganisationName,
            ResourceDetail::getOrganisationRegisteredNumber)
        .containsExactly(1, "New name", "25");

    assertThat(result)
        .hasAllNullFieldsOrPropertiesExcept(
            "organisationId", "organisationName", "organisationRegisteredNumber");
  }

  @Test
  void updateOrganisationName_ifResourceDetailNotPresent_assertNotUpdated() {
    when(resourceDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    resourceDetailsPersistenceService.updateOrganisationName(submissionDetail);
    verifyNoMoreInteractions(resourceDetailsRepository);
  }

  @Test
  void updateOrganisationName_ifResourceDetailOrgIdNotPresent_assertNotUpdated() {
    var resourceDetails = new ResourceDetail();
    resourceDetails.setOrganisationId(null);
    when(resourceDetailsRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(resourceDetails));

    resourceDetailsPersistenceService.updateOrganisationName(submissionDetail);
    verifyNoMoreInteractions(resourceDetailsRepository);
  }

  @Test
  void getOrganisationNameFromPortal() {
    var resourceDetails = new ResourceDetail();
    resourceDetailsPersistenceService.getOrganisationNameFromPortal(resourceDetails);
    verify(organisationUnitQueryService).getOrganisationUnitNameAndNumberByIdOrEmpty(resourceDetails.getOrganisationId());
  }

  @Test
  void getOrganisationDisplay_noOrganisationFoundFromResourceDetails() {
    var resourceDetails = new ResourceDetail();
    var displayName = resourceDetailsPersistenceService.getOrganisationDisplayFromResourceDetails(resourceDetails);
    assertThat(displayName).isBlank();
  }

  @Test
  void getOrganisationDisplay_organisationFound_withoutRegisteredNumberFromResourceDetails() {
    var resourceDetails = new ResourceDetail();
    var name = "name";
    resourceDetails.setOrganisationName(name);
    var displayName = resourceDetailsPersistenceService.getOrganisationDisplayFromResourceDetails(resourceDetails);
    assertThat(displayName).isEqualTo(name);
  }

  @Test
  void getOrganisationDisplay_organisationFound_withRegisteredNumberFromResourceDetails() {
    var resourceDetails = new ResourceDetail();
    var name = "name";
    var registeredNumber = "00001";
    resourceDetails.setOrganisationName(name);
    resourceDetails.setOrganisationRegisteredNumber(registeredNumber);
    var displayName = resourceDetailsPersistenceService.getOrganisationDisplayFromResourceDetails(resourceDetails);
    assertThat(displayName).isEqualTo("%s (%s)".formatted(name, registeredNumber));
  }
}