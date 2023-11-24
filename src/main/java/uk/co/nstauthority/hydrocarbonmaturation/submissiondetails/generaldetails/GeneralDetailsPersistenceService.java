package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceApplicationQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class GeneralDetailsPersistenceService {

  private final GeneralDetailsRepository generalDetailsRepository;
  private final LicenceApplicationQueryService licenceApplicationQueryService;
  private final QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  private final LicencePersistenceService licencePersistenceService;

  @Autowired
  public GeneralDetailsPersistenceService(GeneralDetailsRepository generalDetailsRepository,
                                          QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService,
                                          LicenceApplicationQueryService licenceApplicationQueryService,
                                          LicencePersistenceService licencePersistenceService) {
    this.generalDetailsRepository = generalDetailsRepository;
    this.quadrantAndBlockPersistenceService = quadrantAndBlockPersistenceService;
    this.licenceApplicationQueryService = licenceApplicationQueryService;
    this.licencePersistenceService = licencePersistenceService;
  }

  @Transactional
  public void saveGeneralDetails(GeneralDetailsForm form, SubmissionDetail submissionDetail, OnshoreOffshore onshoreOffshore) {
    var generalDetail = getGeneralDetailBySubmissionDetail(submissionDetail).orElse(new GeneralDetail());

    generalDetail.setSubmissionDetail(submissionDetail);

    setResourceLocated(generalDetail, form, onshoreOffshore);

    if (!submissionDetail.isRelinquishment()) {
      generalDetail.setInformationSource(form.getInformationSource());
    } else {
      generalDetail.setInformationSource(InformationSource.RELINQUISHMENT);
    }

    var licenceApplication = form.getLicenceApplication();
    setLicenceApplication(generalDetail, licenceApplication, form.getInformationSource());

    if (form.getInformationSource() == InformationSource.OTHER) {
      generalDetail.setOtherInformationSource(form.getOtherSourceOfInformation());
    } else {
      generalDetail.setOtherInformationSource(null);
    }

    if (submissionDetail.isRelinquishment()
        && !StringUtils.isAnyBlank(
            form.getRelinquishmentMonth(), form.getRelinquishmentDay(), form.getRelinquishmentYear())) {
      generalDetail.setRelinquishmentDate(
          LocalDate.of(
              Integer.parseInt(form.getRelinquishmentYear()),
              Integer.parseInt(form.getRelinquishmentMonth()),
              Integer.parseInt(form.getRelinquishmentDay()))
      );
    }

    if (submissionDetail.isCessationOfProductionSubmission()
        && !StringUtils.isAnyBlank(
            form.getFieldStoppedProducingMonth(),
            form.getFieldStoppedProducingDay(),
            form.getFieldStoppedProducingYear())) {

      generalDetail.setFieldStoppedProducingDate(
          LocalDate.of(
              Integer.parseInt(form.getFieldStoppedProducingYear()),
              Integer.parseInt(form.getFieldStoppedProducingMonth()),
              Integer.parseInt(form.getFieldStoppedProducingDay()))
      );
    }

    if (submissionDetail.isFieldDevelopmentPlanSubmission() || submissionDetail.isFieldDevelopmentPlanAddendumSubmission()) {
      generalDetail.setCapitalProjectScopeDelivered(form.getCapitalProjectScopeDelivered());
    }

    if (submissionDetail.isFieldSubmission()) {
      generalDetail.setCurrentDriveMechanism(form.getCurrentDriveMechanism());
      if (DriveMechanism.OTHER.equals(form.getCurrentDriveMechanism())) {
        generalDetail.setOtherDriveMechanism(form.getOtherDriveMechanism());
      } else {
        generalDetail.setOtherDriveMechanism(null);
      }
    }

    generalDetailsRepository.save(generalDetail);
    licencePersistenceService.saveLicences(form, generalDetail);
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

    var licenceApplication = form.getLicenceApplication();
    setLicenceApplication(newGeneralDetail, licenceApplication, form.getInformationSource());

    generalDetailsRepository.save(newGeneralDetail);
  }

  @Transactional
  public void setRelinquishmentInformationSource(SubmissionDetail submissionDetail) {
    // Set the information source to Relinquishment
    var generalDetail = getGeneralDetailBySubmissionDetail(submissionDetail)
        .orElse(new GeneralDetail());
    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setInformationSource(InformationSource.RELINQUISHMENT);
    generalDetail.setOtherInformationSource(null);
    generalDetailsRepository.save(generalDetail);
  }

  public Optional<GeneralDetail> getGeneralDetailBySubmissionDetail(SubmissionDetail submissionDetail) {
    return generalDetailsRepository.findBySubmissionDetail(submissionDetail);
  }

  public GeneralDetail getGeneralDetailOrThrow(SubmissionDetail submissionDetail) {
    return getGeneralDetailBySubmissionDetail(submissionDetail)
        .orElseThrow(() -> new RemiEntityNotFoundException(
            String.format("Could not find general detail for submission detail %s", submissionDetail.getId())));
  }

  public boolean isOnAnExtantLicence(GeneralDetail generalDetail) {
    return licencePersistenceService.hasAnExtantLicence(generalDetail);
  }

  private void setLicenceApplication(GeneralDetail generalDetail,
                                     String licenceApplication,
                                     InformationSource informationSource) {
    if (Objects.isNull(licenceApplication) || informationSource != InformationSource.LICENCE_APPLICATION) {
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
  }

  private void setResourceLocated(GeneralDetail generalDetail, GeneralDetailsForm form, OnshoreOffshore onshoreOffshore) {
    generalDetail.setResourceLocatedInUkcs(null);
    generalDetail.setPercentageLocatedInUkcs(null);
    generalDetail.setResourceLocatedInEngland(null);
    generalDetail.setPercentageLocatedInEngland(null);

    if (onshoreOffshore == OnshoreOffshore.OFFSHORE) {
      generalDetail.setResourceLocatedInUkcs(form.getResourceLocatedInUkcs());

      if (BooleanUtils.isFalse(form.getResourceLocatedInUkcs())) {
        var decimalPercentage = form.getPercentageLocatedInUkcs().createBigDecimalOrNull();
        generalDetail.setPercentageLocatedInUkcs(decimalPercentage);
      }
    } else if (onshoreOffshore == OnshoreOffshore.ONSHORE) {
      generalDetail.setResourceLocatedInEngland(form.getResourceLocatedInEngland());

      if (BooleanUtils.isFalse(form.getResourceLocatedInEngland())) {
        var decimalPercentage = form.getPercentageLocatedInEngland().createBigDecimalOrNull();
        generalDetail.setPercentageLocatedInEngland(decimalPercentage);
      }
    }
  }
}
