package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.falseCondition;
import static org.jooq.impl.DSL.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
class OperatorResourceRecordQueryService implements ResourceRecordQueryServiceStrategy {

  private final UserDetailService userDetailService;
  private final IndustryTeamService industryTeamService;
  private final LicenceQueryService licenceQueryService;
  private final DSLContext context;
  private final ResourceQueryCommonService resourceQueryCommonService;

  OperatorResourceRecordQueryService(UserDetailService userDetailService,
                                     IndustryTeamService industryTeamService,
                                     LicenceQueryService licenceQueryService,
                                     DSLContext context,
                                     ResourceQueryCommonService resourceQueryCommonService) {
    this.userDetailService = userDetailService;
    this.industryTeamService = industryTeamService;
    this.licenceQueryService = licenceQueryService;
    this.context = context;
    this.resourceQueryCommonService = resourceQueryCommonService;
  }

  @Override
  public List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    var user = userDetailService.getUserDetail();
    var userOrgUnitIds = industryTeamService.getOrganisationUnitIdsForUserWithIndustryTeamRoles(
          user, Set.of(IndustryTeamRole.SUBMITTER, IndustryTeamRole.VIEWER));
    return resourceQueryCommonService.getResourceRecordQueryResults(
        getOperatorConditions(form, userOrgUnitIds),
        getOperatorAggregateConditions(form, userOrgUnitIds)
    );
  }

  private List<Condition> getOperatorConditions(SearchResourceRecordsForm form, List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterConditions(form));
    conditionList.add(field("rd.organisation_id").in(userOrgUnitIds));

    return conditionList;
  }

  private List<Condition> getOperatorAggregateConditions(SearchResourceRecordsForm form, List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterAggregateConditions(form));
    conditionList.add(getIsResourceOnAnExtantLicenceCondition(userOrgUnitIds));
    return conditionList;
  }

  private Condition getIsResourceOnAnExtantLicenceCondition(List<Integer> userOrganisationUnits) {
    var userLicences = context
        .selectDistinct(field("gdl.licence_id"))
        .from(remiTable("general_details_licences").as("gdl"))
        .join(remiTable("general_details").as("gd"))
        .on(field("gd.id").eq(field("gdl.general_details_id")))
        .join(remiTable("resource_details").as("rd"))
        .on(field("rd.submission_detail_id").eq(field("gd.submission_detail_id")))
        .where(field("rd.organisation_id").in(userOrganisationUnits))
        .fetchInto(String.class);

    if (userLicences.isEmpty()) {
      return falseCondition();
    }

    var userExtantLicences = NumberUtil.toListOfIntegers(
        licenceQueryService.getExtantLicencesByIds(userLicences)
            .stream()
            .map(LicenceView::getId)
            .toList());

    return DSL.arrayAggDistinct(field("gdl.licence_id"))
        .orderBy(field("gdl.licence_id"))
        .filterWhere(field("gdl.licence_id").in(userExtantLicences))
        .isNotNull();
  }

  private Table<Record> remiTable(String tableName) {
    return resourceQueryCommonService.remiTable(tableName);
  }
}
