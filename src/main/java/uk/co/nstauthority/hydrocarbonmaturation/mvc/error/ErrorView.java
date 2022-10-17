package uk.co.nstauthority.hydrocarbonmaturation.mvc.error;

public enum ErrorView {

  DEFAULT_ERROR("remi/error/error"),
  PAGE_NOT_FOUND("remi/error/404"),
  UNAUTHORISED("remi/error/403");

  private final String viewName;

  ErrorView(String viewName) {
    this.viewName = viewName;
  }

  public String getViewName() {
    return viewName;
  }
}
