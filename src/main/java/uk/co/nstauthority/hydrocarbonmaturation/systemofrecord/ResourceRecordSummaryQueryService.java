package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.select;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class ResourceRecordSummaryQueryService {

  private final DSLContext context;
  private final JooqSchemaProviderService jooqSchemaProviderService;

  @Autowired
  ResourceRecordSummaryQueryService(DSLContext context,
                                    JooqSchemaProviderService jooqSchemaProviderService) {
    this.context = context;
    this.jooqSchemaProviderService = jooqSchemaProviderService;
  }

  Optional<ResourceRecordSummaryQueryResult> getResourceRecord(UUID resourceId) {
    return context.select(
            field("sd.id"),
            field("r.type"),
            field("rd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("rd.organisation_id"),
            field("rt.responsible_team"),
            DSL.arrayAggDistinct(field("gdl.licence_id"))
                .orderBy(field("gdl.licence_id"))
                .filterWhere(field("gdl.licence_id").isNotNull()),
            field("sd.created_timestamp"),
            field("sd.approved_timestamp")
        )
        .from(remiTable("resources").as("r")
          .leftJoin(remiTable("responsible_teams").as("rt"))
            .on(field("rt.resource_id").eq(field("r.id")))
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
            .on(field("gdl.general_details_id").eq(field("gd.id"))))
        .where(field("r.id").eq(resourceId))
        .groupBy(List.of(
            field("r.id"),
            field("sd.id"),
            field("gd.id"),
            field("tp.id"),
            field("rd.id"),
            field("rt.id")
        ))
        .fetchOptionalInto(ResourceRecordSummaryQueryResult.class);
  }

  private Table<Record> remiTable(String tableName) {
    return jooqSchemaProviderService.remiTable(tableName);
  }
}
