package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.nvl;
import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_UPDATE;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.TRAP_PROPERTIES;

import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class SubmissionWorkAreaQueryCommonService {
  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");

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
            nvl(RESOURCE_DETAILS.RESOURCE_NAME, FIELDS.CORRECTED_LEGAL_FIELD_NAME),
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            RESOURCE_DETAILS.ORGANISATION_ID,
            DSL.arrayAgg(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.isNotNull()),
            //true if update has been requested and not resolved
            field(SUBMISSION_UPDATE.SUBMISSION_ID.eq(SUBMISSION_DETAILS.SUBMISSION_ID)
                .and(SUBMISSION_UPDATE.RESOLVED.isFalse())),
            SUBMISSION_DETAILS.CORRECTION
        )
        .from(SUBMISSIONS
                .join(RESOURCES).on(RESOURCES.ID.eq(SUBMISSIONS.RESOURCE_ID))
                // Joins the relevant submission detail to the submission.
                .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(
                    select(SD_2.ID)
                        .from(SUBMISSIONS_2.join(SD_2)
                            .on(SD_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID)
                                // Gets the latest submission detail for the submission or the initial draft
                                // submission detail if there are no submitted submission details.
                                .and((SD_2.STATUS.in(SubmissionDetailStatus.DRAFT.name(), SubmissionDetailStatus.SUBMITTED.name())
                                    .and((SD_2.VERSION_NUMBER.eq(1)).or(SD_2.CORRECTION.eq(true))))
                                    .or(SD_2.STATUS.notIn(
                                        SubmissionDetailStatus.DRAFT.name(),
                                        SubmissionDetailStatus.DELETED.name())))))
                        .where(SD_2.SUBMISSION_ID.eq(SUBMISSIONS.ID))
                        .orderBy(SD_2.VERSION_NUMBER.desc())
                        .limit(1)))
            .leftJoin(SUBMISSION_UPDATE)
            .on(SUBMISSIONS.ID.eq(SUBMISSION_UPDATE.SUBMISSION_ID)
                .and(SUBMISSION_UPDATE.RESOLVED.isFalse()))
            .leftJoin(RESOURCE_DETAILS)
            .on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS)
            .on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(TRAP_PROPERTIES)
            .on(TRAP_PROPERTIES.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS_LICENCES)
            .on(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID.eq(GENERAL_DETAILS.ID))
            .leftJoin(REGULATOR_MANAGED_INFORMATION)
            .on(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID.eq(RESOURCES.ID))
            .leftJoin(FIELDS)
            .on(FIELDS.RESOURCE_ID.eq(RESOURCES.ID))
        .where(conditions))
        .groupBy(
            SUBMISSIONS.ID,
            SUBMISSION_DETAILS.ID,
            RESOURCE_DETAILS.RESOURCE_NAME,
            FIELDS.CORRECTED_LEGAL_FIELD_NAME,
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            RESOURCE_DETAILS.ORGANISATION_ID,
            SUBMISSION_UPDATE.ID,
            SUBMISSION_DETAILS.CORRECTION)
        .orderBy(
            SUBMISSION_UPDATE.RESOLVED.desc().nullsLast(),
            SUBMISSION_UPDATE.REQUESTED_TIMESTAMP.desc().nullsLast(),
            SUBMISSION_DETAILS.SUBMITTED_TIMESTAMP.desc().nullsLast(),
            SUBMISSION_DETAILS.CREATED_TIMESTAMP.desc())
        .fetchInto(SubmissionWorkAreaQueryResult.class);
  }

  Condition getVisibleSubmissions() {
    return SUBMISSION_DETAILS.STATUS
        .in(
            SubmissionDetailStatus.DRAFT.name(),
            SubmissionDetailStatus.SUBMITTED.name())
        .and(SUBMISSIONS.WITHDRAWN.isNull());
  }
}
