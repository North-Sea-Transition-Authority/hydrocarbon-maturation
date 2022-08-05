package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class SourceAndSealService {

  private final SourceAndSealFormValidator sourceAndSealFormValidator;
  private final SourceAndSealRepository sourceAndSealRepository;

  @Autowired
  public SourceAndSealService(SourceAndSealFormValidator sourceAndSealFormValidator,
                              SourceAndSealRepository sourceAndSealRepository) {
    this.sourceAndSealFormValidator = sourceAndSealFormValidator;
    this.sourceAndSealRepository = sourceAndSealRepository;
  }

  BindingResult validate(SourceAndSealForm form, BindingResult bindingResult) {
    sourceAndSealFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  @Transactional
  void saveSourceAndSealDetails(SourceAndSealForm form, SubmissionDetail submissionDetail) {
    var sourceAndSeal = new SourceAndSeal()
        .setSubmissionDetail(submissionDetail)
        .setSourceFormationName(form.getSourceFormationName())
        .setSourceAgeStage(form.getSourceAgeStage())
        .setSourceLithology(form.getSourceLithology())
        .setTopSealFormationName(form.getTopSealFormationName())
        .setTopSealAgeStage(form.getTopSealAgeStage())
        .setTopSealLithology(form.getTopSealLithology());

    sourceAndSealRepository.save(sourceAndSeal);
  }
}
