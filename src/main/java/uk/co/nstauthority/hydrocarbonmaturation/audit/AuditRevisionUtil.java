package uk.co.nstauthority.hydrocarbonmaturation.audit;

import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;

public class AuditRevisionUtil {

  private static final ThreadLocal<User> fallbackAuditUser = new ThreadLocal<>();

  private AuditRevisionUtil() {
  }

  public static User getFallbackAuditUser() {
    return fallbackAuditUser.get();
  }

  public static void withFallbackAuditUser(User user, Runnable runnable) {
    fallbackAuditUser.set(user);
    try {
      runnable.run();
    } finally {
      fallbackAuditUser.remove();
    }
  }
}