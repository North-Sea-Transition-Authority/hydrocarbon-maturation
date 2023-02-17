package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

public record ResourceRecordSearchResultItem(
    String headingText,
    String captionText,
    String actionUrl,
    ResourceRecordSearchResultItemModelProperties modelProperties
) {

  public static ResourceRecordSearchResultItem empty() {
    return new ResourceRecordSearchResultItem("", "", "", new ResourceRecordSearchResultItemModelProperties());
  }
}
