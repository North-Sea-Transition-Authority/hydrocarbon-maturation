package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;

@Controller
@RequestMapping("/search-resource-records")
@AccessibleByServiceUsers
public class SearchResourceRecordsController {

  public static final String PAGE_TITLE = "Search resource records";
  private final SearchResourceRecordsService searchResourceRecordsService;
  private final ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;

  @Autowired
  public SearchResourceRecordsController(SearchResourceRecordsService searchResourceRecordsService,
                                         ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory) {
    this.searchResourceRecordsService = searchResourceRecordsService;
    this.resourceRecordQueryServiceFactory = resourceRecordQueryServiceFactory;
  }

  @GetMapping
  public ModelAndView showSearchResourceRecordsScreen() {
    return getModelAndViewWithNoFiltersAdded(searchResourceRecordsService.getSearchResourceRecordsForm());
  }

  @PostMapping
  public ModelAndView searchResourceRecords(@ModelAttribute("form") SearchResourceRecordsForm form) {
    return getModelAndView(
        form,
        searchResourceRecordsService.getResourceRecordSearchResults(
            form,
            resourceRecordQueryServiceFactory.getQueryServiceStrategy()),
        FiltersAdded.YES
    );
  }

  private ModelAndView getModelAndViewWithNoFiltersAdded(SearchResourceRecordsForm form) {
    return getModelAndView(form, Collections.emptyList(), FiltersAdded.NO);
  }

  private ModelAndView getModelAndView(SearchResourceRecordsForm form,
                                       List<ResourceRecordSearchResultItem> resourceRecordSearchResults,
                                       FiltersAdded filtersAdded) {
    return new ModelAndView("remi/systemofrecord/searchResourceRecords")
        .addObject("form", form)
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("filtersAdded", filtersAdded.isHasFilters())
        .addObject("clearFilterUrl", ReverseRouter.route(on(SearchResourceRecordsController.class)
            .showSearchResourceRecordsScreen()))
        .addObject("resourceRecordSearchResultItems", resourceRecordSearchResults)
        .addObject("resourceApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
            .searchResources(null)))
        .addObject("licenceEndpoint",
            SearchSelectorService.route(on(LicenceRestController.class).searchExtantLicences(null)))
        .addObject("preselectedLicence", searchResourceRecordsService.getPreselectedLicence(form))
        .addObject("resourceName", searchResourceRecordsService.getPreSelectedResourceName(form))
        .addObject("organisationUnitEndpoint", SearchSelectorService.route(on(OrganisationUnitRestController.class)
            .searchOrganisationUnits(null)))
        .addObject("responsibleOrganisation", searchResourceRecordsService.getPreSelectedOrganisationUnit(form))
        .addObject("centreOfInterestBlock", searchResourceRecordsService.getPreSelectedQuadAndBlock(form))
        .addObject("quadrantBlocksApiEndpoint",
            SearchSelectorService.route(on(ReferenceBlockRestController.class)
                .searchQuadrantBlocks(null)))
        .addObject("opportunityTypes", searchResourceRecordsService.getOpportunityTypes())
        .addObject("resourceTypes", searchResourceRecordsService.getResourceTypes())
        .addObject("maxResultsReturned", searchResourceRecordsService.maxResultsReturned(resourceRecordSearchResults))
        .addObject("geographicalArea", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class));
  }
}