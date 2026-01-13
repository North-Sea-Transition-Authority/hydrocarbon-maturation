DO $$
DECLARE

    resources RECORD;
    rmi_id UUID;
    rmi_audit_rev INTEGER;

BEGIN

    FOR resources IN (
        SELECT r.id
        FROM remi.resources r
    )
    LOOP

        INSERT INTO remi.regulator_managed_information (id, resource_id, responsible_team)
        VALUES (gen_random_uuid(), resources.id, 'EXPLORATION')
        RETURNING id INTO rmi_id;

        INSERT INTO remi.audit_revisions(
            updated_at,
            updated_by_user_id
        ) VALUES (
             now(),
             1
         ) RETURNING rev INTO rmi_audit_rev;

        INSERT INTO remi.regulator_managed_information_aud (rev, revtype, id, resource_id, responsible_team)
        VALUES (rmi_audit_rev, 0, rmi_id, resources.id, 'EXPLORATION');

    END LOOP;

END;

$$;