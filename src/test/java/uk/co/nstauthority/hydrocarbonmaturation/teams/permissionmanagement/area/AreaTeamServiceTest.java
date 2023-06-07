package uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.onshore.OnshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamService;

@ExtendWith(MockitoExtension.class)
class AreaTeamServiceTest {
  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;

  @Mock
  private SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;

  @Mock
  private OnshoreAreaTeamService onshoreAreaTeamService;

  @InjectMocks
  @SpyBean
  private AreaTeamService areaTeamService;

  @ParameterizedTest
  @MethodSource("calculateIsAreaUserOrTeamMemberConditionArguments")
  void isAreaUser(boolean isNorthernOffshoreAreaUser, boolean isSouthernOffshoreAreaUser, boolean isOnshoreAreaUser) {
    when(northernOffshoreAreaTeamService.isNorthernOffshoreAreaUser(USER)).thenReturn(isNorthernOffshoreAreaUser);
    if (!isNorthernOffshoreAreaUser) {
      when(southernOffshoreAreaTeamService.isSouthernOffshoreAreaUser(USER)).thenReturn(isSouthernOffshoreAreaUser);
    }
    if (!isNorthernOffshoreAreaUser && !isSouthernOffshoreAreaUser) {
      when(onshoreAreaTeamService.isOnshoreAreaUser(USER)).thenReturn(isOnshoreAreaUser);
    }

    assertThat(areaTeamService.isAreaUser(USER))
        .isEqualTo(isNorthernOffshoreAreaUser || isSouthernOffshoreAreaUser || isOnshoreAreaUser);
  }

  @ParameterizedTest
  @MethodSource("calculateIsAreaUserOrTeamMemberConditionArguments")
  void isAreaTeamMember(boolean isNorthernOffshoreTeamMember, boolean isSouthernOffshoreTeamMember,
                        boolean isOnshoreTeamMember) {
    when(northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(USER)).thenReturn(isNorthernOffshoreTeamMember);
    if (!isNorthernOffshoreTeamMember) {
      when(southernOffshoreAreaTeamService.isSouthernOffshoreTeamMember(USER)).thenReturn(isSouthernOffshoreTeamMember);
    }
    if (!isNorthernOffshoreTeamMember && !isSouthernOffshoreTeamMember) {
      when(onshoreAreaTeamService.isOnshoreTeamMember(USER)).thenReturn(isOnshoreTeamMember);
    }

    assertThat(areaTeamService.isAreaTeamMember(USER))
        .isEqualTo(isNorthernOffshoreTeamMember || isSouthernOffshoreTeamMember || isOnshoreTeamMember);
  }

  private static Stream<Arguments> calculateIsAreaUserOrTeamMemberConditionArguments() {
    return Sets.cartesianProduct(Set.of(true, false), Set.of(true, false), Set.of(true, false)).stream()
        .map(booleans -> Arguments.of(booleans.get(0), booleans.get(1), booleans.get(2)));
  }
}