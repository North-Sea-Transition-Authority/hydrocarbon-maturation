package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

interface SupportingDocumentRepository extends CrudRepository<SupportingDocument, UUID> {

  List<SupportingDocument> findAllBySubmissionDetailAndSupportingDocumentTypeIn(
      SubmissionDetail submissionDetail, Set<SupportingDocumentType> supportingDocumentType);

  SupportingDocument findBySubmissionDetailAndUploadedFile(SubmissionDetail submissionDetail, UploadedFile uploadedFile);

  List<SupportingDocument> findAllByUploadedFile(UploadedFile uploadedFile);
}