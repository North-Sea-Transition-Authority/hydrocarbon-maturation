package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.jooq.impl.DSL.field;

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

@Service
class SubmissionRecordSummaryQueryService {

  private final DSLContext context;
  private final JooqSchemaProviderService jooqSchemaProviderService;

  @Autowired
  SubmissionRecordSummaryQueryService(DSLContext dslContext,
                                      JooqSchemaProviderService jooqSchemaProviderService) {
    this.context = dslContext;
    this.jooqSchemaProviderService = jooqSchemaProviderService;
  }

  Optional<SubmissionRecordSummaryQueryResult> getSubmissionRecord(UUID submissionDetailId) {
    return context.select(
            field("sd.id"),
            field("sd.submission_type"),
            field("r.type"),
            field("rd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("rd.organisation_id"),
            field("rt.responsible_team"),
            DSL.arrayAggDistinct(field("gdl.licence_id"))
                .orderBy(field("gdl.licence_id"))
                .filterWhere(field("gdl.licence_id").isNotNull()),
            field("sd.created_timestamp"),
            field("sd.submitted_timestamp")
        )
        .from(remiTable("submission_details").as("sd")
            .join(remiTable("resources").as("r"))
            .on(field("r.id").eq(field("sd.resource_id")))
            .leftJoin(remiTable("responsible_teams").as("rt"))
            .on(field("rt.resource_id").eq(field("r.id")))
            .join(remiTable("resource_details").as("rd"))
            .on(field("rd.submission_detail_id").eq(field("sd.id")))
            .leftJoin(remiTable("general_details").as("gd"))
            .on(field("gd.submission_detail_id").eq(field("sd.id")))
            .leftJoin(remiTable("trap_properties").as("tp"))
            .on(field("tp.submission_detail_id").eq(field("sd.id")))
            .leftJoin(remiTable("general_details_licences").as("gdl"))
            .on(field("gdl.general_details_id").eq(field("gd.id"))))
        .where(field("sd.id").eq(submissionDetailId))
        .groupBy(List.of(
            field("r.id"),
            field("sd.id"),
            field("gd.id"),
            field("tp.id"),
            field("rd.id"),
            field("rt.id")
        ))
        .fetchOptionalInto(SubmissionRecordSummaryQueryResult.class);
  }

  private Table<Record> remiTable(String tableName) {
    return jooqSchemaProviderService.remiTable(tableName);
  }
}
