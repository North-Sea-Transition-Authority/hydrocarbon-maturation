package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.field.operator.FieldOperatorQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.metrics.MetricsProvider;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.FieldDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@Service
class WorkAreaItemService {

  private final SubmissionWorkAreaServiceFactory submissionWorkAreaServiceFactory;
  private final SubmissionDetailDisplayService submissionDetailDisplayService;
  private final SubmissionDetailService submissionDetailService;
  private final FieldDisplayService fieldDisplayService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final LicenceQueryService licenceQueryService;
  private final MetricsProvider metricsProvider;
  private final FieldOperatorQueryService fieldOperatorQueryService;

  @Autowired
  WorkAreaItemService(SubmissionWorkAreaServiceFactory submissionWorkAreaServiceFactory,
                      SubmissionDetailDisplayService submissionDetailDisplayService,
                      SubmissionDetailService submissionDetailService,
                      FieldDisplayService fieldDisplayService,
                      OrganisationUnitQueryService organisationUnitQueryService,
                      LicenceQueryService licenceQueryService,
                      MetricsProvider metricsProvider,
                      FieldOperatorQueryService fieldOperatorQueryService
  ) {
    this.submissionWorkAreaServiceFactory = submissionWorkAreaServiceFactory;
    this.submissionDetailDisplayService = submissionDetailDisplayService;
    this.submissionDetailService = submissionDetailService;
    this.fieldDisplayService = fieldDisplayService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.licenceQueryService = licenceQueryService;
    this.metricsProvider = metricsProvider;
    this.fieldOperatorQueryService = fieldOperatorQueryService;
  }

  List<WorkAreaItem> getWorkAreaItems(User user) {
    var checkStopWatch = Stopwatch.createStarted();

    var workAreaSubmissionItems = submissionWorkAreaServiceFactory.getWorkAreaServiceStrategy(user)
        .getWorkAreaSubmissionItems(user);

    var elapsedMs = checkStopWatch.elapsed(TimeUnit.MILLISECONDS);
    metricsProvider.getWorkAreaQueryTimer().record(elapsedMs, TimeUnit.MILLISECONDS);

    var organisationUnitIds = getOrganisationUnitIdsFromResults(workAreaSubmissionItems);
    var organisationUnitMap = organisationUnitQueryService.getOrganisationUnitMapByIds(organisationUnitIds);

    var fieldOperatorMap = getFieldOperatorOrganisationUnitDtos(workAreaSubmissionItems);

    var licences = getLicencesFromResults(workAreaSubmissionItems);
    var licenceMap = licenceQueryService.getLicenceMapByIds(licences);

    return workAreaSubmissionItems.stream()
        .map(submissionWorkAreaQueryResult ->
            getWorkAreaItem(submissionWorkAreaQueryResult, organisationUnitMap, licenceMap, fieldOperatorMap))
        .toList();
  }


  private List<Integer> getLicencesFromResults(List<SubmissionWorkAreaQueryResult> workAreaSubmissionItems) {
    return workAreaSubmissionItems.stream()
        .map(SubmissionWorkAreaQueryResult::getLicencesTheResourceIsOn)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .distinct()
        .toList();
  }

  private List<Integer> getOrganisationUnitIdsFromResults(List<SubmissionWorkAreaQueryResult> workAreaSubmissionItems) {
    return workAreaSubmissionItems.stream()
        .map(SubmissionWorkAreaQueryResult::getOrganisationResponsibleId)
        .filter(Objects::nonNull)
        .distinct()
        .toList();
  }

  private Map<Integer, OrganisationUnitDto> getFieldOperatorOrganisationUnitDtos(
      List<SubmissionWorkAreaQueryResult> workAreaSubmissionItems
  ) {
    var fieldIds = workAreaSubmissionItems.stream()
        .map(SubmissionWorkAreaQueryResult::getFieldIdentifier)
        .filter(Objects::nonNull)
        .toList();

    return fieldOperatorQueryService.getFieldOperatorIdMap(fieldIds).entrySet().stream()
        .collect(
            StreamUtil.toLinkedHashMap(
              Map.Entry::getKey,
              e -> OrganisationUnitDto.fromOrganisationUnit(e.getValue())
            )
        );
  }

  private WorkAreaItem getWorkAreaItem(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult,
                                       Map<Integer, OrganisationUnitDto> organisationUnitMap,
                                       Map<Integer, LicenceView> licenceMap,
                                       Map<Integer, OrganisationUnitDto> fieldOperatorMap) {
    return new WorkAreaItem(
        WorkAreaItemType.SUBMISSION,
        getHeadingText(submissionWorkAreaQueryResult),
        String.format("%s%s",
            submissionDetailService.getSubmissionTypeNameWithAcronym(submissionWorkAreaQueryResult.getSubmissionType()),
            submissionWorkAreaQueryResult.isCorrection() ? " - correction" : ""),
        getActionUrl(submissionWorkAreaQueryResult),
        getWorkAreaItemModelProperties(submissionWorkAreaQueryResult, organisationUnitMap, licenceMap, fieldOperatorMap));
  }

  private String getHeadingText(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    return SubmissionDetailDisplayUtil.getDisplayName(
        submissionWorkAreaQueryResult.getResourceName(),
        submissionWorkAreaQueryResult.getCentreOfInterestQuadBlock(),
        submissionWorkAreaQueryResult.getLicenceApplicationReference()
    );
  }

  private String getActionUrl(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult) {
    var submissionDetailId = submissionWorkAreaQueryResult.getSubmissionDetailId();

    if (submissionWorkAreaQueryResult.getStatus() == SubmissionDetailStatus.DRAFT) {
      return ReverseRouter.route(on(TaskListController.class)
          .getTaskList(submissionDetailId, null));
    } else {
      var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
      return ReverseRouter.route(on(ReviewSubmissionController.class)
          .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null));
    }
  }

  private WorkAreaItemModelProperties getWorkAreaItemModelProperties(SubmissionWorkAreaQueryResult submissionWorkAreaQueryResult,
                                                                     Map<Integer, OrganisationUnitDto> organisationUnitMap,
                                                                     Map<Integer, LicenceView> licenceMap,
                                                                     Map<Integer, OrganisationUnitDto> fieldOperatorMap) {
    var fieldId = submissionWorkAreaQueryResult.getFieldIdentifier();
    var organisationResponsibleId = submissionWorkAreaQueryResult.getOrganisationResponsibleId();
    var licencesTheResourceIsOn = submissionWorkAreaQueryResult.getLicencesTheResourceIsOn();
    var fieldStatus = submissionWorkAreaQueryResult.getFieldStatus() != null
        ? submissionWorkAreaQueryResult.getFieldStatus().getDisplayName()
        : "";

    String organisationResponsibleDisplay;
    if (fieldId != null && fieldOperatorMap.containsKey(fieldId)) {
      organisationResponsibleDisplay =
          submissionDetailDisplayService.getOrganisationResponsibleDisplay(fieldOperatorMap.get(fieldId));
    } else {
      organisationResponsibleDisplay =
          submissionDetailDisplayService.getOrganisationResponsibleDisplayFromMap(organisationUnitMap, organisationResponsibleId);
    }

    var licencesForDisplay = submissionDetailDisplayService.getLicencesForDisplayFromMap(licenceMap, licencesTheResourceIsOn);

    return new WorkAreaItemModelProperties()
        .addProperty("organisationResponsible", organisationResponsibleDisplay)
        .addProperty("licences", licencesForDisplay)
        .addProperty("submissionStatus", submissionWorkAreaQueryResult.getStatus().getDisplayName())
        .addProperty("updateRequested", submissionWorkAreaQueryResult.isUpdateRequested())
        .addProperty("submissionType", submissionWorkAreaQueryResult.getSubmissionType())
        .addProperty("isFieldResource",
            submissionWorkAreaQueryResult.getSubmissionType().getResourceTypes().contains(ResourceType.FIELD))
        .addProperty("parentFieldName",
            fieldDisplayService.getParentFieldNameOrNoneFound(submissionWorkAreaQueryResult.getParentFieldId()))
        .addProperty("fieldStatus", fieldStatus);
  }
}
