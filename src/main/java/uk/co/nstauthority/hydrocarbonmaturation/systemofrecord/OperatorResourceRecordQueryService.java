package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.falseCondition;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;

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
    return resourceQueryCommonService.getResourceRecordQueryResults(
        getOperatorConditions(form, userOrgUnitIds),
        getOperatorAggregateConditions(form, userOrgUnitIds)
    );
  }

  private List<Condition> getOperatorConditions(SearchResourceRecordsForm form, List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterConditions(form));
    conditionList.add(RESOURCE_DETAILS.ORGANISATION_ID.in(userOrgUnitIds));

    return conditionList;
  }

  private List<Condition> getOperatorAggregateConditions(SearchResourceRecordsForm form, List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterAggregateConditions(form));
    conditionList.add(getIsResourceOnAnExtantLicenceCondition(userOrgUnitIds));
    return conditionList;
  }

  private Condition getIsResourceOnAnExtantLicenceCondition(List<Integer> userOrganisationUnits) {
    var userLicences = context
        .selectDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
        .from(GENERAL_DETAILS_LICENCES)
        .join(GENERAL_DETAILS).on(GENERAL_DETAILS.ID.eq(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID))
        .join(RESOURCE_DETAILS).on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(GENERAL_DETAILS.SUBMISSION_DETAIL_ID))
        .where(RESOURCE_DETAILS.ORGANISATION_ID.in(userOrganisationUnits))
        .fetchInto(String.class);

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
}
