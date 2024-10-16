package uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.RestSearchResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@RestController
@RequestMapping("/api")
@AccessibleByServiceUsers
public class OrganisationUnitRestController {

  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final SearchSelectorService searchSelectorService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public OrganisationUnitRestController(OrganisationUnitQueryService organisationUnitQueryService,
                                        SearchSelectorService searchSelectorService,
                                        SubmissionDetailService submissionDetailService) {
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.searchSelectorService = searchSelectorService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping("/organisation-units")
  public RestSearchResult searchOrganisationUnits(@RequestParam(value = "term") String term, User user) {
    return searchSelectorService.search(
        term,
        organisationUnitQueryService.getOrganisationSearchableResults(term, user)
    );
  }

  @GetMapping("/organisation-units/{submissionDetailId}")
  public RestSearchResult searchOrganisationUnitsWithSubmissionDetail(
      @RequestParam(value = "term") String term,
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return searchSelectorService.search(
        term,
        organisationUnitQueryService.getOrganisationSearchableResults(term, user, submissionDetail)
    );
  }
}
