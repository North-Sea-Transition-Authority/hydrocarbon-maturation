package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_LINKS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.util.List;
import java.util.UUID;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class SubmissionHistoryQueryService {
  private final DSLContext context;

  @Autowired
  SubmissionHistoryQueryService(DSLContext context) {
    this.context = context;
  }

  List<SubmissionQuerySearchRecord> getApprovedAndSubmittedSubmissionsOnResource(UUID resourceId) {
    return getSubmissionsOnResourceQueryResults(
        List.of(
            getResourceIdCondition(resourceId),
            SUBMISSION_DETAILS.STATUS.in(
                SubmissionDetailStatus.APPROVED.name(),
                SubmissionDetailStatus.SUBMITTED.name()))
    );
  }

  List<SubmissionQuerySearchRecord> getApprovedSubmissionsOnResource(UUID resourceId) {
    return getSubmissionsOnResourceQueryResults(
        List.of(
            getResourceIdCondition(resourceId),
            SUBMISSION_DETAILS.STATUS.eq(SubmissionDetailStatus.APPROVED.name()))
    );
  }

  private List<SubmissionQuerySearchRecord> getSubmissionsOnResourceQueryResults(List<Condition> conditions) {
    return context.select(
            SUBMISSION_DETAILS.STATUS,
            SUBMISSION_DETAILS.APPROVED_TIMESTAMP,
            SUBMISSION_DETAILS.ID,
            SUBMISSION_DETAILS.SUBMISSION_TYPE)
        .from(SUBMISSION_DETAILS
            .leftJoin(RESOURCE_LINKS)
            .on(RESOURCE_LINKS.DISCOVERY_RESOURCE_ID.eq(SUBMISSION_DETAILS.RESOURCE_ID)
                .or(RESOURCE_LINKS.PROSPECT_RESOURCE_ID.eq(SUBMISSION_DETAILS.RESOURCE_ID))))
        .where(conditions)
        .orderBy(SUBMISSION_DETAILS.SUBMITTED_TIMESTAMP.desc())
        .fetchInto(SubmissionQuerySearchRecord.class);
  }

  private Condition getResourceIdCondition(UUID resourceId) {
    return SUBMISSION_DETAILS.RESOURCE_ID.eq(resourceId)
        .or(RESOURCE_LINKS.DISCOVERY_RESOURCE_ID.eq(resourceId))
        .or(RESOURCE_LINKS.PROSPECT_RESOURCE_ID.eq(resourceId));
  }
}
