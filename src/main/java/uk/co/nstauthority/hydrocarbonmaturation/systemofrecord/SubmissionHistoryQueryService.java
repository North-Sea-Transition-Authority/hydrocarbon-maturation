package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_LINKS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.List;
import java.util.UUID;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
public class SubmissionHistoryQueryService {
  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");
  private final DSLContext context;

  @Autowired
  SubmissionHistoryQueryService(DSLContext context) {
    this.context = context;
  }

  public List<SubmissionQuerySearchRecord> getApprovedAndSubmittedSubmissionsOnResource(UUID resourceId) {
    return getSubmissionsOnResourceQueryResults(
        List.of(
            getResourceIdAndNotWithdrawnCondition(resourceId),
            SUBMISSION_DETAILS.STATUS.in(
                SubmissionDetailStatus.ACCEPTED.name(),
                SubmissionDetailStatus.SUBMITTED.name()))
    );
  }

  public List<SubmissionQuerySearchRecord> getApprovedSubmissionsOnResource(UUID resourceId) {
    return getSubmissionsOnResourceQueryResults(
        List.of(
            getResourceIdAndNotWithdrawnCondition(resourceId),
            SUBMISSION_DETAILS.STATUS.eq(SubmissionDetailStatus.ACCEPTED.name()))
    );
  }

  private List<SubmissionQuerySearchRecord> getSubmissionsOnResourceQueryResults(List<Condition> conditions) {
    return context.select(
            SUBMISSION_DETAILS.STATUS,
            SUBMISSION_DETAILS.ACCEPTED_TIMESTAMP,
            SUBMISSION_DETAILS.ID,
            SUBMISSION_DETAILS.SUBMISSION_TYPE)
        .from(SUBMISSIONS
            .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(
                select(SD_2.ID)
                    .from(SUBMISSIONS_2.join(SD_2)
                        .on(SD_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID)
                            //gets the latest approved or submitted submission detail on a submission
                            .and(SD_2.STATUS.in(
                                SubmissionDetailStatus.ACCEPTED.name(),
                                SubmissionDetailStatus.SUBMITTED.name()))))
                    .where(SD_2.SUBMISSION_ID.eq(SUBMISSIONS.ID))
                    .orderBy(SD_2.VERSION_NUMBER.desc())
                    .limit(1)))
            .leftJoin(RESOURCE_LINKS)
            .on(RESOURCE_LINKS.DISCOVERY_RESOURCE_ID.eq(SUBMISSIONS.RESOURCE_ID)
                .or(RESOURCE_LINKS.PROSPECT_RESOURCE_ID.eq(SUBMISSIONS.RESOURCE_ID))))
        .where(conditions)
        .orderBy(
            SUBMISSION_DETAILS.SUBMITTED_TIMESTAMP.desc(),
            // below is for legacy cases that have the same submitted date across multiple submissions.
            SUBMISSION_DETAILS.LEGACY_RES_DATA_ID.desc()
        )
        .fetchInto(SubmissionQuerySearchRecord.class);
  }

  private Condition getResourceIdAndNotWithdrawnCondition(UUID resourceId) {
    return SUBMISSIONS.WITHDRAWN.isNull().and(SUBMISSIONS.RESOURCE_ID.eq(resourceId)
        .or(RESOURCE_LINKS.DISCOVERY_RESOURCE_ID.eq(resourceId))
        .or(RESOURCE_LINKS.PROSPECT_RESOURCE_ID.eq(resourceId)));
  }
}
