package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class ResourceRecordQueryService {

  private final DSLContext context;
  private final JooqSchemaProviderService jooqSchemaProviderService;

  @Autowired
  ResourceRecordQueryService(DSLContext context,
                             JooqSchemaProviderService jooqSchemaProviderService) {
    this.context = context;
    this.jooqSchemaProviderService = jooqSchemaProviderService;
  }

  List<ResourceRecordQueryResult> getResourceRecordSearchResults(SearchResourceRecordsForm form) {
    return context
        .select(
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
        .leftJoin(remiTable("general_details").as("gd"))
            .on(field("gd.submission_detail_id").eq(field("sd.id")))
        .leftJoin(remiTable("trap_properties").as("tp"))
            .on(field("tp.submission_detail_id").eq(field("sd.id")))
        .leftJoin(remiTable("general_details_licences").as("gdl"))
            .on(field("gdl.general_details_id").eq(field("gd.id")))
        .where(getConditions(form)))
        .groupBy(
            field("r.id"),
            field("sd.id"),
            field("gd.id"),
            field("tp.id"),
            field("rd.id")
        )
        .orderBy(field("sd.approved_timestamp").desc().nullsLast(),
            field("sd.created_timestamp").desc())
        .fetchInto(ResourceRecordQueryResult.class);
  }

  private List<Condition> getConditions(SearchResourceRecordsForm form) {
    List<Condition> conditionList = new ArrayList<>();

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

    conditionList.add(field("r.type").in(getResourceTypeCondition(form)));

    return conditionList;
  }

  private Set<String> getResourceTypeCondition(SearchResourceRecordsForm form) {
    var resourceTypeSet = new HashSet<String>();
    if (BooleanUtils.isTrue(form.isProspectResourceType())) {
      resourceTypeSet.add(ResourceType.PROSPECT.name());
    }
    if (BooleanUtils.isTrue(form.isDiscoveryResourceType())) {
      resourceTypeSet.add(ResourceType.DISCOVERY.name());
    }
    return resourceTypeSet;
  }

  private Table<Record> remiTable(String tableName) {
    return jooqSchemaProviderService.remiTable(tableName);
  }
}
