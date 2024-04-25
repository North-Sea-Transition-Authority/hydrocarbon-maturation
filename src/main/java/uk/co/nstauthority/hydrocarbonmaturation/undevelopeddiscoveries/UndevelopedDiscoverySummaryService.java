package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class UndevelopedDiscoverySummaryService {

  private final List<SummarySectionService<UndevelopedDiscovery>> summarySectionServices;

  @Autowired
  public UndevelopedDiscoverySummaryService(List<SummarySectionService<UndevelopedDiscovery>> summarySectionServices) {
    this.summarySectionServices = summarySectionServices;
  }

  public List<SummarySection> getSummarySections(UndevelopedDiscovery undevelopedDiscovery) {
    return summarySectionServices.stream()
        .map(service -> service.getSummarySection(undevelopedDiscovery))
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(SummarySection::displayOrder))
        .toList();
  }
}
