package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Controller
@RequestMapping("/search-resource-records/fields")
@AccessibleByServiceUsers
public class SearchFieldRecordsController {
  public static final String PAGE_TITLE = "Search field records";
  private final SearchResourceRecordsService searchResourceRecordsService;
  private final ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;
  private final UserDetailService userDetailService;
  private final RegulatorTeamService regulatorTeamService;
  private final AreaTeamService areaTeamService;

  public SearchFieldRecordsController(SearchResourceRecordsService searchResourceRecordsService,
                                      ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory,
                                      UserDetailService userDetailService,
                                      RegulatorTeamService regulatorTeamService,
                                      AreaTeamService areaTeamService) {
    this.searchResourceRecordsService = searchResourceRecordsService;
    this.resourceRecordQueryServiceFactory = resourceRecordQueryServiceFactory;
    this.userDetailService = userDetailService;
    this.regulatorTeamService = regulatorTeamService;
    this.areaTeamService = areaTeamService;
  }

  @GetMapping
  public ModelAndView showSearchFieldRecordsScreen(
      @ModelAttribute("searchSystemOfRecordFilter") SearchResourceRecordsFilter filter) {
    var form = SearchResourceRecordsForm.from(filter);

    if (filter.isShowResults()) {
      return getModelAndView(
          form,
          searchResourceRecordsService.getResourceRecordSearchResults(
              form,
              resourceRecordQueryServiceFactory.getQueryServiceStrategy()),
          FiltersAdded.YES,
          isRegulatorOrAreaTeamUser()
      );
    }

    return getModelAndViewWithNoFiltersAdded(form);
  }

  private ModelAndView getModelAndViewWithNoFiltersAdded(SearchResourceRecordsForm form) {
    var isRegulatorOrAreaTeamUser = isRegulatorOrAreaTeamUser();
    if (isRegulatorOrAreaTeamUser) {
      form.setShowOnlyPreferredViews(true);
    }
    return getModelAndView(form, Collections.emptyList(), FiltersAdded.NO, isRegulatorOrAreaTeamUser);
  }

  @PostMapping
  public ModelAndView searchResourceRecords(
      @ModelAttribute("searchSystemOfRecordFilter") SearchResourceRecordsFilter filter,
      @ModelAttribute("form") SearchResourceRecordsForm form) {
    filter.update(form);
    return ReverseRouter.redirect(on(SearchFieldRecordsController.class)
        .showSearchFieldRecordsScreen(null));
  }

  private ModelAndView getModelAndView(SearchResourceRecordsForm form,
                                       List<ResourceRecordSearchResultItem> resourceRecordSearchResults,
                                       FiltersAdded filtersAdded,
                                       boolean isRegulatorOrAreaTeamUser) {
    return new ModelAndView("remi/systemofrecord/searchFieldRecords")
        .addObject("form", form)
        .addObject("isRegulatorOrAreaTeamUser", isRegulatorOrAreaTeamUser)
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("filtersAdded", filtersAdded.isHasFilters())
        .addObject("clearFilterUrl", ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
            .clearSearchResourceRecordFilter(null, null)))
        .addObject("resourceRecordSearchResultItems", resourceRecordSearchResults)
        .addObject("resourceApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
            .searchResources(null, null)));
  }

  private boolean isRegulatorOrAreaTeamUser() {
    var userDetail = userDetailService.getUserDetail();
    return regulatorTeamService.isRegulatorUser(userDetail) || areaTeamService.isAreaUser(userDetail);
  }
}
