package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.industry.IndustryTeamMemberService;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailAccessServiceTest {
  @Mock
  private IndustryTeamMemberService industryTeamMemberService;
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @Mock
  private InternalTeamService internalTeamService;
  @InjectMocks
  private SubmissionDetailAccessService underTest;

  @Nested
  class WhenCheckingIfCanViewSubmissionDetail {
    @Mock
    private SubmissionDetail submissionDetail;
    @Mock
    private User user;

    @Test
    void isRegulatorOrAreaTeamUser() {
      when(internalTeamService.isRegulatorOrAreaTeamUser(user)).thenReturn(true);
      var result = underTest.canViewSubmissionDetail(submissionDetail, user);
      assertThat(result).isTrue();
    }
  }

  @Nested
  class WhenCheckingIfCanViewSubmissionDetailAndNotRegulatorOrAreaTeamUser {
    @Mock
    private ResourceDetail resourceDetail;
    @Mock
    private SubmissionDetail submissionDetail;
    @Mock
    private User user;
    private final int ORG_UNIT_ID = 1;

    @BeforeEach
    void setup() {
      when(internalTeamService.isRegulatorOrAreaTeamUser(user)).thenReturn(false);
      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail)).thenReturn(resourceDetail);
      when(resourceDetail.getOrganisationId()).thenReturn(ORG_UNIT_ID);
    }

    @Test
    void isMemberOfOrganisationUnit() {
      when(industryTeamMemberService.isMemberOfOrganisationUnit(user, ORG_UNIT_ID)).thenReturn(true);
      var result = underTest.canViewSubmissionDetail(submissionDetail, user);
      assertThat(result).isTrue();
    }

    @Test
    void isNotMemberOfOrganisationUnit() {
      when(industryTeamMemberService.isMemberOfOrganisationUnit(user, ORG_UNIT_ID)).thenReturn(false);
      var result = underTest.canViewSubmissionDetail(submissionDetail, user);
      assertThat(result).isFalse();
    }
  }
}