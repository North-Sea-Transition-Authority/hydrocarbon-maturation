package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

public record ResourceRecordSearchResultItem(
    String headingText,
    String captionText,
    String actionUrl,
    ResourceRecordSearchResultItemModelProperties modelProperties
) {}
