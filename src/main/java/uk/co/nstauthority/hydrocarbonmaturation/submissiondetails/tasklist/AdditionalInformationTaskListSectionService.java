package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSubmissionService;

@Service
class AdditionalInformationTaskListSectionService implements TaskListSectionService {

  private final AdditionalInformationSubmissionService additionalInformationSubmissionService;

  @Autowired
  AdditionalInformationTaskListSectionService(
      AdditionalInformationSubmissionService additionalInformationSubmissionService) {
    this.additionalInformationSubmissionService = additionalInformationSubmissionService;
  }

  @Override
  public Optional<TaskListSection> getSection(SubmissionDetail submissionDetail) {

    var items = List.of(
        new TaskListItem("Additional information",
            TaskListLabel.notStartedOrComplete(additionalInformationSubmissionService.isSectionSubmittable(submissionDetail)),
            ReverseRouter.route(on(AdditionalInformationController.class)
                .getAdditionalInformation(submissionDetail.getId(), null)))
    );

    return Optional.of(new TaskListSection("Additional information", 70, items));
  }
}
