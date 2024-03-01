package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class ProjectSummaryService {
  private final List<SummarySectionService<Project>> summarySectionServices;

  @Autowired
  public ProjectSummaryService(List<SummarySectionService<Project>> summarySectionServices) {
    this.summarySectionServices = summarySectionServices;
  }

  public List<SummarySection> getSummarySections(Project project) {
    return summarySectionServices.stream()
        .map(service -> service.getSummarySection(project))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(SummarySection::displayOrder))
        .toList();
  }
}
