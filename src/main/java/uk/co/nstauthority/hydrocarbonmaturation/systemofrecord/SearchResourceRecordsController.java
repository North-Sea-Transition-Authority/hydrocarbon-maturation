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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
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
@RequestMapping("/search-resource-records")
@AccessibleByServiceUsers
@SessionAttributes({"searchSystemOfRecordFilter"})
public class SearchResourceRecordsController {

  public static final String PAGE_TITLE = "Search resource records";
  private final SearchResourceRecordsService searchResourceRecordsService;
  private final ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory;
  private final RegulatorTeamService regulatorTeamService;
  private final UserDetailService userDetailService;
  private final AreaTeamService areaTeamService;

  @Autowired
  public SearchResourceRecordsController(SearchResourceRecordsService searchResourceRecordsService,
                                         ResourceRecordQueryServiceFactory resourceRecordQueryServiceFactory,
                                         RegulatorTeamService regulatorTeamService,
                                         UserDetailService userDetailService, AreaTeamService areaTeamService) {
    this.searchResourceRecordsService = searchResourceRecordsService;
    this.resourceRecordQueryServiceFactory = resourceRecordQueryServiceFactory;
    this.regulatorTeamService = regulatorTeamService;
    this.userDetailService = userDetailService;
    this.areaTeamService = areaTeamService;
  }

  @GetMapping
  public ModelAndView showSearchResourceRecordsScreen(
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

  @GetMapping("/clear-filters")
  public ModelAndView clearSearchResourceRecordFilter(
      @ModelAttribute("searchSystemOfRecordFilter") SearchResourceRecordsFilter filter,
      SessionStatus sessionStatus) {
    sessionStatus.setComplete();
    filter.clearFilter();
    return ReverseRouter.redirect(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen(null));
  }

  @PostMapping
  public ModelAndView searchResourceRecords(
      @ModelAttribute("searchSystemOfRecordFilter") SearchResourceRecordsFilter filter,
      @ModelAttribute("form") SearchResourceRecordsForm form) {
    filter.update(form);
    return ReverseRouter.redirect(on(SearchResourceRecordsController.class)
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
    return new ModelAndView("remi/systemofrecord/searchResourceRecords")
        .addObject("form", form)
        .addObject("isRegulatorOrAreaTeamUser", isRegulatorOrAreaTeamUser)
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("filtersAdded", filtersAdded.isHasFilters())
        .addObject("clearFilterUrl", ReverseRouter.route(on(SearchResourceRecordsController.class)
            .clearSearchResourceRecordFilter(null, null)))
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
            SearchSelectorService.route(on(ReferenceBlockRestController.class).searchQuadrantBlocks(null)))
        .addObject("opportunityTypes", searchResourceRecordsService.getOpportunityTypes())
        .addObject("resourceTypes", searchResourceRecordsService.getResourceTypes())
        .addObject("maxResultsReturned", searchResourceRecordsService.maxResultsReturned(resourceRecordSearchResults))
        .addObject("geographicalArea", DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class));
  }

  @ModelAttribute("searchSystemOfRecordFilter")
  private SearchResourceRecordsFilter getDefaultFilter() {
    var filter = new SearchResourceRecordsFilter();
    filter.clearFilter();
    return filter;
  }

  private boolean isRegulatorOrAreaTeamUser() {
    var userDetail = userDetailService.getUserDetail();
    return regulatorTeamService.isRegulatorUser(userDetail) || areaTeamService.isAreaUser(userDetail);
  }
}