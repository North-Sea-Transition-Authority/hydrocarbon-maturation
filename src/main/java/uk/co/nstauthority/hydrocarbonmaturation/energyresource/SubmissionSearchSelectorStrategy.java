package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.stream.Stream;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;

interface SubmissionSearchSelectorStrategy {

  Stream<ResourceDetail> getResourceDetailsForSubmissionsWithTeam(Stream<SubmissionDetail> submissionDetailStream,
                                                                  SubmissionType submissionType,
                                                                  User user);
}
