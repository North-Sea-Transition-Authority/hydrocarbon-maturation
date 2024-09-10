DO $$
DECLARE

    geographic_area_rec RECORD;
    geographic_area TEXT;

BEGIN

    FOR geographic_area_rec IN (
        SELECT prh.ytf_reporting_area, prg.remi_resource_id, prg.legacy_resource_id
        FROM remi.padi_res_header prh
        JOIN remi.padi_resource_groupings prg ON prg.legacy_resource_id = prh.resource_id
        WHERE prh.ytf_reporting_area IS NOT NULL
        GROUP BY prh.ytf_reporting_area, prg.remi_resource_id, prg.legacy_resource_id
    )
    LOOP

        geographic_area := NULL;

        SELECT
            CASE geographic_area_rec.ytf_reporting_area
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
        END
        INTO geographic_area;

        UPDATE remi.regulator_managed_information
        SET geographical_area = geographic_area
        WHERE resource_id = geographic_area_rec.remi_resource_id;

    END LOOP;

END;

$$;

DO $$
DECLARE

    onshore_area_rec RECORD;

BEGIN

    FOR onshore_area_rec IN (
        SELECT rmi.resource_id
        FROM remi.resource_details rd
        JOIN remi.submission_details sd ON sd.id = rd.submission_detail_id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.regulator_managed_information rmi ON rmi.resource_id = r.id
        WHERE rd.onshore_offshore = 'ONSHORE'
        AND rmi.geographical_area IS NULL
    )
    LOOP

        UPDATE remi.regulator_managed_information
        SET geographical_area = 'ONSHORE'
        WHERE resource_id = onshore_area_rec.resource_id;

    END LOOP;

END;

$$;