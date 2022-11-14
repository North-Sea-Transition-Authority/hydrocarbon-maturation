package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import java.util.List;

public record TaskListSection(
    String displayName,
    int displayOrder,
    List<TaskListItem> items
) { }
