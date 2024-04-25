package uk.co.nstauthority.hydrocarbonmaturation.authorisation;

import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

class TestControllers {

  @Controller
  @RequestMapping("{submissionDetailId}/end-point-test")
  static class NoEndpointSecurityController {
    @GetMapping("/no-security")
    String get(@PathVariable UUID submissionDetailId) {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("{submissionDetailId}/end-point-test")
  @Unauthenticated
  static class EndpointSecurityDisabledController {
    @GetMapping("/unauthenticated")
    String get(@PathVariable UUID submissionDetailId) {
      return "some data";
    }
  }

  @Controller
  @AccessibleByServiceUsers
  static class ControllerWithoutSubmissionDetailId {
    @GetMapping("/end-point-test")
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("{submissionDetailId}/end-point-test")
  @AccessibleByServiceUsers
  static class ControllerWithSubmissionDetailId {
    @GetMapping("/with-submission-detail")
    String get(@PathVariable UUID submissionDetailId) {
      return "some data";
    }
  }

  @Controller
  @AccessibleByServiceUsers
  static class ControllerWithoutProjectId {
    @GetMapping("/without-project")
    String get() {
      return "some data";
    }
  }

  @Controller
  @RequestMapping("{projectId}/end-point-test")
  @AccessibleByServiceUsers
  static class ControllerWithProjectId {
    @GetMapping("/with-project")
    String get(@PathVariable UUID projectId) {
      return "some data";
    }
  }

  @Controller
  @AccessibleByServiceUsers
  static class ControllerWithoutResourceId {
    @GetMapping("/without-resource")
    String get() {
      return "some data";
    }
  }

  @Controller
  @AccessibleByServiceUsers
  @RequestMapping("{resourceId}/end-point-test")
  static class ControllerWithResourceId {
    @GetMapping("/with-resource")
    String get(@PathVariable UUID resourceId) {
      return "some data";
    }
  }
}
