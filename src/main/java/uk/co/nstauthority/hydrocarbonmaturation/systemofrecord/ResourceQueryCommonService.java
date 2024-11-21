package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELD_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.OPPORTUNITY_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_GROUP_LINKS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_LINKS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.TRAP_PROPERTIES;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Fields;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.GeneralDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class ResourceQueryCommonService {

  static final int MAX_RESULTS = 200;
  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private static final SubmissionDetails SD_3 = SUBMISSION_DETAILS.as("sd3");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s3");
  private static final GeneralDetails GD_3 = GENERAL_DETAILS.as("gd3");
  private static final FieldStatusHistory FIELD_STATUS_HISTORY = FieldStatusHistory.FIELD_STATUS_HISTORY.as("fsh");
  private static final FieldStatusHistory FSH_FOR_FINDING_MOST_RECENT_FIELD_STATUS =
      FieldStatusHistory.FIELD_STATUS_HISTORY.as("fsh2");
  private static final FieldStatusHistory FSH_FOR_FINDING_MAX_FIELD_STATUS =
      FieldStatusHistory.FIELD_STATUS_HISTORY.as("fsh3");
  private static final Fields FIELDS_FOR_FINDING_PARENT = FIELDS.as("parent_field");
  protected final DSLContext context;

  protected ResourceQueryCommonService(DSLContext context) {
    this.context = context;
  }

  protected List<ResourceRecordQueryResult> getFieldRecordQueryResults(
      List<Condition> conditionList, List<Condition> aggregateConditionList) {
    return context
        .select(
            RESOURCES.ID,
            SUBMISSION_DETAILS.ID,
            SUBMISSION_DETAILS.ACCEPTED_TIMESTAMP,
            RESOURCES.TYPE,
            FIELDS.CORRECTED_LEGAL_FIELD_NAME,
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            FIELDS.RESPONSIBLE_PARTY_PRIOR_TO_FIELD_OPERATOR_ID,
            DSL.arrayAggDistinct(FIELD_LICENCES.LICENCE_ID)
                .orderBy(FIELD_LICENCES.LICENCE_ID)
                .filterWhere(FIELD_LICENCES.LICENCE_ID.isNotNull()),
            REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM,
            FIELDS_FOR_FINDING_PARENT.CORRECTED_LEGAL_FIELD_NAME,
            FIELD_STATUS_HISTORY.FIELD_STATUS,
            GENERAL_DETAILS.LICENCE_APPLICATION_REFERENCE,
            RESOURCES.ARCHIVED)
        .from(RESOURCES
            .join(FIELDS).on(FIELDS.RESOURCE_ID.eq(RESOURCES.ID))
            .join(FIELD_STATUS_HISTORY)
            .on(and(
                FIELD_STATUS_HISTORY.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER),
                FIELD_STATUS_HISTORY.FIELD_STATUS.eq(
                    select(max(FSH_FOR_FINDING_MAX_FIELD_STATUS.FIELD_STATUS))
                        .from(FSH_FOR_FINDING_MAX_FIELD_STATUS)
                        .where(FSH_FOR_FINDING_MAX_FIELD_STATUS.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
                        .and(FSH_FOR_FINDING_MAX_FIELD_STATUS.STATUS_DATE.eq(
                            select(max(FSH_FOR_FINDING_MOST_RECENT_FIELD_STATUS.STATUS_DATE))
                                .from(FSH_FOR_FINDING_MOST_RECENT_FIELD_STATUS)
                                .where(FSH_FOR_FINDING_MOST_RECENT_FIELD_STATUS.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
                        ))
                )))
            .leftJoin(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(
                select(SD_2.ID)
                    .from(SUBMISSIONS.join(SD_2)
                        //gets latest approved submission detail for a submission on a resource
                        .on(SD_2.SUBMISSION_ID.eq(SUBMISSIONS.ID)
                            .and(SD_2.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name()))))
                    .where(SUBMISSIONS.RESOURCE_ID.eq(RESOURCES.ID)
                        .and(SUBMISSIONS.WITHDRAWN.isNull()))
                    .orderBy(SD_2.ACCEPTED_TIMESTAMP.desc().nullsLast())
                    .limit(1)))
            .leftJoin(TRAP_PROPERTIES).on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(FIELD_LICENCES).on(FIELD_LICENCES.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
            .leftJoin(REGULATOR_MANAGED_INFORMATION).on(RESOURCES.ID.eq(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID))
            .leftJoin(FIELDS_FOR_FINDING_PARENT)
            .on(FIELDS_FOR_FINDING_PARENT.FIELD_IDENTIFIER.eq(FIELDS.PARENT_DETERMINED_FIELD_ID))
            .leftJoin(GENERAL_DETAILS).on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID)))
        .where(conditionList)
        .groupBy(
            RESOURCES.ID,
            SUBMISSION_DETAILS.ID,
            FIELD_LICENCES.ID,
            TRAP_PROPERTIES.ID,
            REGULATOR_MANAGED_INFORMATION.ID,
            FIELDS.FIELD_IDENTIFIER,
            FIELDS_FOR_FINDING_PARENT.FIELD_IDENTIFIER,
            FIELD_STATUS_HISTORY.ID,
            GENERAL_DETAILS.ID
        )
        .having(aggregateConditionList)
        .orderBy(
            FIELD_STATUS_HISTORY.STATUS_DATE.desc(),
            FIELDS.CORRECTED_LEGAL_FIELD_NAME)
        .limit(ResourceQueryCommonService.MAX_RESULTS)
        .fetchInto(ResourceRecordQueryResult.class);
  }

  protected List<ResourceRecordQueryResult> getProspectAndDiscoveryRecordQueryResults(
      List<Condition> conditionList, List<Condition> aggregateConditionList) {
    return context
        .select(
            RESOURCES.ID,
            SUBMISSION_DETAILS.ID,
            SUBMISSION_DETAILS.ACCEPTED_TIMESTAMP,
            RESOURCES.TYPE,
            RESOURCE_DETAILS.RESOURCE_NAME,
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            RESOURCE_DETAILS.ORGANISATION_ID,
            DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.isNotNull()),
            REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM,
            DSL.val(null, FIELDS_FOR_FINDING_PARENT.CORRECTED_LEGAL_FIELD_NAME),
            DSL.val(null, FIELD_STATUS_HISTORY.FIELD_STATUS),
            GENERAL_DETAILS.LICENCE_APPLICATION_REFERENCE,
            RESOURCES.ARCHIVED)
        .from(RESOURCES
            .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(
                select(SD_2.ID)
                    .from(SUBMISSIONS.join(SD_2)
                        //gets latest approved submission detail for a submission on a resource
                        .on(SD_2.SUBMISSION_ID.eq(SUBMISSIONS.ID)
                            .and(SD_2.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name()))))
                    .where(SUBMISSIONS.RESOURCE_ID.eq(RESOURCES.ID)
                        .and(SUBMISSIONS.WITHDRAWN.isNull()))
                    .orderBy(SD_2.ACCEPTED_TIMESTAMP.desc().nullsLast(), SD_2.LEGACY_RES_DATA_ID.desc())
                    .limit(1)))
            .leftJoin(RESOURCE_DETAILS).on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(RESOURCE_GROUP_LINKS).on(RESOURCE_GROUP_LINKS.RESOURCE_ID.eq(RESOURCES.ID))
            .leftJoin(RESOURCE_LINKS).on(RESOURCE_LINKS.PROSPECT_RESOURCE_ID.eq(RESOURCES.ID)
                .or(RESOURCE_LINKS.DISCOVERY_RESOURCE_ID.eq(RESOURCES.ID)))
            .leftJoin(GENERAL_DETAILS).on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES).on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS_LICENCES).on(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID.eq(GENERAL_DETAILS.ID))
            .leftJoin(REGULATOR_MANAGED_INFORMATION).on(RESOURCES.ID.eq(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID))
            .leftJoin(OPPORTUNITY_DETAILS).on(OPPORTUNITY_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID)))
        .where(conditionList)
        .groupBy(
            RESOURCES.ID,
            SUBMISSION_DETAILS.ID,
            GENERAL_DETAILS.ID,
            TRAP_PROPERTIES.ID,
            RESOURCE_DETAILS.ID,
            REGULATOR_MANAGED_INFORMATION.ID,
            GENERAL_DETAILS.ID
        )
        .having(aggregateConditionList)
        .orderBy(SUBMISSION_DETAILS.ACCEPTED_TIMESTAMP.desc().nullsLast(),
            SUBMISSION_DETAILS.CREATED_TIMESTAMP.desc(),
            SUBMISSION_DETAILS.LEGACY_RES_DATA_ID.desc(),
            RESOURCE_DETAILS.RESOURCE_NAME)
        .limit(ResourceQueryCommonService.MAX_RESULTS)
        .fetchInto(ResourceRecordQueryResult.class);
  }

  protected List<Condition> getFormFilterConditions(SearchResourceRecordsForm form) {
    var conditionList = new ArrayList<Condition>();

    if (StringUtils.isNotBlank(form.getResourceNameFromResourceDetail())) {
      conditionList.add(
          RESOURCE_DETAILS.RESOURCE_NAME.containsIgnoreCase(form.getResourceNameFromResourceDetail())
      );
    }

    if (StringUtils.isNotBlank(form.getFieldNameForResourceSubmission())) {
      conditionList.add(
          FIELDS.CORRECTED_LEGAL_FIELD_NAME.containsIgnoreCase(form.getFieldNameForResourceSubmission())
      );
    }

    if (StringUtils.isNotBlank(form.getFieldNameForResourceSubmission())) {
      conditionList.add(
          FIELDS.CORRECTED_LEGAL_FIELD_NAME.containsIgnoreCase(form.getFieldNameForResourceSubmission())
      );
    }

    if (Objects.nonNull(form.getResponsibleOrganisationId())) {
      conditionList.add(
          RESOURCE_DETAILS.ORGANISATION_ID.eq(form.getResponsibleOrganisationId())
      );
    }

    if (Objects.nonNull(form.getCentreOfInterestBlock())) {
      conditionList.add(
          TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK.eq(form.getCentreOfInterestBlock())
      );
    }

    if (!form.getResourceTypes().isEmpty()) {
      conditionList.add(RESOURCES.TYPE.in(getResourceTypeCondition(form)));
    }

    if (!form.getOpportunityTypes().isEmpty()) {
      conditionList.add(getOpportunityTypeCondition(form));
    }

    if (!form.getGeographicalArea().isEmpty()) {
      conditionList.add(getGeographicalAreaCondition(form));
    }

    // excludes prospects from search results when they are linked to a discovery
    // rl.discovery_resource_id will be null if there is no link
    if (form.getResourceTypes().contains(ResourceType.PROSPECT.getEnumName())) {
      conditionList.add(
          RESOURCE_LINKS.DISCOVERY_RESOURCE_ID
              .isNull()
              .or(RESOURCES.TYPE
                  .eq(ResourceType.DISCOVERY.name()))
      );
    }

    if (!form.getFieldStatuses().isEmpty()) {
      conditionList.add(FIELD_STATUS_HISTORY.FIELD_STATUS.in(form.getFieldStatuses()));
    }

    if (StringUtils.isNotBlank(form.getParentFieldName())) {
      conditionList.add(
          FIELDS_FOR_FINDING_PARENT.CORRECTED_LEGAL_FIELD_NAME.containsIgnoreCase(form.getParentFieldName())
      );
    }

    return conditionList;
  }

  private Condition getGeographicalAreaCondition(SearchResourceRecordsForm form) {
    return REGULATOR_MANAGED_INFORMATION.GEOGRAPHICAL_AREA.in(form.getGeographicalArea());
  }

  protected List<Condition> getFormFilterAggregateConditions(SearchResourceRecordsForm form) {
    var conditionList = new ArrayList<Condition>();

    if (Objects.nonNull(form.getLicenceId())) {
      conditionList.add(
          DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
              .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
              .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID
                  .eq(Integer.parseInt(form.getLicenceId()))).isNotNull()
      );
    }

    if (Objects.nonNull(form.getLicenceRound())) {
      conditionList.add(getLicenceRoundCondition(form));
    }

    return conditionList;
  }

  Condition getLicenceRoundCondition(SearchResourceRecordsForm form) {
    // Excludes resources from the search results
    // which don't have the licence round and reference supplied to the form
    // on the latest submission or any earlier approved submissions
    return GENERAL_DETAILS.LICENCE_APPLICATION_REFERENCE
        .likeIgnoreCase("%" + form.getLicenceRound() + "%")
        .orExists(select()
                .from(SD_3
                    .join(SUBMISSIONS_2).on(SUBMISSIONS_2.ID.eq(SD_3.SUBMISSION_ID))
                  .join(GD_3).on(GD_3.SUBMISSION_DETAIL_ID.eq(SD_3.ID)
                    .and(SD_3.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name()))))
                .where(GD_3.LICENCE_APPLICATION_REFERENCE
                    .likeIgnoreCase("%" + form.getLicenceRound() + "%")
                    .and(SUBMISSIONS_2.RESOURCE_ID.eq(RESOURCES.ID))));
  }

  Condition getOpportunityTypeCondition(SearchResourceRecordsForm form) {
    var opportunityTypeSet = new HashSet<String>();
    var opportunityTypeCheckboxes = form.getOpportunityTypes();
    opportunityTypeCheckboxes.forEach(type -> {
      if (!type.equals("NOT_SET")) {
        opportunityTypeSet.add(type);
      }
    });

    if (opportunityTypeCheckboxes.contains("NOT_SET")) {
      return OPPORTUNITY_DETAILS.OPPORTUNITY_TYPE.in(opportunityTypeSet).or(OPPORTUNITY_DETAILS.OPPORTUNITY_TYPE.isNull());
    }

    return OPPORTUNITY_DETAILS.OPPORTUNITY_TYPE.in(opportunityTypeSet);
  }

  List<String> getResourceTypeCondition(SearchResourceRecordsForm form) {
    return form.getResourceTypes();
  }
}
