package uk.co.nstauthority.hydrocarbonmaturation.field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
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
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamService;

@ExtendWith(MockitoExtension.class)
class FieldQueryServiceTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private FieldPersistenceService fieldPersistenceService;

  @Mock
  private FieldStatusService fieldStatusService;

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private AreaTeamService areaTeamService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  @Mock
  private IndustryTeamService industryTeamService;

  @Mock
  private IndustryTeamMemberService industryTeamMemberService;

  @InjectMocks
  private FieldQueryService fieldQueryService;


  @Nested
  class userCanSelectField {

    @Test
    void whenFieldSubmission_passesFilter_assertTwoSortedFound() {
      when(areaTeamService.isAreaTeamMember(USER)).thenReturn(false);
      when(industryTeamService.isSubmitter(USER)).thenReturn(true);
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(eq(USER), any())).thenReturn(true);

      var searchTerm = "field";

      var resource1 = new Resource();
      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName");
      var submissionDetail1 = new SubmissionDetail();
      submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource2 = new Resource();
      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("alphabeticalLegalFieldName");
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field1, field2));
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
      when(areaTeamService.isAreaTeamMember(USER)).thenReturn(false);
      when(industryTeamService.isSubmitter(USER)).thenReturn(true);
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(eq(USER), any())).thenReturn(true);

      var searchTerm = "field";

      var resource1 = new Resource();
      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName1");
      var submissionDetail1 = new SubmissionDetail();
      submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource2 = new Resource();
      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_300));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("legalFieldName2");
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      var resource3 = new Resource();
      var field3 = new Field();
      field3.setFieldIdentifier(48);
      when(fieldStatusService.getMostRecentFieldStatus(field3))
          .thenReturn(Optional.of(FieldStatus.STATUS_400));
      field3.setResource(resource3);
      field3.setCorrectedLegalFieldName("legalFieldName3");
      var submissionDetail3 = new SubmissionDetail();
      submissionDetail3.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource4 = new Resource();
      var field4 = new Field();
      field4.setFieldIdentifier(84);
      when(fieldStatusService.getMostRecentFieldStatus(field4))
          .thenReturn(Optional.of(FieldStatus.STATUS_500));
      field4.setResource(resource4);
      field4.setCorrectedLegalFieldName("legalFieldName4");
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
      when(areaTeamService.isAreaTeamMember(USER)).thenReturn(false);
      when(industryTeamService.isSubmitter(USER)).thenReturn(true);
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(eq(USER), any())).thenReturn(true);

      var searchTerm = "field";

      var resource1 = new Resource();
      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_600));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName1");
      var submissionDetail1 = new SubmissionDetail();
      submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource2 = new Resource();
      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_700));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("legalFieldName2");
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      var resource3 = new Resource();
      var field3 = new Field();
      field3.setFieldIdentifier(48);
      when(fieldStatusService.getMostRecentFieldStatus(field3))
          .thenReturn(Optional.of(FieldStatus.STATUS_799));
      field3.setResource(resource3);
      field3.setCorrectedLegalFieldName("legalFieldName3");
      var submissionDetail3 = new SubmissionDetail();
      submissionDetail3.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource4 = new Resource();
      var field4 = new Field();
      field4.setFieldIdentifier(84);
      when(fieldStatusService.getMostRecentFieldStatus(field4))
          .thenReturn(Optional.of(FieldStatus.STATUS_800));
      field4.setResource(resource4);
      field4.setCorrectedLegalFieldName("legalFieldName4");
      var submissionDetail4 = new SubmissionDetail();
      submissionDetail4.setStatus(SubmissionDetailStatus.DELETED);

      var resource5 = new Resource();
      var field5 = new Field();
      field5.setFieldIdentifier(96);
      when(fieldStatusService.getMostRecentFieldStatus(field5))
          .thenReturn(Optional.of(FieldStatus.STATUS_899));
      field5.setResource(resource5);
      field5.setCorrectedLegalFieldName("legalFieldName5");
      var submissionDetail5 = new SubmissionDetail();
      submissionDetail5.setStatus(SubmissionDetailStatus.DELETED);

      var resource6 = new Resource();
      var field6 = new Field();
      field6.setFieldIdentifier(168);
      when(fieldStatusService.getMostRecentFieldStatus(field6))
          .thenReturn(Optional.of(FieldStatus.STATUS_900));
      field6.setResource(resource6);
      field6.setCorrectedLegalFieldName("legalFieldName6");
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
      when(areaTeamService.isAreaTeamMember(USER)).thenReturn(false);
      when(industryTeamService.isSubmitter(USER)).thenReturn(true);
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(eq(USER), any())).thenReturn(true);

      var searchTerm = "field";

      var resource1 = new Resource();
      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_700));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName1");
      var submissionDetail1 = new SubmissionDetail();
      submissionDetail1.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource2 = new Resource();
      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_799));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("legalFieldName2");
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);

      var resource3 = new Resource();
      var field3 = new Field();
      field3.setFieldIdentifier(48);
      when(fieldStatusService.getMostRecentFieldStatus(field3))
          .thenReturn(Optional.of(FieldStatus.STATUS_800));
      field3.setResource(resource3);
      field3.setCorrectedLegalFieldName("legalFieldName3");
      var submissionDetail3 = new SubmissionDetail();
      submissionDetail3.setStatus(SubmissionDetailStatus.ACCEPTED);

      var resource4 = new Resource();
      var field4 = new Field();
      field4.setFieldIdentifier(84);
      when(fieldStatusService.getMostRecentFieldStatus(field4))
          .thenReturn(Optional.of(FieldStatus.STATUS_899));
      field4.setResource(resource4);
      field4.setCorrectedLegalFieldName("legalFieldName4");
      var submissionDetail4 = new SubmissionDetail();
      submissionDetail4.setStatus(SubmissionDetailStatus.DELETED);

      var resource5 = new Resource();
      var field5 = new Field();
      field5.setFieldIdentifier(96);
      when(fieldStatusService.getMostRecentFieldStatus(field5))
          .thenReturn(Optional.of(FieldStatus.STATUS_900));
      field5.setResource(resource5);
      field5.setCorrectedLegalFieldName("legalFieldName5");
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
      when(areaTeamService.isAreaTeamMember(USER)).thenReturn(false);
      when(industryTeamService.isSubmitter(USER)).thenReturn(true);
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(eq(USER), any())).thenReturn(true);

      var searchTerm = "field";

      var resource = new Resource();
      var field = new Field();
      field.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field.setResource(resource);
      field.setCorrectedLegalFieldName("legalFieldName");
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
      when(areaTeamService.isAreaTeamMember(USER)).thenReturn(true);

      var searchTerm = "field";

      var resource1 = new Resource();
      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName");
      var regulatorManagedInformation1 = new RegulatorManagedInformation();
      regulatorManagedInformation1.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
      when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource1))
          .thenReturn(regulatorManagedInformation1);
      when(responsibleTeamService.isInResponsibleTeam(USER, regulatorManagedInformation1.getResponsibleTeamType()))
          .thenReturn(false);

      var resource2 = new Resource();
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
    void whenFieldSubmission_isSubmitter_notSubmitterInResponsibleOrganisation() {
      when(areaTeamService.isAreaTeamMember(USER)).thenReturn(false);
      when(industryTeamService.isSubmitter(USER)).thenReturn(true);

      var searchTerm = "field";

      var resource1 = new Resource();
      var field1 = new Field();
      field1.setFieldIdentifier(24);
      when(fieldStatusService.getMostRecentFieldStatus(field1))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field1.setResource(resource1);
      field1.setCorrectedLegalFieldName("legalFieldName");
      field1.setResponsibleOrganisationId(24);
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(USER, field1.getResponsibleOrganisationId()))
          .thenReturn(false);

      var resource2 = new Resource();
      var field2 = new Field();
      field2.setFieldIdentifier(42);
      when(fieldStatusService.getMostRecentFieldStatus(field2))
          .thenReturn(Optional.of(FieldStatus.STATUS_200));
      field2.setResource(resource2);
      field2.setCorrectedLegalFieldName("alphabeticalLegalFieldName");
      var submissionDetail2 = new SubmissionDetail();
      submissionDetail2.setStatus(SubmissionDetailStatus.DELETED);
      when(industryTeamMemberService.isSubmitterOfOrganisationUnit(USER, field2.getResponsibleOrganisationId()))
          .thenReturn(true);

      when(fieldPersistenceService.getFieldsByCorrectedLegalName(searchTerm)).thenReturn(List.of(field1, field2));
      when(submissionDetailService.getExtantSubmissionDetailsForResource(resource2))
          .thenReturn(List.of(submissionDetail2));

      assertThat(fieldQueryService.getFieldByNameForSubmissionType(searchTerm, SubmissionType.CREATE_CSR, USER))
          .containsExactly(
              new FieldView(field2.getFieldIdentifier(), field2.getCorrectedLegalFieldName()));
    }
  }
}

