DO $$
DECLARE

    project_rec RECORD;
    field_id UUID;
    project_id UUID;

BEGIN

    FOR project_rec IN (
        SELECT
        ptcv.project_name,
        ptcv.field_name,
        ptcv.project_status,
        ptcv.project_type,
        ptcv.estimated_capex,
        ptcv.expected_first_draft_csr_received_date,
        ptcv.expected_first_draft_fdp_received_date,
        ptcv.expected_production_date
        FROM remi.project_tracker_current_view ptcv
    )
    LOOP

        field_id := null;
        project_id := null;

        SELECT f.field_identifier
        INTO field_id
        FROM remi.fields f
        WHERE f.corrected_legal_field_name = project_rec.field_name;

        IF field_id IS NULL THEN

            INSERT INTO remi.project_tracker_migration_log (
                project_name,
                field_name,
                migration_timestamp,
                status,
                failure_reason
            ) VALUES (
                project_rec.project_name,
                project_rec.field_name,
                now(),
                'FAILED',
                'Field name does not exist in ReMI'
            );

            CONTINUE;

        END IF;

        IF project_rec.project_status IS NOT NULL THEN

            IF upper(project_rec.project_status) NOT IN (
                'ASSESSMENT',
                'AUTHORISE',
                'EXECUTE',
                'ON HOLD',
                'WITHDRAWN'
            ) THEN

                INSERT INTO remi.project_tracker_migration_log (
                    project_name,
                    field_name,
                    migration_timestamp,
                    status,
                    failure_reason
                ) VALUES (
                    project_rec.project_name,
                    project_rec.field_name,
                    now(),
                    'FAILED',
                    'Invalid project status'
                );

                CONTINUE;

            END IF;

        END IF;

        IF project_rec.project_type IS NOT NULL THEN

            IF upper(project_rec.project_type) NOT IN ('FDP', 'FDPA') THEN

                INSERT INTO remi.project_tracker_migration_log (
                    project_name,
                    field_name,
                    migration_timestamp,
                    status,
                    failure_reason
                ) VALUES (
                    project_rec.project_name,
                    project_rec.field_name,
                    now(),
                    'FAILED',
                    'Invalid project type'
                );

                CONTINUE;

            END IF;

        END IF;

        INSERT INTO remi.projects (
           id,
           field_identifier,
           project_name,
           project_type,
           project_phase,
           is_part_of_pipeline,
           submission_detail_id,
           created_timestamp,
           fdpa_start_up_date,
           project_status
        ) VALUES (
            gen_random_uuid(),
            field_id,
            project_rec.project_name,
            project_rec.project_type,
            project_rec.project_status,
            NULL,
            NULL,
            NULL,
            NULL,
            'EXTANT'
        ) RETURNING id INTO project_id;

        IF project_rec.estimated_capex IS NOT NULL THEN

            INSERT INTO remi.project_capex (
                id,
                project_id,
                date_provided,
                total_capex,
                source_of_information,
                project_capex_status
            ) VALUES (
                gen_random_uuid(),
                project_id,
                NULL,
                project_rec.estimated_capex,
                NULL,
                'CREATED'
            );

        END IF;

        IF project_rec.expected_first_draft_csr_received_date IS NOT NULL THEN

            INSERT INTO remi.project_submission_estimates (
                id,
                project_id,
                submission_estimate_type,
                earliest_date,
                most_likely_date,
                latest_date,
                date_when_estimated,
                submission_estimate_status
            ) VALUES (
                gen_random_uuid(),
                project_id,
                'CSR',
                NULL,
                project_rec.expected_first_draft_csr_received_date,
                NULL,
                NULL,
                'CREATED'
            );

        END IF;

        IF project_rec.expected_first_draft_fdp_received_date IS NOT NULL THEN

            INSERT INTO remi.project_submission_estimates (
                id,
                project_id,
                submission_estimate_type,
                earliest_date,
                most_likely_date,
                latest_date,
                date_when_estimated,
                submission_estimate_status
            ) VALUES (
                gen_random_uuid(),
                project_id,
                'FDP',
                NULL,
                project_rec.expected_first_draft_fdp_received_date,
                NULL,
                NULL,
                'CREATED'
            );

        END IF;

        IF project_rec.expected_production_date IS NOT NULL THEN

            INSERT INTO remi.project_submission_estimates (
                id,
                project_id,
                submission_estimate_type,
                earliest_date,
                most_likely_date,
                latest_date,
                date_when_estimated,
                submission_estimate_status
            ) VALUES (
                gen_random_uuid(),
                project_id,
                'FIRST_PRODUCTION',
                NULL,
                project_rec.expected_production_date,
                NULL,
                NULL,
                'CREATED'
            );

        END IF;

        INSERT INTO remi.project_tracker_migration_log (
            project_name,
            field_name,
            migration_timestamp,
            status,
            failure_reason
        ) VALUES (
            project_rec.project_name,
            project_rec.field_name,
            now(),
            'COMPLETED',
            NULL
        );

    END LOOP;
END
$$;