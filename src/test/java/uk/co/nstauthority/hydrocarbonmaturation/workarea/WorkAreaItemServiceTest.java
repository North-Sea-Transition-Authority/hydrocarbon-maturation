package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import io.micrometer.core.instrument.Timer;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.fivium.energyportalapi.generated.types.OrganisationUnit;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorAndLicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.MetricsProvider;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.FieldDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@ExtendWith(MockitoExtension.class)
class WorkAreaItemServiceTest {
  private final static boolean UPDATE_REQUESTED = true;
  @Mock
  private SubmissionWorkAreaServiceFactory submissionWorkAreaServiceFactory;
  @Mock
  private OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;
  @Mock
  private ResponsibleTeamSubmissionWorkAreaQueryService responsibleTeamSubmissionWorkAreaQueryService;
  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;
  @Mock
  private SubmissionDetailService submissionDetailService;
  @Mock
  private FieldDisplayService fieldDisplayService;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @Mock
  private LicenceQueryService licenceQueryService;
  @Mock
  private MetricsProvider metricsProvider;
  @Mock
  private FieldOperatorAndLicenceQueryService fieldOperatorAndLicenceQueryService;
  @Mock
  private IndustryTeamService industryTeamService;
  @Mock
  private InternalTeamService internalTeamService;
  @InjectMocks
  private WorkAreaItemService workAreaItemService;

  private List<SubmissionWorkAreaQueryResult> queryResults;
  private SubmissionWorkAreaQueryResult queryResultWithEverythingFilledIn;
  private SubmissionWorkAreaQueryResult queryResultWithoutLicencesAndBlock;
  private Timer timer;

  private static final Integer LICENCE_ID_1 = 1;
  private static final Integer LICENCE_ID_2 = 2;
  private static final Integer ORGANISATION_UNIT_1 = 1;
  private static final Integer ORGANISATION_UNIT_2 = 2;
  private static final String ORGANISATION_NAME_1 = "OneOrg";
  private static final String ORGANISATION_NAME_2 = "organisation";
  private static final User USER = UserTestUtil.Builder().build();
  private static final Map<Integer, OrganisationUnitDto> ORGANISATION_UNIT_MAP = Map.of(
      ORGANISATION_UNIT_1, new OrganisationUnitDto(ORGANISATION_UNIT_1, ORGANISATION_NAME_1, "123", Collections.emptyList()),
      ORGANISATION_UNIT_2, new OrganisationUnitDto(ORGANISATION_UNIT_2, ORGANISATION_NAME_2, "456", Collections.emptyList())
  );
  private static final Map<Integer, LicenceDto> LICENCE_MAP = Map.of(
      1, new LicenceDto("123", LICENCE_ID_1, LicenceStatus.EXTANT),
      2, new LicenceDto("456", LICENCE_ID_2, LicenceStatus.EXTANT)
  );

  @BeforeEach
  void setUp() {
    queryResultWithEverythingFilledIn = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withOrganisationResponsibleId(ORGANISATION_UNIT_1)
        .withLicencesTheResourceIsOn(List.of(LICENCE_ID_1, LICENCE_ID_2))
        .build();
    queryResultWithoutLicencesAndBlock = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withResourceName("other resource")
        .withSubmissionType("PROSPECT_UPDATE")
        .withCentreOfInterestQuadBlock(null)
        .withOrganisationResponsibleId(ORGANISATION_UNIT_2)
        .withLicencesTheResourceIsOn(null)
        .withUpdateRequested(UPDATE_REQUESTED)
        .build();
    queryResults = List.of(queryResultWithEverythingFilledIn, queryResultWithoutLicencesAndBlock);

    when(organisationUnitQueryService.getOrganisationUnitMapByIds(anyList())).thenReturn(ORGANISATION_UNIT_MAP);
    when(licenceQueryService.getLicenceMapByIds(anyList())).thenReturn(LICENCE_MAP);

    timer = mock(Timer.class);
    when(metricsProvider.getWorkAreaQueryTimer()).thenReturn(timer);
  }

  @Test
  void getWorkAreaItems_twoItemsReturned_whenLicencesNotFound_oneWithQuadBlocks() {
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(queryResults);
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, ORGANISATION_UNIT_1))
        .thenReturn(ORGANISATION_NAME_1);
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, ORGANISATION_UNIT_2))
        .thenReturn(ORGANISATION_NAME_2);
    when(submissionDetailDisplayService.getLicencesForDisplayFromMap(eq(LICENCE_MAP), anyList()))
        .thenReturn(List.of());

    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("New prospect");
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.PROSPECT_UPDATE))
        .thenReturn("Prospect update");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT)))
        .thenReturn(List.of(ORGANISATION_UNIT_1, ORGANISATION_UNIT_2));

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            tuple(
                WorkAreaItemType.SUBMISSION,
                "resource name (25/5)",
                "New prospect",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId(), null))),
            tuple(
                WorkAreaItemType.SUBMISSION,
                "other resource",
                "Prospect update",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithoutLicencesAndBlock.getSubmissionDetailId(), null))
            )
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", ORGANISATION_NAME_1),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));

    assertThat(result.get(1).modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", ORGANISATION_NAME_2),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", UPDATE_REQUESTED));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationAndLicencesFound() {

    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("New prospect");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultWithEverythingFilledIn));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(
        ORGANISATION_UNIT_MAP,
        queryResultWithEverythingFilledIn.getProspectOrDiscoveryOrganisationResponsibleId())
    ).thenReturn("organisation (1)");
    var licence1 = new LicenceDto("licence1", LICENCE_ID_1, LicenceStatus.EXTANT);
    var licence2 = new LicenceDto("licence2", LICENCE_ID_2, LicenceStatus.EXTANT);
    when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, List.of(LICENCE_ID_1, LICENCE_ID_2)))
        .thenReturn(List.of(licence1.getName(), licence2.getName()));
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT)))
        .thenReturn(List.of(ORGANISATION_UNIT_1, ORGANISATION_UNIT_2));

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result).hasSize(1);

    assertThat(result.getFirst())
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            WorkAreaItemType.SUBMISSION,
            "resource name (25/5)",
            "New prospect",
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId(), null)));

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", "organisation (1)"),
            entry("licences", List.of(licence1.getName(), licence2.getName())),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenField() {
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_CSR))
        .thenReturn(SubmissionType.CREATE_CSR.getDisplayName());
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    var submission = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withResourceType(ResourceType.FIELD)
        .withSubmissionType(SubmissionType.CREATE_CSR.name())
        .withFieldStatus(FieldStatus.STATUS_700)
        .build();
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(submission));
    var organisationUnit = new OrganisationUnit();
    organisationUnit.setName("organisation (1)");
    organisationUnit.setOrganisationUnitId(ORGANISATION_UNIT_1);
    Map<Integer, OrganisationUnit> fieldOrgMap = Map.of(submission.getFieldIdentifier(), organisationUnit);
    Map<Integer, OrganisationUnitDto> fieldOrgDtoMap = fieldOrgMap.entrySet().stream()
        .collect(
            StreamUtil.toLinkedHashMap(
                Map.Entry::getKey,
                e -> OrganisationUnitDto.fromOrganisationUnit(e.getValue())
            )
        );
    when(fieldOperatorAndLicenceQueryService.getFieldOperatorIdMap(List.of(submission.getFieldIdentifier())))
        .thenReturn(fieldOrgMap);
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(fieldOrgDtoMap.get(submission.getFieldIdentifier()))
    ).thenReturn("organisation (1)");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT)))
        .thenReturn(List.of(ORGANISATION_UNIT_1, ORGANISATION_UNIT_2));

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result).hasSize(1);

    assertThat(result.getFirst())
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            WorkAreaItemType.SUBMISSION,
            "resource name (25/5)",
            submission.getSubmissionType().getDisplayName(),
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(submission.getSubmissionDetailId(), null)));

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", "organisation (1)"),
            entry("submissionStatus", "Draft"),
            entry("fieldStatus", FieldStatus.STATUS_700.getDisplayName()),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenAllButMandatoryNotProvided() {
    var providedName = "resource name";
    var centreOfInterest = "1/1";
    var licenceApplicationReference = "RT9";
    var submissionWorkAreaQueryResult = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withCentreOfInterestQuadBlock(centreOfInterest)
        .withLicencesTheResourceIsOn(null)
        .withOrganisationResponsibleId(ORGANISATION_UNIT_1)
        .withResourceName(providedName)
        .withLicenceApplicationReference(licenceApplicationReference)
        .build();

    var expectedHeadingText = "resource name (1/1) (Round RT9)";

    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("New prospect");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(submissionWorkAreaQueryResult));

    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, ORGANISATION_UNIT_1))
        .thenReturn(ORGANISATION_NAME_1);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT)))
        .thenReturn(List.of(ORGANISATION_UNIT_1, ORGANISATION_UNIT_2));

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result).hasSize(1);

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            tuple(
                WorkAreaItemType.SUBMISSION,
                expectedHeadingText,
                "New prospect",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(submissionWorkAreaQueryResult.getSubmissionDetailId(), null)))
        );

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("isFieldResource", false),
            entry("organisationResponsible", ORGANISATION_NAME_1),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationWithoutNameAndNumberFound() {
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("New prospect");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER)).thenReturn(List.of(
        queryResultWithEverythingFilledIn));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, queryResultWithEverythingFilledIn.getProspectOrDiscoveryOrganisationResponsibleId()))
        .thenReturn("Not provided");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            tuple(
                WorkAreaItemType.SUBMISSION,
                "resource name (25/5)",
                "New prospect",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId(), null)))
        );

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("isFieldResource", false),
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationWithName_assertNameWithoutNUmber() {
    var queryResultOnlyOrganisationName = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withOrganisationResponsibleId(ORGANISATION_UNIT_2)
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultOnlyOrganisationName));
    var organisationName = "organisation name";
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP,
        ORGANISATION_UNIT_2))
        .thenReturn(organisationName);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("isFieldResource", false),
            entry("organisationResponsible", organisationName),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }


  @Test
  void getWorkAreaItems_oneItemReturned_whenLicencesEmptyList_assertEmptyList() {
    var queryResultEmptyLicences = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withOrganisationResponsibleId(ORGANISATION_UNIT_1)
        .withLicencesTheResourceIsOn(List.of())
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultEmptyLicences));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, queryResultWithEverythingFilledIn.getProspectOrDiscoveryOrganisationResponsibleId()))
        .thenReturn("Not provided");
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("isFieldResource", false),
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_oneProspectItemReturned_whenSubmitted_assertReviewActionUrlAndOtherValues() {
    var queryResultInSubmitted = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withStatus(SubmissionDetailStatus.SUBMITTED.name())
        .build();
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("New prospect");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultInSubmitted));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(eq(ORGANISATION_UNIT_MAP), any()))
        .thenReturn("Not provided");
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    when(submissionDetailService.getSubmissionDetailForIdOrError(queryResultInSubmitted.getSubmissionDetailId()))
        .thenReturn(submissionDetail);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            tuple(
                WorkAreaItemType.SUBMISSION,
                "resource name (25/5)",
                "New prospect",
                ReverseRouter.route(on(ReviewSubmissionController.class)
                    .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null)))
        );

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("isFieldResource", false),
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Submitted"),
            entry("updateRequested", false));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT", "SUBMITTED"})
  void getWorkAreaItemsOneItemReturned_whenValidStatus_assertCorrectUrl(SubmissionDetailStatus status) {
    var queryResult = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withStatus(status.name())
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResult));

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    if(status == SubmissionDetailStatus.SUBMITTED) {
      when(submissionDetailService.getSubmissionDetailForIdOrError(queryResult.getSubmissionDetailId()))
          .thenReturn(submissionDetail);
    }
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var result = workAreaItemService.getWorkAreaItems(USER);

    if (status == SubmissionDetailStatus.DRAFT) {
      assertThat(result.getFirst().actionUrl())
          .isEqualTo(ReverseRouter.route(on(TaskListController.class)
              .getTaskList(queryResult.getSubmissionDetailId(), null)));
    } else {
      assertThat(result.getFirst().actionUrl())
          .isEqualTo(ReverseRouter.route(on(ReviewSubmissionController.class)
              .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null)));
    }
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getWorkAreaItemsOneItemReturned_whenFieldSubmission_isSubmitted_assertFieldContextInformation(SubmissionType submissionType) {
    var queryResultInSubmitted = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withStatus(SubmissionDetailStatus.ACCEPTED.name())
        .withSubmissionType(submissionType.getEnumName())
        .build();
    var submissionTypeName ="submission type";
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionType))
        .thenReturn(submissionTypeName);
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultInSubmitted));
    var responsibleOrganisation = "Not provided";
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(eq(ORGANISATION_UNIT_MAP), anyInt()))
        .thenReturn(responsibleOrganisation);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    when(submissionDetailService.getSubmissionDetailForIdOrError(queryResultInSubmitted.getSubmissionDetailId()))
        .thenReturn(submissionDetail);
    var parentFieldName = "parent";
    when(fieldDisplayService.getParentFieldNameOrNoneFound(queryResultInSubmitted.getParentFieldId()))
        .thenReturn(parentFieldName);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var expectedHeading = queryResultInSubmitted.getResourceName() + " (" + queryResultInSubmitted.getCentreOfInterestQuadBlock() +")";
    var result = workAreaItemService.getWorkAreaItems(USER);
    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            tuple(
                WorkAreaItemType.SUBMISSION,
                expectedHeading,
                submissionTypeName,
                ReverseRouter.route(on(ReviewSubmissionController.class)
                    .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null)))
        );

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("isFieldResource", true),
            entry("organisationResponsible", responsibleOrganisation),
            entry("parentFieldName", parentFieldName),
            entry("fieldStatus", queryResultInSubmitted.getFieldStatus().getDisplayName()));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getWorkAreaItemsOneItemReturned_whenFieldSubmission_isAccepted_assertFieldContextInformation(SubmissionType submissionType) {
    var queryResultInSubmitted = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withStatus(SubmissionDetailStatus.SUBMITTED.name())
        .withSubmissionType(submissionType.getEnumName())
        .build();
    var submissionTypeName ="submission type";
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionType))
        .thenReturn(submissionTypeName);
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultInSubmitted));
    var responsibleOrganisation = "Not provided";
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(eq(ORGANISATION_UNIT_MAP), anyInt()))
        .thenReturn(responsibleOrganisation);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    when(submissionDetailService.getSubmissionDetailForIdOrError(queryResultInSubmitted.getSubmissionDetailId()))
        .thenReturn(submissionDetail);
    var parentFieldName = "parent";
    when(fieldDisplayService.getParentFieldNameOrNoneFound(queryResultInSubmitted.getParentFieldId()))
        .thenReturn(parentFieldName);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var expectedHeading = queryResultInSubmitted.getResourceName() + " (" + queryResultInSubmitted.getCentreOfInterestQuadBlock() +")";
    var result = workAreaItemService.getWorkAreaItems(USER);
    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            tuple(
                WorkAreaItemType.SUBMISSION,
                expectedHeading,
                submissionTypeName,
                ReverseRouter.route(on(ReviewSubmissionController.class)
                    .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null)))
        );

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("isFieldResource", true),
            entry("organisationResponsible", responsibleOrganisation),
            entry("parentFieldName", parentFieldName),
            entry("fieldStatus", queryResultInSubmitted.getFieldStatus().getDisplayName()));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"})
  void getWorkAreaItemsOneItemReturned_whenFieldSubmission_hasPearsFieldOperator_assertFieldContextInformation(SubmissionType submissionType) {
    var queryResultInSubmitted = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withStatus(SubmissionDetailStatus.SUBMITTED.name())
        .withSubmissionType(submissionType.getEnumName())
        .build();
    var submissionTypeName ="submission type";
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionType))
        .thenReturn(submissionTypeName);
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultInSubmitted));

    var orgUnit = new OrganisationUnit();
    when(fieldOperatorAndLicenceQueryService.getFieldOperatorIdMap(List.of(queryResultInSubmitted.getFieldIdentifier())))
        .thenReturn(Map.of(queryResultInSubmitted.getFieldIdentifier(), orgUnit));

    var responsibleOrganisation = "Name of organisation";
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(OrganisationUnitDto.fromOrganisationUnit(orgUnit)))
        .thenReturn(responsibleOrganisation);
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    when(submissionDetailService.getSubmissionDetailForIdOrError(queryResultInSubmitted.getSubmissionDetailId()))
        .thenReturn(submissionDetail);
    var parentFieldName = "parent";
    when(fieldDisplayService.getParentFieldNameOrNoneFound(queryResultInSubmitted.getParentFieldId()))
        .thenReturn(parentFieldName);
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(true);
    var expectedHeading = queryResultInSubmitted.getResourceName() + " (" + queryResultInSubmitted.getCentreOfInterestQuadBlock() +")";
    var result = workAreaItemService.getWorkAreaItems(USER);
    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            tuple(
                WorkAreaItemType.SUBMISSION,
                expectedHeading,
                submissionTypeName,
                ReverseRouter.route(on(ReviewSubmissionController.class)
                    .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null)))
        );

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("isFieldResource", true),
            entry("organisationResponsible", responsibleOrganisation),
            entry("parentFieldName", parentFieldName),
            entry("fieldStatus", queryResultInSubmitted.getFieldStatus().getDisplayName()));
  }

  @Test
  void getWorkAreaItems_whenCorrection() {
    var workAreaItem = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withOrganisationResponsibleId(ORGANISATION_UNIT_1)
        .withCorrection(true)
        .build();
    var queryResults = List.of(workAreaItem);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("New prospect");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(queryResults);
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(eq(ORGANISATION_UNIT_MAP), anyInt()))
        .thenReturn("Not provided");
    when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, List.of(LICENCE_ID_1, LICENCE_ID_2)))
        .thenReturn(List.of());
    when(internalTeamService.isRegulatorOrAreaTeamUser(USER)).thenReturn(false);
    when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT)))
        .thenReturn(List.of(ORGANISATION_UNIT_1, ORGANISATION_UNIT_2));

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            tuple(
                WorkAreaItemType.SUBMISSION,
                "resource name (25/5)",
                "New prospect - correction",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(workAreaItem.getSubmissionDetailId(), null))
            )
        );

    assertThat(result.getFirst().modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_assertTimed() {
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of());

    workAreaItemService.getWorkAreaItems(USER);

    verify(timer).record(any(Long.class), eq(TimeUnit.MILLISECONDS));
  }
}