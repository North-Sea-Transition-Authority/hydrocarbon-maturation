package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.TRAP_PROPERTIES;

import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class SubmissionWorkAreaQueryCommonService {
  private final DSLContext context;

  @Autowired
  SubmissionWorkAreaQueryCommonService(DSLContext context) {
    this.context = context;
  }

  List<SubmissionWorkAreaQueryResult> getWorkAreaQueryResults(List<Condition> conditions) {
    return context
        .select(
            SUBMISSION_DETAILS.ID,
            SUBMISSION_DETAILS.SUBMISSION_TYPE,
            SUBMISSION_DETAILS.STATUS,
            RESOURCE_DETAILS.RESOURCE_NAME,
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            RESOURCE_DETAILS.ORGANISATION_ID,
            DSL.arrayAgg(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.isNotNull()),
            SUBMISSION_DETAILS.CREATED_TIMESTAMP,
            SUBMISSION_DETAILS.SUBMITTED_TIMESTAMP)
        .from(SUBMISSION_DETAILS
            .leftJoin(RESOURCE_DETAILS)
            .on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS)
            .on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES)
            .on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS_LICENCES)
            .on(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID.eq(GENERAL_DETAILS.ID))
            .leftJoin(REGULATOR_MANAGED_INFORMATION)
            .on(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID.eq(SUBMISSION_DETAILS.RESOURCE_ID))
            .where(conditions))
        .groupBy(
            SUBMISSION_DETAILS.ID,
            RESOURCE_DETAILS.RESOURCE_NAME,
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            RESOURCE_DETAILS.ORGANISATION_ID)
        .orderBy(SUBMISSION_DETAILS.SUBMITTED_TIMESTAMP.desc().nullsLast(),
            SUBMISSION_DETAILS.CREATED_TIMESTAMP.desc())
        .fetchInto(SubmissionWorkAreaQueryResult.class);
  }

  Condition getVisibleSubmissions() {
    return SUBMISSION_DETAILS.STATUS
        .in(
            SubmissionDetailStatus.DRAFT.name(),
            SubmissionDetailStatus.SUBMITTED.name()
        );
  }
}
