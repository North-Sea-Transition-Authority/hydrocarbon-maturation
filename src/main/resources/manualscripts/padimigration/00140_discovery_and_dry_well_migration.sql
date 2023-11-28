DO $$
DECLARE

    discovery_rec RECORD;
    dry_well_rec RECORD;
    lat_degrees NUMERIC;
    lat_minutes NUMERIC;
    lat_minutes_dec NUMERIC;
    lat_seconds NUMERIC;
    long_degrees NUMERIC;
    long_minutes NUMERIC;
    long_minutes_dec NUMERIC;
    long_seconds NUMERIC;

BEGIN

    FOR discovery_rec IN (
        SELECT sd.id sd_id, prdpw.*, pwm.*
        FROM remi.submission_details sd
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        LEFT JOIN remi.padi_res_data_post_well prdpw ON prdpw.resdat_id = sd.legacy_res_data_id
        LEFT JOIN remi.padi_well_mapping pwm ON pwm.padi_resdat_id = sd.legacy_res_data_id
        WHERE r.type = 'DISCOVERY'
    )
    LOOP

        lat_degrees := NULL;
        lat_minutes_dec := NULL;
        lat_minutes := NULL;
        lat_seconds := NULL;
        long_degrees := NULL;
        long_minutes_dec := NULL;
        long_minutes := NULL;
        long_seconds := NULL;

        IF discovery_rec.well_reservoir_lat_dd IS NOT NULL THEN

            lat_degrees := FLOOR(ABS(discovery_rec.well_reservoir_lat_dd)); -- whole number part of latitude degrees
            lat_minutes_dec := (ABS(discovery_rec.well_reservoir_lat_dd) - FLOOR(ABS(discovery_rec.well_reservoir_lat_dd))) * 60; -- minutes part of latitude with the decimal places
            lat_minutes := FLOOR(lat_minutes_dec); -- whole number part of latitude minutes
            lat_seconds := ROUND((ABS(lat_minutes_dec) - FLOOR(ABS(lat_minutes_dec))) * 60, 2); -- latitude seconds to 2 decimal places

        END IF;

        IF discovery_rec.well_reservoir_long_dd IS NOT NULL THEN

            long_degrees := FLOOR(ABS(discovery_rec.well_reservoir_long_dd)); -- whole number part of longitude degrees
            long_minutes_dec := (ABS(discovery_rec.well_reservoir_long_dd) - FLOOR(ABS(discovery_rec.well_reservoir_long_dd))) * 60; -- minutes part of longitude with the decimal places
            long_minutes := FLOOR(long_minutes_dec); -- whole number part of longitude minutes
            long_seconds := ROUND((ABS(long_minutes_dec) - FLOOR(ABS(long_minutes_dec))) * 60, 2); -- longitude seconds to 2 decimal places

        END IF;

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
            total_depth_formation_comments,
            data_acquisition_programme,
            geophysical_update_commentary,
            discovery_status,
            chance_of_commerciality_comments,
            total_well_cost,
            success_related_costs,
            well_id,
            well_reference
        ) VALUES (
            gen_random_uuid(),
            discovery_rec.sd_id,
            lat_degrees,
            lat_minutes,
            lat_seconds,
            long_degrees,
            long_minutes,
            long_seconds,
            CASE
                WHEN discovery_rec.well_reservoir_long_dd < 0 THEN 'WEST'
                WHEN discovery_rec.well_reservoir_long_dd >= 0 THEN 'EAST'
            END,
            CASE
                WHEN discovery_rec.well_reservoir_lat_dd IS NOT NULL AND discovery_rec.well_reservoir_long_dd IS NOT NULL
                THEN 'ED50' -- to confirm with NSTA
            END,
            discovery_rec.td_formation,
            discovery_rec.td_comment,
            discovery_rec.data_acquisition_programme,
            discovery_rec.geophysical_update_comment,
            CASE
                WHEN discovery_rec.well_result_type = 'Potentially Commercial Discovery' THEN 'POTENTIALLY_COMMERCIAL_DISCOVERY'
                WHEN discovery_rec.well_result_type IN ('Minor Discovery', 'Technical Discovery') THEN 'TECHNICAL_DISCOVERY'
            END,
            discovery_rec.remaining_potential, -- is this the correct field?
            CASE
                WHEN discovery_rec.total_well_cost ~ '^[0-9\.]+$' -- checks to see if the data is a number
                THEN ROUND(discovery_rec.total_well_cost::NUMERIC, 2)
            END,
            CASE
                WHEN discovery_rec.success_related_costs ~ '^[0-9\.]+$' -- checks to see if the data is a number
                THEN ROUND(discovery_rec.success_related_costs::NUMERIC, 2)
            END,
            discovery_rec.well_id,
            discovery_rec.well_registration_no
        );

    END LOOP;

    FOR dry_well_rec IN (
        SELECT sd.id sd_id, prdpw.*, pwm.*
        FROM remi.submission_details sd
        LEFT JOIN remi.padi_res_data_post_well prdpw ON prdpw.resdat_id = sd.legacy_res_data_id
        LEFT JOIN remi.padi_well_mapping pwm ON pwm.padi_resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type = 'POST_DRILL_DRY_HOLE'
    )
    LOOP

        lat_degrees := NULL;
        lat_minutes_dec := NULL;
        lat_minutes := NULL;
        lat_seconds := NULL;
        long_degrees := NULL;
        long_minutes_dec := NULL;
        long_minutes := NULL;
        long_seconds := NULL;

        IF dry_well_rec.well_reservoir_lat_dd IS NOT NULL THEN

            lat_degrees := FLOOR(ABS(dry_well_rec.well_reservoir_lat_dd)); -- whole number part of latitude degrees
            lat_minutes_dec := (ABS(dry_well_rec.well_reservoir_lat_dd) - FLOOR(ABS(dry_well_rec.well_reservoir_lat_dd))) * 60; -- minutes part of latitude with the decimal places
            lat_minutes := FLOOR(lat_minutes_dec); -- whole number part of latitude minutes
            lat_seconds := ROUND((ABS(lat_minutes_dec) - FLOOR(ABS(lat_minutes_dec))) * 60, 2); -- latitude seconds to 2 decimal places

        END IF;

        IF dry_well_rec.well_reservoir_long_dd IS NOT NULL THEN

            long_degrees := FLOOR(ABS(dry_well_rec.well_reservoir_long_dd)); -- whole number part of longitude degrees
            long_minutes_dec := (ABS(dry_well_rec.well_reservoir_long_dd) - FLOOR(ABS(dry_well_rec.well_reservoir_long_dd))) * 60; -- minutes part of longitude with the decimal places
            long_minutes := FLOOR(long_minutes_dec); -- whole number part of longitude minutes
            long_seconds := ROUND((ABS(long_minutes_dec) - FLOOR(ABS(long_minutes_dec))) * 60, 2); -- longitude seconds to 2 decimal places

        END IF;

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
        ) VALUES (
            gen_random_uuid(),
            dry_well_rec.sd_id,
            lat_degrees,
            lat_minutes,
            lat_seconds,
            long_degrees,
            long_minutes,
            long_seconds,
            CASE
                WHEN dry_well_rec.well_reservoir_long_dd < 0 THEN 'WEST'
                WHEN dry_well_rec.well_reservoir_long_dd >= 0 THEN 'EAST'
            END,
            CASE
                WHEN dry_well_rec.well_reservoir_lat_dd IS NOT NULL AND dry_well_rec.well_reservoir_long_dd IS NOT NULL
                THEN 'ED50' -- to confirm with NSTA
            END,
            dry_well_rec.td_formation,
            dry_well_rec.td_comment,
            dry_well_rec.data_acquisition_programme,
            dry_well_rec.geophysical_update_comment,
            CASE dry_well_rec.shows_type
                WHEN 'Gas' THEN 'GAS'
                WHEN 'Oil' THEN 'OIL'
                WHEN 'Oil&Gas' THEN 'MIXED_OIL_AND_GAS'
                ELSE 'NONE'
            END,
            dry_well_rec.shows_description,
                  dry_well_rec.remaining_potential,
                  dry_well_rec.valid_dry_hole_comment,
            CASE
                WHEN dry_well_rec.dry_hole_cost_element ~ '^[0-9\.]+$' -- checks to see if the data is a number
                THEN ROUND(dry_well_rec.dry_hole_cost_element::NUMERIC, 2)
            END,
            NULL, -- are the failure categories in PaDI?
            NULL,
            NULL,
            NULL,
            NULL,
            dry_well_rec.well_id,
            dry_well_rec.well_registration_no
        );

    END LOOP;

END;

$$;