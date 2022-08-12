package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class GeneralDetailsPersistenceService {

  private final GeneralDetailsRepository generalDetailsRepository;

  @Autowired
  public GeneralDetailsPersistenceService(GeneralDetailsRepository generalDetailsRepository) {
    this.generalDetailsRepository = generalDetailsRepository;
  }

  @Transactional
  public void saveGeneralDetails(GeneralDetailsForm form, SubmissionDetail submissionDetail) {
    var generalDetail = getGeneralDetail(submissionDetail).orElse(new GeneralDetail());

    generalDetail.setSubmissionDetail(submissionDetail)
        .setProspectName(form.getProspectName())
        .setOnshoreOffshore(form.getOnshoreOffshore())
        .setProspectLocatedInUkcs(form.getProspectLocatedInUkcs())
        .setInformationSource(form.getInformationSource())
        .setOtherInformationSource(form.getOtherSourceOfInformation());

    if (form.getProspectLocatedInUkcs() !=  null && !form.getProspectLocatedInUkcs()) {
      var decimalPercentage = form.getPercentageLocatedInUkcs().getDecimalInputValue();
      generalDetail.setPercentageLocatedInUkcs(new BigDecimal(decimalPercentage));
    }

    generalDetailsRepository.save(generalDetail);
  }

  Optional<GeneralDetail> getGeneralDetail(SubmissionDetail submissionDetail) {
    return generalDetailsRepository.findBySubmissionDetail(submissionDetail);
  }
}
