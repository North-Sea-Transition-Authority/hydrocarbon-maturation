DO $$
DECLARE

    geographic_area_rec RECORD;
    geographic_area TEXT;
    offshore_rmi_audit_rev INTEGER;

BEGIN

    FOR geographic_area_rec IN (
        SELECT prh.ytf_reporting_area, prg.remi_resource_id, prg.legacy_resource_id, a.rev, a.id rmi_id
        FROM remi.padi_res_header prh
        JOIN remi.padi_resource_groupings prg ON prg.legacy_resource_id = prh.resource_id
        JOIN remi.regulator_managed_information_aud a ON a.resource_id = prg.remi_resource_id
        WHERE prh.ytf_reporting_area IS NOT NULL
        GROUP BY prh.ytf_reporting_area, prg.remi_resource_id, prg.legacy_resource_id, a.rev, a.id
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
            WHEN 'APB' THEN 'SOUTHERN_NORTH_SEA'
        END
        INTO geographic_area;

        UPDATE remi.regulator_managed_information
        SET geographical_area = geographic_area
        WHERE resource_id = geographic_area_rec.remi_resource_id;

        INSERT INTO remi.audit_revisions(
            updated_at,
            updated_by_user_id
        ) VALUES (
             now(),
             1
         ) RETURNING rev INTO offshore_rmi_audit_rev;

        INSERT INTO remi.regulator_managed_information_aud (
            rev,
            revtype,
            id,
            resource_id,
            responsible_team,
            geographical_area
        ) VALUES (
            offshore_rmi_audit_rev,
            1,
            geographic_area_rec.rmi_id,
            geographic_area_rec.remi_resource_id,
            'EXPLORATION',
            geographic_area
        );

    END LOOP;

END;

$$;

DO $$
DECLARE

    onshore_area_rec RECORD;
    onshore_rmi_audit_rev INTEGER;

BEGIN

    FOR onshore_area_rec IN (
        SELECT rmi.resource_id, rmi.id rmi_id
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

        INSERT INTO remi.audit_revisions(
            updated_at,
            updated_by_user_id
        ) VALUES (
             now(),
             1
         ) RETURNING rev INTO onshore_rmi_audit_rev;

        INSERT INTO remi.regulator_managed_information_aud (
            rev,
            revtype,
            id,
            resource_id,
            responsible_team,
            geographical_area
        ) VALUES (
             onshore_rmi_audit_rev,
             1,
             onshore_area_rec.rmi_id,
             onshore_area_rec.resource_id,
             'EXPLORATION',
             'ONSHORE'
         );

    END LOOP;

END;

$$;