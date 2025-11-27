DO $$
    DECLARE

        active_resource_rec RECORD;

    BEGIN

        FOR active_resource_rec IN (
            SELECT pbdh.opportunity_type, r.id remi_res_id
            FROM remi.padi_best_data_header pbdh
            JOIN remi.resource_group_links rgl ON rgl.legacy_resource_id = pbdh.resource_id
            JOIN remi.resources r ON r.id = rgl.resource_id
            WHERE r.active = true
        )
        LOOP

            UPDATE remi.regulator_managed_information
            SET opportunity_type =
            CASE active_resource_rec.opportunity_type
                WHEN 'Discovery' THEN 'DISCOVERY'
                WHEN 'Drill-ready Prospect' THEN 'DRILL_READY_PROSPECT'
                WHEN 'Dry Prospect (for post-well analysis)' THEN 'DRY_WELL'
                WHEN 'Lead' THEN 'LEAD'
                WHEN 'Prospect' THEN 'PROSPECT'
                ELSE 'OTHER'
                END,
            other_type_of_opportunity =
            CASE
                WHEN active_resource_rec.opportunity_type NOT IN ('Discovery', 'Drill-ready Prospect', 'Dry Prospect (for post-well analysis)', 'Lead', 'Prospect')
                THEN active_resource_rec.opportunity_type
            END
            WHERE resource_id = active_resource_rec.remi_res_id;

        END LOOP;

    END;
$$;