package uk.co.nstauthority.hydrocarbonmaturation.workarea;

import static org.jooq.impl.DSL.falseCondition;
import static org.jooq.impl.DSL.field;

import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.configuration.JooqSchemaProviderService;
import uk.co.nstauthority.hydrocarbonmaturation.responsibleteam.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.TeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamRole;

@Service
class SubmissionWorkAreaQueryService {

  private final DSLContext context;
  private final UserDetailService userDetailService;
  private final TeamMemberService teamMemberService;
  private final JooqSchemaProviderService jooqSchemaProviderService;

  @Autowired
  SubmissionWorkAreaQueryService(DSLContext context,
                                 UserDetailService userDetailService,
                                 TeamMemberService teamMemberService,
                                 JooqSchemaProviderService jooqSchemaProviderService) {
    this.context = context;
    this.userDetailService = userDetailService;
    this.teamMemberService = teamMemberService;
    this.jooqSchemaProviderService = jooqSchemaProviderService;
  }

  List<SubmissionWorkAreaQueryResult> getWorkAreaSubmissionItems() {
    return context
        .select(
            field("s.id"),
            field("s.submission_type"),
            field("s.status"),
            field("gd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("gd.organisation_id"),
            DSL.arrayAgg(field("gdl.licence_id")).filterWhere(field("gdl.licence_id").isNotNull()),
            field("s.created_timestamp"),
            field("s.submitted_timestamp"))
        .from(jooqSchemaProviderService.remiTable("submission_details").as("s")
        .leftJoin(jooqSchemaProviderService.remiTable("general_details").as("gd"))
            .on(field("gd.submission_detail_id").eq(field("s.id")))
        .leftJoin(jooqSchemaProviderService.remiTable("trap_properties").as("tp"))
            .on(field("tp.submission_detail_id").eq(field("s.id")))
        .leftJoin(jooqSchemaProviderService.remiTable("general_details_licences").as("gdl"))
            .on(field("gdl.general_details_id").eq(field("gd.id")))
        .leftJoin(jooqSchemaProviderService.remiTable("responsible_teams").as("rt"))
            .on(field("rt.resource_id").eq(field("s.resource_id")))
        .where(getConditions()))
        .groupBy(
            field("s.id"),
            field("gd.resource_name"),
            field("tp.centre_of_interest_quadrant_block"),
            field("gd.organisation_id"))
        .orderBy(field("s.submitted_timestamp").desc().nullsLast(),
            field("s.created_timestamp").desc())
        .fetchInto(SubmissionWorkAreaQueryResult.class);
  }

  private List<Condition> getConditions() {
    var submissionTeamMembershipCondition = getSubmissionsForTeam();
    var draftSubmissionsCondition = getVisibleSubmissions();

    return List.of(submissionTeamMembershipCondition, draftSubmissionsCondition);
  }

  private Condition getSubmissionsForTeam() {
    var user = userDetailService.getUserDetail();

    List<TeamRole> roles = teamMemberService.getUserAsTeamMembers(user)
        .stream()
        .flatMap(teamMember -> teamMember.roles().stream())
        .toList();

    if (roles.contains(RegulatorTeamRole.EXPLORATION)) {
      return field("rt.responsible_team").eq(ResponsibleTeamType.EXPLORATION.name());
    }

    return falseCondition();
  }

  private Condition getVisibleSubmissions() {
    return field("s.status")
        .in(
            SubmissionDetailStatus.DRAFT.name(),
            SubmissionDetailStatus.SUBMITTED.name()
        );
  }
}
