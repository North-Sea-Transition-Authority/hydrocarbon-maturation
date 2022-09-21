package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public
class GeneralDetailsPersistenceService {

  private final GeneralDetailsRepository generalDetailsRepository;

  @Autowired
  public GeneralDetailsPersistenceService(GeneralDetailsRepository generalDetailsRepository) {
    this.generalDetailsRepository = generalDetailsRepository;
  }

  @Transactional
  public void saveGeneralDetails(GeneralDetailsForm form, SubmissionDetail submissionDetail) {
    var generalDetail = getGeneralDetail(submissionDetail).orElse(new GeneralDetail());

    generalDetail.setSubmissionDetail(submissionDetail);
    generalDetail.setProspectName(form.getProspectName());
    generalDetail.setOnshoreOffshore(form.getOnshoreOffshore());
    generalDetail.setProspectLocatedInUkcs(form.getProspectLocatedInUkcs());
    generalDetail.setInformationSource(form.getInformationSource());

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
  }

  public Optional<GeneralDetail> getGeneralDetail(SubmissionDetail submissionDetail) {
    return generalDetailsRepository.findBySubmissionDetail(submissionDetail);
  }

  public GeneralDetail getGeneralDetailOrThrow(SubmissionDetail submissionDetail) {
    return getGeneralDetail(submissionDetail)
        .orElseThrow(() -> new IllegalStateException(
            String.format("Could not find general detail for submission detail %s", submissionDetail.getId())));
  }
}
