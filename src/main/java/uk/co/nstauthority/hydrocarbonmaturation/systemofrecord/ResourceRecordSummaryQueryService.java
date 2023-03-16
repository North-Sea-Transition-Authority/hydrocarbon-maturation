package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.jooq.impl.DSL.select;
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
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
class ResourceRecordSummaryQueryService {

  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private final DSLContext context;

  @Autowired
  ResourceRecordSummaryQueryService(DSLContext context) {
    this.context = context;
  }

  Optional<ResourceRecordSummaryQueryResult> getResourceRecord(UUID resourceId) {
    return context.select(
            SUBMISSION_DETAILS.ID,
            RESOURCES.TYPE,
            RESOURCE_DETAILS.RESOURCE_NAME,
            TRAP_PROPERTIES.CENTRE_OF_INTEREST_QUADRANT_BLOCK,
            RESOURCE_DETAILS.ORGANISATION_ID,
            REGULATOR_MANAGED_INFORMATION.RESPONSIBLE_TEAM,
            DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.isNotNull()),
            SUBMISSION_DETAILS.CREATED_TIMESTAMP,
            SUBMISSION_DETAILS.APPROVED_TIMESTAMP
        )
        .from(RESOURCES
          .leftJoin(REGULATOR_MANAGED_INFORMATION).on(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID.eq(RESOURCES.ID))
          .join(SUBMISSION_DETAILS).on(SUBMISSION_DETAILS.ID.eq(
                select(SD_2.ID)
                    .from(SD_2)
                    .where(SD_2.RESOURCE_ID.eq(RESOURCES.ID))
                    .and(SD_2.STATUS.eq(SubmissionDetailStatus.APPROVED.name()))
                    .orderBy(SD_2.APPROVED_TIMESTAMP.desc())
                    .limit(1)
            ))
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
        .fetchOptionalInto(ResourceRecordSummaryQueryResult.class);
  }
}
