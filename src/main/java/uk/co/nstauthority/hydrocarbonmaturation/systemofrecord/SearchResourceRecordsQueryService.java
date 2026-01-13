package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchItem;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation.RegulatorReportingInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation.RegulatorReportingInformationRepository;

@Service
public class SearchResourceRecordsQueryService {
  private final SearchResourceRecordsService searchResourceRecordsService;
  private final ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;
  private final RegulatorReportingInformationRepository regulatorReportingInformationRepository;

  @Autowired
  public SearchResourceRecordsQueryService(SearchResourceRecordsService searchResourceRecordsService,
                                           ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory,
                                           RegulatorReportingInformationRepository regulatorReportingInformationRepository) {
    this.searchResourceRecordsService = searchResourceRecordsService;
    this.resourceRecordQueryServiceFactory = resourceRecordQueryServiceFactory;
    this.regulatorReportingInformationRepository = regulatorReportingInformationRepository;
  }

  public RestSearchResult searchFields(String searchTerm, User user) {
    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.FIELD.getEnumName()));
    form.setFieldNameForResourceSubmission(searchTerm);

    var results =  searchResourceRecordsService.getResourceRecordSearchResultsDistinctResourceNames(
        form, resourceRecordQueryServiceFactory.getQueryServiceStrategy(user), user);

    List<RestSearchItem> restSearchItems = results.stream()
        .sorted()
        .map(item -> new RestSearchItem(item, item))
        .toList();

    return new RestSearchResult(restSearchItems);
  }

  public RestSearchResult searchProspectsAndDiscoveries(String searchTerm, User user) {
    var form = new SearchResourceRecordsForm();
    form.setResourceTypes(List.of(ResourceType.PROSPECT.getEnumName(), ResourceType.DISCOVERY.getEnumName()));
    form.setResourceNameFromResourceDetail(searchTerm);

    var results =  searchResourceRecordsService.getResourceRecordSearchResultsDistinctResourceNames(
        form, resourceRecordQueryServiceFactory.getQueryServiceStrategy(user), user);

    List<RestSearchItem> restSearchItems = results.stream()
        .sorted()
        .map(item -> new RestSearchItem(item, item))
        .toList();

    return new RestSearchResult(restSearchItems);
  }

  public RestSearchResult searchRegulatorResourceNames(String searchTerm) {
    var regulatorResourceNames = regulatorReportingInformationRepository.findAllByResourceNameContainingIgnoreCase(searchTerm)
        .stream()
        .map(RegulatorReportingInformation::getResourceName)
        .distinct()
        .toList();

    List<RestSearchItem> restSearchItems = regulatorResourceNames.stream()
        .sorted()
        .map(item -> new RestSearchItem(item, item))
        .toList();

    return new RestSearchResult(restSearchItems);
  }
}
