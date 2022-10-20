package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItemType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListSection;

@Service
class ProspectSummaryService {

  private final GeneralDetailSummaryService generalDetailSummaryService;
  private final SourceAndSealSummaryService sourceAndSealSummaryService;
  private final List<ProspectTaskListSection> prospectTaskListSections;
  private final List<ProspectTaskListItem> prospectTaskListItems;
  private List<SubNavigationSectionItem> subNavigationSectionItems;

  @Autowired
  public ProspectSummaryService(GeneralDetailSummaryService generalDetailSummaryService,
                                SourceAndSealSummaryService sourceAndSealSummaryService,
                                List<ProspectTaskListSection> prospectTaskListSections,
                                List<ProspectTaskListItem> prospectTaskListItems) {
    this.generalDetailSummaryService = generalDetailSummaryService;
    this.sourceAndSealSummaryService = sourceAndSealSummaryService;
    this.prospectTaskListSections = prospectTaskListSections;
    this.prospectTaskListItems = prospectTaskListItems;
    this.subNavigationSectionItems = new ArrayList<>();
  }

  public GeneralDetailSummaryView getGeneralDetailSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailSummaryService.getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  public SourceAndSealSummaryView getSourceAndSealSummaryViewFromSubmissionDetail(SubmissionDetail submissionDetail) {
    return sourceAndSealSummaryService.getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  public List<SubNavigationSectionItem> getProspectSubNavigation(SubmissionDetail submissionDetail) {
    subNavigationSectionItems = new ArrayList<>();
    return getProspectSubNavigationItems(prospectTaskListItems, prospectTaskListSections, submissionDetail);
  }

  private List<SubNavigationSectionItem> getProspectSubNavigationItems(
      List<ProspectTaskListItem> items,
      List<ProspectTaskListSection> sections,
      SubmissionDetail submissionDetail) {
    var taskListType = new ProspectTaskListItemType(submissionDetail);

    sections
        .stream()
        .filter(item -> !item.getClass().equals(ReviewAndSubmitTaskListSection.class))
        .map(SubNavigationSectionItem::new)
        .sorted(Comparator.comparing(SubNavigationSectionItem::getOrder))
        .forEach(subNavigationSectionItem -> {
          items
              .stream()
              .filter(taskListItem ->
                  isTaskListForSectionAndVisible(taskListType, taskListItem, subNavigationSectionItem.getSection()))
              .forEach(subNavigationSectionItem::addTaskListItem);
          subNavigationSectionItems.add(subNavigationSectionItem);
        });

    return subNavigationSectionItems;
  }

  private boolean isTaskListForSectionAndVisible(ProspectTaskListItemType taskListItemType,
                                                 ProspectTaskListItem prospectTaskListItem,
                                                 ProspectTaskListSection section) {
    return taskListItemType != null
        && prospectTaskListItem.isVisible(taskListItemType)
        && prospectTaskListItem.getTaskListSection().equals(section.getClass());
  }
}
