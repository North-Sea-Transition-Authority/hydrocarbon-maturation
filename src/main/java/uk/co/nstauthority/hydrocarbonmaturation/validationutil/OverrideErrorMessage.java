package uk.co.nstauthority.hydrocarbonmaturation.validationutil;

public enum OverrideErrorMessage {
  TRUE,
  FALSE;

  static OverrideErrorMessage getFromBoolean(boolean shouldOverride) {
    return shouldOverride ? OverrideErrorMessage.TRUE : OverrideErrorMessage.FALSE;
  }
}
