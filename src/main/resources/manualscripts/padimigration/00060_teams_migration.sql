DO $$
DECLARE

    org_groups RECORD;
    team_audit_rev INTEGER;
    team_id UUID;

BEGIN

    FOR org_groups IN (
        SELECT pom.org_grp_id, pom.org_grp_name
        FROM remi.padi_operator_mapping pom
        WHERE pom.org_grp_id IS NOT NULL
        AND NOT EXISTS (
            SELECT 1
            FROM remi.teams t
            WHERE t.scope_id::integer = pom.org_grp_id
        )
        GROUP BY pom.org_grp_id, pom.org_grp_name
    )
    LOOP

        INSERT INTO remi.teams(id, type, name, scope_type, scope_id)
        VALUES (gen_random_uuid(), 'INDUSTRY', org_groups.org_grp_name, 'ORGGRP', org_groups.org_grp_id)
        RETURNING id INTO team_id;

        INSERT INTO remi.audit_revisions(
            updated_at,
            updated_by_user_id
        ) VALUES (
             now(),
             1
         ) RETURNING rev INTO team_audit_rev;

        INSERT INTO remi.teams_aud (rev, revtype, id, type, name, scope_type, scope_id)
        VALUES (team_audit_rev, 0, team_id, 'INDUSTRY', org_groups.org_grp_name, 'ORGGRP', org_groups.org_grp_id);

    END LOOP;

END;
$$;