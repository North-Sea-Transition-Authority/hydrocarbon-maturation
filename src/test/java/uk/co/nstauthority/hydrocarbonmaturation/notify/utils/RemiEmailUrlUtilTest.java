package uk.co.nstauthority.hydrocarbonmaturation.notify.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

class RemiEmailUrlUtilTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();
  private static final String CONTEXT_PATH = "/remi";
  private static final String SERVICE_URL = "http://localhost" + CONTEXT_PATH;
  private static final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils
      .createNewProspectSubmissionDetail();
  private static MockHttpServletRequest request;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    request.setContextPath(CONTEXT_PATH);
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @Test
  void getSystemOfRecordResourceUrl() {
    assertThat(RemiEmailUrlUtil.getSystemOfRecordResourceUrl(SUBMISSION_DETAIL))
        .isEqualTo(SERVICE_URL + ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(SUBMISSION_DETAIL.getResourceId().getId())));
  }
}