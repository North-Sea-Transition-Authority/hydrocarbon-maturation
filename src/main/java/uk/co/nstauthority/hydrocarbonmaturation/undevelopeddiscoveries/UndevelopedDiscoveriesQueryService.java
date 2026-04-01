package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.jooq.impl.DSL.coalesce;
import static org.jooq.impl.DSL.select;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELDS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.FIELD_STATUS_HISTORY;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.GENERAL_DETAILS_LICENCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.LATEST_SUBMISSION_DETAIL_VIEW;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.REGULATOR_MANAGED_INFORMATION;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCES;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_DETAILS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.RESOURCE_LINKS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSIONS;
import static uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.Tables.SUBMISSION_DETAILS;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.SubmissionDetails;
import uk.co.nstauthority.hydrocarbonmaturation.generated.jooq.tables.Submissions;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Service
public class UndevelopedDiscoveriesQueryService {

  private static final SubmissionDetails SD_2 = SUBMISSION_DETAILS.as("sd2");
  private static final Submissions SUBMISSIONS_2 = SUBMISSIONS.as("s2");

  private final DSLContext context;

  public UndevelopedDiscoveriesQueryService(DSLContext context) {
    this.context = context;
  }

  public List<UndevelopedDiscoveryResourceDto> getLatestResourceRecords(List<UUID> resources) {
    return context.select(
            RESOURCES.ID,
            RESOURCE_DETAILS.RESOURCE_NAME,
            SUBMISSION_DETAILS.SUBMISSION_TYPE,
            REGULATOR_MANAGED_INFORMATION.GEOGRAPHICAL_AREA,
            coalesce(
                DSL.arrayAggDistinct(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                    .orderBy(GENERAL_DETAILS_LICENCES.LICENCE_ID)
                    .filterWhere(GENERAL_DETAILS_LICENCES.LICENCE_ID.isNotNull())
            )
        )
        .from(RESOURCES
            .join(SUBMISSIONS).on(RESOURCES.ID.eq(SUBMISSIONS.RESOURCE_ID))
            // Joins the relevant submission detail to the submission.
            .join(SUBMISSION_DETAILS)
            .on(SUBMISSION_DETAILS.ID.eq(
                select(SD_2.ID)
                    .from(SUBMISSIONS_2.join(SD_2)
                        .on(SD_2.SUBMISSION_ID.eq(SUBMISSIONS_2.ID)
                            .and(SD_2.STATUS.in(SubmissionDetailStatus.ACCEPTED.name()))))
                    .where(SD_2.SUBMISSION_ID.eq(SUBMISSIONS.ID))
                    .orderBy(SD_2.VERSION_NUMBER.desc())
                    .limit(1)))
            .join(LATEST_SUBMISSION_DETAIL_VIEW)
            .on(LATEST_SUBMISSION_DETAIL_VIEW.LATEST_SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(RESOURCE_DETAILS)
            .on(RESOURCE_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS)
            .on(GENERAL_DETAILS.SUBMISSION_DETAIL_ID.eq(SUBMISSION_DETAILS.ID))
            .leftJoin(GENERAL_DETAILS_LICENCES)
            .on(GENERAL_DETAILS_LICENCES.GENERAL_DETAILS_ID.eq(GENERAL_DETAILS.ID))
            .leftJoin(REGULATOR_MANAGED_INFORMATION)
            .on(REGULATOR_MANAGED_INFORMATION.RESOURCE_ID.eq(RESOURCES.ID)))
        .where(RESOURCES.ID.in(resources)
            .and(SUBMISSIONS.WITHDRAWN.isNull()))
        .groupBy(List.of(
            RESOURCES.ID,
            RESOURCE_DETAILS.RESOURCE_NAME,
            SUBMISSION_DETAILS.SUBMISSION_TYPE,
            GENERAL_DETAILS.ID,
            REGULATOR_MANAGED_INFORMATION.GEOGRAPHICAL_AREA
        ))
        .fetchInto(UndevelopedDiscoveryResourceDto.class);
  }

  public List<UndevelopedDiscoveryFieldDto> getLinkedFields(List<UUID> discoveryResourceIds) {
    // rank rows per discovery_resource_id by status_date desc, then field_status desc
    var rankedRowNumber = DSL.rowNumber()
        .over(DSL.partitionBy(RESOURCE_LINKS.FIELD_RESOURCE_ID)
            .orderBy(
                FIELD_STATUS_HISTORY.STATUS_DATE.desc(),
                FIELD_STATUS_HISTORY.FIELD_STATUS.desc()
            ))
        .as("rn");

    // get base list of field history items for each field-linked discovery
    var ranked = context.select(
            RESOURCE_LINKS.DISCOVERY_RESOURCE_ID.as("discovery_resource_id"),
            FIELD_STATUS_HISTORY.FIELD_STATUS.as("field_status"),
            FIELD_STATUS_HISTORY.STATUS_DATE.as("status_date"),
            rankedRowNumber
        )
        .from(RESOURCE_LINKS)
        .join(FIELDS).on(FIELDS.RESOURCE_ID.eq(RESOURCE_LINKS.FIELD_RESOURCE_ID))
        .join(FIELD_STATUS_HISTORY).on(FIELD_STATUS_HISTORY.FIELD_IDENTIFIER.eq(FIELDS.FIELD_IDENTIFIER))
        .where(RESOURCE_LINKS.DISCOVERY_RESOURCE_ID.in(discoveryResourceIds))
        .asTable("ranked");

    // get only the latest field status history item for each field-linked discovery
    return context.select(
            ranked.field("discovery_resource_id", UUID.class),
            ranked.field("field_status", String.class),
            ranked.field("status_date", LocalDateTime.class)
        )
        .from(ranked)
        .where(Objects.requireNonNull(ranked.field("rn", Integer.class)).eq(1))
        .fetchInto(UndevelopedDiscoveryFieldDto.class);
  }
}
