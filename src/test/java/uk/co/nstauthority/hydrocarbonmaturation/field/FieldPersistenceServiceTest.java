package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class FieldPersistenceServiceTest {
  @Mock
  private FieldRepository fieldRepository;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @Mock
  private ResourceLinkService resourceLinkService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @InjectMocks
  private FieldPersistenceService underTest;

  @Nested
  class WhenCreatingNewField {
    private NewFieldForm form;
    @Mock
    private Resource resource;
    private final String LEGAL_NAME = "legal name";
    private final String CORRECTED_LEGAL_NAME = "corrected legal name";
    private final String COMMON_NAME = "common name";
    private final String RESPONSIBLE_ORGANISATION_ID = "123";
    private final OnshoreOffshore ONSHORE_OFFSHORE = OnshoreOffshore.OFFSHORE;
    private final DeterminationStatus DETERMINATION_STATUS = DeterminationStatus.DETERMINED;
    private final DecimalInput FIELD_NUMBER = DecimalInput.createDecimalInput(1);
    private final boolean INFRASTRUCTURE_HOST = true;
    private final GeographicalArea GEOGRAPHICAL_AREA = GeographicalArea.NORTHERN_NORTH_SEA;
    private final ResponsibleTeamType RESPONSIBLE_TEAM_TYPE = ResponsibleTeamType.ONSHORE;
    private final String RESPONSIBLE_ORGANISATION_NAME = "responsible organisation";
    private final String REGISTERED_NUMBER = "123";
    private final UUID ASSOCIATED_DISCOVERY_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
      form = new NewFieldForm();
      form.setLegalFieldName(LEGAL_NAME);
      form.setCorrectedLegalFieldName(CORRECTED_LEGAL_NAME);
      form.setCommonNameForField(COMMON_NAME);
      form.setResponsibleOrganisationId(RESPONSIBLE_ORGANISATION_ID);
      form.setOnshoreOffshore(ONSHORE_OFFSHORE);
      form.setGeographicalArea(GEOGRAPHICAL_AREA);
      form.setResponsibleTeamType(RESPONSIBLE_TEAM_TYPE);
      form.setDeterminationStatus(DETERMINATION_STATUS);
      form.setFieldNumber(FIELD_NUMBER);
      form.setInfrastructureHost(INFRASTRUCTURE_HOST);
      form.setAssociatedDiscoveries(List.of());
    }

    @Test
    void statusAutomaticallySet() {

      var result = underTest.createNewField(form, resource);
      assertThat(result)
          .extracting(
              Field::getResource,
              Field::getFieldStatus
          )
          .containsExactly(
              resource,
              FieldStatus.STATUS_200
          );
    }

    @Test
    void onshoreSelected_GeographicalAreaAndTeamTypeAutomaticallySet() {
      form.setOnshoreOffshore(OnshoreOffshore.ONSHORE);

      var result = underTest.createNewField(form, resource);

      verify(regulatorManagedInformationPersistenceService).saveResponsibleTeamAndGeographicAreaByResource(resource, ResponsibleTeamType.ONSHORE, GeographicalArea.ONSHORE);
      assertThat(result.getOnshoreOffshore()).isEqualTo(OnshoreOffshore.ONSHORE);
    }

    @Test
    void responsibleOrganisationFound() {
      var organisationUnit = OrganisationUnit.newBuilder()
          .organisationUnitId(Integer.valueOf(RESPONSIBLE_ORGANISATION_ID))
          .name(RESPONSIBLE_ORGANISATION_NAME)
          .registeredNumber(REGISTERED_NUMBER)
          .build();
      when(organisationUnitQueryService.findOrganisationUnit(anyInt())).thenReturn(Optional.ofNullable(organisationUnit));

      var result = underTest.createNewField(form, resource);
      assertThat(result)
          .extracting(
              Field::getResponsibleOrganisationId,
              Field::getOrganisationName,
              Field::getOrganisationRegisteredNumber
          )
          .containsExactly(
              Integer.valueOf(RESPONSIBLE_ORGANISATION_ID),
              RESPONSIBLE_ORGANISATION_NAME,
              REGISTERED_NUMBER
          );
    }

    @Test
    void parentFieldSet() {
      var parentDeterminedField = 1;
      var field = mock(Field.class);
      form.setDeterminationStatus(DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD);
      form.setParentDeterminedField(String.valueOf(parentDeterminedField));
      when(fieldRepository.findById(parentDeterminedField)).thenReturn(Optional.ofNullable(field));

      var result = underTest.createNewField(form, resource);
      assertThat(result.getParentDeterminedField())
          .isEqualTo(field);
    }

    @Test
    void associatedDiscoveriesSet() {
      form.setAssociatedDiscoveries(List.of(ASSOCIATED_DISCOVERY_ID.toString()));


      var discovery = new Resource();
      var submission = new Submission();
      submission.setResource(discovery);
      var submissionDetail = new SubmissionDetail();
      submissionDetail.setSubmission(submission);
      when(submissionDetailService.getSubmissionDetailByResourceDetailOrThrow(ASSOCIATED_DISCOVERY_ID))
          .thenReturn(submissionDetail);

      var field = underTest.createNewField(form, discovery);
      verify(resourceLinkService).createNewDiscoveryFieldResourceLink(discovery, field.getResource());
    }

    @Test
    void formValuesSetInField() {

      var result = underTest.createNewField(form, resource);
      assertThat(result)
          .extracting(
              Field::getResource,
              Field::getFieldStatus,
              Field::getLegalFieldName,
              Field::getCorrectedLegalFieldName,
              Field::getCommonNameForField,
              Field::getResponsibleOrganisationId,
              Field::getOnshoreOffshore,
              Field::getDeterminationStatus,
              Field::getFieldNumber,
              Field::getInfrastructureHost
          )
          .containsExactly(
              resource,
              FieldStatus.STATUS_200,
              LEGAL_NAME,
              CORRECTED_LEGAL_NAME,
              COMMON_NAME,
              null,
              ONSHORE_OFFSHORE,
              DETERMINATION_STATUS,
              FIELD_NUMBER.createIntegerOrNull(),
              INFRASTRUCTURE_HOST
          );

      verify(fieldRepository).save(result);
    }
  }

  @Nested
  class WhenGettingDeterminedFieldsByCorrectedLegalName {
    private final String FIELD_NAME = "FIELD_NAME";

    @Test
    void verifyCalls() {
      underTest.getDeterminedFieldsByCorrectedLegalName(FIELD_NAME);
      verify(fieldRepository).findByDeterminationStatusAndCorrectedLegalFieldNameContainingIgnoreCase(DeterminationStatus.DETERMINED, FIELD_NAME);
    }
  }

  @Nested
  class WhenGettingField {
    private final Integer PARENT_DETERMINED_FIELD_ID = 1;

    @Test
    void verifyCalls() {
      underTest.getField(PARENT_DETERMINED_FIELD_ID);
      verify(fieldRepository).findById(PARENT_DETERMINED_FIELD_ID);
    }
  }

  @Nested
  class WhenGettingFieldsByCorrectedLegalName {
    private final String FIELD_NAME = "FIELD_NAME";

    @Test
    void verifyCalls() {
      underTest.getFieldsByCorrectedLegalName(FIELD_NAME);
      verify(fieldRepository).findFieldsByCorrectedLegalFieldNameContainingIgnoreCase(FIELD_NAME);
    }
  }

  @Nested
  class WhenGettingFieldOrThrow {
    private final Integer PARENT_DETERMINED_FIELD_ID = 1;

    @Test
    void verifyCalls() {
      when(fieldRepository.findById(PARENT_DETERMINED_FIELD_ID)).thenReturn(Optional.of(new Field()));

      underTest.getFieldOrThrow(PARENT_DETERMINED_FIELD_ID);
      verify(fieldRepository).findById(PARENT_DETERMINED_FIELD_ID);
    }

    @Test
    void throwsWhenNoField() {
      when(fieldRepository.findById(PARENT_DETERMINED_FIELD_ID)).thenReturn(Optional.empty());
      assertThatThrownBy(() -> underTest.getFieldOrThrow(PARENT_DETERMINED_FIELD_ID))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("No field found for id: %s".formatted(PARENT_DETERMINED_FIELD_ID));
    }
  }

  @Nested
  class WhenGettingFieldsByResource {
    private final Resource RESOURCE = new Resource();

    @Test
    void verifyCalls() {
      underTest.getFieldByResource(RESOURCE);
      verify(fieldRepository).findByResource(RESOURCE);
    }
  }

  @Nested
  class WhenGettingFieldByResourceOrThrow {
    private final static Resource RESOURCE = new Resource();

    @Test
    void verifyCalls() {
      var field = new Field();
      when(fieldRepository.findByResource(RESOURCE)).thenReturn(Optional.of(field));

      assertThat(underTest.getFieldByResourceOrThrow(RESOURCE))
          .isEqualTo(field);
    }

    @Test
    void throwsWhenNoField() {
      when(fieldRepository.findByResource(RESOURCE)).thenReturn(Optional.empty());
      assertThatThrownBy(() -> underTest.getFieldByResourceOrThrow(RESOURCE))
          .isInstanceOf(RemiEntityNotFoundException.class)
          .hasMessageContaining("No field found for resource id: %s".formatted(RESOURCE.getId()));
    }
  }
}