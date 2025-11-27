DO $$
DECLARE

    resource_rec RECORD;
    remi_res_id UUID;
    remi_res_type TEXT;
    remi_submission_id UUID;
    remi_submission_detail_id UUID;
    reservoir_id UUID;
    remi_general_detail_id UUID;
    remi_rsd_id UUID;
    linked_rsd_id UUID;
    group_id UUID;
    missing_group_res_id UUID;
    shore_type TEXT;

BEGIN

    FOR resource_rec IN (
        SELECT
            pbdh.resource_id legacy_res_id,
            pbdh.name_alternative best_data_res_name,
            pbdh.choice,
            prh.ytf_reporting_area,
            prdh.opportunity_type,
            prdh.resdat_id,
            prdh.name_alternative,
            sd.id linked_sd_id,
            phwm.well_id,
            phwm.well_registration_no,
            m.resource_stage_name multi_reservoir_stage_name
        FROM remi.padi_best_data_header pbdh
        JOIN remi.padi_res_header prh ON prh.resource_id = pbdh.resource_id
        JOIN remi.padi_res_data_header prdh ON prdh.resource_id = pbdh.resource_id
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        LEFT JOIN remi.padi_header_well_mapping phwm ON phwm.padi_resource_id = pbdh.resource_id
        LEFT JOIN remi.padi_multi_reservoir_discoveries m ON m.resdat_id = prdh.resdat_id
        WHERE TRIM(prdh.stage_choice) = '1'
        AND r.active = false
    )
    LOOP

        INSERT INTO remi.resources (id, type, created_timestamp, active)
        VALUES (
            gen_random_uuid(),
            CASE WHEN resource_rec.opportunity_type = 'Discovery' THEN 'DISCOVERY' ELSE 'PROSPECT' END,
            now(),
            CASE WHEN resource_rec.choice = '1' THEN TRUE ELSE FALSE END
        ) RETURNING id, type INTO remi_res_id, remi_res_type;

        IF remi_res_type = 'DISCOVERY' THEN
            INSERT INTO remi.undeveloped_discoveries(id, is_undeveloped_discovery, resource_id)
            VALUES (gen_random_uuid(), false, remi_res_id);
        END IF;

        INSERT INTO remi.submissions (id, resource_id)
        VALUES(gen_random_uuid(), remi_res_id)
        RETURNING id INTO remi_submission_id;

        INSERT INTO remi.submission_details (
            id,
            submission_id,
            created_timestamp,
            created_by_wua_id,
            status,
            submission_type,
            version_number,
            submitted_timestamp,
            accepted_timestamp,
            submitted_by_wua_id,
            accepted_by_wua_id,
            legacy_res_data_id
        ) SELECT
            gen_random_uuid(),
            remi_submission_id,
            created_timestamp,
            created_by_wua_id,
            status,
            CASE
                WHEN sd.submission_type IN (
                    'RELINQUISHMENT_PROSPECT',
                    'RELINQUISHMENT_DISCOVERY',
                    'POST_DRILL_DRY_HOLE'
                ) THEN sd.submission_type
                WHEN remi_res_type = 'PROSPECT' THEN 'CREATE_PROSPECT'
                WHEN remi_res_type = 'DISCOVERY' THEN 'CREATE_DISCOVERY'
            END,
            version_number,
            submitted_timestamp,
            accepted_timestamp,
            submitted_by_wua_id,
            accepted_by_wua_id,
            legacy_res_data_id
        FROM remi.submission_details sd
        WHERE sd.id = resource_rec.linked_sd_id
        RETURNING id INTO remi_submission_detail_id;

        INSERT INTO remi.reservoirs (id, name)
        VALUES (
            gen_random_uuid(),
            resource_rec.name_alternative
        ) RETURNING id INTO reservoir_id;

        INSERT INTO remi.reservoir_submission_details (id, reservoir, submission_detail_id, status)
        VALUES (
           gen_random_uuid(),
           reservoir_id,
           remi_submission_detail_id,
           'EXTANT'
        ) RETURNING id INTO remi_rsd_id;

        INSERT INTO remi.resource_details (
            id,
            submission_detail_id,
            resource_name,
            onshore_offshore,
            organisation_id,
            organisation_name,
            organisation_registered_number
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            rd.resource_name,
            rd.onshore_offshore,
            rd.organisation_id,
            rd.organisation_name,
            rd.organisation_registered_number
        FROM remi.resource_details rd
        WHERE rd.submission_detail_id = resource_rec.linked_sd_id
        RETURNING onshore_offshore INTO shore_type;

        INSERT INTO remi.general_details (
            id,
            submission_detail_id,
            prospect_discovery_located_in_ukcs,
            prospect_discovery_located_in_england,
            percentage_located_in_ukcs,
            percentage_located_in_england,
            information_source,
            other_information_source,
            licence_application_reference,
            relinquishment_date
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            gd.prospect_discovery_located_in_ukcs,
            gd.prospect_discovery_located_in_england,
            gd.percentage_located_in_ukcs,
            gd.percentage_located_in_england,
            gd.information_source,
            gd.other_information_source,
            gd.licence_application_reference,
            gd.relinquishment_date
        FROM remi.general_details gd
        WHERE gd.submission_detail_id = resource_rec.linked_sd_id
        RETURNING id INTO remi_general_detail_id;

        INSERT INTO remi.general_details_licences (
            id,
            general_details_id,
            licence_reference,
            licence_id
        )
        SELECT
            gen_random_uuid(),
            remi_general_detail_id,
            gdl.licence_reference,
            gdl.licence_id
        FROM remi.general_details_licences gdl
        JOIN remi.general_details gd ON gd.id = gdl.general_details_id
        WHERE gd.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.chance_of_success(
            id,
            submission_detail_id,
            play_source_percentage,
            play_source_comments,
            play_reservoir_percentage,
            play_reservoir_comments,
            play_top_seal_percentage,
            play_top_seal_comments,
            play_total,
            prospect_specific_source_percentage,
            prospect_specific_source_comments,
            prospect_specific_reservoir_percentage,
            prospect_specific_reservoir_comments,
            prospect_specific_top_seal_percentage,
            prospect_specific_top_seal_comments,
            prospect_specific_trap_percentage,
            prospect_specific_trap_comments,
            prospect_specific_migration_percentage,
            prospect_specific_migration_comments,
            prospect_specific_total,
            overall_total,
            geophysically_modified,
            geophysically_modified_percentage,
            geophysically_modified_comments,
            related_to_specific_well_location,
            well_modified_percentage,
            well_modified_comments
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            play_source_percentage,
            play_source_comments,
            play_reservoir_percentage,
            play_reservoir_comments,
            play_top_seal_percentage,
            play_top_seal_comments,
            play_total,
            prospect_specific_source_percentage,
            prospect_specific_source_comments,
            prospect_specific_reservoir_percentage,
            prospect_specific_reservoir_comments,
            prospect_specific_top_seal_percentage,
            prospect_specific_top_seal_comments,
            prospect_specific_trap_percentage,
            prospect_specific_trap_comments,
            prospect_specific_migration_percentage,
            prospect_specific_migration_comments,
            prospect_specific_total,
            overall_total,
            geophysically_modified,
            geophysically_modified_percentage,
            geophysically_modified_comments,
            related_to_specific_well_location,
            well_modified_percentage,
            well_modified_comments
        FROM remi.chance_of_success cos
        WHERE cos.submission_detail_id = resource_rec.linked_sd_id;

        IF resource_rec.multi_reservoir_stage_name IS NOT NULL THEN

            SELECT rsd.id
            INTO linked_rsd_id
            FROM remi.submission_details sd
            JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
            JOIN remi.reservoirs r ON r.id = rsd.reservoir
            JOIN remi.submissions s ON s.id = sd.submission_id
            JOIN remi.resources r2 ON r2.id = s.resource_id
            WHERE sd.legacy_res_data_id = resource_rec.resdat_id
            AND r.name = resource_rec.multi_reservoir_stage_name
            AND r2.active = false;

        ELSE

            SELECT rsd.id
            INTO linked_rsd_id
            FROM remi.submission_details sd
            JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
            JOIN remi.submissions s ON s.id = sd.submission_id
            JOIN remi.resources r2 ON r2.id = s.resource_id
            WHERE sd.legacy_res_data_id = resource_rec.resdat_id
            AND r2.active = false;

        END IF;

        INSERT INTO remi.probability_distributions (
            id,
            submission_detail_id,
            distribution_type,
            other_distribution_type,
            instance,
            min_value,
            p99,
            p90,
            mode_value,
            p50,
            mean_value,
            p10,
            p1,
            max_value,
            constant_value,
            most_likely_value,
            comments,
            three_c,
            two_c,
            one_c,
            reservoir_submission_detail_id
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            distribution_type,
            other_distribution_type,
            instance,
            min_value,
            p99,
            p90,
            mode_value,
            p50,
            mean_value,
            p10,
            p1,
            max_value,
            constant_value,
            most_likely_value,
            comments,
            three_c,
            two_c,
            one_c,
            CASE WHEN reservoir_submission_detail_id IS NOT NULL THEN remi_rsd_id END
        FROM remi.probability_distributions pd
        WHERE pd.submission_detail_id = resource_rec.linked_sd_id
        AND (pd.reservoir_submission_detail_id IS NULL OR pd.reservoir_submission_detail_id = linked_rsd_id);

        INSERT INTO remi.regulator_managed_information (
            id,
            resource_id,
            responsible_team,
            geographical_area,
            well_id,
            well_reference,
            resource_name
        )
        VALUES (
            gen_random_uuid(),
            remi_res_id,
            'EXPLORATION',
            CASE resource_rec.ytf_reporting_area
                WHEN 'CBA' THEN 'EAST_IRISH_SEA'
                WHEN 'FAA' THEN 'CENTRAL_NORTH_SEA'
                WHEN 'IOM' THEN 'EAST_IRISH_SEA'
                WHEN 'MNSH' THEN 'CENTRAL_NORTH_SEA'
                WHEN 'ROCK' THEN 'WEST_OF_SHETLAND'
                WHEN 'SWAP' THEN 'EAST_IRISH_SEA'
                WHEN 'CNS' THEN 'CENTRAL_NORTH_SEA'
                WHEN 'ISEA' THEN 'EAST_IRISH_SEA'
                WHEN 'NNS' THEN 'NORTHERN_NORTH_SEA'
                WHEN 'SNS' THEN 'SOUTHERN_NORTH_SEA'
                WHEN 'WOS' THEN 'WEST_OF_SHETLAND'
                WHEN 'APB' THEN 'SOUTHERN_NORTH_SEA'
            END,
            resource_rec.well_id,
            resource_rec.well_registration_no,
            resource_rec.best_data_res_name
        );

        IF shore_type = 'ONSHORE' THEN

            UPDATE remi.regulator_managed_information
            SET geographical_area = 'ONSHORE'
            WHERE resource_id = remi_res_id;

        END IF;

        INSERT INTO remi.opportunity_details (
            id,
            submission_detail_id,
            opportunity_type,
            other_type_of_opportunity,
            opportunity_comments,
            key_evaluation_work_carried_out,
            key_work_required_for_further_maturation
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            opportunity_type,
            other_type_of_opportunity,
            opportunity_comments,
            key_evaluation_work_carried_out,
            key_work_required_for_further_maturation
        FROM remi.opportunity_details od
        WHERE od.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.oil_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_gas_recoverable_resources,
            use_industry_standard_conversion
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            has_associated_gas_recoverable_resources,
            use_industry_standard_conversion
        FROM remi.oil_case_outputs o
        WHERE o.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.gas_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_condensate_recoverable_resources,
            use_industry_standard_conversion
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            has_associated_condensate_recoverable_resources,
            use_industry_standard_conversion
        FROM remi.gas_case_outputs o
        WHERE o.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.mixed_case_outputs (
            id,
            reservoir_submission_detail_id,
            has_associated_gas_recoverable_resources,
            use_industry_standard_conversion
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            has_associated_gas_recoverable_resources,
            use_industry_standard_conversion
        FROM remi.mixed_case_outputs o
        WHERE o.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.oil_case_volumetric_totals (
            id,
            submission_detail_id,
            use_industry_standard_conversion
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            use_industry_standard_conversion
        FROM remi.oil_case_volumetric_totals t
        WHERE t.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.gas_case_volumetric_totals (
            id,
            submission_detail_id,
            has_associated_condensate_recoverable_resources,
            use_industry_standard_conversion
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            has_associated_condensate_recoverable_resources,
            use_industry_standard_conversion
        FROM remi.gas_case_volumetric_totals t
        WHERE t.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.combined_case_volumetric_totals (
            id,
            submission_detail_id,
            has_associated_condensate_recoverable_resources,
            use_industry_standard_conversion
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            has_associated_condensate_recoverable_resources,
            use_industry_standard_conversion
        FROM remi.combined_case_volumetric_totals t
        WHERE t.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.trap_properties (
            id,
            submission_detail_id,
            trap_description,
            trap_type,
            other_trap_type,
            latitude_degrees,
            latitude_minutes,
            latitude_seconds,
            longitude_degrees,
            longitude_minutes,
            longitude_seconds,
            longitude_hemisphere,
            datum,
            centre_of_interest_quadrant_block
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            trap_description,
            trap_type,
            other_trap_type,
            latitude_degrees,
            latitude_minutes,
            latitude_seconds,
            longitude_degrees,
            longitude_minutes,
            longitude_seconds,
            longitude_hemisphere,
            datum,
            centre_of_interest_quadrant_block
        FROM remi.trap_properties tp
        WHERE tp.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.hydrocarbon_case_details (
            id,
            submission_detail_id,
            oil_only,
            oil_only_comments,
            gas_only,
            gas_only_comments,
            oil_and_gas,
            oil_and_gas_comments
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            oil_only,
            oil_only_comments,
            gas_only,
            gas_only_comments,
            oil_and_gas,
            oil_and_gas_comments
        FROM remi.hydrocarbon_case_details hcd
        WHERE hcd.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.discovery_well_details (
            id,
            submission_detail_id,
            top_reservoir_latitude_degrees,
            top_reservoir_latitude_minutes,
            top_reservoir_latitude_seconds,
            top_reservoir_longitude_degrees,
            top_reservoir_longitude_minutes,
            top_reservoir_longitude_seconds,
            top_reservoir_longitude_hemisphere,
            top_reservoir_datum,
            total_depth_formation,
            total_depth_formation_manual,
            total_depth_formation_comments,
            data_acquisition_programme,
            geophysical_update_commentary,
            discovery_status,
            chance_of_commerciality_comments,
            total_well_cost,
            success_related_costs,
            well_id,
            well_reference
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            top_reservoir_latitude_degrees,
            top_reservoir_latitude_minutes,
            top_reservoir_latitude_seconds,
            top_reservoir_longitude_degrees,
            top_reservoir_longitude_minutes,
            top_reservoir_longitude_seconds,
            top_reservoir_longitude_hemisphere,
            top_reservoir_datum,
            total_depth_formation,
            total_depth_formation_manual,
            total_depth_formation_comments,
            data_acquisition_programme,
            geophysical_update_commentary,
            discovery_status,
            chance_of_commerciality_comments,
            total_well_cost,
            success_related_costs,
            well_id,
            well_reference
        FROM remi.discovery_well_details dwd
        WHERE dwd.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.dry_well_details (
            id,
            submission_detail_id,
            top_reservoir_latitude_degrees,
            top_reservoir_latitude_minutes,
            top_reservoir_latitude_seconds,
            top_reservoir_longitude_degrees,
            top_reservoir_longitude_minutes,
            top_reservoir_longitude_seconds,
            top_reservoir_longitude_hemisphere,
            top_reservoir_datum,
            total_depth_formation,
            total_depth_formation_manual,
            total_depth_formation_comments,
            data_acquisition_programme,
            geophysical_update_commentary,
            dry_hole_type,
            dry_hole_description,
            dry_hole_scope,
            dry_hole_comments,
            dry_hole_cost,
            source_failure_category,
            reservoir_failure_category,
            seal_failure_category,
            trap_failure_category,
            migration_failure_category,
            well_id,
            well_reference
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            top_reservoir_latitude_degrees,
            top_reservoir_latitude_minutes,
            top_reservoir_latitude_seconds,
            top_reservoir_longitude_degrees,
            top_reservoir_longitude_minutes,
            top_reservoir_longitude_seconds,
            top_reservoir_longitude_hemisphere,
            top_reservoir_datum,
            total_depth_formation,
            total_depth_formation_manual,
            total_depth_formation_comments,
            data_acquisition_programme,
            geophysical_update_commentary,
            dry_hole_type,
            dry_hole_description,
            dry_hole_scope,
            dry_hole_comments,
            dry_hole_cost,
            source_failure_category,
            reservoir_failure_category,
            seal_failure_category,
            trap_failure_category,
            migration_failure_category,
            well_id,
            well_reference
        FROM remi.dry_well_details dwd2
        WHERE dwd2.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.permeability_details(
            id,
            reservoir_submission_detail_id,
            has_permeability,
            permeability_min,
            permeability_max,
            permeability_average,
            average_method_used,
            source,
            source_other,
            permeability_comments,
            kv_kh_average
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            has_permeability,
            permeability_min,
            permeability_max,
            permeability_average,
            average_method_used,
            source,
            source_other,
            permeability_comments,
            kv_kh_average
        FROM remi.permeability_details pd
        WHERE pd.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.oil_fluid_properties(
            id,
            reservoir_submission_detail_id,
            oil_gravity,
            oil_viscosity,
            oil_sulphur,
            oil_wax,
            oil_other_wax_appearance_temperature
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            oil_gravity,
            oil_viscosity,
            oil_sulphur,
            oil_wax,
            oil_other_wax_appearance_temperature
        FROM remi.oil_fluid_properties ofp
        WHERE ofp.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.gas_fluid_properties (
            id,
            reservoir_submission_detail_id,
            gas_specific_gravity,
            gas_h2s,
            gas_co2,
            gas_n2,
            condensate_present,
            condensate_gravity,
            condensate_viscosity,
            condensate_sulphur,
            condensate_wax,
            condensate_other_incl_wax,
            advise_other_gases_concentration
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            gas_specific_gravity,
            gas_h2s,
            gas_co2,
            gas_n2,
            condensate_present,
            condensate_gravity,
            condensate_viscosity,
            condensate_sulphur,
            condensate_wax,
            condensate_other_incl_wax,
            advise_other_gases_concentration
        FROM remi.gas_fluid_properties gfp
        WHERE gfp.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.general_fluid_properties(
            id,
            reservoir_submission_detail_id,
            datum_depth,
            temperature_at_datum_depth,
            pressure_at_datum_depth,
            saturation_pressure_at_datum_depth,
            water_salinity
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            datum_depth,
            temperature_at_datum_depth,
            pressure_at_datum_depth,
            saturation_pressure_at_datum_depth,
            water_salinity
        FROM remi.general_fluid_properties gfp
        WHERE gfp.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.reservoir_hydrocarbons (
            id,
            reservoir_submission_detail_id,
            hydrocarbons_discovered
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            hydrocarbons_discovered
        FROM remi.reservoir_hydrocarbons rh
        WHERE rh.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.reservoir_properties (
            id,
            reservoir_submission_detail_id,
            reservoir_facies,
            reservoir_connectivity,
            age_stage,
            age_stage_manual,
            formation_name,
            formation_name_manual,
            lithology,
            lithology_other
        )
        SELECT
            gen_random_uuid(),
            remi_rsd_id,
            reservoir_facies,
            reservoir_connectivity,
            age_stage,
            age_stage_manual,
            formation_name,
            formation_name_manual,
            lithology,
            lithology_other
        FROM remi.reservoir_properties rp
        WHERE rp.reservoir_submission_detail_id = linked_rsd_id;

        INSERT INTO remi.source_and_seal_details (
            id,
            submission_detail_id,
            source_formation_name,
            source_formation_name_manual,
            source_age_stage,
            source_age_stage_manual,
            source_lithology,
            source_lithology_other,
            top_seal_formation_name,
            top_seal_formation_name_manual,
            top_seal_age_stage,
            top_seal_age_stage_manual,
            top_seal_lithology,
            top_seal_lithology_other
        )
        SELECT
            gen_random_uuid(),
            remi_submission_detail_id,
            source_formation_name,
            source_formation_name_manual,
            source_age_stage,
            source_age_stage_manual,
            source_lithology,
            source_lithology_other,
            top_seal_formation_name,
            top_seal_formation_name_manual,
            top_seal_age_stage,
            top_seal_age_stage_manual,
            top_seal_lithology,
            top_seal_lithology_other
        FROM remi.source_and_seal_details sosd
        WHERE sosd.submission_detail_id = resource_rec.linked_sd_id;

        INSERT INTO remi.general_details_quadrant_blocks (
            id,
            general_details_id,
            quadrant_block_reference,
            quadrant,
            block,
            onshore_offshore
        )
        SELECT
            gen_random_uuid(),
            remi_general_detail_id,
            quadrant_block_reference,
            quadrant,
            block,
            onshore_offshore
        FROM remi.general_details_quadrant_blocks b
        JOIN remi.general_details gd ON b.general_details_id = gd.id
        WHERE gd.submission_detail_id = resource_rec.linked_sd_id;

        -- Get existing group links for PaDI resource
        SELECT rgl.resource_group_id
        INTO group_id
        FROM remi.resource_group_links rgl
        JOIN remi.resources r ON r.id = rgl.resource_id
        WHERE rgl.legacy_resource_id = resource_rec.legacy_res_id
        AND r.type = remi_res_type
        GROUP BY rgl.resource_group_id;

        IF group_id IS NULL THEN
            group_id := gen_random_uuid();
        END IF;

        -- Create group link with existing or new group for our new resource
        INSERT INTO remi.resource_group_links (
            id,
            resource_group_id,
            resource_id,
            legacy_resource_id
        ) VALUES (
            gen_random_uuid(),
            group_id,
            remi_res_id,
            resource_rec.legacy_res_id
        );

        -- Get any resources which are not yet in groups and add them to the newly created group
        SELECT prg.remi_resource_id
        INTO missing_group_res_id
        FROM remi.padi_resource_groupings prg
        WHERE prg.legacy_resource_id = resource_rec.legacy_res_id
        AND NOT EXISTS (
            SELECT 1
            FROM remi.resource_group_links rgl
            WHERE rgl.legacy_resource_id = resource_rec.legacy_res_id
            AND rgl.resource_id != remi_res_id
        )
        GROUP BY prg.remi_resource_id;

        IF missing_group_res_id IS NOT NULL THEN

            INSERT INTO remi.resource_group_links (
                id,
                resource_group_id,
                resource_id,
                legacy_resource_id
            ) VALUES (
                 gen_random_uuid(),
                 group_id,
                 missing_group_res_id,
                 resource_rec.legacy_res_id
             );

        END IF;

    END LOOP;

END;
$$;