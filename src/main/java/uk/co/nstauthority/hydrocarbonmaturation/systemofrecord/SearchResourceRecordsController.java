package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

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

@Controller
@RequestMapping("/search-resource-records")
@AccessibleByServiceUsers
public class SearchResourceRecordsController {
  public static final String PAGE_TITLE = "Search resource records";
  private final SearchResourceRecordsService searchResourceRecordsService;

  @Autowired
  public SearchResourceRecordsController(SearchResourceRecordsService searchResourceRecordsService) {
    this.searchResourceRecordsService = searchResourceRecordsService;
  }

  @GetMapping
  public ModelAndView showSearchResourceRecordsScreen() {
    return getModelAndViewWithNoFiltersAdded(new SearchResourceRecordsForm());
  }

  @PostMapping
  public ModelAndView searchResourceRecords(@ModelAttribute("form") SearchResourceRecordsForm form) {
    return getModelAndView(
        form,
        searchResourceRecordsService.getResourceRecordSearchResults(form),
        searchResourceRecordsService.getFiltersAdded(form)
    );
  }

  private ModelAndView getModelAndViewWithNoFiltersAdded(SearchResourceRecordsForm form) {
    return getModelAndView(form, Collections.emptyList(), false);
  }

  private ModelAndView getModelAndView(SearchResourceRecordsForm form,
                                       List<ResourceRecordSearchResultItem> resourceRecordSearchResults, boolean filtersAdded) {
    return new ModelAndView("remi/systemofrecord/searchResourceRecords")
        .addObject("form", form)
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("filtersAdded", filtersAdded)
        .addObject("resourceRecordSearchResultItems", resourceRecordSearchResults);
  }
}