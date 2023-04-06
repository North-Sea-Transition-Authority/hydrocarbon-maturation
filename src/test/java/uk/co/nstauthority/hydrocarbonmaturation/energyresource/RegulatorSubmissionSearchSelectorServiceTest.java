package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ExtendWith(MockitoExtension.class)
class RegulatorSubmissionSearchSelectorServiceTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private PermissionService permissionService;

  @InjectMocks
  private RegulatorSubmissionSearchSelectorService regulatorSubmissionSearchSelectorService;

  @BeforeEach
  void setUp() {
    when(userDetailService.getUserDetail()).thenReturn(USER);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getResourceDetailsForSubmissions_whenExploration_assertReturn(SubmissionType submissionType) {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(true);
    var submissionDetail1 = mock(SubmissionDetail.class);
    var submissionDetail2 = mock(SubmissionDetail.class);

    var resourceDetail1 = mock(ResourceDetail.class);
    var resourceDetail2 = mock(ResourceDetail.class);

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail1))
        .thenReturn(resourceDetail1);
    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail2))
        .thenReturn(resourceDetail2);
    assertThat(regulatorSubmissionSearchSelectorService
        .getResourceDetailsForSubmissions(Stream.of(submissionDetail1, submissionDetail2),
            submissionType)
        .toList())
        .containsExactly(resourceDetail1, resourceDetail2);
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void getResourceDetailsForSubmissions_whenNotExploration_assertEmpty(SubmissionType submissionType) {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION)))
        .thenReturn(false);
    var submissionDetail1 = mock(SubmissionDetail.class);
    var resourceDetail1 = mock(ResourceDetail.class);

    when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail1))
        .thenReturn(resourceDetail1);
    assertThat(regulatorSubmissionSearchSelectorService
        .getResourceDetailsForSubmissions(Stream.of(submissionDetail1),
            submissionType)
        .toList())
        .isEmpty();
  }
}