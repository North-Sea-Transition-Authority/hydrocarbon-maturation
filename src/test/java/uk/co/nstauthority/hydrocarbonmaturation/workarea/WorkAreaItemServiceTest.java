package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@ExtendWith(MockitoExtension.class)
class WorkAreaItemServiceTest {

  @Mock
  private SubmissionWorkAreaServiceFactory submissionWorkAreaServiceFactory;

  @Mock
  private OperatorSubmissionWorkAreaQueryService operatorSubmissionWorkAreaQueryService;

  @Mock
  private RegulatorSubmissionWorkAreaQueryService regulatorSubmissionWorkAreaQueryService;

  @Mock
  private SubmissionDetailDisplayService submissionDetailDisplayService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @InjectMocks
  private WorkAreaItemService workAreaItemService;

  private List<SubmissionWorkAreaQueryResult> queryResults;
  private SubmissionWorkAreaQueryResult queryResultWithEverythingFilledIn;
  private SubmissionWorkAreaQueryResult queryResultWithoutLicencesAndBlock;

  private final Integer LICENCE_ID_1 = 1;
  private final Integer LICENCE_ID_2 = 2;
  private final Integer ORGANISATION_ID = 5;

  @BeforeEach
  void setUp() {
    queryResultWithEverythingFilledIn = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withLicencesTheResourceIsOn(List.of(LICENCE_ID_1, LICENCE_ID_2)).build();
    queryResultWithoutLicencesAndBlock = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withResourceName("other resource")
        .withSubmissionType("PROSPECT_UPDATE")
        .withCentreOfInterestQuadBlock(null)
        .withOrganisationResponsibleId(ORGANISATION_ID)
        .withLicencesTheResourceIsOn(null)
        .build();
    queryResults = List.of(queryResultWithEverythingFilledIn, queryResultWithoutLicencesAndBlock);
  }

  @Test
  void getWorkAreaItems_twoItemsReturned_whenOrganisationAndLicencesNotFound_oneWithQuadBlocks() {
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems())
        .thenReturn(queryResults);
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(anyInt()))
        .thenReturn("Not provided");
    when(submissionDetailDisplayService.getLicencesForDisplay(List.of(LICENCE_ID_1, LICENCE_ID_2)))
        .thenReturn(List.of());

    var result = workAreaItemService.getWorkAreaItems();

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .containsExactly(
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
        .containsExactly(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"));

    assertThat(result.get(1).modelProperties().getProperties())
        .containsExactly(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationAndLicencesFound() {

    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems())
        .thenReturn(List.of(queryResultWithEverythingFilledIn));
    var organisationUnit = OrganisationUnit.newBuilder()
        .name("organisation")
        .registeredNumber("1")
        .build();
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(queryResultWithEverythingFilledIn.getOrganisationResponsibleId()))
        .thenReturn("organisation (1)");
    var licence1 = new LicenceView("licence1", LICENCE_ID_1, LicenceStatus.EXTANT);
    var licence2 = new LicenceView("licence2", LICENCE_ID_2, LicenceStatus.EXTANT);
    var licences = List.of(licence1, licence2);
    when(submissionDetailDisplayService.getLicencesForDisplay(List.of(LICENCE_ID_1, LICENCE_ID_2)))
        .thenReturn(List.of(licence1.getName(), licence2.getName()));

    var result = workAreaItemService.getWorkAreaItems();

    assertThat(result).hasSize(1);

    assertThat(result.get(0))
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .containsExactly(
            WorkAreaItemType.SUBMISSION,
            "resource name (25/5)",
            "Prospect create",
            ReverseRouter.route(on(TaskListController.class)
                .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId())));

    assertThat(result.get(0).modelProperties().getProperties())
        .containsExactly(
            entry("organisationResponsible", "organisation (1)"),
            entry("licences", List.of(licence1.getName(), licence2.getName())),
            entry("submissionStatus", "Draft"));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenAllNotProvided() {
    var createdOn = Instant.now();
    var submissionWorkAreaQueryResult = SubmissionWorkAreaQueryResultTestUtil.builder()
        .withCentreOfInterestQuadBlock(null)
        .withLicencesTheResourceIsOn(null)
        .withOrganisationResponsibleId(null)
        .withResourceName(null)
        .withCreatedTime(createdOn)
        .build();

    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems())
        .thenReturn(List.of(submissionWorkAreaQueryResult));

    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(null))
        .thenReturn("Not provided");

    var result = workAreaItemService.getWorkAreaItems();

    assertThat(result).hasSize(1);

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .containsExactly(
            tuple(
                WorkAreaItemType.SUBMISSION,
                "Created on " + LocalDateTime.ofInstant(
                    createdOn,
                    ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")),
                "Prospect create",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(submissionWorkAreaQueryResult.getSubmissionDetailId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .containsExactly(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationWithoutNameFound() {
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(operatorSubmissionWorkAreaQueryService);
    when(operatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems())
        .thenReturn(List.of(queryResultWithEverythingFilledIn));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(queryResultWithEverythingFilledIn.getOrganisationResponsibleId()))
        .thenReturn("Not provided");

    var result = workAreaItemService.getWorkAreaItems();

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .containsExactly(
            tuple(
                WorkAreaItemType.SUBMISSION,
                "resource name (25/5)",
                "Prospect create",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .containsExactly(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationWithoutNameAndNumberFound() {
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(regulatorSubmissionWorkAreaQueryService);
    when(regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems()).thenReturn(List.of(
        queryResultWithEverythingFilledIn));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(queryResultWithEverythingFilledIn.getOrganisationResponsibleId()))
        .thenReturn("Not provided");

    var result = workAreaItemService.getWorkAreaItems();

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .containsExactly(
            tuple(
                WorkAreaItemType.SUBMISSION,
                "resource name (25/5)",
                "Prospect create",
                ReverseRouter.route(on(TaskListController.class)
                    .getTaskList(queryResultWithEverythingFilledIn.getSubmissionDetailId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .containsExactly(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenOrganisationWithName_assertNameWithoutNUmber() {
    var queryResultOnlyOrganisationName = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withOrganisationResponsibleId(ORGANISATION_ID)
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(regulatorSubmissionWorkAreaQueryService);
    when(regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems())
        .thenReturn(List.of(queryResultOnlyOrganisationName));
    var organisationName = "organisation name";
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(ORGANISATION_ID))
        .thenReturn(organisationName);

    var result = workAreaItemService.getWorkAreaItems();

    assertThat(result.get(0).modelProperties().getProperties())
        .containsExactly(
            entry("organisationResponsible", organisationName),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"));
  }


  @Test
  void getWorkAreaItems_oneItemReturned_whenLicencesEmptyList_assertEmptyList() {
    var queryResultEmptyLicences = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withLicencesTheResourceIsOn(List.of())
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(regulatorSubmissionWorkAreaQueryService);
    when(regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems())
        .thenReturn(List.of(queryResultEmptyLicences));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(queryResultWithEverythingFilledIn.getOrganisationResponsibleId()))
        .thenReturn("Not provided");

    var result = workAreaItemService.getWorkAreaItems();

    assertThat(result.get(0).modelProperties().getProperties())
        .containsExactly(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Draft"));
  }

  @Test
  void getWorkAreaItems_oneItemReturned_whenSubmitted_assertReviewActionUrlAndOtherValues() {
    var queryResultInSubmitted = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withStatus(SubmissionDetailStatus.SUBMITTED.name())
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(regulatorSubmissionWorkAreaQueryService);
    when(regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems())
        .thenReturn(List.of(queryResultInSubmitted));
    when(submissionDetailDisplayService.getOrganisationResponsibleDisplay(any()))
        .thenReturn("Not provided");
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    when(submissionDetailService.getSubmissionDetailForIdOrError(queryResultInSubmitted.getSubmissionDetailId()))
        .thenReturn(submissionDetail);
    var result = workAreaItemService.getWorkAreaItems();

    assertThat(result)
        .extracting(WorkAreaItem::type,
            WorkAreaItem::headingText,
            WorkAreaItem::captionText,
            WorkAreaItem::actionUrl)
        .containsExactly(
            tuple(
                WorkAreaItemType.SUBMISSION,
                "resource name (25/5)",
                "Prospect create",
                ReverseRouter.route(on(ReviewSubmissionController.class)
                    .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        );

    assertThat(result.get(0).modelProperties().getProperties())
        .containsExactly(
            entry("organisationResponsible", "Not provided"),
            entry("licences", List.of()),
            entry("submissionStatus", "Submitted"));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionDetailStatus.class, names = {"DRAFT", "SUBMITTED"})
  void getWorkAreaItemsOneItemReturned_whenValidStatus_assertCorrectUrl(SubmissionDetailStatus status) {
    var queryResult = SubmissionWorkAreaQueryResultTestUtil
        .builder()
        .withStatus(status.name())
        .build();
    when(submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy())
        .thenReturn(regulatorSubmissionWorkAreaQueryService);
    when(regulatorSubmissionWorkAreaQueryService.getWorkAreaSubmissionItems())
        .thenReturn(List.of(queryResult));

    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_PROSPECT);
    if(status == SubmissionDetailStatus.SUBMITTED) {
      when(submissionDetailService.getSubmissionDetailForIdOrError(queryResult.getSubmissionDetailId()))
          .thenReturn(submissionDetail);
    }
    var result = workAreaItemService.getWorkAreaItems();

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
}