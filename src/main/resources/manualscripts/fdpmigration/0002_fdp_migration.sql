-- FDPs
DO $$
DECLARE

    fdp_rec RECORD;
    remi_submission_id UUID;
    remi_submission_detail_id UUID;

BEGIN

    FOR fdp_rec IN (
        SELECT *
        FROM remi.fdp_migration fm
        JOIN remi.fields f ON f.field_identifier = fm.field_id
    )
    LOOP

        INSERT INTO remi.submissions (id, resource_id)
        VALUES (
          gen_random_uuid(),
          fdp_rec.resource_id
        ) RETURNING id INTO remi_submission_id;

        INSERT INTO remi.submission_details (
            id,
            created_timestamp,
            created_by_wua_id,
            status,
            submission_type,
            version_number,
            submitted_timestamp,
            accepted_timestamp,
            submitted_by_wua_id,
            accepted_by_wua_id,
            submitter_type,
            submission_id
        ) VALUES (
            gen_random_uuid(),
            TO_DATE(fdp_rec.date, 'YYYY/MM/DD HH24:MI:SS'),
            1,
            'ACCEPTED',
            'CREATE_FDP',
            1,
            TO_DATE(fdp_rec.date, 'YYYY/MM/DD HH24:MI:SS'),
            TO_DATE(fdp_rec.date, 'YYYY/MM/DD HH24:MI:SS'),
            1,
            1,
            'REGULATOR',
            remi_submission_id
        ) RETURNING id INTO remi_submission_detail_id;

        INSERT INTO remi.general_details (id, submission_detail_id, fdp_title, fdp_date)
        VALUES (gen_random_uuid(), remi_submission_detail_id, fdp_rec.title, TO_DATE(fdp_rec.date, 'YYYY-MM-DD'));

    END LOOP;

END;
$$;

-- FDPAs
DO $$
DECLARE

    fdpa_rec RECORD;
    remi_submission_id UUID;
    remi_submission_detail_id UUID;

BEGIN

    FOR fdpa_rec IN (
        SELECT *
        FROM remi.fdpa_migration fm
        JOIN remi.fields f ON f.field_identifier = fm.field_id
        WHERE fm.date IS NOT NULL
        ORDER BY f.field_identifier, TO_DATE(fm.date, 'YYYY-MM-DD'), fm.position
    )
    LOOP

        INSERT INTO remi.submissions (id, resource_id)
        VALUES (
          gen_random_uuid(),
          fdpa_rec.resource_id
        ) RETURNING id INTO remi_submission_id;

        INSERT INTO remi.submission_details (
            id,
            created_timestamp,
            created_by_wua_id,
            status,
            submission_type,
            version_number,
            submitted_timestamp,
            accepted_timestamp,
            submitted_by_wua_id,
            accepted_by_wua_id,
            submitter_type,
            submission_id
        ) VALUES (
            gen_random_uuid(),
            TO_DATE(fdpa_rec.date, 'YYYY/MM/DD HH24:MI:SS'),
            1,
            'ACCEPTED',
            'CREATE_FDPA',
            1,
            TO_DATE(fdpa_rec.date, 'YYYY/MM/DD HH24:MI:SS'),
            TO_DATE(fdpa_rec.date, 'YYYY/MM/DD HH24:MI:SS'),
            1,
            1,
            'REGULATOR',
            remi_submission_id
        ) RETURNING id INTO remi_submission_detail_id;

        INSERT INTO remi.general_details (id, submission_detail_id, fdp_title, fdp_date)
        VALUES (gen_random_uuid(), remi_submission_detail_id, fdpa_rec.title, TO_DATE(fdpa_rec.date, 'YYYY-MM-DD'));

    END LOOP;

END;
$$;