package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;
import static uk.co.nstauthority.hydrocarbonmaturation.util.NotificationBannerTestUtil.notificationBanner;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBanner;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@ContextConfiguration(classes = ManageFieldStatusHistoryController.class)
class ManageFieldStatusHistoryControllerTest extends AbstractSubmissionControllerTest {
  @MockBean
  private FieldStatusHistoryService fieldStatusHistoryService;
  @MockBean
  private FieldStatusFormValidator fieldStatusFormValidator;

  private Field field;
  private FieldStatusHistory fieldStatusHistory;

  @BeforeEach
  void setUp() {
    field = new Field();
    field.setFieldIdentifier(8000);
    field.setResource(ResourceTestUtils.buildResource(ResourceType.FIELD));

    fieldStatusHistory = new FieldStatusHistory();
    fieldStatusHistory.setId(UUID.randomUUID());
    fieldStatusHistory.setFieldStatus(FieldStatus.STATUS_300);
    fieldStatusHistory.setStatusDate(LocalDate.now());
  }

  // not in nested class to pass ArchRule
  @SecurityTest
  void getManageFieldStatusHistory_whenProjectAnalyst_assertOk() throws Exception {
    when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
        .thenReturn(true);
    when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
        .thenReturn(field);

    var form = new FieldStatusForm();
    when(fieldStatusHistoryService.fieldStatusHistoryToForm(any(FieldStatusHistory.class)))
        .thenReturn(form);

    mockMvc.perform(
            get(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                .getAddFieldStatus(field.getFieldIdentifier())))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Nested
  class WhenAddingFieldStatus {

    @SecurityTest
    void getManageFieldStatusHistory_whenNotProjectAnalyst_assertForbidden() throws Exception {
      mockMvc.perform(
              get(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                  .getAddFieldStatus(field.getFieldIdentifier())))
                  .with(user(USER)))
          .andExpect(status().isForbidden());
    }

    @Test
    void getManageFieldStatusHistory_whenFieldDoesNotExist_assertThrowsEntityNotFound() throws Exception {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);
      when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
          .thenCallRealMethod();

      assertThatThrownBy(() -> mockMvc.perform(
          get(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
              .getAddFieldStatus(field.getFieldIdentifier())))
              .with(user(USER)))
      ).hasCauseExactlyInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getManageFieldStatusHistory_assertProperties() throws Exception {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);
      when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
          .thenReturn(field);

      var form = new FieldStatusForm();
      when(fieldStatusHistoryService.fieldStatusHistoryToForm(any(FieldStatusHistory.class)))
          .thenReturn(form);

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  get(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                      .getAddFieldStatus(field.getFieldIdentifier())))
                      .with(user(USER)))
              .andExpect(status().isOk())
              .andExpect(view().name("remi/field/fieldstatushistory/manageFieldStatusHistory"))
              .andReturn()
              .getModelAndView())
          .getModelMap();

      assertThat(modelMap)
          .contains(
              entry("form", form),
              entry("pageTitle", ManageFieldStatusHistoryController.ADD_FIELD_STATUS_TITLE),
              entry("fieldStatusOptions", DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(FieldStatus.class)),
              entry("cancelUrl", ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                  .getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
          );
    }

    @Test
    void saveFieldStatus_whenValidForm_assertRedirection() throws Exception {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);

      var form = new FieldStatusForm();
      form.setFieldStatus(FieldStatus.STATUS_400);
      form.setFieldStatusDateDay("03");
      form.setFieldStatusDateMonth("11");
      form.setFieldStatusDateYear("2002");

      when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
          .thenReturn(field);
      when(fieldStatusFormValidator.isValid(eq(form), any(BindingResult.class), eq(field)))
          .thenReturn(true);

      mockMvc.perform(
              post(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                  .saveFieldStatus(field.getFieldIdentifier(), null, null)))
                  .with(user(USER))
                  .with(csrf())
                  .flashAttr("form", form))
          .andExpect(status().is3xxRedirection())
          .andExpect(redirectedUrl(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
              .getViewFieldStatusHistory(field.getFieldIdentifier(), null))));

      var captor = ArgumentCaptor.forClass(FieldStatusForm.class);
      verify(fieldStatusHistoryService).saveNewFieldStatusHistoryFromForm(captor.capture(), eq(field));

      assertThat(captor.getValue()).isEqualTo(form);
    }

    @Test
    void saveFieldStatus_whenFieldDoesNotExist_assertThrowsEntityNotFound() throws Exception {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);
      when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
          .thenCallRealMethod();

      assertThatThrownBy(() -> mockMvc.perform(
          post(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
              .getAddFieldStatus(field.getFieldIdentifier())))
              .with(user(USER))
              .with(csrf()))
      ).hasCauseExactlyInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void saveFieldStatus_whenInvalidForm_assertProperties() throws Exception {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);

      var form = new FieldStatusForm();
      form.setFieldStatus(FieldStatus.STATUS_400);
      form.setFieldStatusDateDay("03");
      form.setFieldStatusDateMonth("11");
      form.setFieldStatusDateYear("2002");

      when(fieldPersistenceService.getFieldOrThrow(field.getFieldIdentifier()))
          .thenReturn(field);
      when(fieldStatusFormValidator.isValid(eq(form), any(BindingResult.class), eq(field)))
          .thenReturn(false);

      mockMvc.perform(
              post(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                  .saveFieldStatus(field.getFieldIdentifier(), null, null)))
                  .with(user(USER))
                  .with(csrf())
                  .flashAttr("form", form))
          .andExpect(status().isOk())
          .andExpect(view().name("remi/field/fieldstatushistory/manageFieldStatusHistory"));

      verifyNoInteractions(fieldStatusHistoryService);
    }
  }

  @Nested
  class WhenRemovingAFieldStatus {

    @SecurityTest
    void getRemoveFieldStatusConfirmation_whenNotProjectAnalyst_assertForbidden() throws Exception {
      mockMvc.perform(
              get(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                  .getRemoveFieldStatusConfirmation(field.getFieldIdentifier(), fieldStatusHistory.getId())))
                  .with(user(USER)))
          .andExpect(status().isForbidden());
    }

    @SecurityTest
    void getRemoveFieldStatusConfirmation_whenFieldStatusHistoryDoesNotExist_throwsRemiEntityNotFound() throws Exception {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);

      when(fieldStatusHistoryService.getFieldStatusHistoryOrThrow(fieldStatusHistory.getId()))
          .thenThrow(RemiEntityNotFoundException.class);

      mockMvc.perform(
          get(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
              .getRemoveFieldStatusConfirmation(field.getFieldIdentifier(), fieldStatusHistory.getId())))
              .with(user(USER)))
          .andExpect(status().isNotFound());
    }

    @SecurityTest
    void getRemoveFieldStatusConfirmation_whenFieldIdentifierDoesNotMatchFieldStatusHistory_throwsIllegalArgumentException() {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);

      field.setFieldIdentifier(8000);

      var differentField = new Field();
      differentField.setFieldIdentifier(8102);
      fieldStatusHistory.setField(differentField);

      when(fieldStatusHistoryService.getFieldStatusHistoryOrThrow(fieldStatusHistory.getId()))
          .thenReturn(fieldStatusHistory);

      assertThatThrownBy(() -> mockMvc.perform(
          get(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
              .getRemoveFieldStatusConfirmation(field.getFieldIdentifier(), fieldStatusHistory.getId())))
              .with(user(USER)))
      ).hasCauseExactlyInstanceOf(IllegalArgumentException.class);
    }

    @SecurityTest
    void removeFieldStatus_whenNotProjectAnalyst_assertForbidden() throws Exception {
      mockMvc.perform(
              post(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                  .removeFieldStatus(field.getFieldIdentifier(), fieldStatusHistory.getId(), null)))
                  .with(user(USER))
                  .with(csrf()))
          .andExpect(status().isForbidden());
    }

    @Test
    void getRemoveFieldStatusConfirmation_assertProperties() throws Exception {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);

      fieldStatusHistory.setField(field);

      when(fieldStatusHistoryService.getFieldStatusHistoryOrThrow(fieldStatusHistory.getId()))
          .thenReturn(fieldStatusHistory);

      var modelMap = Objects.requireNonNull(mockMvc.perform(
                  get(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                      .getRemoveFieldStatusConfirmation(field.getFieldIdentifier(), fieldStatusHistory.getId())))
                      .with(user(USER)))
              .andExpect(status().isOk())
              .andReturn()
              .getModelAndView()
              .getModelMap()
      );

      assertThat(modelMap)
          .contains(
              entry("pageTitle", ManageFieldStatusHistoryController.REMOVE_FIELD_STATUS_TITLE),
              entry("fieldStatusHistory", ViewFieldStatusHistoryService.toFieldStatusDto(fieldStatusHistory)),
              entry("cancelUrl", ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                  .getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
          );
    }

    @Test
    void removeFieldStatus_assertCallsAndRedirection() throws Exception {
      when(permissionService.hasPermission(USER, Set.of(RolePermission.PROJECT_ANALYST)))
          .thenReturn(true);

      fieldStatusHistory.setField(field);

      when(fieldStatusHistoryService.getFieldStatusHistoryOrThrow(fieldStatusHistory.getId()))
          .thenReturn(fieldStatusHistory);

      var fieldStatusHistoryDto = ViewFieldStatusHistoryService.toFieldStatusDto(fieldStatusHistory);
      var expectedBanner = NotificationBanner.builder()
          .withBannerType(NotificationBannerType.SUCCESS)
          .withTitle("Field status entry deleted")
          .withContent("The field status entry for status '%s' on date '%s' has been deleted."
              .formatted(fieldStatusHistoryDto.fieldStatus(), fieldStatusHistoryDto.fieldStatusDate()))
          .build();

      mockMvc.perform(
              post(ReverseRouter.route(on(ManageFieldStatusHistoryController.class)
                  .removeFieldStatus(field.getFieldIdentifier(), fieldStatusHistory.getId(), null)))
                  .with(user(USER))
                  .with(csrf()))
          .andExpect(status().is3xxRedirection())
              .andExpect(redirectedUrl(ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                      .getViewFieldStatusHistory(field.getFieldIdentifier(), null))))
          .andExpect(notificationBanner(expectedBanner));

      verify(fieldStatusHistoryService).removeFieldStatusHistory(fieldStatusHistory);
    }
  }
}