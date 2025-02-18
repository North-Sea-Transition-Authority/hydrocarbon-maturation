package uk.co.nstauthority.hydrocarbonmaturation.energyportal.user;

import uk.co.fivium.energyportalapi.generated.types.AccountStatus;
import uk.co.fivium.energyportalapi.generated.types.User;
import uk.co.nstauthority.hydrocarbonmaturation.exception.IllegalUtilClassInstantiationException;

class PortalUserTestUtil {

  private PortalUserTestUtil() {
    throw new IllegalUtilClassInstantiationException(this.getClass());
  }

  static Builder Builder() {
    return new Builder();
  }

  static class Builder {

    private long webUserAccountId = 1L;
    private long personId = 2L;
    private String loginId = "login@test.com";
    private String title = "title";
    private String forename = "forename";
    private String surname = "surname";
    private String middleInitials = "initial";
    private String primaryEmailAddress = "email address";
    private String telephoneNumber = "telephone number";
    private boolean canLogin = true;
    private boolean isAccountShared = false;
    private AccountStatus status = AccountStatus.ACTIVE;
    private Builder() {}

    Builder withWebUserAccountId(long webUserAccountId) {
      this.webUserAccountId = webUserAccountId;
      return this;
    }

    Builder withPersonId(long personId) {
      this.personId = personId;
      return this;
    }

    Builder withLoginId(String loginId) {
      this.loginId = loginId;
      return this;
    }

    Builder withTitle(String title) {
      this.title = title;
      return this;
    }

    Builder withForename(String forename) {
      this.forename = forename;
      return this;
    }

    Builder withSurname(String surname) {
      this.surname = surname;
      return this;
    }

    Builder withInitials(String initials) {
      this.middleInitials = initials;
      return this;
    }

    Builder withEmailAddress(String emailAddress) {
      this.primaryEmailAddress = emailAddress;
      return this;
    }

    Builder withPhoneNumber(String phoneNumber) {
      this.telephoneNumber = phoneNumber;
      return this;
    }

    Builder canLogin(boolean canLogin) {
      this.canLogin = canLogin;
      return this;
    }

    Builder isSharedAccount(boolean isAccountShared) {
      this.isAccountShared = isAccountShared;
      return this;
    }

    Builder withStatus(AccountStatus status) {
      this.status = status;
      return this;
    }

    User build() {
      return new User(
          webUserAccountId,
          personId,
          loginId,
          title,
          forename,
          surname,
          middleInitials,
          primaryEmailAddress,
          telephoneNumber,
          canLogin,
          isAccountShared,
          status
      );
    }

  }

}
