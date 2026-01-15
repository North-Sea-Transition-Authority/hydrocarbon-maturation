DO $$
DECLARE

    resource_rec RECORD;
    resource_audit_rev INTEGER;
    reporting_id UUID;
    reporting_audit_rev INTEGER;

BEGIN

    FOR resource_rec IN (
        SELECT
            pbdh.resource_id legacy_res_id,
            pbdh.name_alternative best_data_res_name,
            pbdh.opportunity_type,
            pbdh.hc_case,
            pbdh.choice,
            r.id remi_res_id,
            r.type resource_type,
            r.created_timestamp resource_created,
            prdh.stage_choice,
            rgl.resource_group_id,
            phwm.well_id,
            phwm.well_registration_no
        FROM remi.padi_best_data_header pbdh
        JOIN remi.padi_res_header prh ON prh.resource_id = pbdh.resource_id
        JOIN remi.padi_res_data_header prdh ON prdh.resource_id = pbdh.resource_id
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.resource_group_links rgl ON rgl.resource_id = r.id
        LEFT JOIN remi.padi_header_well_mapping phwm ON phwm.padi_resource_id = prh.resource_id
        ORDER BY pbdh.resource_id
    )
    LOOP

        IF resource_rec.choice = 1 AND TRIM(resource_rec.stage_choice) = '1' THEN

            UPDATE remi.resources
            SET active = true
            WHERE resource_rec.remi_res_id = id;

            INSERT INTO remi.audit_revisions(
                updated_at,
                updated_by_user_id
            ) VALUES (
                 now(),
                 1
             ) RETURNING rev INTO resource_audit_rev;

            INSERT INTO remi.resources_aud (
                rev,
                revtype,
                id,
                type,
                created_timestamp,
                active
            )
            VALUES (
                resource_audit_rev,
                1,
                resource_rec.remi_res_id,
                resource_rec.resource_type,
                resource_rec.resource_created,
                true
            );

        END IF;

        SELECT rri.id
        INTO reporting_id
        FROM remi.regulator_reporting_information rri
        WHERE rri.resource_group_id = resource_rec.resource_group_id;

        IF reporting_id IS NULL THEN

            reporting_id := gen_random_uuid();

            INSERT INTO remi.regulator_reporting_information (
                id,
                resource_group_id,
                resource_name,
                opportunity_type,
                other_type_of_opportunity,
                hydrocarbon_case,
                well_id,
                well_reference
            ) VALUES (
                reporting_id,
                resource_rec.resource_group_id,
                resource_rec.best_data_res_name,
                CASE resource_rec.opportunity_type
                    WHEN 'Discovery' THEN 'DISCOVERY'
                    WHEN 'Drill-ready Prospect' THEN 'DRILL_READY_PROSPECT'
                    WHEN 'Dry Prospect (for post-well analysis)' THEN 'DRY_WELL'
                    WHEN 'Lead' THEN 'LEAD'
                    WHEN 'Prospect' THEN 'PROSPECT'
                    ELSE 'OTHER'
                END,
                CASE
                    WHEN resource_rec.opportunity_type NOT IN ('Discovery', 'Drill-ready Prospect', 'Dry Prospect (for post-well analysis)', 'Lead', 'Prospect')
                    THEN resource_rec.opportunity_type
                END,
                CASE LOWER(resource_rec.hc_case)
                    WHEN 'oil' THEN 'OIL_ONLY'
                    WHEN 'gas' THEN 'GAS_ONLY'
                    WHEN 'condensate' THEN 'GAS_ONLY'
                    WHEN 'mixed oil and gas' THEN 'MIXED_OIL_AND_GAS'
                END,
                resource_rec.well_id,
                resource_rec.well_registration_no
            );

            INSERT INTO remi.audit_revisions(
                updated_at,
                updated_by_user_id
            ) VALUES (
                 now(),
                 1
             ) RETURNING rev INTO reporting_audit_rev;

            INSERT INTO remi.regulator_reporting_information_aud (
                rev,
                revtype,
                id,
                resource_group_id,
                resource_name,
                opportunity_type,
                other_type_of_opportunity,
                hydrocarbon_case,
                well_id,
                well_reference
            ) VALUES (
                reporting_audit_rev,
                0,
                 reporting_id,
                 resource_rec.resource_group_id,
                 resource_rec.best_data_res_name,
                 CASE resource_rec.opportunity_type
                     WHEN 'Discovery' THEN 'DISCOVERY'
                     WHEN 'Drill-ready Prospect' THEN 'DRILL_READY_PROSPECT'
                     WHEN 'Dry Prospect (for post-well analysis)' THEN 'DRY_WELL'
                     WHEN 'Lead' THEN 'LEAD'
                     WHEN 'Prospect' THEN 'PROSPECT'
                     ELSE 'OTHER'
                     END,
                 CASE
                     WHEN resource_rec.opportunity_type NOT IN ('Discovery', 'Drill-ready Prospect', 'Dry Prospect (for post-well analysis)', 'Lead', 'Prospect')
                         THEN resource_rec.opportunity_type
                     END,
                CASE LOWER(resource_rec.hc_case)
                    WHEN 'oil' THEN 'OIL_ONLY'
                    WHEN 'gas' THEN 'GAS_ONLY'
                    WHEN 'condensate' THEN 'GAS_ONLY'
                    WHEN 'mixed oil and gas' THEN 'MIXED_OIL_AND_GAS'
                END,
                 resource_rec.well_id,
                 resource_rec.well_registration_no
         );

        END IF;

    END LOOP;

END;
$$;