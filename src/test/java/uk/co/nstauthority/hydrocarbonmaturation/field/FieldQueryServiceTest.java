package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.field.FieldQueryService.AREA_TEAM_MEMBER;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.IndustryTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@ExtendWith(MockitoExtension.class)
class FieldQueryServiceTest {

  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  @Mock
  private FieldStatusService fieldStatusService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private TeamQueryService teamQueryService;

  @InjectMocks
  private FieldQueryService fieldQueryService;


  @Nested
  class userCanSelectField {

    @Test
    void whenFieldSubmission_passesFilter_assertTwoSortedFound() {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.ONSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(true);
      var orgUnitId = 42;
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT))).thenReturn(List.of(orgUnitId));

      var searchTerm = "field";

      var resource1 = new Resource();
      resource1.setArchived(false);

      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName");
      field1.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail1 = new SubmissionDetail();
      submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource2 = new Resource();
      resource2.setArchived(false);

      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("alphabeticalLegalFieldName");
      field2.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      var archivedResource = new Resource();
      archivedResource.setArchived(true);
      var archivedField = new Field();
      archivedField.setResource(archivedResource);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm))
          .thenReturn(List.of(field1, field2, archivedField));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource1))
          .thenReturn(List.of(submissionDetail1));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource2))
          .thenReturn(List.of(submissionDetail2));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_CSR, USER))
          .containsExactly(
              new FieldView(field2.getFieldIdentifier(), field2.getCorrectedLegalFieldName()),
              new FieldView(field1.getFieldIdentifier(), field1.getCorrectedLegalFieldName()));
    }

    @Test
    void whenFieldSubmission_createFdp_assertFiltersFieldStatuses() {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.ONSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(true);
      var orgUnitId = 42;
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT))).thenReturn(List.of(orgUnitId));

      var searchTerm = "field";

      var resource1 = new Resource();
      resource1.setArchived(false);

      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName1");
      field1.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail1 = new SubmissionDetail();
      submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource2 = new Resource();
      resource2.setArchived(false);

      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_300));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("legalFieldName2");
      field2.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      var resource3 = new Resource();
      resource3.setArchived(false);

      var field3 = new Field();
      field3.setFieldIdentifier(48);
      when(fieldStatusService.getMostRecentFieldStatus(field3))
          .thenReturn(Optional.of(FieldStatus.STATUS_400));
      field3.setResource(resource3);
      field3.setCorrectedLegalFieldName("legalFieldName3");
      field3.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail3 = new SubmissionDetail();
      submissionDetail3.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource4 = new Resource();
      resource4.setArchived(false);

      var field4 = new Field();
      field4.setFieldIdentifier(84);
      when(fieldStatusService.getMostRecentFieldStatus(field4))
          .thenReturn(Optional.of(FieldStatus.STATUS_500));
      field4.setResource(resource4);
      field4.setCorrectedLegalFieldName("legalFieldName4");
      field4.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail4 = new SubmissionDetail();
      submissionDetail4.setStatus(SubmissionDetailStatus.DELETED);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm))
          .thenReturn(List.of(field1, field2, field3, field4));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource1))
          .thenReturn(List.of(submissionDetail1, submissionDetail2, submissionDetail3, submissionDetail4));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_FDP, USER))
          .containsExactly(
              new FieldView(field1.getFieldIdentifier(), field1.getCorrectedLegalFieldName()),
              new FieldView(field2.getFieldIdentifier(), field2.getCorrectedLegalFieldName()),
              new FieldView(field3.getFieldIdentifier(), field3.getCorrectedLegalFieldName()));
    }

    @Test
    void whenFieldSubmission_createFdpa_assertFiltersFieldStatuses() {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.ONSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(true);
      var orgUnitId = 42;
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT))).thenReturn(List.of(orgUnitId));

      var searchTerm = "field";

      var resource1 = new Resource();
      resource1.setArchived(false);

      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_600));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName1");
      field1.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail1 = new SubmissionDetail();
      submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource2 = new Resource();
      resource2.setArchived(false);

      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_700));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("legalFieldName2");
      field2.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      var resource3 = new Resource();
      resource3.setArchived(false);

      var field3 = new Field();
      field3.setFieldIdentifier(48);
      when(fieldStatusService.getMostRecentFieldStatus(field3))
          .thenReturn(Optional.of(FieldStatus.STATUS_799));
      field3.setResource(resource3);
      field3.setCorrectedLegalFieldName("legalFieldName3");
      field3.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail3 = new SubmissionDetail();
      submissionDetail3.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource4 = new Resource();
      resource4.setArchived(false);

      var field4 = new Field();
      field4.setFieldIdentifier(84);
      when(fieldStatusService.getMostRecentFieldStatus(field4))
          .thenReturn(Optional.of(FieldStatus.STATUS_800));
      field4.setResource(resource4);
      field4.setCorrectedLegalFieldName("legalFieldName4");
      field4.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail4 = new SubmissionDetail();
      submissionDetail4.setStatus(SubmissionDetailStatus.DELETED);

      var resource5 = new Resource();
      resource5.setArchived(false);

      var field5 = new Field();
      field5.setFieldIdentifier(96);
      when(fieldStatusService.getMostRecentFieldStatus(field5))
          .thenReturn(Optional.of(FieldStatus.STATUS_899));
      field5.setResource(resource5);
      field5.setCorrectedLegalFieldName("legalFieldName5");
      field5.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail5 = new SubmissionDetail();
      submissionDetail5.setStatus(SubmissionDetailStatus.DELETED);

      var resource6 = new Resource();
      resource6.setArchived(false);

      var field6 = new Field();
      field6.setFieldIdentifier(168);
      when(fieldStatusService.getMostRecentFieldStatus(field6))
          .thenReturn(Optional.of(FieldStatus.STATUS_900));
      field6.setResource(resource6);
      field6.setCorrectedLegalFieldName("legalFieldName6");
      field6.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail6 = new SubmissionDetail();
      submissionDetail6.setStatus(SubmissionDetailStatus.DELETED);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm))
          .thenReturn(List.of(field1, field2, field3, field4, field5, field6));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource1))
          .thenReturn(List.of(submissionDetail1, submissionDetail2, submissionDetail3, submissionDetail4, submissionDetail5, submissionDetail6));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_FDPA, USER))
          .containsExactly(
              new FieldView(field1.getFieldIdentifier(), field1.getCorrectedLegalFieldName()),
              new FieldView(field2.getFieldIdentifier(), field2.getCorrectedLegalFieldName()),
              new FieldView(field3.getFieldIdentifier(), field3.getCorrectedLegalFieldName()),
              new FieldView(field4.getFieldIdentifier(), field4.getCorrectedLegalFieldName()),
              new FieldView(field5.getFieldIdentifier(), field5.getCorrectedLegalFieldName()));
    }

    @Test
    void whenFieldSubmission_createCop_assertFiltersFieldStatuses() {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.ONSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(true);
      var orgUnitId = 42;
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT))).thenReturn(List.of(orgUnitId));

      var searchTerm = "field";

      var resource1 = new Resource();
      resource1.setArchived(false);

      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_700));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName1");
      field1.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail1 = new SubmissionDetail();
      submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource2 = new Resource();
      resource2.setArchived(false);

      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_799));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("legalFieldName2");
      field2.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      var resource3 = new Resource();
      resource3.setArchived(false);

      var field3 = new Field();
      field3.setFieldIdentifier(48);
      when(fieldStatusService.getMostRecentFieldStatus(field3))
          .thenReturn(Optional.of(FieldStatus.STATUS_800));
      field3.setResource(resource3);
      field3.setCorrectedLegalFieldName("legalFieldName3");
      field3.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail3 = new SubmissionDetail();
      submissionDetail3.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource4 = new Resource();
      resource4.setArchived(false);

      var field4 = new Field();
      field4.setFieldIdentifier(84);
      when(fieldStatusService.getMostRecentFieldStatus(field4))
          .thenReturn(Optional.of(FieldStatus.STATUS_899));
      field4.setResource(resource4);
      field4.setCorrectedLegalFieldName("legalFieldName4");
      field4.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail4 = new SubmissionDetail();
      submissionDetail4.setStatus(SubmissionDetailStatus.DELETED);

      var resource5 = new Resource();
      resource5.setArchived(false);

      var field5 = new Field();
      field5.setFieldIdentifier(96);
      when(fieldStatusService.getMostRecentFieldStatus(field5))
          .thenReturn(Optional.of(FieldStatus.STATUS_900));
      field5.setResource(resource5);
      field5.setCorrectedLegalFieldName("legalFieldName5");
      field5.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail5 = new SubmissionDetail();
      submissionDetail5.setStatus(SubmissionDetailStatus.DELETED);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm))
          .thenReturn(List.of(field1, field2, field3, field4, field5));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource1))
          .thenReturn(List.of(submissionDetail1, submissionDetail2, submissionDetail3, submissionDetail4, submissionDetail5));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_COP, USER))
          .containsExactly(
              new FieldView(field1.getFieldIdentifier(), field1.getCorrectedLegalFieldName()),
              new FieldView(field2.getFieldIdentifier(), field2.getCorrectedLegalFieldName()),
              new FieldView(field3.getFieldIdentifier(), field3.getCorrectedLegalFieldName()),
              new FieldView(field4.getFieldIdentifier(), field4.getCorrectedLegalFieldName()));
    }

    @ParameterizedTest
    @EnumSource(value = SubmissionType.class,
        names = {
            "CREATE_CSR",
            "CREATE_FDP",
            "CREATE_FDPA",
            "CREATE_COP"},
        mode = EnumSource.Mode.EXCLUDE)
    void whenInvalidSubmissionType_assertThrows(SubmissionType submissionType) {
      var searchTerm = "field";

      var resource1 = new Resource();
      resource1.setArchived(false);

      var field1 = new Field();
      field1.setFieldIdentifier(24);
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName");

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field1));

      assertThatThrownBy(() -> fieldQueryService.getFieldByNameForSubmissionType(searchTerm, submissionType, USER))
          .isInstanceOf(ActionNotAllowedException.class)
          .hasMessage("No field statuses for submission type: %s".formatted(submissionType));
    }

    @ParameterizedTest
    @EnumSource(value = FieldStatus.class,
        names = {
            "STATUS_200"},
        mode = EnumSource.Mode.EXCLUDE)
    void whenFieldSubmission_incorrectFieldStatus_assertEmpty(FieldStatus fieldStatus) {
      var searchTerm = "field";

      var resource = new Resource();
      resource.setArchived(false);

      var field = new Field();
      field.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field))
          .thenReturn(Optional.of(fieldStatus));
      field.setResource(resource);
      field.setCorrectedLegalFieldName("legalFieldName");

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_CSR, USER)).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = SubmissionDetailStatus.class,
        names = {
            "DRAFT",
            "SUBMITTED"},
        mode = EnumSource.Mode.INCLUDE)
    void whenFieldSubmission_incorrectSubmissionDetailStatus_assertEmpty(SubmissionDetailStatus submissionDetailStatus) {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.ONSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(true);
      var orgUnitId = 42;
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT))).thenReturn(List.of(orgUnitId));

      var searchTerm = "field";

      var resource = new Resource();
      resource.setArchived(false);

      var field = new Field();
      field.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field.setResource(resource);
      field.setCorrectedLegalFieldName("legalFieldName");
      field.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail = new SubmissionDetail();
      submissionDetail.setStatus(submissionDetailStatus);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource)).thenReturn(
          List.of(submissionDetail));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_CSR, USER)).isEmpty();
    }
  }

  @Nested
  class userCannotSelectField {

    @Test
    void whenFieldSubmission_isAreaTeamMember_notInResponsibleTeam() {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER))
          .thenReturn(true);

      var searchTerm = "field";

      var resource1 = new Resource();
      resource1.setArchived(false);

      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName");
      var regulatorManagedInformation1 = new RegulatorManagedInformation();
      regulatorManagedInformation1.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource1))
          .thenReturn(regulatorManagedInformation1);
      when(responsibleTeamService.isInResponsibleTeam(USER, regulatorManagedInformation1.getResponsibleTeamType()))
          .thenReturn(false);

      var resource2 = new Resource();
      resource2.setArchived(false);

      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("alphabeticalLegalFieldName");
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);
      var regulatorManagedInformation2 = new RegulatorManagedInformation();
      regulatorManagedInformation2.setResponsibleTeamType(ResponsibleTeamType.ONSHORE);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource2))
          .thenReturn(regulatorManagedInformation2);
      when(responsibleTeamService.isInResponsibleTeam(USER, regulatorManagedInformation2.getResponsibleTeamType()))
          .thenReturn(true);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field1, field2));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource2))
          .thenReturn(List.of(submissionDetail2));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_CSR, USER))
          .containsExactly(
              new FieldView(field2.getFieldIdentifier(), field2.getCorrectedLegalFieldName()));
    }

    @Test
    void whenFieldSubmission_isSubmitter_checkOnlyGetsFieldsForOrganisation() {
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.ONSHORE_AREA, AREA_TEAM_MEMBER)).thenReturn(false);
      when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.INDUSTRY, Role.INDUSTRY_SUBMIT)).thenReturn(true);
      var orgUnitId = 100;
      var otherOrgUnit = 50;
      when(industryTeamService.getOrganisationUnitIdsForUserWithRoles(USER, Set.of(Role.INDUSTRY_SUBMIT))).thenReturn(List.of(orgUnitId));

      var searchTerm = "field";

      var resource1 = new Resource();
      resource1.setArchived(false);

      var field1 = new Field();
      field1.setFieldIdentifier(1);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName");
      field1.setPrePearsOrganisationId(otherOrgUnit);

      var resource2 = new Resource();
      resource2.setArchived(false);

      var field2 = new Field();
      field2.setFieldIdentifier(2);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("alphabeticalLegalFieldName");
      field2.setPrePearsOrganisationId(orgUnitId);
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field1, field2));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource2))
          .thenReturn(List.of(submissionDetail2));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_CSR, USER))
          .containsExactly(
              new FieldView(field2.getFieldIdentifier(), field2.getCorrectedLegalFieldName()));
    }
  }

  @Test
  void getShelvedOrCopFieldByName_isAreaTeamUser_shelvedCopFieldsExist_showFields() {
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER))
        .thenReturn(true);

    var searchTerm = "field";

    var resource1 = new Resource();
    resource1.setArchived(false);

    var field1 = new Field();
    field1.setFieldIdentifier(1);
    when(fieldStatusService.getMostRecentFieldStatus(field1))
        .thenReturn(Optional.of(FieldStatus.STATUS_000));
    field1.setResource(resource1);
    field1.setCorrectedLegalFieldName("Shelved field");
    var submissionDetail1 = new SubmissionDetail();
    submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

    var regulatorManagedInformation1 = new RegulatorManagedInformation();
    regulatorManagedInformation1.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource1))
        .thenReturn(regulatorManagedInformation1);
    when(responsibleTeamService.isInResponsibleTeam(USER, regulatorManagedInformation1.getResponsibleTeamType()))
        .thenReturn(true);

    var resource2 = new Resource();
    resource2.setArchived(false);

    var field2 = new Field();
    field2.setFieldIdentifier(2);
    when(fieldStatusService.getMostRecentFieldStatus(field2))
        .thenReturn(Optional.of(FieldStatus.STATUS_900));
    field2.setResource(resource2);
    field2.setCorrectedLegalFieldName("Cop field");
    var submissionDetail2 = new SubmissionDetail();
    submissionDetail2.setStatus(SubmissionDetailStatus.ACCEPTED);

    var regulatorManagedInformation2 = new RegulatorManagedInformation();
    regulatorManagedInformation2.setResponsibleTeamType(ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource2))
        .thenReturn(regulatorManagedInformation2);
    when(responsibleTeamService.isInResponsibleTeam(USER, regulatorManagedInformation2.getResponsibleTeamType()))
        .thenReturn(true);

    when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field1, field2));
    when(submissionDetailService.getExtantSubmissionDetailsForResource(resource1))
        .thenReturn(List.of(submissionDetail1));
    when(submissionDetailService.getExtantSubmissionDetailsForResource(resource2))
        .thenReturn(List.of(submissionDetail2));

    assertThat(fieldQueryService.getShelvedOrCopFieldByName(searchTerm, USER))
        .containsExactly(
            new FieldView(field2.getFieldIdentifier(), field2.getCorrectedLegalFieldName()),
            new FieldView(field1.getFieldIdentifier(), field1.getCorrectedLegalFieldName())
        );
  }

  @Test
  void getShelvedOrCopFieldByName_isNotAreaTeamUser_shelvedCopFieldsExist_doNotShowFields() {
    when(teamQueryService.userHasRoleInTeamType(USER.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, AREA_TEAM_MEMBER))
        .thenReturn(false);

    var searchTerm = "field";

    var resource1 = new Resource();
    resource1.setArchived(false);

    var field1 = new Field();
    field1.setFieldIdentifier(1);
    when(fieldStatusService.getMostRecentFieldStatus(field1))
        .thenReturn(Optional.of(FieldStatus.STATUS_000));
    field1.setResource(resource1);
    field1.setCorrectedLegalFieldName("Shelved field");

    var resource2 = new Resource();
    resource2.setArchived(false);

    var field2 = new Field();
    field2.setFieldIdentifier(2);
    when(fieldStatusService.getMostRecentFieldStatus(field2))
        .thenReturn(Optional.of(FieldStatus.STATUS_900));
    field2.setResource(resource2);
    field2.setCorrectedLegalFieldName("Cop field");

    when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field1, field2));

    assertThat(fieldQueryService.getShelvedOrCopFieldByName(searchTerm, USER)).isEqualTo(List.of());
  }

  @Test
  void getShelvedOrCopFieldByName_isAreaTeamUser_shelvedCopFieldsDoNotExist_doNotShowFields() {
    var searchTerm = "field";

    var resource1 = new Resource();
    resource1.setArchived(false);

    var field1 = new Field();
    field1.setFieldIdentifier(1);
    when(fieldStatusService.getMostRecentFieldStatus(field1))
        .thenReturn(Optional.of(FieldStatus.STATUS_200));
    field1.setResource(resource1);
    field1.setCorrectedLegalFieldName("CSR field");
    var submissionDetail1 = new SubmissionDetail();
    submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

    var resource2 = new Resource();
    resource2.setArchived(false);

    var field2 = new Field();
    field2.setFieldIdentifier(2);
    when(fieldStatusService.getMostRecentFieldStatus(field2))
        .thenReturn(Optional.of(FieldStatus.STATUS_700));
    field2.setResource(resource2);
    field2.setCorrectedLegalFieldName("Producing field");
    var submissionDetail2 = new SubmissionDetail();
    submissionDetail2.setStatus(SubmissionDetailStatus.ACCEPTED);


    when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field1, field2));

    assertThat(fieldQueryService.getShelvedOrCopFieldByName(searchTerm, USER)).isEqualTo(List.of());
  }

}

