package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListItemView;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListLabel;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListLabelType;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSectionView;
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = ProspectTaskListController.class)
class ProspectTaskListControllerTest extends AbstractControllerTest {

  @MockBean
  SubmissionDetailService submissionDetailService;

  @MockBean
  ProspectTaskListSection prospectTaskListSection;

  @MockBean
  ProspectTaskListItem prospectTaskListItem;

  @Test
  void getTaskList_assertModelProperties() throws Exception {
    var submissionDetailId = UUID.randomUUID();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);

    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
    var sectionName = "section name";
    var sectionDisplayOrder = 10;
    var sectionWarningText = "section warning text";

    when(prospectTaskListSection.getSectionName()).thenReturn(sectionName);
    when(prospectTaskListSection.getDisplayOrder()).thenReturn(sectionDisplayOrder);
    when(prospectTaskListSection.getSectionWarningText()).thenReturn(sectionWarningText);

    var prospectTaskListItemType = new ProspectTaskListItemType(submissionDetail);

    var expectedTaskListItemView = TaskListTestUtil.getItemViewBuilder(10, "display name", "/action-url")
        .withTaskListLabels(true)
        .withNotCompletedLabel(false)
        .withItemValid(true)
        .withCustomTaskListLabel(new TaskListLabel("label text", TaskListLabelType.GREY))
        .build();

    doReturn(prospectTaskListSection.getClass()).when(prospectTaskListItem).getTaskListSection();
    when(prospectTaskListItem.isVisible(prospectTaskListItemType)).thenReturn(true);

    when(prospectTaskListItem.getDisplayOrder()).thenReturn(expectedTaskListItemView.getDisplayOrder());
    when(prospectTaskListItem.getActionUrl(prospectTaskListItemType)).thenReturn(expectedTaskListItemView.getActionUrl());
    when(prospectTaskListItem.getItemDisplayText()).thenReturn(expectedTaskListItemView.getDisplayName());
    when(prospectTaskListItem.showTaskListLabels(prospectTaskListItemType)).thenReturn(expectedTaskListItemView.showTaskListLabels());
    when(prospectTaskListItem.showNotCompletedLabels(prospectTaskListItemType)).thenReturn(expectedTaskListItemView.showNotCompletedLabel());
    when(prospectTaskListItem.isValid(prospectTaskListItemType)).thenReturn(expectedTaskListItemView.isItemValid());
    when(prospectTaskListItem.getCustomTaskListLabel(prospectTaskListItemType)).thenReturn(expectedTaskListItemView.getCustomTaskListLabel());


    var modelAndView =
        mockMvc.perform(
            get(ReverseRouter.route(on(ProspectTaskListController.class).getTaskList(submissionDetailId))))
            .andExpect(status().isOk())
            .andExpect(view().name("remi/prospect/tasklist/taskList"))
            .andReturn()
            .getModelAndView();

    @SuppressWarnings("unchecked")
    var taskListSectionViews = (List<TaskListSectionView>) modelAndView.getModel().get("taskListSections");

    @SuppressWarnings("unchecked")
    var breadcrumbs = (Map<String, String>) modelAndView.getModel().get("breadcrumbsMap");

    assertThat(breadcrumbs)
        .containsExactly(
            entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()), WorkAreaController.WORK_AREA_TITLE)
        );

    assertEquals(modelAndView.getModel().get("currentPage"), ProspectTaskListController.PAGE_NAME);

    assertThat(taskListSectionViews)
        .extracting(
            TaskListSectionView::sectionName,
            TaskListSectionView::displayOrder,
            TaskListSectionView::sectionWarningText
        )
        .containsExactly(
            tuple(
                sectionName,
                sectionDisplayOrder,
                sectionWarningText
            )
        );

    assertThat(taskListSectionViews.get(0).taskListItemViews())
        .extracting(
            TaskListItemView::getDisplayOrder,
            TaskListItemView::getDisplayName,
            TaskListItemView::getActionUrl,
            TaskListItemView::isItemValid,
            TaskListItemView::showTaskListLabels,
            TaskListItemView::showNotCompletedLabel,
            TaskListItemView::getCustomTaskListLabel
        )
        .containsExactly(
            tuple(
                expectedTaskListItemView.getDisplayOrder(),
                expectedTaskListItemView.getDisplayName(),
                expectedTaskListItemView.getActionUrl(),
                expectedTaskListItemView.isItemValid(),
                expectedTaskListItemView.showTaskListLabels(),
                expectedTaskListItemView.showNotCompletedLabel(),
                expectedTaskListItemView.getCustomTaskListLabel()
            )
        );
  }
}