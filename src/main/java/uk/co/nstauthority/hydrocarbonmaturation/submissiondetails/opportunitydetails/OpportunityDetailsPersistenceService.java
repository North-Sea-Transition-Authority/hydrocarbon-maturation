package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;

@Service
public class OpportunityDetailsPersistenceService {

  private final OpportunityDetailsRepository opportunityDetailsRepository;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  OpportunityDetailsPersistenceService(OpportunityDetailsRepository opportunityDetailsRepository,
                                       SupportingDocumentService supportingDocumentService) {
    this.opportunityDetailsRepository = opportunityDetailsRepository;
    this.supportingDocumentService = supportingDocumentService;
  }

  @Transactional
  public void saveOpportunityDetails(OpportunityDetailsForm form, SubmissionDetail submissionDetail) {
    var opportunityDetail = getOpportunityDetail(submissionDetail).orElse(new OpportunityDetail());

    opportunityDetail.setSubmissionDetail(submissionDetail);
    if (submissionDetail.isProspectSubmission()) {
      opportunityDetail.setOpportunityType(form.getOpportunityType());
    } else if (submissionDetail.isDiscoverySubmission()) {
      opportunityDetail.setOpportunityType(OpportunityType.DISCOVERY);
    } else if (submissionDetail.isDryWellSubmission()) {
      opportunityDetail.setOpportunityType(OpportunityType.DRY_WELL);
    }
    opportunityDetail.setOpportunityComments(form.getOpportunityComments());
    opportunityDetail.setKeyEvaluationWorkCarriedOut(form.getKeyEvaluationWorkCarriedOut());
    opportunityDetail.setKeyWorkRequiredForFurtherMaturation(form.getKeyWorkRequiredForFurtherMaturation());

    if (submissionDetail.isProspectSubmission() && form.getOpportunityType() == OpportunityType.OTHER) {
      opportunityDetail.setOtherTypeOfOpportunity(form.getOtherTypeOfOpportunity());
    } else {
      opportunityDetail.setOtherTypeOfOpportunity(null);
    }

    supportingDocumentService.updateSupportingDocumentFileDescriptions(form.getMapOfOpportunity());
    supportingDocumentService.updateSupportingDocumentFileDescriptions(form.getGeologicalCrossSection());
    opportunityDetailsRepository.save(opportunityDetail);
  }

  @Transactional
  public void createOrUpdateOpportunityDetail(SubmissionDetail submissionDetail) {
    var opportunityDetail = getOpportunityDetail(submissionDetail)
        .orElse(new OpportunityDetail());
    opportunityDetail.setSubmissionDetail(submissionDetail);
    if (submissionDetail.isDryWellSubmission()) {
      opportunityDetail.setOpportunityType(OpportunityType.DRY_WELL);
    } else if (submissionDetail.isDiscoverySubmission()) {
      opportunityDetail.setOpportunityType(OpportunityType.DISCOVERY);
    }
    opportunityDetailsRepository.save(opportunityDetail);
  }

  Optional<OpportunityDetail> getOpportunityDetail(SubmissionDetail submissionDetail) {
    return opportunityDetailsRepository.findBySubmissionDetail(submissionDetail);
  }
}
