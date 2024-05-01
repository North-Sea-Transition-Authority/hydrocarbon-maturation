INSERT INTO remi.teams (id, type, name)
VALUES (gen_random_uuid(), 'NORTHERN_OFFSHORE_AREA', 'Northern North Sea and West of Shetland');

INSERT INTO remi.teams (id, type, name)
VALUES (gen_random_uuid(), 'SOUTHERN_OFFSHORE_AREA', 'Central North Sea, Southern North Sea and East Irish Sea');

INSERT INTO remi.teams (id, type, name)
VALUES (gen_random_uuid(), 'ONSHORE_AREA', 'Onshore area');

DO $$
DECLARE

    industry_rec RECORD;

BEGIN

    FOR industry_rec IN (
        SELECT t.display_name, t.organisation_group_id
        FROM remi.legacy_teams t
        WHERE t.type = 'INDUSTRY'
    )
    LOOP

        INSERT INTO remi.teams(id, type, name, scope_type, scope_id)
        VALUES (gen_random_uuid(), 'INDUSTRY', industry_rec.display_name, 'ORGGRP', industry_rec.organisation_group_id);

    END LOOP;

END;
$$;

DO $$
DECLARE

    legacy_team_id UUID;
    regulator_team_id UUID;
    regulator_admins RECORD;
    organisation_managers RECORD;
    exploration_users RECORD;
    regulator_viewers RECORD;
    reserves_advisors RECORD;
    project_analysts RECORD;

BEGIN

    SELECT t.uuid
    INTO legacy_team_id
    FROM remi.legacy_teams t
    WHERE t.type = 'REGULATOR';

    SELECT t.id
    INTO regulator_team_id
    FROM remi.teams t
    WHERE t.type = 'REGULATOR';

    FOR regulator_admins IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'ACCESS_MANAGER'
        AND tmr.team_id = legacy_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), regulator_team_id, 'MANAGE_TEAM', regulator_admins.wua_id);

    END LOOP;

    FOR organisation_managers IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'INDUSTRY_ACCESS_MANAGER'
        AND tmr.team_id = legacy_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), regulator_team_id, 'REGULATOR_MANAGE_ALL_ORGANISATIONS', organisation_managers.wua_id);

    END LOOP;

    FOR exploration_users IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'EXPLORATION'
        AND tmr.team_id = legacy_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), regulator_team_id, 'EXPLORATION', exploration_users.wua_id);

    END LOOP;

    FOR regulator_viewers IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'VIEWER'
        AND tmr.team_id = legacy_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), regulator_team_id, 'VIEW_ANY_RESOURCE', regulator_viewers.wua_id);

    END LOOP;

    FOR reserves_advisors IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'RESERVES_AND_RESOURCES_ADVISOR'
        AND tmr.team_id = legacy_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), regulator_team_id, 'RESERVES_AND_RESOURCES_ADVISOR', reserves_advisors.wua_id);

    END LOOP;

    FOR project_analysts IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'PROJECT_ANALYST'
        AND tmr.team_id = legacy_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), regulator_team_id, 'PROJECT_ANALYST', project_analysts.wua_id);

    END LOOP;

END;
$$;

DO $$
DECLARE

    legacy_northern_team_id UUID;
    new_northern_team_id UUID;
    legacy_southern_team_id UUID;
    new_southern_team_id UUID;
    legacy_onshore_team_id UUID;
    new_onshore_team_id UUID;
    northern_admins RECORD;
    southern_admins RECORD;
    onshore_admins RECORD;
    northern_members RECORD;
    southern_members RECORD;
    onshore_members RECORD;

BEGIN

    SELECT t.uuid
    INTO legacy_northern_team_id
    FROM remi.legacy_teams t
    WHERE t.type = 'NORTHERN_OFFSHORE_AREA';

    SELECT t.id
    INTO new_northern_team_id
    FROM remi.teams t
    WHERE t.type = 'NORTHERN_OFFSHORE_AREA';

    SELECT t.uuid
    INTO legacy_southern_team_id
    FROM remi.legacy_teams t
    WHERE t.type = 'SOUTHERN_OFFSHORE_AREA';

    SELECT t.id
    INTO new_southern_team_id
    FROM remi.teams t
    WHERE t.type = 'SOUTHERN_OFFSHORE_AREA';

    SELECT t.uuid
    INTO legacy_onshore_team_id
    FROM remi.legacy_teams t
    WHERE t.type = 'ONSHORE_AREA';

    SELECT t.id
    INTO new_onshore_team_id
    FROM remi.teams t
    WHERE t.type = 'ONSHORE_AREA';

    FOR northern_admins IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'NORTHERN_OFFSHORE_AREA_MANAGE_TEAM'
        AND tmr.team_id = legacy_northern_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), new_northern_team_id, 'MANAGE_TEAM', northern_admins.wua_id);

    END LOOP;

    FOR northern_members IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'NORTHERN_OFFSHORE_AREA_TEAM_MEMBER'
        AND tmr.team_id = legacy_northern_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), new_northern_team_id, 'AREA_TEAM_MEMBER', northern_members.wua_id);

    END LOOP;

    FOR southern_admins IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'SOUTHERN_OFFSHORE_AREA_MANAGE_TEAM'
        AND tmr.team_id = legacy_southern_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), new_southern_team_id, 'MANAGE_TEAM', southern_admins.wua_id);

    END LOOP;

    FOR southern_members IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER'
        AND tmr.team_id = legacy_southern_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), new_southern_team_id, 'AREA_TEAM_MEMBER', southern_members.wua_id);

    END LOOP;

    FOR onshore_admins IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'ONSHORE_AREA_MANAGE_TEAM'
        AND tmr.team_id = legacy_onshore_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), new_onshore_team_id, 'MANAGE_TEAM', onshore_admins.wua_id);

    END LOOP;

    FOR onshore_members IN (
        SELECT tmr.wua_id
        FROM remi.team_member_roles tmr
        WHERE tmr.role = 'ONSHORE_AREA_TEAM_MEMBER'
        AND tmr.team_id = legacy_onshore_team_id
    )
    LOOP

        INSERT INTO remi.team_roles(id, team_id, role, wua_id)
        VALUES (gen_random_uuid(), new_onshore_team_id, 'AREA_TEAM_MEMBER', onshore_members.wua_id);

    END LOOP;

END;
$$;

DO $$
DECLARE

    industry_rec RECORD;

BEGIN

    FOR industry_rec IN (
        SELECT tmr.team_id, tmr.wua_id, tmr.role, t.id new_team_id, t.scope_id, t.scope_type
        FROM remi.team_member_roles tmr
        JOIN remi.legacy_teams lt ON lt.uuid = tmr.team_id
        JOIN remi.teams t ON t.scope_id::INTEGER = lt.organisation_group_id
        WHERE t.type = 'INDUSTRY'
    )
    LOOP

        IF industry_rec.role = 'ACCESS_MANAGER' THEN

            INSERT INTO remi.team_roles(id, team_id, role, wua_id)
            VALUES (gen_random_uuid(), industry_rec.new_team_id, 'MANAGE_TEAM', industry_rec.wua_id);

        END IF;

        IF industry_rec.role = 'SUBMITTER' THEN

            INSERT INTO remi.team_roles(id, team_id, role, wua_id)
            VALUES (gen_random_uuid(), industry_rec.new_team_id, 'INDUSTRY_SUBMIT', industry_rec.wua_id);

        END IF;

        IF industry_rec.role = 'VIEWER' THEN

            INSERT INTO remi.team_roles(id, team_id, role, wua_id)
            VALUES (gen_random_uuid(), industry_rec.new_team_id, 'INDUSTRY_VIEWER', industry_rec.wua_id);

        END IF;

    END LOOP;

END;
$$;