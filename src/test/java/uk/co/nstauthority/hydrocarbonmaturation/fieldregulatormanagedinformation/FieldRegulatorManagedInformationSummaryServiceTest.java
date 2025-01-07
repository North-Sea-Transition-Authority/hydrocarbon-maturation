package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
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
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldConsultingAuthority;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
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
  @Mock
  private PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  @Mock
  private FieldOperatorQueryService fieldOperatorQueryService;
  @InjectMocks
  private FieldRegulatorManagedInformationSummaryService fieldRegulatorManagedInformationSummaryService;

  private Resource resource;
  private final String RESPONSIBLE_ORGANISATION = "Responsible org";

  @BeforeEach
  void setUp() {
    resource = ResourceTestUtils.buildResource(ResourceType.FIELD);
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_emptyForm_thenEmptySummary() {
    var field = new Field();

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

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
            FieldRegulatorManagedInformationSummaryService.RESPONSIBLE_ORGANISATION_PRE_PEARS,
            FieldRegulatorManagedInformationSummaryService.IS_ON_PRE_PEARS_LICENCE,
            FieldRegulatorManagedInformationSummaryService.ONSHORE_OFFSHORE,
            FieldRegulatorManagedInformationSummaryService.LOCATED_IN_ENGLAND,
            FieldRegulatorManagedInformationSummaryService.DETERMINATION_STATUS,
            FieldRegulatorManagedInformationSummaryService.FIELD_NUMBER,
            FieldRegulatorManagedInformationSummaryService.INFRASTRUCTURE_HOST,
            FieldRegulatorManagedInformationSummaryService.HAS_CONSULTING_AUTHORITIES
        );

    assertThat(summaryData.keyValues())
        .extracting(SummaryKeyValue::value)
        .containsOnlyNulls();
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_populatedForm_thenAssertSummaryData() {
    var field = getCompleteField();

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayById(24))
        .thenReturn(RESPONSIBLE_ORGANISATION);

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
              field.getLegalFieldName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.CORRECTED_LEGAL_FIELD_NAME,
              field.getCorrectedLegalFieldName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.COMMON_NAME_FOR_FIELD,
              field.getCommonNameForField()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.RESPONSIBLE_ORGANISATION_PRE_PEARS,
                RESPONSIBLE_ORGANISATION),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.IS_ON_PRE_PEARS_LICENCE,
                BooleanUtil.yesNoFromBoolean(field.getIsOnPrePearsLicence())),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.ONSHORE_OFFSHORE,
              field.getOnshoreOffshore().getDisplayName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.LOCATED_IN_ENGLAND,
                BooleanUtil.yesNoFromBoolean(field.getFieldLocatedInEngland())),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.DETERMINATION_STATUS,
                field.getDeterminationStatus().getDisplayName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.FIELD_NUMBER,
                String.valueOf(field.getFieldNumber())),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.INFRASTRUCTURE_HOST,
              BooleanUtil.yesNoFromBoolean(field.getInfrastructureHost())),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.HAS_CONSULTING_AUTHORITIES,
                BooleanUtil.yesNoFromBoolean(field.getHasConsultingAuthorities()))
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_offshore_assertAreaAndTeam() {
    var field = getCompleteField();
    field.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    field.setFieldLocatedInUkcs(false);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

    var managedInfo = new RegulatorManagedInformation();
    managedInfo.setGeographicalArea(GeographicalArea.NORTHERN_NORTH_SEA);
    managedInfo.setResponsibleTeamType(ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND);

    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
        .thenReturn(managedInfo);

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
                BooleanUtil.yesNoFromBoolean(field.getFieldLocatedInUkcs())),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.GEOGRAPHICAL_AREA,
                managedInfo.getGeographicalArea().getDisplayName()),
            Tuple.tuple(FieldRegulatorManagedInformationSummaryService.RESPONSIBLE_TEAM_TYPE,
                managedInfo.getResponsibleTeamType().getDisplayName())
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_subAreaOfDeterminedField_thenShowParentField() {
    var parentFieldName = "Parent field name";
    var parentField = new Field();
    parentField.setCorrectedLegalFieldName(parentFieldName);
    parentField.setFieldNumber(1);

    var field = getCompleteField();
    field.setDeterminationStatus(DeterminationStatus.SUB_AREA_OF_DETERMINED_FIELD);
    field.setParentDeterminedField(parentField);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

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
            ),
            Tuple.tuple(
                FieldRegulatorManagedInformationSummaryService.FIELD_NUMBER,
                String.valueOf(parentField.getFieldNumber())
            )
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_hasConsultingAuthorities() {
    var field = getCompleteField();
    field.setHasConsultingAuthorities(true);
    var consultingAuthority = FieldConsultingAuthority.ENGLISH_AUTHORITIES;
    field.setConsultingAuthority(consultingAuthority);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

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
                FieldRegulatorManagedInformationSummaryService.HAS_CONSULTING_AUTHORITIES,
                BooleanUtil.yesNoFromBoolean(field.getHasConsultingAuthorities())
            ),
            Tuple.tuple(
                FieldRegulatorManagedInformationSummaryService.CONSULTING_AUTHORITIES,
                field.getConsultingAuthority().getDisplayName()
            )
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_fieldOnLicence_thenShowLicences() {
    var field = getCompleteField();
    field.setIsOnPrePearsLicence(true);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

    var licenceView1 = new LicenceView("ref1", 1, LicenceStatus.EXTANT);
    var licenceView2 = new LicenceView("ref2", 2, LicenceStatus.EXTANT);
    var mostCurrentLicences = List.of(licenceView1, licenceView2);

    when(fieldLicenceService.getFieldLicencesViewsIgnoreStatus(field)).thenReturn(mostCurrentLicences);

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
              FieldRegulatorManagedInformationSummaryService.PRE_PEARS_LICENCES,
                "ref1, ref2"
            )
        );
  }

  @Test
  void getFieldRegulatorManagedInformationSummaryCard_fieldHasPearLicences_thenShowPearsLicences() {
    var field = getCompleteField();
    field.setIsOnPrePearsLicence(false);

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

    var pearsLicenceList = List.of(
        new LicenceView("ref1", 1, LicenceStatus.EXTANT),
        new LicenceView("ref2", 2, LicenceStatus.EXTANT)
    );

    when(fieldLicenceService.getPearsLicenceList(field)).thenReturn(pearsLicenceList);
    when(fieldLicenceService.filterExtantLicences(pearsLicenceList)).thenReturn(pearsLicenceList);
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
  void getFieldRegulatorManagedInformationSummaryCard_fieldHasOperator_thenShowFieldOperator() {
    var field = getCompleteField();

    when(fieldPersistenceService.getFieldOrThrow(resource)).thenReturn(field);

    var orgUnit = new OrganisationUnit();
    orgUnit.setName("Org name");

    when(fieldOperatorQueryService.getFieldOperatorByFieldIdentifier(field.getFieldIdentifier())).thenReturn(Optional.of(orgUnit));

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
                FieldRegulatorManagedInformationSummaryService.RESPONSIBLE_ORGANISATION,
                orgUnit.getName()
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

  @Test
  void getLinkedPostFieldDiscoveriesMap() {
    var discovery1 = new Resource();
    discovery1.setId(UUID.randomUUID());
    var discovery2 = new Resource();
    discovery2.setId(UUID.randomUUID());

    var submission1 = new Submission();
    submission1.setResource(discovery1);
    var submission2 = new Submission();
    submission2.setResource(discovery2);

    var submissionDetail1 = new SubmissionDetail();
    submissionDetail1.setSubmission(submission1);
    var submissionDetail2 = new SubmissionDetail();
    submissionDetail2.setSubmission(submission2);

    var resourceDetail1 = new ResourceDetail();
    resourceDetail1.setSubmissionDetail(submissionDetail1);
    resourceDetail1.setResourceName("Name1");
    var resourceDetail2 = new ResourceDetail();
    resourceDetail2.setSubmissionDetail(submissionDetail2);
    resourceDetail2.setResourceName("Name2");

    when(postFieldDiscoveryLinkService.getAllDiscoveriesLinkedToField(resource)).thenReturn(List.of(discovery1, discovery2));
    when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(discovery1)).thenReturn(Optional.of(submissionDetail1));
    when(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(discovery2)).thenReturn(Optional.of(submissionDetail2));
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail1)).thenReturn(resourceDetail1);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail2)).thenReturn(resourceDetail2);

    var returnedMap = fieldRegulatorManagedInformationSummaryService.getLinkedPostFieldDiscoveriesMap(resource);

    assertThat(returnedMap.entrySet()).containsExactly(
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(discovery1.getId(),null)),
            resourceDetail1.getResourceName()
        ),
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(discovery2.getId(), null)),
            resourceDetail2.getResourceName()
        )
    );
  }

  @Test
  void getAssociatedDiscoveriesMap() {
    var discovery1 = new Resource();
    discovery1.setId(UUID.randomUUID());
    var discovery2 = new Resource();
    discovery2.setId(UUID.randomUUID());

    var submission1 = new Submission();
    submission1.setResource(discovery1);
    var submission2 = new Submission();
    submission2.setResource(discovery2);

    var submissionDetail1 = new SubmissionDetail();
    submissionDetail1.setSubmission(submission1);
    var submissionDetail2 = new SubmissionDetail();
    submissionDetail2.setSubmission(submission2);

    var resourceDetail1 = new ResourceDetail();
    resourceDetail1.setSubmissionDetail(submissionDetail1);
    resourceDetail1.setResourceName("Name1");
    var resourceDetail2 = new ResourceDetail();
    resourceDetail2.setSubmissionDetail(submissionDetail2);
    resourceDetail2.setResourceName("Name2");

    var associatedDiscoveryDetails = List.of(resourceDetail1, resourceDetail2);

    when(fieldRegulatorManagedInformationService.getAssociatedDiscoveryResourceDetails(resource))
        .thenReturn(associatedDiscoveryDetails);

    assertThat(fieldRegulatorManagedInformationSummaryService.getAssociatedDiscoveriesMap(resource)).containsExactly(
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(discovery1.getId(),null)),
            resourceDetail1.getResourceName()
        ),
        entry(
            ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(discovery2.getId(), null)),
            resourceDetail2.getResourceName()
        )
    );
  }

  @NotNull
  private Field getCompleteField() {
    var field = new Field();
    field.setLegalFieldName("legal name");
    field.setCorrectedLegalFieldName("corrected name");
    field.setCommonNameForField("common name");
    field.setPrePearsOrganisationId(24);
    field.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    field.setFieldLocatedInEngland(true);
    field.setDeterminationStatus(DeterminationStatus.DETERMINED);
    field.setFieldNumber(1);
    field.setInfrastructureHost(true);
    field.setIsOnPrePearsLicence(false);
    field.setHasConsultingAuthorities(false);
    return field;
  }
}