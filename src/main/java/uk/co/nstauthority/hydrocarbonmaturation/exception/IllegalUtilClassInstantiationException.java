package uk.co.nstauthority.hydrocarbonmaturation.exception;

public class IllegalUtilClassInstantiationException extends IllegalStateException {

  public IllegalUtilClassInstantiationException(Class<?> clazz) {
    super("%s is a util class and should not be instantiated".formatted(clazz.getName()));
  }
}
