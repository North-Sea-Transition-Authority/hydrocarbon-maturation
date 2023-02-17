package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
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
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
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

    var submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_PROSPECT);
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new OpportunityDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    opportunityDetailsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(opportunityDetailsFormValidator, times(1)).validate(
        eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(
            SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void opportunityDetailEntityToForm_newEntity_thenEmptyForm() {
    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail)).thenReturn(Optional.empty());
    var fileUploadFormList = List.of(mock(FileUploadForm.class));
    when(supportingDocumentService.getFileUploadFormListForSubmissionAndType(submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY))
        .thenReturn(fileUploadFormList);

    assertThat(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail))
        .extracting(
            OpportunityDetailsForm::getOpportunityType,
            OpportunityDetailsForm::getOtherTypeOfOpportunity,
            OpportunityDetailsForm::getOpportunityComments,
            OpportunityDetailsForm::getKeyEvaluationWorkCarriedOut,
            OpportunityDetailsForm::getKeyWorkRequiredForFurtherMaturation,
            OpportunityDetailsForm::getMapOfOpportunity
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            fileUploadFormList
        );
  }

  @Test
  void opportunityDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var opportunityDetail = getOpportunityDetail(submissionDetail);
    when(opportunityDetailsPersistenceService.getOpportunityDetail(submissionDetail)).thenReturn(Optional.of(opportunityDetail));
    var fileUploadFormList = List.of(mock(FileUploadForm.class));
    when(supportingDocumentService.getFileUploadFormListForSubmissionAndType(submissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY))
        .thenReturn(fileUploadFormList);

    assertThat(opportunityDetailsFormService.getOpportunityDetailsForm(submissionDetail))
        .extracting(
            OpportunityDetailsForm::getOpportunityType,
            OpportunityDetailsForm::getOtherTypeOfOpportunity,
            OpportunityDetailsForm::getOpportunityComments,
            OpportunityDetailsForm::getKeyEvaluationWorkCarriedOut,
            OpportunityDetailsForm::getKeyWorkRequiredForFurtherMaturation,
            OpportunityDetailsForm::getMapOfOpportunity
        )
        .containsExactly(
            opportunityDetail.getOpportunityType(),
            opportunityDetail.getOtherTypeOfOpportunity(),
            opportunityDetail.getOpportunityComments(),
            opportunityDetail.getKeyEvaluationWorkCarriedOut(),
            opportunityDetail.getKeyWorkRequiredForFurtherMaturation(),
            fileUploadFormList
        );
  }

  @Test
  void getMapOfOpportunityUploads_VerifyCalls() {
    var fileUploadForm1 = getFileUploadForm();
    var fileUploadForm2 = getFileUploadForm();

    var form = new OpportunityDetailsForm();
    form.setMapOfOpportunity(List.of(fileUploadForm1, fileUploadForm2));

    opportunityDetailsFormService.getMapOfOpportunityUploads(form);

    verify(supportingDocumentService, times(1)).getUploadedFileViewList(anyList());
  }

  private OpportunityDetail getOpportunityDetail(SubmissionDetail submissionDetail) {
    var opportunityDetail =  new OpportunityDetail();
    opportunityDetail.setSubmissionDetail(submissionDetail);
    opportunityDetail.setOpportunityType(OpportunityType.LEAD);
    opportunityDetail.setOpportunityComments("comments");
    opportunityDetail.setKeyEvaluationWorkCarriedOut("key");
    return opportunityDetail;
  }

  private FileUploadForm getFileUploadForm() {
    var fileUploadForm = new FileUploadForm();
    fileUploadForm.setUploadedFileId(UUID.randomUUID());
    return fileUploadForm;
  }
}