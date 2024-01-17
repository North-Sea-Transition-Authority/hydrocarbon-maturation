package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.falseCondition;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
class OperatorResourceRecordQueryService implements ResourceRecordQueryServiceStrategy {

  private final UserService userService;
  private final IndustryTeamService industryTeamService;
  private final LicenceQueryService licenceQueryService;
  private final DSLContext context;
  private final ResourceQueryCommonService resourceQueryCommonService;

  OperatorResourceRecordQueryService(UserService userService,
                                     IndustryTeamService industryTeamService,
                                     LicenceQueryService licenceQueryService,
                                     DSLContext context,
                                     ResourceQueryCommonService resourceQueryCommonService) {
    this.userService = userService;
    this.industryTeamService = industryTeamService;
    this.licenceQueryService = licenceQueryService;
    this.context = context;
    this.resourceQueryCommonService = resourceQueryCommonService;
  }

  @Override
  public List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    var user = userService.getUser();
    var userOrgUnitIds = industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
          user, Set.of(IndustryTeamRole.SUBMITTER, IndustryTeamRole.VIEWER));

    var resourceTypes = form.getResourceTypes();
    if (resourceTypes.contains(ResourceType.FIELD.getEnumName())) {
      return resourceQueryCommonService.getFieldRecordQueryResults(
          getFieldOperatorConditions(form, userOrgUnitIds),
          getFieldOperatorAggregateConditions(form, userOrgUnitIds)
      );
    } else {
      return resourceQueryCommonService.getProspectAndDiscoveryRecordQueryResults(
          getProspectAndDiscoveryOperatorConditions(form, userOrgUnitIds),
          getProspectAndDiscoveryOperatorAggregateConditions(form, userOrgUnitIds)
      );
    }
  }

  private List<Condition> getProspectAndDiscoveryOperatorConditions(SearchResourceRecordsForm form,
                                                                    List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterConditions(form));
    conditionList.add(RESOURCE_DETAILS.ORGANISATION_ID.in(userOrgUnitIds));

    return conditionList;
  }

  private List<Condition> getFieldOperatorConditions(SearchResourceRecordsForm form, List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterConditions(form));
    conditionList.add(FIELDS.RESPONSIBLE_ORGANISATION_ID.in(userOrgUnitIds));

    return conditionList;
  }

  private List<Condition> getFieldOperatorAggregateConditions(SearchResourceRecordsForm form, List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterAggregateConditions(form));
    conditionList.add(getIsFieldOnAnExtantLicenceCondition(userOrgUnitIds));
    return conditionList;
  }

  private List<Condition> getProspectAndDiscoveryOperatorAggregateConditions(SearchResourceRecordsForm form,
                                                                             List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterAggregateConditions(form));
    conditionList.add(getIsProspectOrDiscoveryOnAnExtantLicenceCondition(userOrgUnitIds));
    return conditionList;
  }

  private Condition getIsFieldOnAnExtantLicenceCondition(List<Integer> userOrganisationUnits) {
    var userLicences = getFieldUserLicences(userOrganisationUnits);

    if (userLicences.isEmpty()) {
      return falseCondition();
    }

    var userExtantLicences = NumberUtil.toListOfIntegers(
        licenceQueryService.getExtantLicencesByIds(userLicences)
            .stream()
            .map(LicenceView::getId)
            .toList());

    return DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
        .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
        .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.in(userExtantLicences))
        .isNotNull();
  }

  private Condition getIsProspectOrDiscoveryOnAnExtantLicenceCondition(List<Integer> userOrganisationUnits) {
    var userLicences = getProspectAndDiscoveryUserLicences(userOrganisationUnits);

    if (userLicences.isEmpty()) {
      return falseCondition();
    }

    var userExtantLicences = NumberUtil.toListOfIntegers(
        licenceQueryService.getExtantLicencesByIds(userLicences)
            .stream()
            .map(LicenceView::getId)
            .toList());

    return DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
        .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
        .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.in(userExtantLicences))
        .isNotNull();
  }

  private List<String> getFieldUserLicences(List<Integer> userOrganisationUnits) {
    return context
        .selectDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
        .from(GENERAL_DETAILS_LICENCES)
        .join(GENERAL_DETAILS).on(GENERAL_DETAILS.ID.eq(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID))
        .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(GENERAL_DETAILS.SUBMISSION_DETAIL_ID))
        .join(SUBMISSIONS).on(SUBMISSIONS.ID.eq(SUBMISSION_DETAILS.SUBMISSION_ID))
        .join(FIELDS).on(FIELDS.RESOURCE_ID.eq(SUBMISSIONS.RESOURCE_ID))
        .where(FIELDS.RESPONSIBLE_ORGANISATION_ID.in(userOrganisationUnits))
        .fetchInto(String.class);
  }

  private List<String> getProspectAndDiscoveryUserLicences(List<Integer> userOrganisationUnits) {
    return context
        .selectDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
        .from(GENERAL_DETAILS_LICENCES)
        .join(GENERAL_DETAILS).on(GENERAL_DETAILS.ID.eq(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID))
        .join(RESOURCE_DETAILS).on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(GENERAL_DETAILS.SUBMISSION_DETAIL_ID))
        .where(RESOURCE_DETAILS.ORGANISATION_ID.in(userOrganisationUnits))
        .fetchInto(String.class);
  }
}
