DO $$
DECLARE

    resources RECORD;

BEGIN

    FOR resources IN (
        SELECT r.id
        FROM remi.resources r
    )
    LOOP

        INSERT INTO remi.responsible_teams (id, resource_id, responsible_team)
        VALUES (gen_random_uuid(), resources.id, 'EXPLORATION');

    END LOOP;

END;

$$;