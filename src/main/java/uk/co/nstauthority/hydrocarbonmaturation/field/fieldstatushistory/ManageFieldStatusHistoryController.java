package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/manage-field-status/{fieldIdentifier}")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
public class ManageFieldStatusHistoryController {
  public static final String ADD_FIELD_STATUS_TITLE = "Add field status";
  public static final String REMOVE_FIELD_STATUS_TITLE = "Are you sure you want to remove this field status entry?";

  private final FieldPersistenceService fieldPersistenceService;
  private final FieldStatusHistoryService fieldStatusHistoryService;
  private final FieldStatusFormValidator fieldStatusFormValidator;

  @Autowired
  public ManageFieldStatusHistoryController(FieldPersistenceService fieldPersistenceService,
                                            FieldStatusHistoryService fieldStatusHistoryService,
                                            FieldStatusFormValidator fieldStatusFormValidator) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldStatusHistoryService = fieldStatusHistoryService;
    this.fieldStatusFormValidator = fieldStatusFormValidator;
  }

  @GetMapping("/add")
  public ModelAndView getAddFieldStatus(@PathVariable("fieldIdentifier") Integer fieldIdentifier) {
    fieldPersistenceService.getFieldOrThrow(fieldIdentifier);

    var cancelUrl = ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
        .getViewFieldStatusHistory(fieldIdentifier, null));

    return getManageFieldStatusModelAndView(
        fieldStatusHistoryService.fieldStatusHistoryToForm(new FieldStatusHistory()),
        cancelUrl
    );
  }

  @PostMapping("/add")
  public ModelAndView saveFieldStatus(@PathVariable("fieldIdentifier") Integer fieldIdentifier,
                                      @ModelAttribute("form") FieldStatusForm form,
                                      BindingResult bindingResult) {
    var cancelUrl = ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
        .getViewFieldStatusHistory(fieldIdentifier, null));

    return validateSaveFieldStatusAndRedirect(fieldIdentifier, form, bindingResult, cancelUrl);
  }

  @GetMapping("{fieldStatusHistoryId}/remove")
  public ModelAndView getRemoveFieldStatusConfirmation(@PathVariable("fieldIdentifier") Integer fieldIdentifier,
                                                       @PathVariable("fieldStatusHistoryId") UUID fieldStatusHistoryId) {
    var cancelUrl = ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
        .getViewFieldStatusHistory(fieldIdentifier, null));

    return getRemoveConfirmationModelAndView(fieldStatusHistoryId, fieldIdentifier, cancelUrl);
  }

  @PostMapping("{fieldStatusHistoryId}/remove")
  public ModelAndView removeFieldStatus(@PathVariable("fieldIdentifier") Integer fieldIdentifier,
                                        @PathVariable("fieldStatusHistoryId") UUID fieldStatusHistoryId,
                                        RedirectAttributes redirectAttributes) {

    doRemoveFieldStatusAndAddNotificationBanner(fieldIdentifier, fieldStatusHistoryId, redirectAttributes);

    return ReverseRouter.redirect(on(ViewFieldStatusHistoryController.class).getViewFieldStatusHistory(fieldIdentifier, null));
  }

  private ModelAndView getManageFieldStatusModelAndView(FieldStatusForm fieldStatusForm, String cancelUrl) {
    return new ModelAndView("remi/field/fieldstatushistory/manageFieldStatusHistory")
        .addObject("form", fieldStatusForm)
        .addObject("pageTitle", ManageFieldStatusHistoryController.ADD_FIELD_STATUS_TITLE)
        .addObject("fieldStatusOptions",
            DisplayableEnumOptionUtil.getDisplayableOptionsWithDescription(FieldStatus.class))
        .addObject(
            "cancelUrl", cancelUrl);
  }

  private ModelAndView getRemoveConfirmationModelAndView(UUID fieldStatusHistoryId, Integer fieldIdentifier, String cancelUrl) {
    var fieldStatusHistory = fieldStatusHistoryService.getFieldStatusHistoryOrThrow(fieldStatusHistoryId);
    var fieldStatusHistoryDto = ViewFieldStatusHistoryService.toFieldStatusDto(fieldStatusHistory);

    fieldStatusHistoryFieldEqualsFieldOrThrow(fieldStatusHistoryId, fieldIdentifier, fieldStatusHistory);

    return new ModelAndView("remi/field/fieldstatushistory/removeFieldStatusHistory")
        .addObject("pageTitle", REMOVE_FIELD_STATUS_TITLE)
        .addObject("fieldStatusHistory", fieldStatusHistoryDto)

        .addObject("cancelUrl", cancelUrl);
  }

  private static void fieldStatusHistoryFieldEqualsFieldOrThrow(UUID fieldStatusHistoryId,
                                                                Integer fieldIdentifier,
                                                                FieldStatusHistory fieldStatusHistory) {
    if (!fieldStatusHistory.getField().getFieldIdentifier().equals(fieldIdentifier)) {
      throw new IllegalArgumentException("Field with id %s has no field status history with id %s"
          .formatted(fieldIdentifier, fieldStatusHistoryId));
    }
  }

  private ModelAndView validateSaveFieldStatusAndRedirect(Integer fieldIdentifier,
                                                          FieldStatusForm form,
                                                          BindingResult bindingResult,
                                                          String cancelUrl) {
    var field = fieldPersistenceService.getFieldOrThrow(fieldIdentifier);

    if (fieldStatusFormValidator.isValid(form, bindingResult, field)) {
      fieldStatusHistoryService.saveNewFieldStatusHistoryFromForm(form, field);
      return ReverseRouter.redirect(on(ViewFieldStatusHistoryController.class)
          .getViewFieldStatusHistory(field.getFieldIdentifier(), null));
    }

    return getManageFieldStatusModelAndView(form, cancelUrl);
  }

  private void doRemoveFieldStatusAndAddNotificationBanner(Integer fieldIdentifier,
                                                           UUID fieldStatusHistoryId,
                                                           RedirectAttributes redirectAttributes) {
    var fieldStatusHistory = fieldStatusHistoryService.getFieldStatusHistoryOrThrow(fieldStatusHistoryId);
    var fieldStatusHistoryDto = ViewFieldStatusHistoryService.toFieldStatusDto(fieldStatusHistory);

    fieldStatusHistoryFieldEqualsFieldOrThrow(fieldStatusHistoryId, fieldIdentifier, fieldStatusHistory);

    fieldStatusHistoryService.removeFieldStatusHistory(fieldStatusHistory);

    NotificationBannerUtil.addSuccessNotification(redirectAttributes, "Field status entry deleted",
        "The field status entry for status '%s' on date '%s' has been deleted."
            .formatted(fieldStatusHistoryDto.fieldStatus(), fieldStatusHistoryDto.fieldStatusDate()));
  }
}
