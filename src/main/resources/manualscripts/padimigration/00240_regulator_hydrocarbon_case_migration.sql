DO $$
DECLARE

    active_submission_detail_rec RECORD;

BEGIN

    FOR active_submission_detail_rec IN (
        SELECT pbdh.hc_case, r.id remi_res_id
        FROM remi.padi_best_data_header pbdh
        JOIN remi.resource_group_links rgl ON rgl.legacy_resource_id = pbdh.resource_id
        JOIN remi.resources r ON r.id = rgl.resource_id
        WHERE r.active = true
    )
    LOOP

        UPDATE remi.regulator_managed_information
        SET hydrocarbon_case =
            CASE active_submission_detail_rec.hc_case
                WHEN 'Oil' THEN 'OIL_ONLY'
                WHEN 'Gas' THEN 'GAS_ONLY'
                WHEN 'Mixed oil and gas' THEN 'MIXED_OIL_AND_GAS'
            END
        WHERE resource_id = active_submission_detail_rec.remi_res_id;

    END LOOP;

END;
$$;