package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
public class GeneralDetailsService implements SubmissionFormService {

  private final GeneralDetailsFormValidator generalDetailsFormValidator;
  private final GeneralDetailsRepository generalDetailsRepository;

  @Autowired
  public GeneralDetailsService(GeneralDetailsFormValidator generalDetailsFormValidator,
                               GeneralDetailsRepository generalDetailsRepository) {
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.generalDetailsRepository = generalDetailsRepository;
  }

  @Override
  public BindingResult validate(Object form, BindingResult bindingResult, ValidationType validationType) {
    generalDetailsFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  @Transactional
  public void saveGeneralDetails(GeneralDetailsForm form, SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail()
        .setSubmissionDetail(submissionDetail)
        .setProspectName(form.getProspectName())
        .setOnshoreOffshore(form.getOnshoreOffshore())
        .setProspectLocatedInUkcs(form.getProspectLocatedInUkcs())
        .setInformationSource(form.getInformationSource())
        .setOtherInformationSource(form.getOtherSourceOfInformation());

    if (!form.getProspectLocatedInUkcs()) {
      var decimalPercentage = form.getPercentageLocatedInUkcs().getDecimalInputValue();
      generalDetail.setPercentageLocatedInUkcs(new BigDecimal(decimalPercentage));
    }

    generalDetailsRepository.save(generalDetail);
  }
}
