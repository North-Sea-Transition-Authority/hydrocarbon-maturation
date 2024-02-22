package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class FieldExternalDtoService {
  private final FieldInternalDtoRepository fieldInternalDtoRepository;

  @Autowired
  FieldExternalDtoService(FieldInternalDtoRepository fieldInternalDtoRepository) {
    this.fieldInternalDtoRepository = fieldInternalDtoRepository;
  }

  List<FieldExternalDto> searchFields(List<Integer> ids, String fieldName, List<String> fieldStatusIn) {
    return fieldInternalDtoRepository.searchFields(
            fieldName,
            fieldStatusIn != null ? FieldEnumsTransformerUtil.getInternalFieldStatusesFromExternalNames(fieldStatusIn) : null,
            ids
        )
        .stream()
        .map(FieldExternalDto::fromInternal)
        .toList();
  }
}
