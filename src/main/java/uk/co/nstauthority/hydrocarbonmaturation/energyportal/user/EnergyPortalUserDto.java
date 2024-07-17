package uk.co.nstauthority.hydrocarbonmaturation.energyportal.user;

import uk.co.fivium.digitalnotificationlibrary.core.notification.email.EmailRecipient;

public record EnergyPortalUserDto(
    long webUserAccountId,
    String title,
    String forename,
    String surname,
    String emailAddress,
    String telephoneNumber,
    boolean isSharedAccount,
    boolean canLogin
) implements EmailRecipient {

  public String displayName() {
    return "%s %s".formatted(forename, surname);
  }

  @Override
  public String getEmailAddress() {
    return emailAddress;
  }
}