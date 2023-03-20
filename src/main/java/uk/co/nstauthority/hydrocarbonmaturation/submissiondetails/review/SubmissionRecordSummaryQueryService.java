package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.TRAP_PROPERTIES;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class SubmissionRecordSummaryQueryService {

  private final DSLContext context;

  @Autowired
  SubmissionRecordSummaryQueryService(DSLContext dslContext) {
    this.context = dslContext;
  }

  Optional<SubmissionRecordSummaryQueryResult> getSubmissionRecord(UUID resourceId) {
    return context.select(
            SUBMISSION_DETAILS.ID,
            SUBMISSION_DETAILS.SUBMISSION_TYPE,
            RESOURCES.TYPE,
            RESOURCE_DETAILS.RESOURCE_NAME,
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            RESOURCE_DETAILS.ORGANISATION_ID,
            REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM,
            DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.isNotNull()),
            SUBMISSION_DETAILS.CREATED_TIMESTAMP,
            SUBMISSION_DETAILS.SUBMITTED_TIMESTAMP
        )
        .from(SUBMISSION_DETAILS
            .join(RESOURCES).on(RESOURCES.ID.eq(SUBMISSION_DETAILS.RESOURCE_ID))
            .leftJoin(REGULATOR_MANAGED_INFORMATION).on(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID.eq(RESOURCES.ID))
            .join(RESOURCE_DETAILS).on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS).on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES).on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS_LICENCES).on(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID.eq(GENERAL_DETAILS.ID)))
        .where(RESOURCES.ID.eq(resourceId))
        .groupBy(List.of(
            RESOURCES.ID,
            SUBMISSION_DETAILS.ID,
            GENERAL_DETAILS.ID,
            TRAP_PROPERTIES.ID,
            RESOURCE_DETAILS.ID,
            REGULATOR_MANAGED_INFORMATION.ID
        ))
        .orderBy(SUBMISSION_DETAILS.SUBMITTED_TIMESTAMP.desc())
        .limit(1)
        .fetchOptionalInto(SubmissionRecordSummaryQueryResult.class);
  }
}
