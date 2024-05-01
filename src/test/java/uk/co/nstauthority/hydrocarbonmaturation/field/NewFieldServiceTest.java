package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceView;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class NewFieldServiceTest {

  @Mock
  private FieldFormValidator fieldFormValidator;
  @Mock
  private FieldPersistenceService fieldPersistenceService;
  @Mock
  private ResourceService resourceService;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private SubmissionResourceService submissionResourceService;
  @Mock
  private FieldStatusService fieldStatusService;
  @InjectMocks
  private NewFieldService underTest;
  @Mock
  private SearchSelectorService searchSelectorService;

  @Nested
  class WhenCreatingNewField {
    @Mock
    private FieldForm form;
    @Mock
    private Resource resource;

    @Test
    void verifyMethodCalls() {
      when(resourceService.createResource(ResourceType.FIELD)).thenReturn(resource);

      var newField = mock(Field.class);
      when(fieldPersistenceService.saveFieldFromFieldForm(form, resource)).thenReturn(newField);

      underTest.createNewField(form);

      verify(fieldPersistenceService).saveFieldFromFieldForm(form, resource);
      verify(fieldStatusService, times(1)).setMostRecentFieldStatus(newField, FieldStatus.STATUS_200);
    }
  }

  @Nested
  class WhenGettingResponsibleLegacyTeamTypeDisplayableOptions {

    @Test
    void onlyOffshoreTeamOptions() {
      var result = underTest.getResponsibleTeamTypeDisplayableOptions();
      assertThat(result)
          .containsExactly(
              entry("NORTHERN_NORTH_SEA_WEST_OF_SHETLAND", "Northern North Sea and West of Shetland team"),
              entry("CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA", "Central North Sea, Southern North Sea and East Irish Sea team")
          );
    }
  }

  @Nested
  class WhenGettingGeographicalAreaDisplayableOptions {
    @Test
    void onlyOffshoreOptions() {
      var result = underTest.getGeographicalAreaDisplayableOptions();
      assertThat(result)
          .containsExactly(
              entry("NORTHERN_NORTH_SEA", "Northern North Sea (NNS)"),
              entry("WEST_OF_SHETLAND", "West of Shetland (WoS)"),
              entry("CENTRAL_NORTH_SEA", "Central North Sea (CNS)"),
              entry("SOUTHERN_NORTH_SEA", "Southern North Sea (SNS)"),
              entry("EAST_IRISH_SEA", "East Irish Sea")
          );
    }
  }

  @Nested
  class WhenGettingPreSelectedOrganisationUnit {
    private final String RESPONSIBLE_ORGANISATION_ID = "1";
    @Test
    void emptyId() {
      var result = underTest.getPreSelectedOrganisationUnit("");
      assertThat(result).isEqualTo(Map.of());
    }

    @Test
    void verifyMethodCalls() {
      underTest.getPreSelectedOrganisationUnit(RESPONSIBLE_ORGANISATION_ID);
      verify(organisationUnitQueryService).getPreSelectedOrganisationUnit(Integer.valueOf(RESPONSIBLE_ORGANISATION_ID));
    }
  }

  @Nested
  class WhenGettingPreSelectedParentDeterminedField {
    private final String PARENT_DETERMINED_FIELD_ID = "1";
    private final String CORRECTED_FIELD_NAME = "corrected field name";
    private Field field;

    @BeforeEach
    void setUp() {
      field = new Field();
      field.setCorrectedLegalFieldName(CORRECTED_FIELD_NAME);
    }

    @Test
    void emptyId() {
      var result = underTest.getPreSelectedParentDeterminedField("");
      assertThat(result).isEqualTo(Map.of());
    }

    @Test
    void noField() {
      when(fieldPersistenceService.getField(Integer.valueOf(PARENT_DETERMINED_FIELD_ID))).thenReturn(Optional.empty());
      var result = underTest.getPreSelectedParentDeterminedField(PARENT_DETERMINED_FIELD_ID);
      assertThat(result).isEqualTo(Map.of());
    }

    @Test
    void verifyMethodCalls() {
      when(fieldPersistenceService.getField(Integer.valueOf(PARENT_DETERMINED_FIELD_ID))).thenReturn(Optional.of(field));
      underTest.getPreSelectedParentDeterminedField(PARENT_DETERMINED_FIELD_ID);
      verify(searchSelectorService).buildPrePopulatedSelection(PARENT_DETERMINED_FIELD_ID, field.getCorrectedLegalFieldName());
    }
  }

  @Nested
  class WhenGettingPreSelectedAssociatedDiscoveriesMap {

    @Test
    void emptyIds() {
      var result = underTest.getPreSelectedAssociatedDiscoveriesMap(List.of());
      assertThat(result).isEmpty();
    }

    @Test
    void assertResult() {
      var resourceDetail = new ResourceDetail();
      resourceDetail.setId(UUID.randomUUID());
      var displayName = "dispaly name";

      when(resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(resourceDetail.getId()))
          .thenReturn(resourceDetail);
      when(submissionResourceService.getResourceDisplayName(resourceDetail))
          .thenReturn(displayName);

      assertThat(underTest.getPreSelectedAssociatedDiscoveriesMap(List.of(resourceDetail.getId().toString())))
          .extracting(ResourceView::getId, ResourceView::getName)
          .containsExactly(Tuple.tuple(resourceDetail.getId().toString(), displayName));
    }
  }
}