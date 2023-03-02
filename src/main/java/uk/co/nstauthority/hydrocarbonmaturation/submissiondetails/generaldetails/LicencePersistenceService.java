package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;

@Service
public class LicencePersistenceService {
  private final LicenceRepository licenceRepository;
  private final LicenceQueryService licenceQueryService;

  @Autowired
  LicencePersistenceService(LicenceRepository licenceRepository, LicenceQueryService licenceQueryService) {
    this.licenceRepository = licenceRepository;
    this.licenceQueryService = licenceQueryService;
  }

  @Transactional
  public void saveLicences(GeneralDetailsForm form, GeneralDetail generalDetail) {

    var licences = licenceQueryService
        .getLicencesByIds(form.getLicences())
        .stream()
        .map(view -> createLicence(generalDetail, view.licenceRef(), view.licenceId()))
        .toList();

    licenceRepository.deleteAllByGeneralDetail(generalDetail);
    licenceRepository.saveAll(licences);
  }

  public List<Licence> getLicencesByGeneralDetail(GeneralDetail generalDetail) {
    return licenceRepository.findAllByGeneralDetail(generalDetail);
  }

  public boolean hasAnExtantLicence(GeneralDetail generalDetail) {
    return !licenceQueryService.getExtantLicencesByIds(
        licenceRepository.findAllByGeneralDetail(generalDetail)
            .stream()
            .map(licence -> licence.getLicenceId().toString())
            .toList())
        .isEmpty();
  }

  private Licence createLicence(GeneralDetail generalDetail, String licenceRef, Integer licenceId) {
    var licence = new Licence();
    licence.setGeneralDetail(generalDetail);
    licence.setLicenceId(licenceId);
    licence.setLicenceReference(licenceRef);
    return licence;
  }
}
