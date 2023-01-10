package uk.co.nstauthority.hydrocarbonmaturation.workarea;

public record WorkAreaItem(
    WorkAreaItemType type,
    String headingText,
    String captionText,
    String actionUrl,
    WorkAreaItemModelProperties modelProperties
) {}
