package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@Service
public class FieldQueryService {

  private final FieldPersistenceService fieldPersistenceService;
  private final FieldStatusService fieldStatusService;
  private final SubmissionDetailService submissionDetailService;
  private final AreaTeamService areaTeamService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResponsibleTeamService responsibleTeamService;
  private final IndustryTeamService industryTeamService;
  private final IndustryTeamMemberService industryTeamMemberService;

  @Autowired
  public FieldQueryService(FieldPersistenceService fieldPersistenceService,
                           FieldStatusService fieldStatusService,
                           SubmissionDetailService submissionDetailService,
                           AreaTeamService areaTeamService,
                           RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
                           ResponsibleTeamService responsibleTeamService,
                           IndustryTeamService industryTeamService,
                           IndustryTeamMemberService industryTeamMemberService) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldStatusService = fieldStatusService;
    this.submissionDetailService = submissionDetailService;
    this.areaTeamService = areaTeamService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.responsibleTeamService = responsibleTeamService;
    this.industryTeamService = industryTeamService;
    this.industryTeamMemberService = industryTeamMemberService;
  }

  public List<FieldView> getFieldByNameForSubmissionType(String name, SubmissionType submissionType, ServiceUserDetail user) {
    //gets every field by name
    var fields = fieldPersistenceService.getFieldsByCorrectedLegalName(name);

    HashSet<FieldStatus> allowedStatuses = new HashSet<>();

    switch (submissionType) {
      case CREATE_CSR -> allowedStatuses.add(FieldStatus.STATUS_200);
      case CREATE_FDP -> allowedStatuses.addAll(Set.of(
          FieldStatus.STATUS_200,
          FieldStatus.STATUS_300,
          FieldStatus.STATUS_400));
      case CREATE_FDPA -> allowedStatuses.addAll(Set.of(
          FieldStatus.STATUS_600,
          FieldStatus.STATUS_700,
          FieldStatus.STATUS_799,
          FieldStatus.STATUS_800,
          FieldStatus.STATUS_899));
      case CREATE_COP -> allowedStatuses.addAll(Set.of(
          FieldStatus.STATUS_700,
          FieldStatus.STATUS_799,
          FieldStatus.STATUS_800,
          FieldStatus.STATUS_899));
      default -> throw new ActionNotAllowedException("No field statuses for submission type: %s".formatted(submissionType));
    }

    // Filters out fields with an incorrect status or with other preventing submissions
    return fields
        .stream()
        .filter(
            field -> {
              var mostRecentStatus = fieldStatusService.getMostRecentFieldStatus(field);
              // status hasn't been set yet, so exclude
              return mostRecentStatus.filter(allowedStatuses::contains).isPresent();
            })
        .filter(field -> filterFieldByUser(field, user))
        .filter(field -> filterFieldBySubmissions(field.getResource()))
        .map(this::getFieldViewFromField)
        .sorted(Comparator.comparing(FieldView::getSelectionText))
        .toList();

  }

  /**
   * Checks the user can select the field based on if they are in the responsible team or organisation.
   * @param field The field to be checked if the user has permission to select it
   * @return True if the user can select the field
   */
  private boolean filterFieldByUser(Field field, ServiceUserDetail user) {
    if (areaTeamService.isAreaTeamMember(user)) {
      var responsibleTeamType = regulatorManagedInformationPersistenceService
          .getRegulatorManagedInformationByResourceOrThrow(field.getResource())
          .getResponsibleTeamType();
      return responsibleTeamService.isInResponsibleTeam(user, responsibleTeamType);
    } else if (industryTeamService.isSubmitter(user)) {
      return industryTeamMemberService.isSubmitterOfOrganisationUnit(user, field.getResponsibleOrganisationId());
    }
    return false;
  }

  /** Checks submissions on the field to determine whether a new submission can be made.
   * @param resource Current resource
   * @return if the resource should be available to be chosen
   */
  private boolean filterFieldBySubmissions(Resource resource) {
    var submissionsOnResource = submissionDetailService.getExtantSubmissionDetailsForResource(resource);

    return submissionsOnResource
        .stream()
        .noneMatch(this::isSubmissionPreventingNewSubmission);
  }

  /**
   *  Checks the business rules relating to other submissions on the same resource
   *  for the submission type that is being created.
   * @param submissionDetailsToBeChecked The submission detail to be checked whether
   *                                     it prevents newSubmissionType from being submitted.
   * @return True if the old submission prevents a new submission of type newSubmissionType.
   */
  private boolean isSubmissionPreventingNewSubmission(SubmissionDetail submissionDetailsToBeChecked) {
    return submissionDetailsToBeChecked.isDraftOrSubmittedSubmission();
  }

  private FieldView getFieldViewFromField(Field field) {
    var legalFieldName = field.getCorrectedLegalFieldName();
    return new FieldView(field.getFieldIdentifier(), legalFieldName);
  }
}
