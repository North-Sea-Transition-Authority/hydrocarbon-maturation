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
import java.util.List;
import java.util.Map;
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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
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
  @InjectMocks
  private WorkAreaItemService workAreaItemService;

  private List<SubmissionWorkAreaQueryResult> queryResults;
  private SubmissionWorkAreaQueryResult queryResultWithEverythingFilledIn;
  private SubmissionWorkAreaQueryResult queryResultWithoutLicencesAndBlock;
  private Timer timer;

  private final Integer LICENCE_ID_1 = 1;
  private final Integer LICENCE_ID_2 = 2;
  private final Integer ORGANISATION_ID = 5;
  private final User USER = UserTestUtil.Builder().build();
  private final Map<Integer, OrganisationUnitDto> ORGANISATION_UNIT_MAP = Map.of(
      1, new OrganisationUnitDto(1, "OneOrg", "123"),
      ORGANISATION_ID, new OrganisationUnitDto(ORGANISATION_ID, "organisation", "456")
  );
  private final Map<Integer, LicenceView> LICENCE_MAP = Map.of(
      1, new LicenceView("123", LICENCE_ID_1, LicenceStatus.EXTANT),
      2, new LicenceView("456", LICENCE_ID_2, LicenceStatus.EXTANT)
  );

  @BeforeEach
  void setUp() {
    queryResultWithEverythingFilledIn = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withLicencesTheResourceIsOn(List.of(LICENCE_ID_1, LICENCE_ID_2))
        .build();
    queryResultWithoutLicencesAndBlock = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withResourceName("other resource")
        .withSubmissionType("PROSPECT_UPDATE")
        .withCentreOfInterestQuadBlock(null)
        .withOrganisationResponsibleId(ORGANISATION_ID)
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
  void getWorkAreaItems_twoItemsReturned_whenOrganisationAndLicencesNotFound_oneWithQuadBlocks() {
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(queryResults);
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(eq(ORGANISATION_UNIT_MAP), anyInt()))
        .thenReturn("Not provided");
    when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, List.of(LICENCE_ID_1, LICENCE_ID_2)))
        .thenReturn(List.of());

    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("Prospect create");
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.PROSPECT_UPDATE))
        .thenReturn("Prospect update");

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
                "Prospect create",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId()))),
            tuple(
                WorkAreaItemType.SUBMISSION,
                "other resource",
                "Prospect update",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithoutLicencesAndBlock.getSubmissionDetailId()))
            )
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));

    assertThat(result.get(1).modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", UPDATE_REQUESTED));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationAndLicencesFound() {

    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("Prospect create");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultWithEverythingFilledIn));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, queryResultWithEverythingFilledIn.getOrganisationResponsibleId()))
        .thenReturn("organisation (1)");
    var licence1 = new LicenceView("licence1", LICENCE_ID_1, LicenceStatus.EXTANT);
    var licence2 = new LicenceView("licence2", LICENCE_ID_2, LicenceStatus.EXTANT);
    when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, List.of(LICENCE_ID_1, LICENCE_ID_2)))
        .thenReturn(List.of(licence1.getName(), licence2.getName()));

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result).hasSize(1);

    assertThat(result.get(0))
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .contains(
            WorkAreaItemType.SUBMISSION,
            "resource name (25/5)",
            "Prospect create",
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId())));

    assertThat(result.get(0).modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", "organisation (1)"),
            entry("licences", List.of(licence1.getName(), licence2.getName())),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenAllButMandatoryNotProvided() {
    var providedName = "resource name";
    var submissionWorkAreaQueryResult = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withCentreOfInterestQuadBlock(null)
        .withLicencesTheResourceIsOn(null)
        .withOrganisationResponsibleId(null)
        .withResourceName(providedName)
        .build();

    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("Prospect create");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(submissionWorkAreaQueryResult));

    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, null))
        .thenReturn("Not provided");

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
                providedName,
                "Prospect create",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(submissionWorkAreaQueryResult.getSubmissionDetailId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .contains(
            entry("isFieldResource", false),
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationWithoutNameFound() {
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("Prospect create");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultWithEverythingFilledIn));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, queryResultWithEverythingFilledIn.getOrganisationResponsibleId()))
        .thenReturn("Not provided");

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
                "Prospect create",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .contains(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"),
            entry("updateRequested", false));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationWithoutNameAndNumberFound() {
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("Prospect create");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER)).thenReturn(List.of(
        queryResultWithEverythingFilledIn));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, queryResultWithEverythingFilledIn.getOrganisationResponsibleId()))
        .thenReturn("Not provided");

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
                "Prospect create",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
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
        .withOrganisationResponsibleId(ORGANISATION_ID)
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultOnlyOrganisationName));
    var organisationName = "organisation name";
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, ORGANISATION_ID))
        .thenReturn(organisationName);

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result.get(0).modelProperties().getProperties())
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
        .withLicencesTheResourceIsOn(List.of())
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultEmptyLicences));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(ORGANISATION_UNIT_MAP, queryResultWithEverythingFilledIn.getOrganisationResponsibleId()))
        .thenReturn("Not provided");

    var result = workAreaItemService.getWorkAreaItems(USER);

    assertThat(result.get(0).modelProperties().getProperties())
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
        .thenReturn("Prospect create");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(responsibleTeamSubmissionWorkAreaQueryService);
    when(responsibleTeamSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(List.of(queryResultInSubmitted));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(eq(ORGANISATION_UNIT_MAP), any()))
        .thenReturn("Not provided");
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    when(submissionDetailService.getSubmissionDetailForIdOrError(queryResultInSubmitted.getSubmissionDetailId()))
        .thenReturn(submissionDetail);
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
                "Prospect create",
                ReverseRouter.route(on(ReviewSubmissionController.class)
                    .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
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
    var result = workAreaItemService.getWorkAreaItems(USER);

    if (status == SubmissionDetailStatus.DRAFT) {
      assertThat(result.get(0).actionUrl())
          .isEqualTo(ReverseRouter.route(on(TaskListController.class)
              .getTaskList(queryResult.getSubmissionDetailId())));
    } else {
      assertThat(result.get(0).actionUrl())
          .isEqualTo(ReverseRouter.route(on(ReviewSubmissionController.class)
              .showReviewPage(submissionDetail.getSubmission().getResource().getId())));
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
                    .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
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
                    .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .contains(
            entry("isFieldResource", true),
            entry("organisationResponsible", responsibleOrganisation),
            entry("parentFieldName", parentFieldName),
            entry("fieldStatus", queryResultInSubmitted.getFieldStatus().getDisplayName()));
  }

  @Test
  void getWorkAreaItems_whenCorrection() {
    var workAreaItem = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withCorrection(true)
        .build();
    var queryResults = List.of(workAreaItem);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(SubmissionType.CREATE_PROSPECT))
        .thenReturn("Prospect create");
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(USER))
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems(USER))
        .thenReturn(queryResults);
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(eq(ORGANISATION_UNIT_MAP), anyInt()))
        .thenReturn("Not provided");
    when(submissionDetailDisplayService.getLicencesForDisplayFromMap(LICENCE_MAP, List.of(LICENCE_ID_1, LICENCE_ID_2)))
        .thenReturn(List.of());

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
                "Prospect create - correction",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(workAreaItem.getSubmissionDetailId()))
            )
        );

    assertThat(result.get(0).modelProperties().getProperties())
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