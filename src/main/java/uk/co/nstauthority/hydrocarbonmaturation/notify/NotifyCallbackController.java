package uk.co.nstauthority.hydrocarbonmaturation.notify;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.co.fivium.notify.library.model.NotifyCallback;
import uk.co.fivium.notify.library.service.FiviumNotifyCallbackAccessDeniedException;
import uk.co.fivium.notify.library.service.FiviumNotifyCallbackService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.Unauthenticated;

/**
 * Rest controller to handle GOV.UK Notify callback requests. A callback message is formatted in JSON.
 * For more information about callbacks visit https://docs.notifications.service.gov.uk/java.html#callbacks
 */
@RestController
@Unauthenticated
public class NotifyCallbackController {

  private final FiviumNotifyCallbackService fiviumNotifyCallbackService;

  public NotifyCallbackController(FiviumNotifyCallbackService fiviumNotifyCallbackService) {
    this.fiviumNotifyCallbackService = fiviumNotifyCallbackService;
  }

  @PostMapping("/notify/callback")
  public ResponseEntity<Object> notifyCallback(@RequestBody NotifyCallback callbackRequest,
                                               @RequestHeader("Authorization") String bearerToken) {
    try {
      fiviumNotifyCallbackService.handleCallback(callbackRequest, bearerToken);
    } catch (FiviumNotifyCallbackAccessDeniedException e) {
      throw new NotifyCallbackAccessDeniedException(e);
    }
    return ResponseEntity.ok().build();
  }
}