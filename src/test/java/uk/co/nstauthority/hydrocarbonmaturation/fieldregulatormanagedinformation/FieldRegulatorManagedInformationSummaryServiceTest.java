package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.assertj.core.groups.Tuple;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@ExtendWith(MockitoExtension.class)
class FieldRegulatorManagedInformationSummaryServiceTest {
  @Mock
  private FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;
  @Mock
  private FieldPersistenceService fieldPersistenceService;
  @Mock
  private FieldLicenceService fieldLicenceService;
  @InjectMocks
  private FieldRegulatorManagedInformationSummaryService fieldRegulatorManagedInformationSummaryService;

  private Resource resource;
  private final String ASSOCIATED_DISCOVERIES = "Discovery 1, Discovery 2";
  private final String RESPONSIBLE_ORGANISATION = "Responsible org";

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.FIELD);
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_emptyForm_thenEmptySummary() {

    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(new FieldForm());

    var card = fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource);

    assertThat(card)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).contains(
            FieldRegulatorManagedInformationSummaryService.REGULATOR_MANAGED_INFORMATION,
            SummaryCardType.SIMPLE_SUMMARY
        );

    var summaryData = (SummaryDataView) card.summaryData();

    assertThat(summaryData.keyValues())
        .extracting(SummaryKeyValue::key)
        .containsExactly(
            FieldRegulatorManagedInformationSummaryService.LEGAL_FIELD_NAME,
            FieldRegulatorManagedInformationSummaryService.CORRECTED_LEGAL_FIELD_NAME,
            FieldRegulatorManagedInformationSummaryService.COMMON_NAME_FOR_FIELD,
            FieldRegulatorManagedInformationSummaryService.ASSOCIATED_DISCOVERIES,
            FieldRegulatorManagedInformationSummaryService.RESPONSIBLE_ORGANISATION,
            FieldRegulatorManagedInformationSummaryService.IS_ON_LICENCE,
            FieldRegulatorManagedInformationSummaryService.ONSHORE_OFFSHORE,
            FieldRegulatorManagedInformationSummaryService.LOCATED_IN_ENGLAND,
            FieldRegulatorManagedInformationSummaryService.DETERMINATION_STATUS,
            FieldRegulatorManagedInformationSummaryService.FIELD_NUMBER,
            FieldRegulatorManagedInformationSummaryService.INFRASTRUCTURE_HOST
        );

    assertThat(summaryData.keyValues())
        .extracting(SummaryKeyValue::value)
        .containsOnlyNulls();
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_populatedForm_thenAssertSummaryData() {
    var form = getFieldFormWithMockedAssociatedDiscoveries();

    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(form);

    var card = fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource);

    assertThat(card)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).contains(
            FieldRegulatorManagedInformationSummaryService.REGULATOR_MANAGED_INFORMATION,
            SummaryCardType.SIMPLE_SUMMARY
        );

    var summaryData = (SummaryDataView) card.summaryData();

    assertThat(summaryData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value)
        .containsExactly(
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.LEGAL_FIELD_NAME,
              form.getLegalFieldName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.CORRECTED_LEGAL_FIELD_NAME,
              form.getCorrectedLegalFieldName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.COMMON_NAME_FOR_FIELD,
              form.getCommonNameForField()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.ASSOCIATED_DISCOVERIES,
                ASSOCIATED_DISCOVERIES),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.RESPONSIBLE_ORGANISATION,
                RESPONSIBLE_ORGANISATION),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.IS_ON_LICENCE,
                BooleanUtil.yesNoFromBoolean(form.getIsOnLicence())),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.ONSHORE_OFFSHORE,
              form.getOnshoreOffshore().getDisplayName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.LOCATED_IN_ENGLAND,
                BooleanUtil.yesNoFromBoolean(form.getFieldLocatedInEngland())),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.DETERMINATION_STATUS,
                form.getDeterminationStatus().getDisplayName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.FIELD_NUMBER,
                form.getFieldNumber().getDecimalInputValue()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.INFRASTRUCTURE_HOST,
              BooleanUtil.yesNoFromBoolean(form.getInfrastructureHost()))
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_offshore_assertAreaAndTeam() {
    var form = getFieldFormWithMockedAssociatedDiscoveries();
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setFieldLocatedInUkcs(false);
    form.setGeographicalArea(GeographicalArea.NORTHERN_NORTH_SEA);
    form.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);

    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(form);

    var card = fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource);

    var summaryData = (SummaryDataView) card.summaryData();

    assertThat(summaryData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value)
        .contains(
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.ONSHORE_OFFSHORE,
                OnshoreOffshore.OFFSHORE.getDisplayName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.LOCATED_IN_UKCS,
                BooleanUtil.yesNoFromBoolean(form.getFieldLocatedInUkcs())),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.GEOGRAPHICAL_AREA,
                form.getGeographicalArea().getDisplayName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.RESPONSIBLE_TEAM_TYPE,
                form.getResponsibleTeamType().getDisplayName())
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_subAreaOfDeterminedField_thenShowParentField() {
    var form = getFieldFormWithMockedAssociatedDiscoveries();
    form.setDeterminationStatus(DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD);
    var parentFieldId = 8000;
    form.setParentDeterminedField(String.valueOf(parentFieldId));

    var parentFieldName = "Parent field name";
    var parentField = new Field();
    parentField.setCorrectedLegalFieldName(parentFieldName);
    when(fieldPersistenceService.getFieldOrThrow(parentFieldId))
        .thenReturn(parentField);

    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(form);

    var card = fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource);

    assertThat(card)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).contains(
            FieldRegulatorManagedInformationSummaryService.REGULATOR_MANAGED_INFORMATION,
            SummaryCardType.SIMPLE_SUMMARY
        );

    var summaryData = (SummaryDataView) card.summaryData();

    assertThat(summaryData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value)
        .contains(
            Tuple.tuple(
                 FieldRegulatorManagedInformationSummaryService.DETERMINATION_STATUS,
                 DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD.getDisplayName()
            ),
            Tuple.tuple(
                FieldRegulatorManagedInformationSummaryService.PARENT_DETERMINED_FIELD,
                parentFieldName
            )
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_fieldOnLicence_thenShowLicences() {
    var form = getFieldFormWithMockedAssociatedDiscoveries();
    form.setIsOnLicence(true);

    var field = FieldTestUtils.createField(8000);

    when(fieldPersistenceService.getFieldOrThrow(resource))
        .thenReturn(field);

    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(form);

    List<LicenceView> pearsLicenceList = List.of();
    var licenceView1 = new LicenceView("ref1", 1, LicenceStatus.EXTANT);
    var licenceView2 = new LicenceView("ref2", 2, LicenceStatus.EXTANT);
    var mostCurrentLicences = List.of(licenceView1, licenceView2);

    when(fieldLicenceService.getPearsLicenceList(field)).thenReturn(pearsLicenceList);
    when(fieldLicenceService.getMostCurrentLicencesForField(pearsLicenceList, field)).thenReturn(mostCurrentLicences);

    var card = fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource);

    assertThat(card)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).contains(
            FieldRegulatorManagedInformationSummaryService.REGULATOR_MANAGED_INFORMATION,
            SummaryCardType.SIMPLE_SUMMARY
        );

    var summaryData = (SummaryDataView) card.summaryData();

    assertThat(summaryData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value)
        .contains(
            Tuple.tuple(
              FieldRegulatorManagedInformationSummaryService.LICENCES_TENTATIVE,
                "ref1, ref2"
            )
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_fieldHasPearLicences_thenShowPearsLicences() {
    var form = getFieldFormWithMockedAssociatedDiscoveries();
    form.setIsOnLicence(true);

    var field = FieldTestUtils.createField(8000);

    when(fieldPersistenceService.getFieldOrThrow(resource))
        .thenReturn(field);

    when(fieldRegulatorManagedInformationService.resourceToFieldForm(resource))
        .thenReturn(form);

    var pearsLicenceList = List.of(
        new LicenceView("ref1", 1, LicenceStatus.EXTANT),
        new LicenceView("ref2", 2, LicenceStatus.EXTANT)
    );

    when(fieldLicenceService.getPearsLicenceList(field)).thenReturn(pearsLicenceList);
    when(fieldLicenceService.getMostCurrentLicencesForField(pearsLicenceList, field)).thenReturn(pearsLicenceList);
    when(fieldLicenceService.fieldHasPearsLicences(pearsLicenceList)).thenReturn(true);

    var card = fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource);

    assertThat(card)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryCardType
        ).contains(
            FieldRegulatorManagedInformationSummaryService.REGULATOR_MANAGED_INFORMATION,
            SummaryCardType.SIMPLE_SUMMARY
        );

    var summaryData = (SummaryDataView) card.summaryData();

    assertThat(summaryData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value)
        .contains(
            Tuple.tuple(
              FieldRegulatorManagedInformationSummaryService.LICENCES,
                "ref1, ref2"
            )
        );
  }


  @Test
  void getLicenceDisplayNames_EmptyList() {
    var result = fieldRegulatorManagedInformationSummaryService.getLicenceDisplayNames(List.of());
    assertThat(result).isEmpty();
  }

  @Test
  void getLicenceDisplayNames_MultipleElements() {
    var licenceList = List.of(
        new LicenceView("license1", 1, LicenceStatus.EXTANT),
        new LicenceView("license2", 2, LicenceStatus.EXTANT)
    );
    var result = fieldRegulatorManagedInformationSummaryService.getLicenceDisplayNames(licenceList);
    assertThat(result).containsExactly("license1", "license2");
  }

  @Test
  void getLicenceDisplayNames_shouldReturnLicencesSorted() {

    var result = fieldRegulatorManagedInformationSummaryService.getLicenceDisplayNames(List.of(
        new LicenceView("licenceRef100", 2, LicenceStatus.EXTANT),
        new LicenceView("licenceRef2", 3, LicenceStatus.EXTANT),
        new LicenceView("licenceRef1", 1, LicenceStatus.EXTANT)
    ));

    assertThat(result)
        .containsExactly(
            "licenceRef1",
            "licenceRef2",
            "licenceRef100"
        );
  }

  @NotNull
  private FieldForm getFieldFormWithMockedAssociatedDiscoveries() {
    var discovery1 = UUID.randomUUID();
    var resourceDetail1 = new ResourceDetail();
    resourceDetail1.setResourceName("Discovery 1");

    var discovery2 = UUID.randomUUID();
    var resourceDetail2 = new ResourceDetail();
    resourceDetail2.setResourceName("Discovery 2");

    var associatedDiscoveriesIds = List.of(discovery1.toString(), discovery2.toString());
    when(resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(discovery1))
        .thenReturn(resourceDetail1);
    when(resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(discovery2))
        .thenReturn(resourceDetail2);

    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayById(24))
        .thenReturn(RESPONSIBLE_ORGANISATION);

    var form = new FieldForm();
    form.setLegalFieldName("legal name");
    form.setCorrectedLegalFieldName("corrected name");
    form.setCommonNameForField("common name");
    form.setAssociatedDiscoveries(associatedDiscoveriesIds);
    form.setResponsibleOrganisationId("24");
    form.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    form.setFieldLocatedInEngland(true);
    form.setDeterminationStatus(DeterminationStatus.DETERMINED);
    form.setFieldNumber(DecimalInput.createDecimalInput(1));
    form.setInfrastructureHost(true);
    form.getIsOnLicence();
    return form;
  }
}