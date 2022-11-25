package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import uk.co.nstauthority.hydrocarbonmaturation.IntegrationTest;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;

@IntegrationTest
class TaskListEnforceUniqueValuesTest {

  @Autowired
  private List<TaskListSectionService> taskListSectionServices;

  private SubmissionDetail submissionDetail;

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void verifyUniqueSectionNamesAndOrders(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

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
  @EnumSource(SubmissionType.class)
  void verifyUniqueTaskListItems(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

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
}
