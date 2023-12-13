package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Set;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus.ConsideringCcusController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus.ConsideringCcusSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.FieldComplexityController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.FieldComplexitySubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.relatedresources.RelatedResourcesSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsTaskListSectionServiceTest {
  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();
  @Mock
  private GeneralDetailSubmissionService generalDetailSubmissionService;
  @Mock
  private RelatedResourcesSubmissionService relatedResourcesSubmissionService;
  @Mock
  private ResourceDetailsSubmissionService resourceDetailsSubmissionService;
  @Mock
  private ConsideringCcusSubmissionService consideringCcusSubmissionService;
  @Mock
  private UserDetailService userDetailService;
  @Mock
  private PermissionService permissionService;
  @Mock
  private FieldComplexitySubmissionService fieldComplexitySubmissionService;

  @InjectMocks
  private GeneralDetailsTaskListSectionService generalDetailsTaskListSectionService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "CREATE_PROSPECT",
      "CREATE_DISCOVERY",
      "CREATE_DISCOVERY_FROM_PROSPECT",
      "CREATE_FIELD",
      "CREATE_CSR",
      "CREATE_FDP",
      "CREATE_FDPA",
      "CREATE_COP"},
      mode = EnumSource.Mode.EXCLUDE)
  void getSection_expectOnlyGeneralDetails(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    if (submissionDetail.isInitialResourceSubmission()) {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(true);
    }
    var taskListSectionOptional = generalDetailsTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Resource details",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(ResourceDetailsController.class)
                    .getResourceDetails(submissionDetail.getId()))),
            new TaskListItem("General details",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(GeneralDetailsController.class)
                    .getGeneralDetails(submissionDetail.getId(), null)))),
            "General details",
            10
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "CREATE_PROSPECT",
      "CREATE_DISCOVERY",
      "CREATE_DISCOVERY_FROM_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void getSection_SubmissionTypeIsCreateProspect_ShowRelatedProspects(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(true);

    var taskListSectionOptional = generalDetailsTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
              new TaskListItem("Resource details",
                  TaskListLabel.NOT_COMPLETE,
                  ReverseRouter.route(on(ResourceDetailsController.class)
                      .getResourceDetails(submissionDetail.getId()))),
              new TaskListItem("General details",
                  TaskListLabel.NOT_COMPLETE,
                  ReverseRouter.route(on(GeneralDetailsController.class)
                      .getGeneralDetails(submissionDetail.getId(), null))),
              new TaskListItem("Related prospects",
                  TaskListLabel.NOT_COMPLETE,
                  ReverseRouter.route(on(RelatedResourcesController.class)
                      .getRelatedResources(submissionDetail.getId(), null)))
            ),
            "General details",
            10
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "CREATE_PROSPECT",
      "CREATE_DISCOVERY",
      "CREATE_DISCOVERY_FROM_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void getSection_whenInitialSubmissionAndHasPermission_whenDiscovery_ShowRelatedDiscoveries(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.getSubmission().getResource().setType(ResourceType.DISCOVERY);
    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(true);

    var taskListSectionOptional = generalDetailsTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
                new TaskListItem("Resource details",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ResourceDetailsController.class)
                        .getResourceDetails(submissionDetail.getId()))),
                new TaskListItem("General details",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(GeneralDetailsController.class)
                        .getGeneralDetails(submissionDetail.getId(), null))),
                new TaskListItem("Related discoveries",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(RelatedResourcesController.class)
                        .getRelatedResources(submissionDetail.getId(), null)))
            ),
            "General details",
            10
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "CREATE_PROSPECT",
      "CREATE_DISCOVERY",
      "CREATE_DISCOVERY_FROM_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void getSection_whenCorrection_dontShowRelatedResources(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setCorrection(true);
    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(true);

    var taskListSectionOptional = generalDetailsTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
                new TaskListItem("Resource details",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(ResourceDetailsController.class)
                        .getResourceDetails(submissionDetail.getId()))),
                new TaskListItem("General details",
                    TaskListLabel.NOT_COMPLETE,
                    ReverseRouter.route(on(GeneralDetailsController.class)
                        .getGeneralDetails(submissionDetail.getId(), null)))
            ),
            "General details",
            10
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "CREATE_PROSPECT",
      "CREATE_DISCOVERY",
      "CREATE_DISCOVERY_FROM_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void getSection_SubmissionTypeIsCreateProspectAndUserDoesNotHaveCorrectRole_ShowRelatedProspects(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(false);

    var taskListSectionOptional = generalDetailsTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section)
        .extracting(
            TaskListSection::items,
            TaskListSection::displayName,
            TaskListSection::displayOrder)
        .containsExactly(List.of(
            new TaskListItem("Resource details", TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(ResourceDetailsController.class)
                    .getResourceDetails(submissionDetail.getId()))),
            new TaskListItem("General details",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(GeneralDetailsController.class)
                    .getGeneralDetails(submissionDetail.getId(), null)))),
            "General details",
            10
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT"}, mode = EnumSource.Mode.EXCLUDE)
  void isSectionSubmittable_AllSubmissionTypesExceptCreateProspect_Submittable(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(generalDetailsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT"}, mode = EnumSource.Mode.EXCLUDE)
  void isSectionSubmittable_AllSubmissionTypesExceptCreateProspect_NotSubmittable(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(generalDetailsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT"}, mode = EnumSource.Mode.INCLUDE)
  void isSectionSubmittable_SubmissionTypeIsCreateProspect_Submittable(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(true);

    when(relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    assertTrue(generalDetailsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT"}, mode = EnumSource.Mode.INCLUDE)
  void isSectionSubmittable_SubmissionTypeIsCreateProspectAndNstaUserDoesNotHaveCorrectRole_NotSubmittable(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(false);

    assertTrue(generalDetailsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT"}, mode = EnumSource.Mode.INCLUDE)
  void isSectionSubmittable_SubmissionTypeIsCreateProspect_NotSubmittable(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(true);

    when(relatedResourcesSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    assertFalse(generalDetailsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT"}, mode = EnumSource.Mode.INCLUDE)
  void isSectionSubmittable_whenCorrection_submittable(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setCorrection(true);
    when(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(resourceDetailsSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(true);

    assertTrue(generalDetailsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"CREATE_PROSPECT"}, mode = EnumSource.Mode.INCLUDE)
  void isSectionSubmittable_whenCorrection_notSubmittable(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    submissionDetail.setCorrection(true);
    when(generalDetailSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(false);

    when(userDetailService.getUserDetail()).thenReturn(USER);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION))).thenReturn(true);

    assertFalse(generalDetailsTaskListSectionService.isSectionSubmittable(submissionDetail));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {
      "CREATE_CSR",
      "CREATE_FDP",
      "CREATE_FDPA",
      "CREATE_COP"},
      mode = EnumSource.Mode.INCLUDE)
  void getSection_SubmissionTypeIsField_allFieldOnlySections(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(consideringCcusSubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);
    when(fieldComplexitySubmissionService.isSectionSubmittable(submissionDetail)).thenReturn(true);

    var taskListSectionOptional = generalDetailsTaskListSectionService.getSection(submissionDetail);

    var section = taskListSectionOptional.get();

    assertThat(section.items())
        .extracting(
            TaskListItem::displayName,
            TaskListItem::label,
            TaskListItem::actionUrl)
        .contains(
            Tuple.tuple("General details",
                TaskListLabel.NOT_COMPLETE,
                ReverseRouter.route(on(GeneralDetailsController.class)
                    .getGeneralDetails(submissionDetail.getId(), null))),
            Tuple.tuple(ConsideringCcusController.PAGE_NAME,
                TaskListLabel.COMPLETE,
                ReverseRouter.route(on(ConsideringCcusController.class)
                    .getConsideringCcus(submissionDetail.getId()))),
            Tuple.tuple(FieldComplexityController.FIELD_COMPLEXITY,
                    TaskListLabel.COMPLETE,
                    ReverseRouter.route(on(FieldComplexityController.class)
                        .getFieldComplexity(submissionDetail.getId())))
            );
  }
}