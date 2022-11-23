package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import java.util.UUID;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;

public interface GeneralDetailChildEntity {
  void setId(UUID id);

  void setGeneralDetail(GeneralDetail generalDetail);
}
