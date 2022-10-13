package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments;

import org.springframework.core.convert.converter.Converter;

public class StringToSupportingDocumentTypeConverter implements Converter<String, SupportingDocumentType> {
  @Override
  public SupportingDocumentType convert(String source) {
    return SupportingDocumentType.valueOf(source.toUpperCase());
  }
}
