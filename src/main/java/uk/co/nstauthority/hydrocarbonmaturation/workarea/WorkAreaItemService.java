package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@Service
class WorkAreaItemService {

  private final SubmissionWorkAreaQueryService submissionWorkAreaQueryService;
  private final LicenceQueryService licenceQueryService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  private static final String DEFAULT_TEXT = "Not provided";

  @Autowired
  WorkAreaItemService(SubmissionWorkAreaQueryService submissionWorkAreaQueryService,
                             LicenceQueryService licenceQueryService,
                             OrganisationUnitQueryService organisationUnitQueryService) {
    this.submissionWorkAreaQueryService = submissionWorkAreaQueryService;
    this.licenceQueryService = licenceQueryService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  List<WorkAreaItem> getWorkAreaItems() {
    return submissionWorkAreaQueryService.getWorkAreaSubmissionItems()
        .stream()
        .map(this::getWorkAreaItem)
        .toList();
  }

  private WorkAreaItem getWorkAreaItem(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return new WorkAreaItem(
        WorkAreaItemType.SUBMISSION,
        getHeadingText(submissionWorkAreaQueryResult),
        submissionWorkAreaQueryResult.getSubmissionType().getTypeName(),
        getActionUrl(submissionWorkAreaQueryResult),
        getWorkAreaItemModelProperties(submissionWorkAreaQueryResult));
  }

  private String getCentreOfInterestBlockDisplay(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return submissionWorkAreaQueryResult.getCentreOfInterestQuadBlock() != null
        ? " (" + submissionWorkAreaQueryResult.getCentreOfInterestQuadBlock() + ")"
        : "";
  }

  private String getHeadingText(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return submissionWorkAreaQueryResult.getResourceName() != null
        ? submissionWorkAreaQueryResult.getResourceName() + getCentreOfInterestBlockDisplay(submissionWorkAreaQueryResult)
        : "Created on %s"
        .formatted(
            LocalDateTime.ofInstant(
                submissionWorkAreaQueryResult.getCreatedOn().toInstant(),
                ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")));
  }

  private String getActionUrl(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return ReverseRouter.route(on(TaskListController.class)
        .getTaskList(submissionWorkAreaQueryResult.getSubmissionDetailId()));
  }

  private WorkAreaItemModelProperties getWorkAreaItemModelProperties(
      SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return new WorkAreaItemModelProperties()
        .addProperty("organisationResponsible",
            getOrganisationResponsibleDisplay(submissionWorkAreaQueryResult.getOrganisationResponsibleId()))
        .addProperty("licences", getLicencesForDisplay(submissionWorkAreaQueryResult.getLicencesTheResourceIsOn()))
        .addProperty("submissionStatus", submissionWorkAreaQueryResult.getStatus().getDisplayName())
        .addProperty("defaultText", DEFAULT_TEXT);
  }

  private String getOrganisationResponsibleDisplay(Integer organisationResponsibleId) {
    if (organisationResponsibleId != null) {
      return organisationUnitQueryService.findOrganisationUnit(organisationResponsibleId)
          .map(organisationUnit -> {
            if (organisationUnit.getRegisteredNumber() != null && organisationUnit.getName() != null) {
              return organisationUnit.getName() + " (" + organisationUnit.getRegisteredNumber() + ")";
            } else if (organisationUnit.getName() != null) {
              return organisationUnit.getName();
            } else {
              return DEFAULT_TEXT;
            }
          })
          .orElse(DEFAULT_TEXT);
    }
    return DEFAULT_TEXT;
  }

  private List<String> getLicencesForDisplay(List<Integer> licenceIds) {
    if (licenceIds != null) {
      var licences = licenceIds
          .stream()
          .map(Object::toString)
          .toList();
      if (!licences.isEmpty()) {
        return licenceQueryService.getLicencesByIds(licences)
            .stream()
            .map(LicenceView::licenceRef)
            .toList();
      } else {
        return List.of();
      }
    } else {
      return List.of();
    }
  }
}
