package uk.co.nstauthority.hydrocarbonmaturation.authentication;

public class InvalidAuthenticationException extends RuntimeException {

  public InvalidAuthenticationException(String message) {
    super(message);
  }
}
