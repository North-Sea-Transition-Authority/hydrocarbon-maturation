package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.and;
import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.max;
import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELD_LICENCES;
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
class ResourceRecordSummaryQueryService {
  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");

  private static final FieldStatusHistory FIELD_STATUS_HISTORY = FieldStatusHistory.FIELD_STATUS_HISTORY.as("fsh");
  private static final FieldStatusHistory FSH_2 = FieldStatusHistory.FIELD_STATUS_HISTORY.as("fsh2");

  private final DSLContext context;

  @Autowired
  ResourceRecordSummaryQueryService(DSLContext context) {
    this.context = context;
  }

  Optional<ResourceRecordSummaryQueryResult> getResourceRecord(UUID resourceId) {
    return context.select(
            SUBMISSION_DETAILS.ID,
            RESOURCES.TYPE,
            coalesce(
                RESOURCE_DETAILS.RESOURCE_NAME,
                FIELDS.CORRECTED_LEGAL_FIELD_NAME),
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            GENERAL_DETAILS.LICENCE_APPLICATION_REFERENCE,
            coalesce(
                RESOURCE_DETAILS.ORGANISATION_ID,
                FIELDS.RESPONSIBLE_PARTY_PRIOR_TO_FIELD_OPERATOR_ID),
            REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM,
            coalesce(
                DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.isNotNull()),
                DSL.arrayAggDistinct(FIELD_LICENCES.LICENCE_PRIOR_TO_FIELD_OPERATOR_ID)
                    .orderBy(FIELD_LICENCES.LICENCE_PRIOR_TO_FIELD_OPERATOR_ID)
                    .filterWhere(FIELD_LICENCES.LICENCE_PRIOR_TO_FIELD_OPERATOR_ID.isNotNull())
            ),
            FIELDS.PARENT_DETERMINED_FIELD_ID,
            max(FIELD_STATUS_HISTORY.FIELD_STATUS)
        )
        .from(RESOURCES
            .leftJoin(SUBMISSIONS).on(RESOURCES.ID.eq(SUBMISSIONS.RESOURCE_ID))
            // Joins the relevant submission detail to the submission.
            .leftJoin(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(
                select(SD_2.ID)
                    .from(SUBMISSIONS_2.join(SD_2)
                        .on(SD_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID)
                            .and(SD_2.STATUS.in(SubmissionDetailStatus.ACCEPTED.name()))))
                    .where(SD_2.SUBMISSION_ID.eq(SUBMISSIONS.ID))
                    .orderBy(SD_2.VERSION_NUMBER.desc())
                    .limit(1)))
            .leftJoin(RESOURCE_DETAILS)
            .on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS)
            .on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS_LICENCES)
            .on(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID.eq(GENERAL_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES)
            .on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(REGULATOR_MANAGED_INFORMATION)
            .on(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID.eq(RESOURCES.ID))
            .leftJoin(FIELDS)
            .on(FIELDS.RESOURCE_ID.eq(RESOURCES.ID))
            .leftJoin(FIELD_LICENCES)
            .on(FIELD_LICENCES.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
            .leftJoin(FIELD_STATUS_HISTORY)
            .on(and(
                FIELD_STATUS_HISTORY.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER),
                FIELD_STATUS_HISTORY.STATUS_DATE.eq(
                    select(max(FSH_2.STATUS_DATE))
                        .from(FSH_2)
                        .where(FSH_2.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
                ))))
        .where(RESOURCES.ID.eq(resourceId).and(SUBMISSIONS.WITHDRAWN.isNull()))
        .groupBy(List.of(
            SUBMISSION_DETAILS.ID,
            RESOURCES.TYPE,
            RESOURCE_DETAILS.RESOURCE_NAME,
            FIELDS.CORRECTED_LEGAL_FIELD_NAME,
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            GENERAL_DETAILS.ID,
            RESOURCE_DETAILS.ORGANISATION_ID,
            FIELDS.RESPONSIBLE_PARTY_PRIOR_TO_FIELD_OPERATOR_ID,
            REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM,
            FIELDS.FIELD_IDENTIFIER,
            FIELD_STATUS_HISTORY.FIELD_IDENTIFIER
        ))
        .orderBy(SUBMISSION_DETAILS.ACCEPTED_TIMESTAMP.desc().nullsLast(), SUBMISSION_DETAILS.LEGACY_RES_DATA_ID.desc())
        .limit(1)
        .fetchOptionalInto(ResourceRecordSummaryQueryResult.class);
  }
}
