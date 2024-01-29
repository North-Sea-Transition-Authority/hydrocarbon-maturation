package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
public class OpportunityDetailsPersistenceService {

  private final OpportunityDetailsRepository opportunityDetailsRepository;
  private final SubmissionSupportingDocumentService supportingDocumentService;

  @Autowired
  OpportunityDetailsPersistenceService(OpportunityDetailsRepository opportunityDetailsRepository,
                                       SubmissionSupportingDocumentService supportingDocumentService) {
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

    supportingDocumentService
        .saveDocuments(submissionDetail, form.getMapOfOpportunity(), FileDocumentType.MAP_OF_OPPORTUNITY);
    supportingDocumentService
        .saveDocuments(submissionDetail, form.getGeologicalCrossSection(), FileDocumentType.GEOLOGICAL_CROSS_SECTION);
    supportingDocumentService
        .saveDocuments(submissionDetail, form.getRepresentativeSeismicSection(), FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION);
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
