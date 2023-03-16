package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;

@Repository
interface ResourceSupportingDocumentRepository extends CrudRepository<ResourceSupportingDocument, UUID> {

  List<ResourceSupportingDocument> findAllByResourceAndResourceSupportingDocumentType(
      Resource resource, ResourceSupportingDocumentType resourceSupportingDocumentType);

  ResourceSupportingDocument findByResourceAndUploadedFile(Resource resource, UploadedFile uploadedFile);

  List<ResourceSupportingDocument> findAllByUploadedFile(UploadedFile uploadedFile);
}