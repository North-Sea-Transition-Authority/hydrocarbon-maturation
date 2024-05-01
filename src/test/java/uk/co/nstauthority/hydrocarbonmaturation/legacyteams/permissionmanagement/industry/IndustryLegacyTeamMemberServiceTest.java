package uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.industry;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;

@ExtendWith(MockitoExtension.class)
class IndustryLegacyTeamMemberServiceTest {

  private static final Integer ORGANISATION_UNIT_ID = 1;

  @Mock
  private IndustryTeamService industryTeamService;
  @Mock
  private OrganisationUnitQueryService organisationUnitQueryService;
  @InjectMocks
  private IndustryTeamMemberService underTest;

  private User user = UserTestUtil.Builder().build();

  private List<Integer> ORGANISATION_GROUP_IDS = List.of(1,2);

  @Nested
  class WhenCheckingIfIsMemberOfOrganisationUnit {

    @Test
    void isMemberOfOrganisationUnit_whenTrue() {
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_UNIT_ID))
          .thenReturn(ORGANISATION_GROUP_IDS);
      when(industryTeamService.isTeamMemberForOrganisationGroups(user, ORGANISATION_GROUP_IDS))
          .thenReturn(true);

      assertThat(underTest.isMemberOfOrganisationUnit(user, ORGANISATION_UNIT_ID))
          .isTrue();
    }

    @Test
    void isMemberOfOrganisationUnit_whenFalse() {
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_UNIT_ID))
          .thenReturn(ORGANISATION_GROUP_IDS);
      when(industryTeamService.isTeamMemberForOrganisationGroups(user, ORGANISATION_GROUP_IDS))
          .thenReturn(false);

      assertThat(underTest.isMemberOfOrganisationUnit(user, ORGANISATION_UNIT_ID))
          .isFalse();
    }
  }

  @Nested
  class WhenCheckingIfIsSubmitterOfOrganisationUnit {

    @Test
    void isSubmitterOfOrganisationUnit_whenTrue() {
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_UNIT_ID))
          .thenReturn(ORGANISATION_GROUP_IDS);
      when(industryTeamService.isSubmitterForOrganisationGroups(user, ORGANISATION_GROUP_IDS))
          .thenReturn(true);

      assertThat(underTest.isSubmitterOfOrganisationUnit(user, ORGANISATION_UNIT_ID))
          .isTrue();
    }

    @Test
    void isSubmitterOfOrganisationUnit_whenFalse() {
      when(organisationUnitQueryService.findOrganisationGroupIdsByUnitId(ORGANISATION_UNIT_ID))
          .thenReturn(ORGANISATION_GROUP_IDS);
      when(industryTeamService.isSubmitterForOrganisationGroups(user, ORGANISATION_GROUP_IDS))
          .thenReturn(false);

      assertThat(underTest.isSubmitterOfOrganisationUnit(user, ORGANISATION_UNIT_ID))
          .isFalse();
    }
  }
}