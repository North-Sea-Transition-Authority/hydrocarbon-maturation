package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInput;

@Service
public class GeneralDetailsService {

  private final GeneralDetailsFormValidator generalDetailsFormValidator;
  private final GeneralDetailsRepository generalDetailsRepository;

  @Autowired
  public GeneralDetailsService(GeneralDetailsFormValidator generalDetailsFormValidator,
                               GeneralDetailsRepository generalDetailsRepository) {
    this.generalDetailsFormValidator = generalDetailsFormValidator;
    this.generalDetailsRepository = generalDetailsRepository;
  }

  BindingResult validate(GeneralDetailsForm form, BindingResult bindingResult) {
    generalDetailsFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  @Transactional
  public void saveGeneralDetails(GeneralDetailsForm form, SubmissionDetail submissionDetail) {
    var generalDetail = new GeneralDetail()
        .setSubmissionDetail(submissionDetail)
        .setProspectName(form.getProspectName())
        .setOnshoreOffshore(form.getOnshoreOffshore())
        .setProspectLocatedInUkcs(form.getProspectLocatedInUkcs())
        .setPercentageLocatedInUkcs(convertToDouble(form.getPercentageLocatedInUkcs()))
        .setInformationSource(form.getInformationSource())
        .setOtherInformationSource(form.getOtherSourceOfInformation());
    generalDetailsRepository.save(generalDetail);
  }

  private Double convertToDouble(DecimalInput value) {
    if (value != null && !value.getDecimalInputValue().isEmpty()) {
      return Double.parseDouble(value.getDecimalInputValue());
    }
    return null;
  }
}
