package uk.co.nstauthority.hydrocarbonmaturation.field;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.transaction.Transactional;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceView;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.GeographicalArea;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Service
class NewFieldService {
  private final NewFieldFormValidator newFieldFormValidator;
  private final FieldPersistenceService fieldPersistenceService;
  private final ResourceService resourceService;
  private final OrganisationUnitQueryService organisationUnitQueryService;
  private final SearchSelectorService searchSelectorService;
  private final SubmissionResourceService submissionResourceService;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Autowired
  NewFieldService(NewFieldFormValidator newFieldFormValidator,
                  FieldPersistenceService fieldPersistenceService,
                  ResourceService resourceService, OrganisationUnitQueryService organisationUnitQueryService,
                  SearchSelectorService searchSelectorService, SubmissionResourceService submissionResourceService,
                  ResourceDetailsPersistenceService resourceDetailsPersistenceService) {
    this.newFieldFormValidator = newFieldFormValidator;
    this.fieldPersistenceService = fieldPersistenceService;
    this.resourceService = resourceService;
    this.organisationUnitQueryService = organisationUnitQueryService;
    this.searchSelectorService = searchSelectorService;
    this.submissionResourceService = submissionResourceService;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
  }

  BindingResult validate(NewFieldForm form, BindingResult bindingResult) {
    newFieldFormValidator.validate(form, bindingResult);
    return bindingResult;
  }

  @Transactional
  public Field createNewField(NewFieldForm form) {
    var resource = resourceService.createResource(ResourceType.FIELD);

    return fieldPersistenceService.createNewField(form, resource);
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
    return fieldPersistenceService.getDeterminedFieldsByCorrectedLegalName(fieldName).stream()
        .map(FieldDto::from)
        .toList();
  }

  public Map<String, String> getPreSelectedParentDeterminedField(String parentDeterminedFieldId) {
    if (StringUtils.isBlank(parentDeterminedFieldId)) {
      return Map.of();
    }

    var key = fieldPersistenceService.getField(Integer.valueOf(parentDeterminedFieldId));

    if (key.isEmpty()) {
      return Map.of();
    }

    return searchSelectorService.buildPrePopulatedSelection(parentDeterminedFieldId, key.get().getCorrectedLegalFieldName());
  }

  public List<ResourceView> getPreSelectedAssociatedDiscoveriesMap(List<String> preSelectedAssociatedDiscoveryIds) {
    if (ObjectUtils.isEmpty(preSelectedAssociatedDiscoveryIds)) {
      return Collections.emptyList();
    }

    return preSelectedAssociatedDiscoveryIds
        .stream()
        .map(resourceDetailId -> resourceDetailsPersistenceService
            .getResourceDetailByIdOrThrow(UUID.fromString(resourceDetailId)))
        .map(this::getResourceViewFromResourceDetail)
        .toList();
  }

  private ResourceView getResourceViewFromResourceDetail(ResourceDetail resourceDetail) {
    var resourceDisplayName = submissionResourceService.getResourceDisplayName(resourceDetail);
    return new ResourceView(resourceDetail.getId(), resourceDisplayName);
  }
}