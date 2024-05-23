package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.TRAP_PROPERTIES;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.FieldStatusHistory;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class SubmissionRecordSummaryQueryService {
  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");

  private static final FieldStatusHistory FIELD_STATUS_HISTORY = FieldStatusHistory.FIELD_STATUS_HISTORY.as("fsh");
  private static final FieldStatusHistory FSH_FOR_FINDING_MOST_RECENT_FIELD_STATUS =
      FieldStatusHistory.FIELD_STATUS_HISTORY.as("fsh2");

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
            coalesce(
                RESOURCE_DETAILS.RESOURCE_NAME,
                FIELDS.CORRECTED_LEGAL_FIELD_NAME),
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            coalesce(
                RESOURCE_DETAILS.ORGANISATION_ID,
                FIELDS.RESPONSIBLE_ORGANISATION_ID),
            REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM,
            DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.isNotNull()),
            FIELDS.PARENT_DETERMINED_FIELD_ID,
            max(FIELD_STATUS_HISTORY.FIELD_STATUS),
            GENERAL_DETAILS.LICENCE_APPLICATION_REFERENCE
            )
        .from(SUBMISSIONS
            .join(RESOURCES).on(RESOURCES.ID.eq(SUBMISSIONS.RESOURCE_ID))
            .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.SUBMISSION_ID.eq(
                select(SD_2.SUBMISSION_ID)
                    .from(SUBMISSIONS_2.join(SD_2)
                        .on(SUBMISSIONS_2.ID.eq(SD_2.SUBMISSION_ID)
                            //gets latest accepted or submitted submission detail for a submission
                            .and(SD_2.STATUS.in(
                                SubmissionDetailStatus.SUBMITTED.name(),
                                SubmissionDetailStatus.ACCEPTED.name()))))
                    .where(SD_2.SUBMISSION_ID.eq(SUBMISSIONS.ID))
                    .orderBy(SD_2.ACCEPTED_TIMESTAMP.desc().nullsLast(), SD_2.VERSION_NUMBER.desc())
                    .limit(1)))
            .leftJoin(REGULATOR_MANAGED_INFORMATION).on(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID.eq(RESOURCES.ID))
            .leftJoin(RESOURCE_DETAILS).on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS).on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES).on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS_LICENCES).on(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID.eq(GENERAL_DETAILS.ID))
            .leftJoin(FIELDS).on(FIELDS.RESOURCE_ID.eq(RESOURCES.ID))
            .leftJoin(FIELD_STATUS_HISTORY)
            .on(and(
                FIELD_STATUS_HISTORY.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER),
                FIELD_STATUS_HISTORY.STATUS_DATE.eq(
                    select(max(FSH_FOR_FINDING_MOST_RECENT_FIELD_STATUS.STATUS_DATE))
                        .from(FSH_FOR_FINDING_MOST_RECENT_FIELD_STATUS)
                        .where(FSH_FOR_FINDING_MOST_RECENT_FIELD_STATUS.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
                ))))
        .where(RESOURCES.ID.eq(resourceId)
        .and(SUBMISSION_DETAILS.STATUS.in(
            SubmissionDetailStatus.SUBMITTED.name(),
            SubmissionDetailStatus.ACCEPTED.name()))
        .and(SUBMISSIONS.WITHDRAWN.isNull()))
        .groupBy(List.of(
            RESOURCES.ID,
            SUBMISSION_DETAILS.ID,
            RESOURCE_DETAILS.RESOURCE_NAME,
            FIELDS.CORRECTED_LEGAL_FIELD_NAME,
            GENERAL_DETAILS.ID,
            TRAP_PROPERTIES.ID,
            RESOURCE_DETAILS.ID,
            FIELDS.FIELD_IDENTIFIER,
            REGULATOR_MANAGED_INFORMATION.ID,
            FIELD_STATUS_HISTORY.FIELD_IDENTIFIER,
            GENERAL_DETAILS.ID
        ))
        .orderBy(SUBMISSION_DETAILS.SUBMITTED_TIMESTAMP.desc())
        .limit(1)
        .fetchOptionalInto(SubmissionRecordSummaryQueryResult.class);
  }
}
