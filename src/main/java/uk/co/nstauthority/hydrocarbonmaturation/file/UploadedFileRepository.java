package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

interface UploadedFileRepository extends CrudRepository<UploadedFile, UUID> {

  void removeUploadedFileById(UUID id);
}