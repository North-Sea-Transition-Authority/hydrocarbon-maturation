package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionResourceServiceTest {
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
  @Mock
  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  @InjectMocks
  private SubmissionResourceService underTest;

  @Nested
  class WhenGettingResourcesBySubmissionTypeAndResourceName {
    private final ResourceType RESOURCE_TYPE = ResourceType.PROSPECT;
    private final String NAME = "NAME";

    @BeforeEach
    void setup() {
    }

    @Test
    void returnsOnlyResourcesOfCertainType() {
      var resource1 = buildResource(RESOURCE_TYPE);
      var generalDetail1 = buildGeneralDetail(resource1);
      var generalDetail2 = buildGeneralDetail(resource1);

      var resource2 = buildResource(ResourceType.FIELD);
      var generalDetail3 = buildGeneralDetail(resource2);

      when(generalDetailsPersistenceService.getGeneralDetailsByName(NAME)).thenReturn(List.of(generalDetail1, generalDetail2, generalDetail3));

      var result = underTest.getResourcesBySubmissionTypeAndResourceName(RESOURCE_TYPE, NAME);

      assertThat(result)
          .hasSize(1)
          .extracting(
              Resource::getId,
              Resource::getType
          )
          .containsExactly(
              tuple(
                  resource1.getId(),
                  RESOURCE_TYPE
              )
          );
    }
  }

  @Nested
  class WhenGettingResourcesByName {
    private final String NAME = "NAME";

    @Test
    void name() {
      var resource1 = buildResource(ResourceType.PROSPECT);
      var generalDetail1 = buildGeneralDetail(resource1);
      var generalDetail2 = buildGeneralDetail(resource1);

      when(generalDetailsPersistenceService.getGeneralDetailsByName(NAME)).thenReturn(List.of(generalDetail1, generalDetail2));

      var result = underTest.getResourcesByName(NAME);

      assertThat(result)
          .hasSize(1)
          .extracting(
              Resource::getId,
              Resource::getType
          )
          .containsExactly(
              tuple(
                  resource1.getId(),
                  resource1.getType()
              )
          );
    }
  }

  @Nested
  class WhenGettingResourceName {
    @Mock
    private SubmissionDetail submissionDetail;

    @Test
    void getFromGeneralDetail() {
      var generalDetail = new GeneralDetail();
      var resourceName = "name";
      generalDetail.setResourceName(resourceName);
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(generalDetail));

      var resourceNameOptional = underTest.getResourceName(submissionDetail);

      assertThat(resourceNameOptional).isEqualTo(Optional.of(resourceName));
    }
  }

  @Nested
  class WhenGettingResourceNameOrDefault {
    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setup() {
      submissionDetail = new SubmissionDetail();
    }

    @Test
    void getFromGeneralDetail() {
      submissionDetail.setCreatedTimestamp(Instant.now());

      var generalDetail = new GeneralDetail();
      when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(generalDetail));

      var resourceNameOptional = underTest.getResourceNameOrDefault(submissionDetail);

      assertThat(resourceNameOptional).isEqualTo(SubmissionDetailUtil.getDefaultForNamelessResource(submissionDetail.getCreatedTimestamp()));
    }
  }

  @Nested
  class WhenGettingResourceDisplayName {

    private final UUID GENERAL_DETAIL_ID = UUID.randomUUID();
    private final String CENTRE_OF_INTEREST = "CENTRE_OF_INTEREST";
    private static final String RESOURCE_NAME = "RESOURCE_NAME";
    private GeneralDetail generalDetail;

    @BeforeEach
    void setup() {

      generalDetail = new GeneralDetail();
      generalDetail.setId(GENERAL_DETAIL_ID);
      generalDetail.setResourceName(RESOURCE_NAME);
    }

    @Test
    void byGeneralDetail_CentreOfInterestExists() {
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail()))
          .thenReturn(Optional.of(CENTRE_OF_INTEREST));

      var result = underTest.getResourceDisplayName(generalDetail);

      assertThat(result).isEqualTo("%s (%s)".formatted(RESOURCE_NAME, CENTRE_OF_INTEREST));
    }

    @Test
    void byGeneralDetail_CentreOfInterestDoesNotExist() {
      when(generalDetailsPersistenceService.getGeneralDetailByIdOrThrow(GENERAL_DETAIL_ID)).thenReturn(generalDetail);
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail()))
          .thenReturn(Optional.empty());

      var result = underTest.getResourceDisplayName(GENERAL_DETAIL_ID);

      assertThat(result).isEqualTo(RESOURCE_NAME);
    }

    @Test
    void byGeneralDetailId_CentreOfInterestExists() {
      when(generalDetailsPersistenceService.getGeneralDetailByIdOrThrow(GENERAL_DETAIL_ID)).thenReturn(generalDetail);
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail()))
          .thenReturn(Optional.of(CENTRE_OF_INTEREST));

      var result = underTest.getResourceDisplayName(GENERAL_DETAIL_ID);

      assertThat(result).isEqualTo("%s (%s)".formatted(RESOURCE_NAME, CENTRE_OF_INTEREST));
    }

    @Test
    void byGeneralDetailID_CentreOfInterestDoesNotExist() {
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail()))
          .thenReturn(Optional.empty());

      var result = underTest.getResourceDisplayName(generalDetail);

      assertThat(result).isEqualTo(RESOURCE_NAME);
    }
  }

  @NotNull
  private Resource buildResource(ResourceType resourceType) {
    return ResourceTestUtils.buildResource(resourceType);
  }

  @NotNull
  private GeneralDetail buildGeneralDetail(Resource resource) {
    return GeneralDetailUtils.buildGeneralDetail(resource);
  }
}