package uk.co.nstauthority.hydrocarbonmaturation.file;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

interface UploadedFileRepository extends CrudRepository<UploadedFile, UUID> {

  List<UploadedFile> findAllByIdIn(List<UUID> id);
}