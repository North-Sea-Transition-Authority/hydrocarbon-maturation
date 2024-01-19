package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class ConsideringCcusSummaryService {

  public static final String CONSIDERING_CCUS = "Considering CCUS";

  public static final String IS_CONNECTED_TO_PROJECTS = "Is field hydrodynamically connected to a known carbon storage project?";
  public static final String CONNECTED_PROJECTS = "Connected project(s)";
  public static final String IS_SHALLOWER_HORIZONS = "Are there shallower horizons that could be used for carbon storage?";
  public static final String AGE_AND_FORMATION_OF_HORIZONS = "Age and formation of horizons";

  public static final int CONSIDERING_CCUS_DISPLAY_ORDER = 15;

  private final ConsideringCcusDetailRepository consideringCcusDetailRepository;

  @Autowired
  public ConsideringCcusSummaryService(ConsideringCcusDetailRepository consideringCcusDetailRepository) {
    this.consideringCcusDetailRepository = consideringCcusDetailRepository;
  }

  public SummaryCard getConsideringCcusSummary(SubmissionDetail submissionDetail) {
    var summaryDataView = new SummaryDataView(new ArrayList<>());

    return consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail)
        .map(
            consideringCcusDetail -> {
              summaryDataView.addKeyValue(IS_CONNECTED_TO_PROJECTS,
                  BooleanUtil.yesNoFromBoolean(consideringCcusDetail.getIsConnectedToProject()));
              if (Boolean.TRUE.equals(consideringCcusDetail.getIsConnectedToProject())) {
                summaryDataView.addKeyValue(CONNECTED_PROJECTS, consideringCcusDetail.getConnectedProjects());
              }

              summaryDataView.addKeyValue(IS_SHALLOWER_HORIZONS,
                  BooleanUtil.yesNoFromBoolean(consideringCcusDetail.getIsAnyShallowerHorizons()));
              if (Boolean.TRUE.equals(consideringCcusDetail.getIsAnyShallowerHorizons())) {
                summaryDataView.addKeyValue(AGE_AND_FORMATION_OF_HORIZONS, consideringCcusDetail.getAgeAndFormationOfHorizons());
              }

              return SummaryCard.simpleSummaryCardWithHeading(CONSIDERING_CCUS, summaryDataView);
            }
        ).orElse(SummaryCard.emptySummaryCardWithHeading(CONSIDERING_CCUS));
  }
}
