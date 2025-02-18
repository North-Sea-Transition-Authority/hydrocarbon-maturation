package uk.co.nstauthority.hydrocarbonmaturation.energyportal;

import java.io.Serial;
import java.io.Serializable;

public record WebUserAccountId(long id) implements Serializable {

  @Serial
  private static final long serialVersionUID = 5589243432900587805L;

  public static WebUserAccountId valueOf(String value) {
    return new WebUserAccountId(Long.parseLong(value));
  }

  public static WebUserAccountId from(Long userWuaId) {
    return new WebUserAccountId(userWuaId);
  }

  @Override
  public long id() {
    return id;
  }

  public int toInt() {
    return ((Long) id).intValue();
  }

  @Override
  public String toString() {
    return String.valueOf(id);
  }
}
