DO $$
    DECLARE

        cop_project_rec RECORD;
        field_id UUID;
        project_id UUID;

    BEGIN

        FOR cop_project_rec IN (
            SELECT
                ptccv.project_name,
                ptccv.field_name,
                ptccv.estimated_final_production_date
            FROM remi.project_tracker_cop_current_view ptccv
        )
            LOOP

                field_id := null;
                project_id := null;

                SELECT field_identifier
                INTO field_id
                FROM remi.fields f
                WHERE f.corrected_legal_field_name = cop_project_rec.field_name;

                IF field_id IS NULL THEN

                    INSERT INTO remi.project_tracker_migration_log (
                        project_name,
                        field_name,
                        migration_timestamp,
                        status,
                        failure_reason
                    ) VALUES (
                        cop_project_rec.project_name,
                        cop_project_rec.field_name,
                        now(),
                        'FAILED',
                        'Field name does not exist in ReMI'
                    );

                    CONTINUE;

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
                    cop_project_rec.project_name,
                    'COP',
                    NULL,
                    NULL,
                    NULL,
                    NULL,
                    NULL,
                    'EXTANT'
                ) RETURNING id INTO project_id;

                IF cop_project_rec.estimated_final_production_date IS NOT NULL THEN

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
                        'COP',
                        NULL,
                        cop_project_rec.estimated_final_production_date,
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
                    cop_project_rec.project_name,
                    cop_project_rec.field_name,
                    now(),
                    'COMPLETED',
                    NULL
                );

        END LOOP;
    END
$$;