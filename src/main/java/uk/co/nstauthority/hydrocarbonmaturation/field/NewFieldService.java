package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.transaction.Transactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Service
class NewFieldService {
  private final NewFieldFormValidator newFieldFormValidator;
  private final NewFieldPersistenceService newFieldPersistenceService;
  private final ResourceService resourceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  NewFieldService(NewFieldFormValidator newFieldFormValidator,
                  NewFieldPersistenceService newFieldPersistenceService,
                  ResourceService resourceService, OrganisationUnitQueryService organisationUnitQueryService,
                  SearchSelectorService searchSelectorService) {
    this.newFieldFormValidator = newFieldFormValidator;
    this.newFieldPersistenceService = newFieldPersistenceService;
    this.resourceService = resourceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.searchSelectorService = searchSelectorService;
  }

  BindingResult validate(NewFieldForm form, BindingResult bindingResult) {
    newFieldFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  @Transactional
  public Field createNewField(NewFieldForm form) {
    var resource = resourceService.createResource(ResourceType.FIELD);

    return newFieldPersistenceService.createNewField(form, resource);
  }

  Map<String, String> getResponsibleTeamTypeDisplayableOptions() {
    var options = DisplayableEnumOptionUtil.getDisplayableOptions(ResponsibleTeamType.class);

    var offshoreTeams = Set.of(
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER
    );

    for (var responsibleTeamType : ResponsibleTeamType.values()) {
      var doesNotHaveRolePermission = responsibleTeamType.getRolePermissions().stream()
          .noneMatch(offshoreTeams::contains);

      if (doesNotHaveRolePermission) {
        options.remove(responsibleTeamType.getEnumName());
      }
    }

    return options;
  }

  Map<String, String> getGeographicalAreaDisplayableOptions() {
    var options = DisplayableEnumOptionUtil.getDisplayableOptions(GeographicalArea.class);

    options.remove(GeographicalArea.ONSHORE.getEnumName());

    return options;
  }

  Map<String, String> getPreSelectedOrganisationUnit(String responsibleOrganisationId) {
    if (StringUtils.isBlank(responsibleOrganisationId)) {
      return Map.of();
    }
    return organisationUnitQueryService.getPreSelectedOrganisationUnit(Integer.valueOf(responsibleOrganisationId));
  }

  public List<FieldDto> getDeterminedFields(String fieldName) {
    return newFieldPersistenceService.getDeterminedFieldsByCorrectedLegalName(fieldName).stream()
        .map(FieldDto::from)
        .toList();
  }

  public Map<String, String> getPreSelectedParentDeterminedField(String parentDeterminedFieldId) {
    if (StringUtils.isBlank(parentDeterminedFieldId)) {
      return Map.of();
    }

    var key = newFieldPersistenceService.getField(Integer.valueOf(parentDeterminedFieldId));

    if (key.isEmpty()) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(parentDeterminedFieldId, key.get().getCorrectedLegalFieldName());
  }
}
