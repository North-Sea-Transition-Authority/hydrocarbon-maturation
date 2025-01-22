DO $$
DECLARE

    trap_rec RECORD;
    remi_trap_type TEXT;
    lat_degrees NUMERIC;
    lat_minutes NUMERIC;
    lat_minutes_dec NUMERIC;
    lat_seconds NUMERIC;
    long_degrees NUMERIC;
    long_minutes NUMERIC;
    long_minutes_dec NUMERIC;
    long_seconds NUMERIC;

BEGIN

    FOR trap_rec IN (
        SELECT prdh.*, prh.latitude_centroid, prh.longitude_centroid, sd.id sd_id
        FROM remi.padi_res_header prh
        JOIN remi.padi_res_data_header prdh ON prh.resource_id = prdh.resource_id
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
    )
    LOOP

        SELECT
            CASE
                WHEN trap_rec.trap_type IN (
                    'Structural',
                    '4-way dip closure',
                    'Anticline',
                    'Diapir',
                    'Faul trap',
                    'Fault',
                    'Faulted anticline',
                    'Low relief fault block',
                    'Tilted fault block',
                    'Unconformity'
                ) THEN 'STRUCTURAL'
                WHEN trap_rec.trap_type = 'Combination' THEN 'COMBINATION'
                WHEN trap_rec.trap_type = 'Stratigraphic' THEN 'STRATIGRAPHIC'
                ELSE 'OTHER'
            END
        INTO remi_trap_type;

        lat_degrees := NULL;
        lat_minutes_dec := NULL;
        lat_minutes := NULL;
        lat_seconds := NULL;
        long_degrees := NULL;
        long_minutes_dec := NULL;
        long_minutes := NULL;
        long_seconds := NULL;

        -- set the latitude based on the data header if available
        IF trap_rec.latitude_c_of_i IS NOT NULL THEN

            lat_degrees := FLOOR(ABS(trap_rec.latitude_c_of_i)); -- whole number part of latitude degrees
            lat_minutes_dec := (ABS(trap_rec.latitude_c_of_i) - FLOOR(ABS(trap_rec.latitude_c_of_i))) * 60; -- minutes part of latitude with the decimal places
            lat_minutes := FLOOR(lat_minutes_dec); -- whole number part of latitude minutes
            lat_seconds := ROUND((ABS(lat_minutes_dec) - FLOOR(ABS(lat_minutes_dec))) * 60, 2); -- latitude seconds to 2 decimal places

        -- else set the latitude based on the header if available
        ELSIF trap_rec.latitude_centroid IS NOT NULL THEN

            lat_degrees := FLOOR(ABS(trap_rec.latitude_centroid)); -- whole number part of latitude degrees
            lat_minutes_dec := (ABS(trap_rec.latitude_centroid) - FLOOR(ABS(trap_rec.latitude_centroid))) * 60; -- minutes part of latitude with the decimal places
            lat_minutes := FLOOR(lat_minutes_dec); -- whole number part of latitude minutes
            lat_seconds := ROUND((ABS(lat_minutes_dec) - FLOOR(ABS(lat_minutes_dec))) * 60, 2); -- latitude seconds to 2 decimal places

        END IF;

        -- set the latitude based on the data header if available
        IF trap_rec.longitude_c_of_i IS NOT NULL THEN

            long_degrees := FLOOR(ABS(trap_rec.longitude_c_of_i)); -- whole number part of longitude degrees
            long_minutes_dec := (ABS(trap_rec.longitude_c_of_i) - FLOOR(ABS(trap_rec.longitude_c_of_i))) * 60; -- minutes part of longitude with the decimal places
            long_minutes := FLOOR(long_minutes_dec); -- whole number part of longitude minutes
            long_seconds := ROUND((ABS(long_minutes_dec) - FLOOR(ABS(long_minutes_dec))) * 60, 2); -- longitude seconds to 2 decimal places

        -- else set the longitude based on the header if available
        ELSIF trap_rec.longitude_centroid IS NOT NULL THEN

            long_degrees := FLOOR(ABS(trap_rec.longitude_centroid)); -- whole number part of longitude degrees
            long_minutes_dec := (ABS(trap_rec.longitude_centroid) - FLOOR(ABS(trap_rec.longitude_centroid))) * 60; -- minutes part of longitude with the decimal places
            long_minutes := FLOOR(long_minutes_dec); -- whole number part of longitude minutes
            long_seconds := ROUND((ABS(long_minutes_dec) - FLOOR(ABS(long_minutes_dec))) * 60, 2); -- longitude seconds to 2 decimal places

        END IF;

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
            datum
        ) VALUES (
            gen_random_uuid(),
            trap_rec.sd_id,
            trap_rec.description,
            remi_trap_type,
            CASE WHEN remi_trap_type = 'OTHER' THEN trap_rec.trap_type_comment END,
            lat_degrees,
            lat_minutes,
            lat_seconds,
            long_degrees,
            long_minutes,
            long_seconds,
            CASE
                WHEN (trap_rec.longitude_c_of_i IS NOT NULL AND trap_rec.longitude_c_of_i < 0)
                    OR (trap_rec.longitude_centroid IS NOT NULL AND trap_rec.longitude_centroid < 0) THEN 'WEST'
                WHEN (trap_rec.longitude_c_of_i IS NOT NULL AND trap_rec.longitude_c_of_i >= 0)
                    OR (trap_rec.longitude_centroid IS NOT NULL AND trap_rec.longitude_centroid >= 0) THEN 'EAST'
            END,
            CASE
                WHEN (trap_rec.latitude_c_of_i IS NOT NULL AND trap_rec.longitude_c_of_i IS NOT NULL)
                    OR (trap_rec.latitude_centroid IS NOT NULL AND trap_rec.longitude_centroid IS NOT NULL)
                THEN 'ED50'
            END -- to confirm with NSTA
        );

    END LOOP;

END;

$$;