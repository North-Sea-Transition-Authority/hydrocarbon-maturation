package uk.co.nstauthority.hydrocarbonmaturation.authentication;

class InvalidAuthenticationException extends RuntimeException {

  public InvalidAuthenticationException(String message) {
    super(message);
  }
}
