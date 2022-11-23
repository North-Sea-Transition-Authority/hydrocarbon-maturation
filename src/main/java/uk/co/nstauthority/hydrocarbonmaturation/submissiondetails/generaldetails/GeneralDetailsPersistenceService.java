package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class GeneralDetailsPersistenceService {

  private final GeneralDetailsRepository generalDetailsRepository;
  private final LicenceApplicationQueryService licenceApplicationQueryService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;

  @Autowired
  public GeneralDetailsPersistenceService(GeneralDetailsRepository generalDetailsRepository,
                                          QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                          LicenceApplicationQueryService licenceApplicationQueryService,
                                          OrganisationUnitQueryService organisationUnitQueryService) {
    this.generalDetailsRepository = generalDetailsRepository;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.licenceApplicationQueryService = licenceApplicationQueryService;
    this.organisationUnitQueryService = organisationUnitQueryService;
  }

  @Transactional
  public void saveGeneralDetails(GeneralDetailsForm form, SubmissionDetail submissionDetail) {
    var generalDetail = getGeneralDetailBySubmissionDetail(submissionDetail).orElse(new GeneralDetail());

    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setProspectName(form.getProspectName());
    generalDetail.setOnshoreOffshore(form.getOnshoreOffshore());
    generalDetail.setProspectLocatedInUkcs(form.getProspectLocatedInUkcs());
    generalDetail.setInformationSource(form.getInformationSource());

    var licenceApplication = form.getLicenceApplication();
    if (Objects.isNull(licenceApplication) || form.getInformationSource() != InformationSource.LICENCE_APPLICATION) {
      generalDetail.setLicenceApplicationReference(null);
      generalDetail.setLicenceApplicationId(null);
    } else {
      if (SearchSelectorService.isManualEntry(licenceApplication)) {
        generalDetail.setLicenceApplicationReference(SearchSelectorService.removePrefix(licenceApplication));
        generalDetail.setLicenceApplicationId(null);
      } else {
        var licenceApplicationId = Integer.parseInt(licenceApplication);
        generalDetail.setLicenceApplicationId(licenceApplicationId);
        var licenceApplicationRef = licenceApplicationQueryService.getLicenceApplicationRef(licenceApplicationId);
        generalDetail.setLicenceApplicationReference(licenceApplicationRef);
      }
    }

    var organisationId = form.getResponsibleOrganisationId();
    if (Objects.isNull(organisationId)) {
      generalDetail.setOrganisationId(null);
      generalDetail.setOrganisationName(null);
      generalDetail.setOrganisationRegisteredNumber(null);
    } else {
      var organisationUnit = organisationUnitQueryService.findOrganisationUnit(Integer.valueOf(organisationId));
      organisationUnit.ifPresent(unit -> {
        generalDetail.setOrganisationName(unit.getName());
        generalDetail.setOrganisationId(unit.getOrganisationUnitId());
        generalDetail.setOrganisationRegisteredNumber(unit.getRegisteredNumber());
      });
    }

    if (form.getProspectLocatedInUkcs() !=  null && !form.getProspectLocatedInUkcs()) {
      var decimalPercentage = form.getPercentageLocatedInUkcs().createBigDecimalOrNull();
      generalDetail.setPercentageLocatedInUkcs(decimalPercentage);
    } else {
      generalDetail.setPercentageLocatedInUkcs(null);
    }

    if (form.getInformationSource() == InformationSource.OTHER) {
      generalDetail.setOtherInformationSource(form.getOtherSourceOfInformation());
    } else {
      generalDetail.setOtherInformationSource(null);
    }

    generalDetailsRepository.save(generalDetail);
    quadrantAndBlockPersistenceService.saveQuadrantAndBlocks(form, generalDetail);
  }

  @Transactional
  public void updateGeneralDetailProspect(UpdateProspectForm form,
                                          GeneralDetail newGeneralDetail) {
    newGeneralDetail.setInformationSource(form.getInformationSource());

    if (form.getInformationSource() == InformationSource.OTHER) {
      newGeneralDetail.setOtherInformationSource(form.getOtherSourceOfInformation());
    } else {
      newGeneralDetail.setOtherInformationSource(null);
    }

    generalDetailsRepository.save(newGeneralDetail);
  }

  public Optional<GeneralDetail> getGeneralDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailsRepository.findBySubmissionDetail(submissionDetail);
  }

  public GeneralDetail getGeneralDetailOrThrow(SubmissionDetail submissionDetail) {
    return getGeneralDetailBySubmissionDetail(submissionDetail)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            String.format("Could not find general detail for submission detail %s", submissionDetail.getId())));
  }

  public Optional<GeneralDetail> getGeneralDetailById(UUID generalDetailId) {
    return generalDetailsRepository.findById(generalDetailId);
  }

  public GeneralDetail getGeneralDetailByIdOrThrow(UUID generalDetailId) {
    return generalDetailsRepository.findById(generalDetailId)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            String.format("Could not find general detail by ID %s", generalDetailId)));
  }

  public List<GeneralDetail> getGeneralDetailsByName(String name) {
    return generalDetailsRepository.findByProspectNameContainingIgnoreCase(name);
  }
}
