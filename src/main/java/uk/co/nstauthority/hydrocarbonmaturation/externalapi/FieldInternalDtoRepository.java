package uk.co.nstauthority.hydrocarbonmaturation.externalapi;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

@Repository
interface FieldInternalDtoRepository extends CrudRepository<Field, Integer> {

  @Query("SELECT new uk.co.nstauthority.hydrocarbonmaturation.externalapi.FieldInternalDto(" +
      "  f.fieldIdentifier" +
      ", f.correctedLegalFieldName" +
      ", f.onshoreOffshore" +
      ", f.determinationStatus" +
      ", f.responsibleOrganisationId" +
      ", rmi.geographicalArea" +
      ", fsh.fieldStatus" +
      ") " +
      "FROM Field f " +
      "JOIN FieldStatusHistory fsh ON fsh.field = f AND fsh.fieldStatus = " +
      "  (SELECT MAX(fsh2.fieldStatus) FROM FieldStatusHistory fsh2 WHERE fsh2.field = f AND fsh2.statusDate = " +
      "    (SELECT MAX(fsh3.statusDate) FROM FieldStatusHistory fsh3 WHERE fsh3.field = f)) " +
      "JOIN RegulatorManagedInformation rmi ON f.resource = rmi.resource " +
      "WHERE (f.correctedLegalFieldName LIKE '%'||:fieldName||'%' OR :fieldName IS NULL) " +
      "AND (:fieldStatusIn IS NULL OR fsh.fieldStatus IN (:fieldStatusIn)) " +
      "AND (:ids IS NULL OR f.fieldIdentifier IN (:ids))"
  )
  List<FieldInternalDto> searchFields(String fieldName, List<FieldStatus> fieldStatusIn, List<Integer> ids);
}
