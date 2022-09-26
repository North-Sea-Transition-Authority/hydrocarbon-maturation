package uk.co.nstauthority.hydrocarbonmaturation.file;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@WithMockUser
@ContextConfiguration(classes = FileUploadsController.class)
class FileUploadsControllerTest extends AbstractControllerTest {

  @MockBean
  FileUploadService fileUploadService;

  @BeforeAll
  static void beforeAll() {

  }

  @Test
  void upload_assertStatusOk() throws Exception {

    var multipartFile = Mockito.mock(MultipartFile.class);
    mockMvc.perform(
            get(ReverseRouter.route(on(FileUploadsController.class).upload(VirtualFolder.SUPPORTING_DOCUMENTS.name(), multipartFile))))
        .andExpect(status().isOk());
  }

  @Test
  void download() {
  }

  @Test
  void delete() {
  }
}