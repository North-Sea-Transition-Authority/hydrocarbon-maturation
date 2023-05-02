package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.EnumUtils;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;

@Service
class SourceAndSealPersistenceService {

  private final SourceAndSealRepository sourceAndSealRepository;

  @Autowired
  public SourceAndSealPersistenceService(SourceAndSealRepository sourceAndSealRepository) {
    this.sourceAndSealRepository = sourceAndSealRepository;
  }

  @Transactional
  public void saveSourceAndSealDetails(SourceAndSealForm form, SubmissionDetail submissionDetail) {

    var sourceAndSeal = getSourceAndSeal(submissionDetail).orElse(new SourceAndSeal());
    sourceAndSeal.setSubmissionDetail(submissionDetail);

    if (!Objects.isNull(form.getSourceFormationName())) {
      if (SearchSelectorService.isManualEntry(form.getSourceFormationName())) {
        sourceAndSeal.setSourceFormationNameManual(SearchSelectorService.removePrefix(form.getSourceFormationName()));
        sourceAndSeal.setSourceFormationName(null);
      } else {
        sourceAndSeal.setSourceFormationName(EnumUtils.getEnumValue(Formation.class, form.getSourceFormationName()));
        sourceAndSeal.setSourceFormationNameManual(null);
      }
    } else {
      sourceAndSeal.setSourceFormationName(null);
      sourceAndSeal.setSourceFormationNameManual(null);
    }

    sourceAndSeal.setSourceAgeStage(form.getSourceAgeStage());
    sourceAndSeal.setSourceGroupName(form.getSourceGroupName());
    sourceAndSeal.setSourceLithology(form.getSourceLithology());

    if (!Objects.isNull(form.getTopSealFormationName())) {
      if (SearchSelectorService.isManualEntry(form.getTopSealFormationName())) {
        sourceAndSeal.setTopSealFormationNameManual(SearchSelectorService.removePrefix(form.getTopSealFormationName()));
        sourceAndSeal.setTopSealFormationName(null);
      } else {
        sourceAndSeal.setTopSealFormationName(EnumUtils.getEnumValue(Formation.class, form.getTopSealFormationName()));
        sourceAndSeal.setTopSealFormationNameManual(null);
      }
    } else {
      sourceAndSeal.setTopSealFormationName(null);
      sourceAndSeal.setTopSealFormationNameManual(null);
    }

    sourceAndSeal.setTopSealAgeStage(form.getTopSealAgeStage());
    sourceAndSeal.setTopSealGroupName(form.getTopSealGroupName());
    sourceAndSeal.setTopSealLithology(form.getTopSealLithology());
    sourceAndSeal.setTopSealLithologyOther(
        form.getTopSealLithology() == TopSealLithology.OTHER ? form.getTopSealLithologyOther() : null
    );

    sourceAndSealRepository.save(sourceAndSeal);
  }

  Optional<SourceAndSeal> getSourceAndSeal(SubmissionDetail submissionDetail) {
    return sourceAndSealRepository.findBySubmissionDetail(submissionDetail);
  }
}
