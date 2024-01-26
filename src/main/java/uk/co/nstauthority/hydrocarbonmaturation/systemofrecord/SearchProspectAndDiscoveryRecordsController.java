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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.area.AreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Controller
@RequestMapping("/search-resource-records/prospects-and-discoveries")
@AccessibleByServiceUsers
@SessionAttributes({"searchProspectAndDiscoveryRecordsFilter"})
public class SearchProspectAndDiscoveryRecordsController {

  public static final String PAGE_TITLE = "Search prospect and discovery records";
  private final SearchResourceRecordsService searchResourceRecordsService;
  private final ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;
  private final RegulatorTeamService regulatorTeamService;
  private final UserService userService;
  private final AreaTeamService areaTeamService;

  @Autowired
  public SearchProspectAndDiscoveryRecordsController(SearchResourceRecordsService searchResourceRecordsService,
                                                     ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory,
                                                     RegulatorTeamService regulatorTeamService,
                                                     UserService userService, AreaTeamService areaTeamService) {
    this.searchResourceRecordsService = searchResourceRecordsService;
    this.resourceRecordQueryServiceFactory = resourceRecordQueryServiceFactory;
    this.regulatorTeamService = regulatorTeamService;
    this.userService = userService;
    this.areaTeamService = areaTeamService;
  }

  @GetMapping
  public ModelAndView showSearchResourceRecordsScreen(
      @ModelAttribute("searchProspectAndDiscoveryRecordsFilter") SearchProspectAndDiscoveryRecordsFilter filter) {
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

  @GetMapping("/clear-filters")
  public ModelAndView clearSearchResourceRecordFilter(
      @ModelAttribute("searchProspectAndDiscoveryRecordsFilter") SearchProspectAndDiscoveryRecordsFilter filter,
      SessionStatus sessionStatus) {
    sessionStatus.setComplete();
    filter.clearFilter();
    return ReverseRouter.redirect(on(SearchProspectAndDiscoveryRecordsController.class).showSearchResourceRecordsScreen(null));
  }

  @PostMapping
  public ModelAndView searchResourceRecords(
      @ModelAttribute("searchProspectAndDiscoveryRecordsFilter") SearchProspectAndDiscoveryRecordsFilter filter,
      @ModelAttribute("form") SearchResourceRecordsForm form) {
    filter.update(form);
    return ReverseRouter.redirect(on(SearchProspectAndDiscoveryRecordsController.class)
        .showSearchResourceRecordsScreen(null));
  }

  private ModelAndView getModelAndViewWithNoFiltersAdded(SearchResourceRecordsForm form) {
    var isRegulatorOrAreaTeamUser = isRegulatorOrAreaTeamUser();
    if (isRegulatorOrAreaTeamUser) {
      form.setShowOnlyPreferredViews(true);
    }
    return getModelAndView(form, Collections.emptyList(), FiltersAdded.NO, isRegulatorOrAreaTeamUser);
  }

  private ModelAndView getModelAndView(SearchResourceRecordsForm form,
                                       List<ResourceRecordSearchResultItem> resourceRecordSearchResults,
                                       FiltersAdded filtersAdded,
                                       boolean isRegulatorOrAreaTeamUser) {
    return new ModelAndView("remi/systemofrecord/searchProspectAndDiscoveryRecords")
        .addObject("form", form)
        .addObject("backLinkUrl", ReverseRouter.route(on(SystemOfRecordLandingPageController.class).getLandingPage()))
        .addObject("isRegulatorOrAreaTeamUser", isRegulatorOrAreaTeamUser)
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("filtersAdded", filtersAdded.isHasFilters())
        .addObject("clearFilterUrl", ReverseRouter.route(on(SearchProspectAndDiscoveryRecordsController.class)
            .clearSearchResourceRecordFilter(null, null)))
        .addObject("resourceRecordSearchResultItems", resourceRecordSearchResults)
        .addObject("resourceApiEndpoint", SearchSelectorService.route(on(ResourceRestController.class)
            .searchProspectsAndDiscoveries(null)))
        .addObject("licenceEndpoint",
            SearchSelectorService.route(on(LicenceRestController.class).searchLiveLicences(null)))
        .addObject("preselectedLicence", searchResourceRecordsService.getPreselectedLicence(form))
        .addObject("resourceName", searchResourceRecordsService.getPreSelectedResourceName(form))
        .addObject("organisationUnitEndpoint", SearchSelectorService.route(on(OrganisationUnitRestController.class)
            .searchOrganisationUnits(null)))
        .addObject("responsibleOrganisation", searchResourceRecordsService.getPreSelectedOrganisationUnit(form))
        .addObject("centreOfInterestBlock", searchResourceRecordsService.getPreSelectedQuadAndBlock(form))
        .addObject("quadrantBlocksApiEndpoint",
            SearchSelectorService.route(on(ReferenceBlockRestController.class).searchQuadrantBlocks(null)))
        .addObject("opportunityTypes", searchResourceRecordsService.getOpportunityTypes())
        .addObject("resourceTypes", searchResourceRecordsService.getResourceTypes())
        .addObject("maxResultsReturned", searchResourceRecordsService.maxResultsReturned(resourceRecordSearchResults))
        .addObject("geographicalArea", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class));
  }

  @ModelAttribute("searchProspectAndDiscoveryRecordsFilter")
  private SearchProspectAndDiscoveryRecordsFilter getDefaultFilter() {
    return SearchProspectAndDiscoveryRecordsFilter.createInstance();
  }

  private boolean isRegulatorOrAreaTeamUser() {
    var user = userService.getUser();
    return regulatorTeamService.isRegulatorUser(user) || areaTeamService.isAreaUser(user);
  }
}