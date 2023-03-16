package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments;

import org.springframework.core.convert.converter.Converter;

public class StringToResourceSupportingDocumentTypeConverter implements Converter<String, ResourceSupportingDocumentType> {

  @Override
  public ResourceSupportingDocumentType convert(String source) {
    return ResourceSupportingDocumentType.valueOf(source.toUpperCase());
  }
}
