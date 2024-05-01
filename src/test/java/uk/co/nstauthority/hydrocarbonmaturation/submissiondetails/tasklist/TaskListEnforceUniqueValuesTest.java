package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.SamlAuthenticationUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.WebUserAccountId;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamMemberRoleService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamRoleUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Transactional
@IntegrationTest
class TaskListEnforceUniqueValuesTest {

  @Autowired
  private List<TaskListSectionService> taskListSectionServices;

  @Autowired
  private UserService userService;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private TeamMemberRoleService teamMemberRoleService;

  @MockBean
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @MockBean
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  private final ResourceDetail resourceDetail = new ResourceDetail();
  {
    resourceDetail.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
  }

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    Set<TeamRole> userRoles = Set.of(RegulatorTeamRole.values());
    setUpTest(userRoles);
  }
  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FIELD"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void verifyUniqueSectionNamesAndOrders(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailIntegrationTestUtils.createSubmissionDetail(submissionType, entityManager);
    reservoirSubmissionDetail = ReservoirSubmissionDetailsIntegrationTestUtils.createReservoirSubmissionDetail(submissionDetail, entityManager);

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail)).thenReturn(resourceDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    var taskListSectionItemsList = taskListSectionServices.stream()
        .map(taskListSectionService -> taskListSectionService.getSection(submissionDetail))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();

    var originalSize = taskListSectionItemsList
        .size();

    var uniqueDisplayOrdersList = taskListSectionItemsList
        .stream()
        .map(TaskListSection::displayOrder)
        .distinct()
        .toList();

    var uniquenessDisplayOrderViolationErrorMessagesSuffix = taskListSectionItemsList
        .stream()
        .map(section -> "[%s: %s]".formatted(
            section.getClass().getSimpleName(),
            section.displayOrder()))
        .toList();

    var uniqueNamesList = taskListSectionItemsList
        .stream()
        .map(TaskListSection::displayName)
        .distinct()
        .toList();

    var uniquenessNamesListViolationErrorMessagesSuffix = taskListSectionItemsList
        .stream()
        .map(section -> "[%s: %s]".formatted(
            section.getClass().getSimpleName(),
            section.displayName()))
        .toList();

    assertEquals(
        originalSize,
        uniqueNamesList.size(),
        "Some sections don't have unique names for submission type %s for sections %s"
            .formatted(submissionType.getDisplayName(),
                uniquenessNamesListViolationErrorMessagesSuffix));

    assertEquals(
        originalSize,
        uniqueDisplayOrdersList.size(),
        "Some sections don't have unique display orders for submission type %s in sections %s"
            .formatted(submissionType.getDisplayName(),
                uniquenessDisplayOrderViolationErrorMessagesSuffix));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FIELD"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void verifyUniqueTaskListItems(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailIntegrationTestUtils.createSubmissionDetail(submissionType, entityManager);
    reservoirSubmissionDetail = ReservoirSubmissionDetailsIntegrationTestUtils.createReservoirSubmissionDetail(submissionDetail, entityManager);

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail)).thenReturn(resourceDetail);
    when(reservoirSubmissionDetailPersistenceService.getExtantReservoirSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetail);

    var sectionItemMap = taskListSectionServices.stream()
        .map(taskListSectionService -> taskListSectionService.getSection(submissionDetail))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(
            Collectors.toMap(
                TaskListSection::displayName,
                TaskListSection::items));

    sectionItemMap.forEach((taskListSection, taskListItems) -> {
      var originalSize = taskListItems.size();
      var distinctNameList = taskListItems
          .stream()
          .map(TaskListItem::displayName)
          .distinct()
          .toList()
          .size();

      var uniquenessNamesViolationErrorMessagesSuffix = taskListItems
          .stream()
          .map(item -> "[%s: %s]".formatted(
              item.getClass().getSimpleName(),
              item.displayName()))
          .toList();

      var distinctActionUrl = taskListItems
          .stream()
          .map(TaskListItem::actionUrl)
          .distinct()
          .toList()
          .size();

      var uniquenessActionUrlViolationErrorMessagesSuffix = taskListItems
          .stream()
          .map(item -> "[%s: %s]".formatted(
              item.getClass().getSimpleName(),
              item.actionUrl()))
          .toList();

      assertEquals(originalSize,
          distinctNameList, "The items in %s do not all have distinct names in items %s"
              .formatted(taskListSection, uniquenessNamesViolationErrorMessagesSuffix));
      assertEquals(originalSize,
          distinctActionUrl, "The items in %s do not have distinct action urls in items %s"
              .formatted(taskListSection, uniquenessActionUrlViolationErrorMessagesSuffix));
    });
  }

  private void setUpTest(Set<TeamRole> userRoles) {
    // Setup user
    var user = UserTestUtil.Builder()
        .withWuaId(1000L)
        .build();

    // Ensure user is treated as currently logged in
    SamlAuthenticationUtil.Builder()
        .withUser(user)
        .setSecurityContext();

    // Stub regulator team
    var team = TeamTestUtil.Builder()
        .withTeamType(TeamType.REGULATOR)
        .withId(null)
        .build();

    entityManager.persist(team);
    entityManager.flush();

    // Add user to team with the previously defined roles
    teamMemberRoleService.updateUserTeamRoles(team, new WebUserAccountId(user.wuaId()),
        TeamRoleUtil.getRoleNames(userRoles));
  }
}
