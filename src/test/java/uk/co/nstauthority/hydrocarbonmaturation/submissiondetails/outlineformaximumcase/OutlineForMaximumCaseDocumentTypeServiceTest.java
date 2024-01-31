package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCaseDocumentTypeServiceTest {


  @Mock
  private ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  @InjectMocks
  private OutlineForMaximumCaseDocumentTypeService underTest;

  @Nested
  class WhenGettingOutlineForMaximumCaseDocumentType {
    @Mock
    private SubmissionDetail submissionDetail;
    @Mock
    private ResourceDetail resourceDetail;

    @BeforeEach
    void setup() {
      when(resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail)).thenReturn(resourceDetail);
    }

    @Test
    void onshore() {
      when(resourceDetail.getOnshoreOffshore()).thenReturn(OnshoreOffshore.ONSHORE);
      var result = underTest.getOutlineForMaximumCaseDocumentType(submissionDetail);
      assertThat(result).isEqualTo(FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_ONSHORE);
    }

    @Test
    void offshore() {
      when(resourceDetail.getOnshoreOffshore()).thenReturn(OnshoreOffshore.OFFSHORE);
      var result = underTest.getOutlineForMaximumCaseDocumentType(submissionDetail);
      assertThat(result).isEqualTo(FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE);
    }
  }

  @Nested
  class WhenGettingOutlineForMaximumCaseDocumentTypeForAResourceDetail {
    @Mock
    private ResourceDetail resourceDetail;

    void onshore() {
      when(resourceDetail.getOnshoreOffshore()).thenReturn(OnshoreOffshore.ONSHORE);
      var result = underTest.getOutlineForMaximumCaseDocumentType(resourceDetail);
      assertThat(result).isEqualTo(FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_ONSHORE);
    }

    @Test
    void offshore() {
      when(resourceDetail.getOnshoreOffshore()).thenReturn(OnshoreOffshore.OFFSHORE);
      var result = underTest.getOutlineForMaximumCaseDocumentType(resourceDetail);
      assertThat(result).isEqualTo(FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE);
    }
  }
}