package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsPersistenceServiceTest {

  @Mock
  private OpportunityDetailsRepository opportunityDetailsRepository;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;
  private OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    opportunityDetailsPersistenceService = new OpportunityDetailsPersistenceService(opportunityDetailsRepository,
        supportingDocumentService);

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void saveOpportunityDetails_whenGivenAForm_verifyCreatedEntity() {
    var form = new OpportunityDetailsForm();
    form.setOpportunityType(OpportunityType.OTHER);
    form.setOtherTypeOfOpportunity("Other type");
    form.setOpportunityComments("Comments");
    form.setKeyEvaluationWorkCarriedOut("Key evluation");
    form.setKeyWorkRequiredForFurtherMaturation("Key work");
    form.setMapOfOpportunity(List.of(mock(UploadedFileForm.class)));
    form.setGeologicalCrossSection(List.of(mock(UploadedFileForm.class)));

    var opportunityDetailsArgumentCaptor = ArgumentCaptor.forClass(OpportunityDetail.class);

    opportunityDetailsPersistenceService.saveOpportunityDetails(form, submissionDetail);

    verify(supportingDocumentService, times(1)).saveDocuments(submissionDetail, form.getMapOfOpportunity(), FileDocumentType.MAP_OF_OPPORTUNITY);
    verify(supportingDocumentService, times(1)).saveDocuments(submissionDetail, form.getGeologicalCrossSection(), FileDocumentType.GEOLOGICAL_CROSS_SECTION);
    verify(supportingDocumentService, times(1)).saveDocuments(submissionDetail, form.getRepresentativeSeismicSection(), FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION);
    verify(opportunityDetailsRepository, times(1)).save(opportunityDetailsArgumentCaptor.capture());
    var savedOpportunityDetail = (OpportunityDetail) opportunityDetailsArgumentCaptor.getValue();

    assertThat(savedOpportunityDetail)
        .extracting(
            OpportunityDetail::getOpportunityType,
            OpportunityDetail::getOtherTypeOfOpportunity,
            OpportunityDetail::getOpportunityComments,
            OpportunityDetail::getKeyEvaluationWorkCarriedOut,
            OpportunityDetail::getKeyWorkRequiredForFurtherMaturation
        )
        .containsExactly(
            form.getOpportunityType(),
            form.getOtherTypeOfOpportunity(),
            form.getOpportunityComments(),
            form.getKeyEvaluationWorkCarriedOut(),
            form.getKeyWorkRequiredForFurtherMaturation()
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void saveOpportunityDetails_whenSubmission_whenGivenAForm_verifyCreatedEntity(SubmissionType submissionType) {
    var form = new OpportunityDetailsForm();
    submissionDetail.setSubmissionType(submissionType);
    OpportunityType expectedOpportunityType;
    if (submissionDetail.isDryWellSubmission()) {
      expectedOpportunityType = OpportunityType.DRY_WELL;
    } else if (submissionDetail.isDiscoverySubmission()) {
      expectedOpportunityType = OpportunityType.DISCOVERY;
    } else {
      expectedOpportunityType = null;
    }
    form.setOpportunityComments("Comments");
    form.setKeyEvaluationWorkCarriedOut("Key evluation");
    form.setKeyWorkRequiredForFurtherMaturation("Key work");

    var opportunityDetailsArgumentCaptor = ArgumentCaptor.forClass(OpportunityDetail.class);

    opportunityDetailsPersistenceService.saveOpportunityDetails(form, submissionDetail);

    verify(supportingDocumentService, times(1)).saveDocuments(submissionDetail, form.getMapOfOpportunity(), FileDocumentType.MAP_OF_OPPORTUNITY);
    verify(supportingDocumentService, times(1)).saveDocuments(submissionDetail, form.getGeologicalCrossSection(), FileDocumentType.GEOLOGICAL_CROSS_SECTION);
    verify(supportingDocumentService, times(1)).saveDocuments(submissionDetail, form.getRepresentativeSeismicSection(), FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION);
    verify(opportunityDetailsRepository, times(1)).save(opportunityDetailsArgumentCaptor.capture());
    var savedOpportunityDetail = (OpportunityDetail) opportunityDetailsArgumentCaptor.getValue();

    assertThat(savedOpportunityDetail)
        .extracting(
            OpportunityDetail::getOtherTypeOfOpportunity,
            OpportunityDetail::getOpportunityType,
            OpportunityDetail::getOpportunityComments,
            OpportunityDetail::getKeyEvaluationWorkCarriedOut,
            OpportunityDetail::getKeyWorkRequiredForFurtherMaturation
        )
        .containsExactly(
            null,
            expectedOpportunityType,
            form.getOpportunityComments(),
            form.getKeyEvaluationWorkCarriedOut(),
            form.getKeyWorkRequiredForFurtherMaturation()
        );
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void createInitialOpportunityDetail(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    OpportunityType expectedOpportunityType;
    if (submissionDetail.isDryWellSubmission()) {
      expectedOpportunityType = OpportunityType.DRY_WELL;
    } else if (submissionDetail.isDiscoverySubmission()) {
      expectedOpportunityType = OpportunityType.DISCOVERY;
    } else {
      expectedOpportunityType = null;
    }
    var opportunityDetailsArgumentCaptor = ArgumentCaptor.forClass(OpportunityDetail.class);

    opportunityDetailsPersistenceService.createOrUpdateOpportunityDetail(submissionDetail);

    verify(opportunityDetailsRepository, times(1)).save(opportunityDetailsArgumentCaptor.capture());
    var savedOpportunityDetail = (OpportunityDetail) opportunityDetailsArgumentCaptor.getValue();

    assertThat(savedOpportunityDetail)
        .hasAllNullFieldsOrPropertiesExcept("opportunityType", "submissionDetail")
        .extracting(
            OpportunityDetail::getOpportunityType,
            OpportunityDetail::getSubmissionDetail)
        .containsExactly(
            expectedOpportunityType,
            submissionDetail);
  }

  @Test
  void getOpportunityDetail_verifyMethodCall() {
    opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail);
    verify(opportunityDetailsRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}