package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;

@Controller
@RequestMapping("/search-resource-records/fields")
@AccessibleByServiceUsers
@SessionAttributes({"searchFieldRecordsFilter"})
public class SearchFieldRecordsController {
  public static final String PAGE_TITLE = "Search field records";
  private final SearchResourceRecordsService searchResourceRecordsService;
  private final ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;

  public SearchFieldRecordsController(SearchResourceRecordsService searchResourceRecordsService,
                                      ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory) {
    this.searchResourceRecordsService = searchResourceRecordsService;
    this.resourceRecordQueryServiceFactory = resourceRecordQueryServiceFactory;
  }

  @GetMapping
  public ModelAndView showSearchFieldRecordsScreen(
      @ModelAttribute("searchFieldRecordsFilter") SearchFieldRecordsFilter filter) {
    var form = SearchResourceRecordsForm.from(filter);

    if (filter.isShowResults()) {
      // get the actual results from the search
      var fieldResultRecords = searchResourceRecordsService.getResourceRecordSearchResults(
          form, resourceRecordQueryServiceFactory.getQueryServiceStrategy());

      return getModelAndView(form, fieldResultRecords, FiltersAdded.YES);
    }

    return getModelAndViewWithNoFiltersAdded(form);
  }

  @GetMapping("/clear-filters")
  public ModelAndView clearSearchResourceRecordFilter(
      @ModelAttribute("searchFieldRecordsFilter") SearchFieldRecordsFilter filter,
      SessionStatus sessionStatus) {
    sessionStatus.setComplete();
    filter.clearFilter();
    return ReverseRouter.redirect(on(SearchFieldRecordsController.class).showSearchFieldRecordsScreen(null));
  }

  private ModelAndView getModelAndViewWithNoFiltersAdded(SearchResourceRecordsForm form) {
    return getModelAndView(form, Collections.emptyList(), FiltersAdded.NO);
  }

  @PostMapping
  public ModelAndView searchResourceRecords(
      @ModelAttribute("searchFieldRecordsFilter") SearchFieldRecordsFilter filter,
      @ModelAttribute("form") SearchResourceRecordsForm form) {
    filter.update(form);
    return ReverseRouter.redirect(on(SearchFieldRecordsController.class)
        .showSearchFieldRecordsScreen(null));
  }

  private ModelAndView getModelAndView(SearchResourceRecordsForm form,
                                       List<ResourceRecordSearchResultItem> resourceRecordSearchResults,
                                       FiltersAdded filtersAdded) {
    return new ModelAndView("remi/systemofrecord/searchFieldRecords")
        .addObject("form", form)
        .addObject("backLinkUrl", ReverseRouter.route(on(SystemOfRecordLandingPageController.class).getLandingPage()))
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("filtersAdded", filtersAdded.isHasFilters())
        .addObject("clearFilterUrl", ReverseRouter.route(on(SearchFieldRecordsController.class)
            .clearSearchResourceRecordFilter(null, null)))
        .addObject("resourceRecordSearchResultItems", resourceRecordSearchResults)
        .addObject("maxResultsReturned", searchResourceRecordsService.maxResultsReturned(resourceRecordSearchResults))
        .addObject("fieldApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
            .searchFieldsByFieldName(null)))
        .addObject("fieldStatusOptions", DisplayableEnumOptionUtil.getDisplayableOptions(FieldStatus.class))
        .addObject("resourceName", searchResourceRecordsService.getPreSelectedFieldName(form))
        .addObject("geographicalArea", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class))
        .addObject("parentFieldName", searchResourceRecordsService.getPreSelectedParentFieldName(form));
  }

  @ModelAttribute("searchFieldRecordsFilter")
  private SearchFieldRecordsFilter getDefaultFilter() {
    return SearchFieldRecordsFilter.createInstance();
  }
}
