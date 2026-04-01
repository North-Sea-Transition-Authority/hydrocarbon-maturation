-- Remove decimal place from years
UPDATE remi.pars_undeveloped_discoveries
SET year_volumetric_information_relevant_to = REPLACE(year_volumetric_information_relevant_to, '.0', '');

-- Unmatched resource names
DO $$
DECLARE

    pars_rec RECORD;

BEGIN

    FOR pars_rec IN (
        SELECT pud.remi_resource_name, pud.pars_resource_name
        FROM remi.pars_undeveloped_discoveries pud
        WHERE NOT EXISTS(
            SELECT 1
            FROM remi.resource_details rd
            JOIN remi.submission_details sd ON sd.id = rd.submission_detail_id
            JOIN remi.submissions s ON s.id = sd.submission_id
            JOIN remi.resources r ON r.id = s.resource_id
            WHERE r.type = 'DISCOVERY'
            AND rd.resource_name = pud.remi_resource_name
        )
    )
    LOOP

        INSERT INTO remi.pars_migration_log (
            remi_resource_id,
            pars_resource_name,
            remi_resource_name,
            migration_timestamp,
            status,
            failure_reason
        ) VALUES (
            null,
            pars_rec.pars_resource_name,
            pars_rec.remi_resource_name,
             now(),
             'FAILED',
             'Resource name does not match a discovery resource in ReMI'
         );

    END LOOP;

END;
$$;


-- PARs submissions -> remi resources
DO $$
DECLARE

    pars_rec RECORD;

BEGIN

    FOR pars_rec IN (
        SELECT pud.remi_resource_name, sd.id sd_id, r.id r_id, COUNT(*) cnt
        FROM remi.pars_undeveloped_discoveries pud
        JOIN remi.resource_details rd ON rd.resource_name = pud.remi_resource_name
        JOIN remi.submission_details sd ON sd.id = rd.submission_detail_id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.latest_submission_detail_view v ON v.latest_submission_detail_id = sd.id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE r.type = 'DISCOVERY'
        AND prdh.stage_name = 'PARs'
        AND pud.remi_resource_id IS NULL
        GROUP BY pud.remi_resource_name, sd.id, r.id
    )
    LOOP

        IF pars_rec.cnt = 1 THEN

            UPDATE remi.pars_undeveloped_discoveries pud2
            SET remi_resource_id = pars_rec.r_id
            WHERE pud2.remi_resource_name = pars_rec.remi_resource_name;

        END IF;

    END LOOP;
END;
$$;

-- Not listed as PARs, but have a single discovery match
DO $$
DECLARE

    pars_rec RECORD;

BEGIN

    FOR pars_rec IN (
        WITH pud_counts AS (
            SELECT pud.remi_resource_name, sd.id sd_id, r.id r_id, COUNT(*) cnt
            FROM remi.pars_undeveloped_discoveries pud
            JOIN remi.resource_details rd ON rd.resource_name = pud.remi_resource_name
            JOIN remi.submission_details sd ON sd.id = rd.submission_detail_id
            JOIN remi.submissions s ON s.id = sd.submission_id
            JOIN remi.resources r ON r.id = s.resource_id
            LEFT JOIN remi.pars_migration_log pml ON pml.pars_resource_name = pud.pars_resource_name AND pml.status != 'FAILED'
            WHERE r.type = 'DISCOVERY'
            AND pud.remi_resource_id IS NULL
            GROUP BY pud.remi_resource_name, sd.id, r.id
        )
        , agg AS (
            SELECT pc.remi_resource_name, COUNT(*) cnt
            FROM pud_counts pc
            WHERE pc.cnt = 1
            GROUP BY pc.remi_resource_name
        )
        SELECT pc.remi_resource_name, pc.r_id
        FROM agg a
        JOIN pud_counts pc ON pc.remi_resource_name = a.remi_resource_name
        WHERE a.cnt = 1
    )
        LOOP

            UPDATE remi.pars_undeveloped_discoveries pud2
            SET remi_resource_id = pars_rec.r_id
            WHERE pud2.remi_resource_name = pars_rec.remi_resource_name;

        END LOOP;
END;
$$;

-- With multiple discovery matches, set to earliest discovery with matching name
DO $$
DECLARE

    pars_rec RECORD;

BEGIN
    FOR pars_rec IN (
        WITH ordered_submissions AS (
            SELECT
                ROW_NUMBER() OVER (PARTITION BY pud.remi_resource_name ORDER BY sd.created_timestamp, active DESC) row_no,
                pud.remi_resource_name,
                r.id r_id
            FROM remi.pars_undeveloped_discoveries pud
            JOIN remi.resource_details rd ON rd.resource_name = pud.remi_resource_name
            JOIN remi.submission_details sd ON sd.id = rd.submission_detail_id
            JOIN remi.submissions s ON s.id = sd.submission_id
            JOIN remi.resources r ON r.id = s.resource_id
            LEFT JOIN remi.general_details gd ON gd.submission_detail_id = sd.id
            WHERE r.type = 'DISCOVERY'
            AND pud.remi_resource_id IS NULL
            AND EXTRACT(YEAR FROM sd.created_timestamp)::TEXT >= pud.year_volumetric_information_relevant_to
            GROUP BY
                pud.remi_resource_name,
                sd.created_timestamp,
                r.id
        )
         SELECT os.remi_resource_name, os.r_id
         FROM ordered_submissions os
         WHERE os.row_no = 1
    )
    LOOP

        UPDATE remi.pars_undeveloped_discoveries pud2
        SET remi_resource_id = pars_rec.r_id
        WHERE pud2.remi_resource_name = pars_rec.remi_resource_name;

    END LOOP;

END;
$$;