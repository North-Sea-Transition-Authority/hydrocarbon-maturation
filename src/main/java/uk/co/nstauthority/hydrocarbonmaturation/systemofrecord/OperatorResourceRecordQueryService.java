package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.falseCondition;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELD_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
public class OperatorResourceRecordQueryService implements ResourceRecordQueryServiceStrategy {

  private final IndustryTeamService industryTeamService;
  private final LicenceQueryService licenceQueryService;
  private final DSLContext context;
  private final ResourceQueryCommonService resourceQueryCommonService;

  @Autowired
  OperatorResourceRecordQueryService(IndustryTeamService industryTeamService,
                                     LicenceQueryService licenceQueryService,
                                     DSLContext context,
                                     ResourceQueryCommonService resourceQueryCommonService) {
    this.industryTeamService = industryTeamService;
    this.licenceQueryService = licenceQueryService;
    this.context = context;
    this.resourceQueryCommonService = resourceQueryCommonService;
  }

  @Override
  public List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form, User user) {
    var userOrgUnitIds = industryTeamService.getOrganisationUnitIdsForUserWithRoles(
          user, Set.of(Role.INDUSTRY_SUBMIT, Role.INDUSTRY_VIEWER));

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
    conditionList.add(RESOURCES.ARCHIVED.eq(false));

    return conditionList;
  }

  private List<Condition> getFieldOperatorConditions(SearchResourceRecordsForm form, List<Integer> userOrgUnitIds) {
    var conditionList = new ArrayList<>(resourceQueryCommonService.getFormFilterConditions(form));
    conditionList.add(FIELDS.RESPONSIBLE_PARTY_PRIOR_TO_FIELD_OPERATOR_ID.in(userOrgUnitIds));
    conditionList.add(RESOURCES.ARCHIVED.eq(false));

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

    return DSL.arrayAggDistinct(FIELD_LICENCES.LICENCE_ID)
        .orderBy(FIELD_LICENCES.LICENCE_ID)
        .filterWhere(FIELD_LICENCES.LICENCE_ID.in(userExtantLicences))
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
        .selectDistinct(FIELD_LICENCES.LICENCE_ID)
        .from(FIELD_LICENCES)
        .join(FIELDS).on(FIELDS.FIELD_IDENTIFIER.eq(FIELD_LICENCES.FIELD_IDENTIFIER))
        .where(FIELDS.RESPONSIBLE_PARTY_PRIOR_TO_FIELD_OPERATOR_ID.in(userOrganisationUnits))
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
