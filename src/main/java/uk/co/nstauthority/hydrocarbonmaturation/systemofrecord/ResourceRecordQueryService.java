package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.falseCondition;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@Service
class ResourceRecordQueryService {

  public static final int MAX_RESULTS = 200;

  private final DSLContext context;
  private final JooqSchemaProviderService jooqSchemaProviderService;
  private final UserDetailService userDetailService;
  private final IndustryTeamService industryTeamService;
  private final RegulatorTeamService regulatorTeamService;
  private final LicenceQueryService licenceQueryService;

  @Autowired
  ResourceRecordQueryService(DSLContext context,
                             JooqSchemaProviderService jooqSchemaProviderService,
                             UserDetailService userDetailService,
                             IndustryTeamService industryTeamService,
                             RegulatorTeamService regulatorTeamService,
                             LicenceQueryService licenceQueryService) {
    this.context = context;
    this.jooqSchemaProviderService = jooqSchemaProviderService;
    this.userDetailService = userDetailService;
    this.industryTeamService = industryTeamService;
    this.regulatorTeamService = regulatorTeamService;
    this.licenceQueryService = licenceQueryService;
  }

  List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    var user = userDetailService.getUserDetail();
    var isRegulator = regulatorTeamService.isRegulatorUser(user);
    List<Integer> userOrgUnits = List.of();
    if (!isRegulator) {
      userOrgUnits = industryTeamService.getOrganisationUnitIdsForUserWithAnyIndustryTeamRoles(
          user, Set.of(IndustryTeamRole.SUBMITTER, IndustryTeamRole.VIEWER));
    }

    return context
        .select(
            field("r.id"),
            field("sd.id"),
            field("r.type"),
            field("rd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("rd.organisation_id"),
            DSL.arrayAggDistinct(field("gdl.licence_id"))
                .orderBy(field("gdl.licence_id"))
                .filterWhere(field("gdl.licence_id").isNotNull()),
            field("sd.created_timestamp"),
            field("sd.approved_timestamp"))
        .from(remiTable("resources").as("r")
        .join(remiTable("submission_details").as("sd"))
            .on(field("sd.id").eq(
                select(field("sd2.id"))
                .from(remiTable("submission_details").as("sd2"))
                .where(field("sd2.resource_id").eq(field("r.id")))
                .and(field("sd2.status").eq(SubmissionDetailStatus.APPROVED.name()))
                .orderBy(field("sd2.approved_timestamp").desc())
                .limit(1)
            ))
        .join(remiTable("resource_details").as("rd"))
            .on(field("rd.submission_detail_id").eq(field("sd.id")))
        .leftJoin(remiTable("resource_links").as("rl"))
            .on(field("prospect_resource_id").eq(field("r.id"))
                .or(field("discovery_resource_id").eq(field("r.id"))))
        .leftJoin(remiTable("general_details").as("gd"))
            .on(field("gd.submission_detail_id").eq(field("sd.id")))
        .leftJoin(remiTable("trap_properties").as("tp"))
            .on(field("tp.submission_detail_id").eq(field("sd.id")))
        .leftJoin(remiTable("general_details_licences").as("gdl"))
            .on(field("gdl.general_details_id").eq(field("gd.id")))
        .leftJoin(remiTable("opportunity_details").as("od"))
            .on(field("od.submission_detail_id").eq(field("sd.id")))
        .where(getConditions(form, isRegulator, userOrgUnits)))
        .groupBy(
            field("r.id"),
            field("sd.id"),
            field("gd.id"),
            field("tp.id"),
            field("rd.id")
        )
        .having(getAggregateConditions(form, isRegulator, userOrgUnits))
        .orderBy(field("sd.approved_timestamp").desc().nullsLast(),
            field("sd.created_timestamp").desc(),
            field("rd.resource_name"))
        .limit(ResourceRecordQueryService.MAX_RESULTS)
        .fetchInto(ResourceRecordQueryResult.class);
  }

  private List<Condition> getConditions(SearchResourceRecordsForm form,
                                        boolean isRegulator,
                                        List<Integer> userOrganisationUnitIds) {
    var conditionList = new ArrayList<Condition>();

    if (!isRegulator) {
      conditionList.add(field("rd.organisation_id").in(userOrganisationUnitIds));
    }

    if (StringUtils.isNotBlank(form.getResourceDetailIdForResourceSubmission())) {
      conditionList.add(
          field("rd.id").eq(UUID.fromString(form.getResourceDetailIdForResourceSubmission()))
      );
    }

    if (Objects.nonNull(form.getResponsibleOrganisationId())) {
      conditionList.add(
          field("rd.organisation_id").eq(form.getResponsibleOrganisationId())
      );
    }

    if (Objects.nonNull(form.getCentreOfInterestBlock())) {
      conditionList.add(
          field("tp.centre_of_interest_quadrant_block").eq(form.getCentreOfInterestBlock())
      );
    }

    if (!form.getResourceTypes().isEmpty()) {
      conditionList.add(field("r.type").in(getResourceTypeCondition(form)));
    }

    if (!form.getOpportunityTypes().isEmpty()) {
      conditionList.add(getOpportunityTypeCondition(form));
    }

    // excludes prospects from search results when they are linked to a discovery
    // rl.discovery_resource_id will be null if there is no link
    conditionList.add(
        field("rl.discovery_resource_id")
            .isNull()
            .or(field("r.type")
                .eq(ResourceType.DISCOVERY.name()))
    );

    return conditionList;
  }

  private List<Condition> getAggregateConditions(SearchResourceRecordsForm form,
                                                 boolean isRegulator,
                                                 List<Integer> userOrgUnits) {
    var conditionList = new ArrayList<Condition>();

    if (!isRegulator) {
      conditionList.add(getIsResourceOnAnExtantLicenceCondition(userOrgUnits));
    }

    if (Objects.nonNull(form.getLicenceId())) {
      conditionList.add(
          DSL.arrayAggDistinct(field("gdl.licence_id"))
              .orderBy(field("gdl.licence_id"))
              .filterWhere(field("gdl.licence_id")
                  .eq(Integer.parseInt(form.getLicenceId()))).isNotNull()
      );
    }

    if (Objects.nonNull(form.getLicenceRound())) {
      conditionList.add(getLicenceRoundCondition(form));
    }

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

  private Condition getLicenceRoundCondition(SearchResourceRecordsForm form) {
    // Excludes resources from the search results
    // which don't have the licence round and reference supplied to the form
    // on the latest submission or any earlier approved submissions
    return field("gd.licence_application_reference")
        .likeIgnoreCase("%" + form.getLicenceRound() + "%")
        .orExists(
            context
                .select()
                .from(remiTable("submission_details").as("sd3"))
                .join(remiTable("general_details").as("gd3"))
                .on(field("gd3.submission_detail_id").eq(field("sd3.id"))
                    .and(field("sd3.status").eq(SubmissionDetailStatus.APPROVED.name()))
                    .and(field("sd3.resource_id").eq(field("sd.resource_id"))))
                .where(field("gd3.licence_application_reference")
                    .likeIgnoreCase("%" + form.getLicenceRound() + "%")));
  }

  private Condition getOpportunityTypeCondition(SearchResourceRecordsForm form) {
    var opportunityTypeSet = new HashSet<String>();
    var opportunityTypeCheckboxes = form.getOpportunityTypes();
    opportunityTypeCheckboxes.forEach(type -> {
      if (!type.equals("NOT_SET")) {
        opportunityTypeSet.add(type);
      }
    });

    if (opportunityTypeCheckboxes.contains("NOT_SET")) {
      return field("od.opportunity_type").in(opportunityTypeSet).or(field("od.opportunity_type").isNull());
    }

    return field("od.opportunity_type").in(opportunityTypeSet);
  }

  private List<String> getResourceTypeCondition(SearchResourceRecordsForm form) {
    return form.getResourceTypes();
  }

  private Table<Record> remiTable(String tableName) {
    return jooqSchemaProviderService.remiTable(tableName);
  }
}
