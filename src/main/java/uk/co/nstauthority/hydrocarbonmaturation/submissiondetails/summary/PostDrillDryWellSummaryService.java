package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ShowOnSubNav;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListSectionService;

@Service
class PostDrillDryWellSummaryService {

  private final DryWellDetailsSummaryService dryWellDetailsSummaryService;
  private final List<TaskListSectionService> postWellDrillTaskListSections;

  public PostDrillDryWellSummaryService(DryWellDetailsSummaryService dryWellDetailsSummaryService,
                                        List<TaskListSectionService> postWellDrillTaskListSections) {
    this.dryWellDetailsSummaryService = dryWellDetailsSummaryService;
    this.postWellDrillTaskListSections = postWellDrillTaskListSections;
  }

  public DryWellDetailsSummaryView getDryWellDetailSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return dryWellDetailsSummaryService.getDryWellDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public List<SubNavigationItem> getPostDrillDryWellSubNavigation(SubmissionDetail submissionDetail) {
    return getPostDrillDryWellSubNavigationItems(postWellDrillTaskListSections, submissionDetail);
  }

  private List<SubNavigationItem> getPostDrillDryWellSubNavigationItems(
      List<TaskListSectionService> sections,
      SubmissionDetail submissionDetail) {

    List<SubNavigationItem> listOfSubNavItems = new ArrayList<>();
    sections.stream()
        .map(tlss -> tlss.getSection(submissionDetail))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(TaskListSection::displayOrder))
        .forEach(section ->
          section.items()
              .stream()
              .filter(item -> item.showOnSubNav() == ShowOnSubNav.TRUE)
              .forEach(item -> listOfSubNavItems.add(new SubNavigationItem(item.displayName(),
                  getActionUrl(item))))
        );

    return listOfSubNavItems;
  }

  private String getActionUrl(TaskListItem item) {
    return  "#" + item.displayName().replace(" ", "").toLowerCase();
  }
}
