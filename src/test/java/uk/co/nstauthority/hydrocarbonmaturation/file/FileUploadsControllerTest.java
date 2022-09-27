package uk.co.nstauthority.hydrocarbonmaturation.file;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;

@WithMockUser
@ContextConfiguration(classes = SubmissionSupportingDocumentsController.class)
class FileUploadsControllerTest extends AbstractControllerTest {

  @MockBean
  FileUploadService fileUploadService;

  @BeforeAll
  static void beforeAll() {

  }

  @Test
  void upload_assertStatusOk() throws Exception {

  }

  @Test
  void download() {
  }

  @Test
  void delete() {
  }
}