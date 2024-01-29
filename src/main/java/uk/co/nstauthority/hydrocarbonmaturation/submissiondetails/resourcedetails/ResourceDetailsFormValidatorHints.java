package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails;

import jakarta.annotation.Nullable;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

public record ResourceDetailsFormValidatorHints(ValidationType validationType,
                                                boolean isRegulator,
                                                ResourceType resourceType,
                                                @Nullable ResourceDetail latestApprovedResourceDetail) {
}

