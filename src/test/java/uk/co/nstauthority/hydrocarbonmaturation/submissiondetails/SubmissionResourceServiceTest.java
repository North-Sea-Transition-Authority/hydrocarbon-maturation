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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionResourceServiceTest {
  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
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

      when(resourceDetailsPersistenceService.getResourceDetailsByName(NAME))
          .thenReturn(List.of(generalDetail1, generalDetail2, generalDetail3));

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
      var resourceDetail = buildGeneralDetail(resource1);
      var resourceDetail1 = buildGeneralDetail(resource1);

      when(resourceDetailsPersistenceService.getResourceDetailsByName(NAME))
          .thenReturn(List.of(resourceDetail, resourceDetail1));

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
      var resourceDetail = new ResourceDetail();
      var resourceName = "name";
      resourceDetail.setResourceName(resourceName);
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(resourceDetail));

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

      var resourceDetail = new ResourceDetail();
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(resourceDetail));

      var resourceNameOptional = underTest.getResourceNameOrDefault(submissionDetail);

      assertThat(resourceNameOptional).isEqualTo(SubmissionDetailUtil.getDefaultForNamelessResource(submissionDetail.getCreatedTimestamp()));
    }
  }

  @Nested
  class WhenGettingResourceDisplayName {

    private final UUID RESOURCE_DETAIL_ID = UUID.randomUUID();
    private final String CENTRE_OF_INTEREST = "CENTRE_OF_INTEREST";
    private static final String RESOURCE_NAME = "RESOURCE_NAME";
    private ResourceDetail resourceDetail;

    @BeforeEach
    void setup() {

      resourceDetail = new ResourceDetail();
      resourceDetail.setId(RESOURCE_DETAIL_ID);
      resourceDetail.setResourceName(RESOURCE_NAME);
    }

    @Test
    void byGeneralDetail_CentreOfInterestExists() {
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(resourceDetail.getSubmissionDetail()))
          .thenReturn(Optional.of(CENTRE_OF_INTEREST));

      var result = underTest.getResourceDisplayName(resourceDetail);

      assertThat(result).isEqualTo("%s (%s)".formatted(RESOURCE_NAME, CENTRE_OF_INTEREST));
    }

    @Test
    void byGeneralDetail_CentreOfInterestDoesNotExist() {
      when(resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(RESOURCE_DETAIL_ID)).thenReturn(resourceDetail);
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(resourceDetail.getSubmissionDetail()))
          .thenReturn(Optional.empty());

      var result = underTest.getResourceDisplayName(RESOURCE_DETAIL_ID);

      assertThat(result).isEqualTo(RESOURCE_NAME);
    }

    @Test
    void byGeneralDetailId_CentreOfInterestExists() {
      when(resourceDetailsPersistenceService.getResourceDetailByIdOrThrow(RESOURCE_DETAIL_ID)).thenReturn(resourceDetail);
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(resourceDetail.getSubmissionDetail()))
          .thenReturn(Optional.of(CENTRE_OF_INTEREST));

      var result = underTest.getResourceDisplayName(RESOURCE_DETAIL_ID);

      assertThat(result).isEqualTo("%s (%s)".formatted(RESOURCE_NAME, CENTRE_OF_INTEREST));
    }

    @Test
    void byGeneralDetailID_CentreOfInterestDoesNotExist() {
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(resourceDetail.getSubmissionDetail()))
          .thenReturn(Optional.empty());

      var result = underTest.getResourceDisplayName(resourceDetail);

      assertThat(result).isEqualTo(RESOURCE_NAME);
    }
  }

  @Nested
  class getResourceNameWithCentreOfInterestOrDefault {

    private SubmissionDetail submissionDetail;
    private ResourceDetail resourceDetail;

    @BeforeEach
    void setUp() {
      submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
      resourceDetail = new ResourceDetail();
    }

    @Test
    void whenCentreOfInterestPresent() {
      var expectedName = "name";
      var expectedBlock = "15/2";
      resourceDetail.setResourceName(expectedName);
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(resourceDetail));
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(expectedBlock));

      assertThat(underTest.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
          .isEqualTo("%s (%s)".formatted(expectedName, expectedBlock));
    }

    @Test
    void whenCentreOfInterestNotPresent() {
      var expectedName = "name";
      resourceDetail.setResourceName(expectedName);
      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(resourceDetail));
      when(trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(submissionDetail))
          .thenReturn(Optional.empty());

      assertThat(underTest.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
          .isEqualTo("%s".formatted(expectedName));
    }

    @Test
    void whenNameNotPresent() {
      var submissionInstant = Instant.now();
      submissionDetail.setCreatedTimestamp(submissionInstant);
      var expectedName = SubmissionDetailUtil.getDefaultForNamelessResource(submissionInstant);

      when(resourceDetailsPersistenceService.getResourceDetailBySubmissionDetail(submissionDetail))
          .thenReturn(Optional.of(resourceDetail));

      assertThat(underTest.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
          .isEqualTo("%s".formatted(expectedName));
    }
  }

  @NotNull
  private Resource buildResource(ResourceType resourceType) {
    return ResourceTestUtils.buildResource(resourceType);
  }

  @NotNull
  private ResourceDetail buildGeneralDetail(Resource resource) {
    var resourceDetail = new ResourceDetail();
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setResourceId(resource);
    resourceDetail.setSubmissionDetail(submissionDetail);
    return resourceDetail;
  }
}