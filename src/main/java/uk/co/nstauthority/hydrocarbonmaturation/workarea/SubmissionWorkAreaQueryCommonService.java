package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.field;

import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class SubmissionWorkAreaQueryCommonService {

  private final JooqSchemaProviderService jooqSchemaProviderService;
  private final DSLContext context;

  @Autowired
  SubmissionWorkAreaQueryCommonService(JooqSchemaProviderService jooqSchemaProviderService, DSLContext context) {
    this.jooqSchemaProviderService = jooqSchemaProviderService;
    this.context = context;
  }

  List<SubmissionWorkAreaQueryResult> getWorkAreaQueryResults(List<Condition> conditions) {
    return context
        .select(
            field("s.id"),
            field("s.submission_type"),
            field("s.status"),
            field("rd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("rd.organisation_id"),
            DSL.arrayAgg(field("gdl.licence_id"))
                .filterWhere(field("gdl.licence_id").isNotNull()),
            field("s.created_timestamp"),
            field("s.submitted_timestamp"))
        .from(remiTable("submission_details").as("s")
            .leftJoin(remiTable("resource_details").as("rd"))
            .on(field("rd.submission_detail_id").eq(field("s.id")))
            .leftJoin(remiTable("general_details").as("gd"))
            .on(field("gd.submission_detail_id").eq(field("s.id")))
            .leftJoin(remiTable("trap_properties").as("tp"))
            .on(field("tp.submission_detail_id").eq(field("s.id")))
            .leftJoin(remiTable("general_details_licences").as("gdl"))
            .on(field("gdl.general_details_id").eq(field("gd.id")))
            .leftJoin(remiTable("responsible_teams").as("rt"))
            .on(field("rt.resource_id").eq(field("s.resource_id")))
            .where(conditions))
        .groupBy(
            field("s.id"),
            field("rd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("rd.organisation_id"))
        .orderBy(field("s.submitted_timestamp").desc().nullsLast(),
            field("s.created_timestamp").desc())
        .fetchInto(SubmissionWorkAreaQueryResult.class);
  }

  Condition getVisibleSubmissions() {
    return field("s.status")
        .in(
            SubmissionDetailStatus.DRAFT.name(),
            SubmissionDetailStatus.SUBMITTED.name()
        );
  }

  private Table<Record> remiTable(String tableName) {
    return jooqSchemaProviderService.remiTable(tableName);
  }

}
