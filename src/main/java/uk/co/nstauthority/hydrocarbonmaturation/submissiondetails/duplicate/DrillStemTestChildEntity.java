package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTest;

public interface DrillStemTestChildEntity {

  void setId(UUID id);

  void setDrillStemTest(DrillStemTest drillStemTest);
}
