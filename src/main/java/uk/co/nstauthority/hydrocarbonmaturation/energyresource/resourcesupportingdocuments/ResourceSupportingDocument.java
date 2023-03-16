package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;

@Entity
@Table(name = "resource_supporting_documents")
class ResourceSupportingDocument {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  @Enumerated(EnumType.STRING)
  private ResourceSupportingDocumentType resourceSupportingDocumentType;

  @ManyToOne
  @JoinColumn(name = "uploaded_file_id")
  private UploadedFile uploadedFile;

  UUID getId() {
    return id;
  }

  void setId(UUID id) {
    this.id = id;
  }

  Resource getResource() {
    return resource;
  }

  void setResource(Resource resource) {
    this.resource = resource;
  }

  ResourceSupportingDocumentType getResourceSupportingDocumentType() {
    return resourceSupportingDocumentType;
  }

  void setResourceSupportingDocumentType(
      ResourceSupportingDocumentType resourceSupportingDocumentType) {
    this.resourceSupportingDocumentType = resourceSupportingDocumentType;
  }

  UploadedFile getUploadedFile() {
    return uploadedFile;
  }

  void setUploadedFile(UploadedFile uploadedFile) {
    this.uploadedFile = uploadedFile;
  }
}