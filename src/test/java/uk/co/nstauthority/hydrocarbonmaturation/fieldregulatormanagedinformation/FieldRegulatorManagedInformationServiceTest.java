package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class FieldRegulatorManagedInformationServiceTest {
  @Mock
  private FieldPersistenceService fieldPersistenceService;
  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private ResourceLinkService resourceLinkService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private FieldFormValidator fieldFormValidator;
  @Mock
  private FieldLicenceService fieldLicenceService;
  @InjectMocks
  private FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService;

  private Resource resource;

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.FIELD);
  }

  @Test
  void resourceToFieldForm_noExistingField_assertEmptyForm() {
    var emptyFieldForm = new FieldForm();

    when(fieldPersistenceService.getFieldByResource(resource))
        .thenReturn(Optional.empty());

    assertThat(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .usingRecursiveComparison()
        .isEqualTo(emptyFieldForm);
  }

  @Test
  void resourceToFieldForm_existingField_noParentField_assertFormAttributes() {
    var field = new Field();
    field.setLegalFieldName("legal name");
    field.setCorrectedLegalFieldName("corrected name");
    field.setCommonNameForField("common name");
    field.setResponsibleOrganisationId(1);
    field.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    field.setFieldNumber(1);
    field.setInfrastructureHost(false);
    field.setDeterminationStatus(DeterminationStatus.DETERMINED);
    field.setIsOnLicence(false);

    when(fieldPersistenceService.getFieldByResource(resource))
        .thenReturn(Optional.of(field));

    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setGeographicalArea(GeographicalArea.NORTHERN_NORTH_SEA);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));

    var expectedResourceDetailIds = new ArrayList<UUID>();
    var link1 = createAndMockNewResourceLink(expectedResourceDetailIds);
    var link2 = createAndMockNewResourceLink(expectedResourceDetailIds);

    when(resourceLinkService.getResourceLinksByField(resource))
        .thenReturn(List.of(link1, link2));

    assertThat(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .extracting(
            FieldForm::getLegalFieldName,
            FieldForm::getCorrectedLegalFieldName,
            FieldForm::getCommonNameForField,
            FieldForm::getResponsibleOrganisationId,
            FieldForm::getOnshoreOffshore,
            FieldForm::getGeographicalArea,
            FieldForm::getResponsibleTeamType,
            fieldForm -> fieldForm.getFieldNumber().createIntegerOrNull(),
            FieldForm::getInfrastructureHost,
            FieldForm::getDeterminationStatus,
            FieldForm::getParentDeterminedField,
            FieldForm::getAssociatedDiscoveries,
            FieldForm::getIsOnLicence
        ).containsExactly(
            field.getLegalFieldName(),
            field.getCorrectedLegalFieldName(),
            field.getCommonNameForField(),
            field.getResponsibleOrganisationId().toString(),
            field.getOnshoreOffshore(),
            regulatorManagedInformation.getGeographicalArea(),
            regulatorManagedInformation.getResponsibleTeamType(),
            field.getFieldNumber(),
            field.getInfrastructureHost(),
            field.getDeterminationStatus(),
            "",
            expectedResourceDetailIds.stream().map(UUID::toString).toList(),
            field.getIsOnLicence()
        );
  }

  @Test
  void resourceToFieldForm_existingField_withParentField_assertFormAttributes() {
    var field = new Field();
    field.setLegalFieldName("legal name");
    field.setCorrectedLegalFieldName("corrected name");
    field.setCommonNameForField("common name");
    field.setResponsibleOrganisationId(1);
    field.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    field.setFieldNumber(1);
    field.setInfrastructureHost(false);
    field.setDeterminationStatus(DeterminationStatus.DETERMINED);
    var parentField = new Field();
    parentField.setFieldIdentifier(8000);
    field.setParentDeterminedField(parentField);
    field.setIsOnLicence(false);

    when(fieldPersistenceService.getFieldByResource(resource))
        .thenReturn(Optional.of(field));

    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setGeographicalArea(GeographicalArea.NORTHERN_NORTH_SEA);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));

    var expectedResourceDetailIds = new ArrayList<UUID>();
    var link1 = createAndMockNewResourceLink(expectedResourceDetailIds);
    var link2 = createAndMockNewResourceLink(expectedResourceDetailIds);

    when(resourceLinkService.getResourceLinksByField(resource))
        .thenReturn(List.of(link1, link2));

    assertThat(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .extracting(
            FieldForm::getLegalFieldName,
            FieldForm::getCorrectedLegalFieldName,
            FieldForm::getCommonNameForField,
            FieldForm::getResponsibleOrganisationId,
            FieldForm::getOnshoreOffshore,
            FieldForm::getGeographicalArea,
            FieldForm::getResponsibleTeamType,
            fieldForm -> fieldForm.getFieldNumber().createIntegerOrNull(),
            FieldForm::getInfrastructureHost,
            FieldForm::getDeterminationStatus,
            FieldForm::getParentDeterminedField,
            FieldForm::getAssociatedDiscoveries,
            FieldForm::getIsOnLicence
        ).containsExactly(
            field.getLegalFieldName(),
            field.getCorrectedLegalFieldName(),
            field.getCommonNameForField(),
            field.getResponsibleOrganisationId().toString(),
            field.getOnshoreOffshore(),
            regulatorManagedInformation.getGeographicalArea(),
            regulatorManagedInformation.getResponsibleTeamType(),
            field.getFieldNumber(),
            field.getInfrastructureHost(),
            field.getDeterminationStatus(),
            field.getParentDeterminedField().getFieldIdentifier().toString(),
            expectedResourceDetailIds.stream().map(UUID::toString).toList(),
            field.getIsOnLicence()
        );
  }

  @Test
  void resourceToFieldForm_existingField_withLicences_assertFormAttributes() {
    var field = new Field();
    field.setLegalFieldName("legal name");
    field.setCorrectedLegalFieldName("corrected name");
    field.setCommonNameForField("common name");
    field.setResponsibleOrganisationId(1);
    field.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    field.setFieldNumber(1);
    field.setInfrastructureHost(false);
    field.setDeterminationStatus(DeterminationStatus.DETERMINED);
    field.setIsOnLicence(true);

    when(fieldPersistenceService.getFieldByResource(resource))
        .thenReturn(Optional.of(field));

    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setGeographicalArea(GeographicalArea.NORTHERN_NORTH_SEA);
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource))
        .thenReturn(Optional.of(regulatorManagedInformation));

    var expectedResourceDetailIds = new ArrayList<UUID>();
    var link1 = createAndMockNewResourceLink(expectedResourceDetailIds);
    var link2 = createAndMockNewResourceLink(expectedResourceDetailIds);

    when(resourceLinkService.getResourceLinksByField(resource))
        .thenReturn(List.of(link1, link2));

    var licenceIds = List.of("ref1", "ref2");
    when(fieldLicenceService.getFieldLicenceIds(field))
        .thenReturn(licenceIds);

    assertThat(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .extracting(
            FieldForm::getLegalFieldName,
            FieldForm::getCorrectedLegalFieldName,
            FieldForm::getCommonNameForField,
            FieldForm::getResponsibleOrganisationId,
            FieldForm::getOnshoreOffshore,
            FieldForm::getGeographicalArea,
            FieldForm::getResponsibleTeamType,
            fieldForm -> fieldForm.getFieldNumber().createIntegerOrNull(),
            FieldForm::getInfrastructureHost,
            FieldForm::getDeterminationStatus,
            FieldForm::getAssociatedDiscoveries,
            FieldForm::getIsOnLicence,
            FieldForm::getLicences
        ).containsExactly(
            field.getLegalFieldName(),
            field.getCorrectedLegalFieldName(),
            field.getCommonNameForField(),
            field.getResponsibleOrganisationId().toString(),
            field.getOnshoreOffshore(),
            regulatorManagedInformation.getGeographicalArea(),
            regulatorManagedInformation.getResponsibleTeamType(),
            field.getFieldNumber(),
            field.getInfrastructureHost(),
            field.getDeterminationStatus(),
            expectedResourceDetailIds.stream().map(UUID::toString).toList(),
            field.getIsOnLicence(),
            licenceIds
        );
  }

  @Test
  void saveField() {
    var fieldForm = new FieldForm();

    var formCaptor = ArgumentCaptor.forClass(FieldForm.class);
    var resourceCaptor = ArgumentCaptor.forClass(Resource.class);
    fieldRegulatorManagedInformationService.saveField(fieldForm, resource);

    verify(fieldPersistenceService, times(1)).saveFieldFromFieldForm(formCaptor.capture(), resourceCaptor.capture());
    verifyNoMoreInteractions(fieldPersistenceService);

    assertThat(formCaptor.getValue()).isEqualTo(fieldForm);
    assertThat(resourceCaptor.getValue()).isEqualTo(resource);
  }

  @Test
  void isValidNewFieldForm_invalidForm_thenReturnFalse() {
    var form = new FieldForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    // add error when validator called
    doAnswer(new Answer<Void>() {
      @Override
      public Void answer(InvocationOnMock invocation) throws Throwable {
        bindingResult.reject("testError");
        return null;
      }
    }).when(fieldFormValidator).validate(any(), any());

    assertFalse(fieldRegulatorManagedInformationService.isValidNewFieldForm(form, bindingResult));
  }

  @Test
  void isValidNewFieldForm_validForm_thenReturnTrue() {
    var form = new FieldForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertTrue(fieldRegulatorManagedInformationService.isValidNewFieldForm(form, bindingResult));
  }

  private ResourceLink createAndMockNewResourceLink(ArrayList<UUID> resourceDetailIds) {
    var link = new ResourceLink();
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var resourceDetail = new ResourceDetail();
    resourceDetail.setId(UUID.randomUUID());
    link.setDiscoveryResource(discoverySubmissionDetail.getSubmission().getResource());
    when(submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(link.getDiscoveryResource()))
        .thenReturn(Optional.of(discoverySubmissionDetail));
    when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(discoverySubmissionDetail))
        .thenReturn(Optional.of(resourceDetail));

    resourceDetailIds.add(resourceDetail.getId());

    return link;
  }
}