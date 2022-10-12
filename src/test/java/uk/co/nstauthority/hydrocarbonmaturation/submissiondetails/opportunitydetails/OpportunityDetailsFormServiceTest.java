package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsFormServiceTest {

  private static OpportunityDetailsFormValidator opportunityDetailsFormValidator;
  private static OpportunityDetailsFormService opportunityDetailsFormService;
  private static OpportunityDetailsPersistenceService opportunityDetailsPersistenceService;
  private static SubmissionDetail submissionDetail;
  private static SupportingDocumentService supportingDocumentService;

  @BeforeAll
  static void setup() {
    opportunityDetailsFormValidator = mock(OpportunityDetailsFormValidator.class);
    opportunityDetailsPersistenceService = mock(OpportunityDetailsPersistenceService.class);
    supportingDocumentService = mock(SupportingDocumentService.class);
    opportunityDetailsFormService = new OpportunityDetailsFormService(opportunityDetailsFormValidator, opportunityDetailsPersistenceService,
        supportingDocumentService);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new OpportunityDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    opportunityDetailsFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(opportunityDetailsFormValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void generalDetailEntityToForm_newEntity_thenEmptyForm() {
    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail)).thenReturn(Optional.empty());

    assertThat(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail))
        .extracting(
            OpportunityDetailsForm::getOpportunityType,
            OpportunityDetailsForm::getOtherTypeOfOpportunity,
            OpportunityDetailsForm::getOpportunityComments,
            OpportunityDetailsForm::getKeyEvaluationWorkCarriedOut,
            OpportunityDetailsForm::getKeyWorkRequiredForFurtherMaturation
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void generalDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var opportunityDetail = getOpportunityDetail(submissionDetail);
    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail)).thenReturn(Optional.of(opportunityDetail));

    assertThat(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail))
        .extracting(
            OpportunityDetailsForm::getOpportunityType,
            OpportunityDetailsForm::getOtherTypeOfOpportunity,
            OpportunityDetailsForm::getOpportunityComments,
            OpportunityDetailsForm::getKeyEvaluationWorkCarriedOut,
            OpportunityDetailsForm::getKeyWorkRequiredForFurtherMaturation
        )
        .containsExactly(
            opportunityDetail.getOpportunityType(),
            opportunityDetail.getOtherTypeOfOpportunity(),
            opportunityDetail.getOpportunityComments(),
            opportunityDetail.getKeyEvaluationWorkCarriedOut(),
            opportunityDetail.getKeyWorkRequiredForFurtherMaturation()
        );
  }

  @Test
  void getMapOfOpportunityUploads_VerifyCalls() {
    FileUploadDetail fileUploadDetail1 = getFileUploadDetail();
    FileUploadDetail fileUploadDetail2 = getFileUploadDetail();

    var form = new OpportunityDetailsForm();
    form.setMapOfOpportunity(List.of(fileUploadDetail1, fileUploadDetail2));

    opportunityDetailsFormService.getMapOfOpportunityUploads(form);

    verify(supportingDocumentService, times(1)).getFileUploadFdsViewList(anyList());
  }

  private OpportunityDetail getOpportunityDetail(SubmissionDetail submissionDetail) {
    var opportunityDetail =  new OpportunityDetail();
    opportunityDetail.setSubmissionDetail(submissionDetail);
    opportunityDetail.setOpportunityType(OpportunityType.LEAD);
    opportunityDetail.setOpportunityComments("comments");
    opportunityDetail.setKeyEvaluationWorkCarriedOut("key");
    return opportunityDetail;
  }

  private FileUploadDetail getFileUploadDetail() {
    FileUploadDetail fileUploadDetail = new FileUploadDetail();
    fileUploadDetail.setUploadedFileId(UUID.randomUUID().toString());
    return fileUploadDetail;
  }
}