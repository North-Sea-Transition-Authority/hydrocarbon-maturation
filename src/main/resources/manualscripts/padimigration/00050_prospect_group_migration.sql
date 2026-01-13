DO $$
    DECLARE

        prospect_group_rec RECORD;
        group_id UUID;
        group_audit_rev INTEGER;
        group_link_audit_rev INTEGER;
        resource_group_link_id UUID;
        ungrouped_prospect_rec RECORD;
        new_group_id UUID;
        new_group_audit_rev INTEGER;
        new_group_link_id UUID;
        new_group_link_audit_rev INTEGER;

BEGIN

        -- Get all prospects which have linked prospect resources based on the legacy resource ID
        FOR prospect_group_rec IN (
            SELECT prg.legacy_resource_id, prg.remi_resource_id
            FROM remi.padi_resource_groupings prg
            JOIN remi.resources r ON r.id = prg.remi_resource_id
            JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prg.legacy_res_data_id
            WHERE r.type = 'PROSPECT'
            AND EXISTS (
                SELECT 1
                FROM remi.padi_resource_groupings prg2
                JOIN remi.resources r2 ON r2.id = prg2.remi_resource_id
                WHERE prg2.legacy_resource_id = prg.legacy_resource_id
                AND prg2.remi_resource_id != prg.remi_resource_id
                AND r2.type = 'PROSPECT'
            )
            GROUP BY prg.legacy_resource_id, prg.remi_resource_id
            ORDER BY prg.legacy_resource_id, prg.remi_resource_id
        )
        LOOP

            -- Get the existing group ID if one exists, else create one
            SELECT rgl.resource_group_id
            INTO group_id
            FROM remi.resource_group_links rgl
            JOIN remi.resources r ON r.id = rgl.resource_id
            WHERE rgl.legacy_resource_id = prospect_group_rec.legacy_resource_id
            AND r.type = 'PROSPECT';

            IF group_id IS NULL THEN

                group_id := gen_random_uuid();

                INSERT INTO remi.resource_groups (id)
                VALUES (group_id);

                INSERT INTO remi.audit_revisions(
                    updated_at,
                    updated_by_user_id
                ) VALUES (
                    now(),
                  1
                ) RETURNING rev INTO group_audit_rev;

                INSERT INTO remi.resource_groups_aud (rev, revtype, id)
                VALUES (group_audit_rev, 0, group_id);

            END IF;

            -- Create group links
            INSERT INTO remi.resource_group_links (
                id,
                resource_group_id,
                resource_id,
                legacy_resource_id
            ) VALUES (
                gen_random_uuid(),
                group_id,
                prospect_group_rec.remi_resource_id,
                prospect_group_rec.legacy_resource_id
            ) RETURNING id INTO resource_group_link_id;

            INSERT INTO remi.audit_revisions(
                updated_at,
                updated_by_user_id
            ) VALUES (
                 now(),
                 1
             ) RETURNING rev INTO group_link_audit_rev;

            INSERT INTO remi.resource_group_links_aud (
               rev,
               revtype,
               id,
               resource_id,
               legacy_resource_id,
               resource_group_id
            ) VALUES (
                group_link_audit_rev,
                0,
                resource_group_link_id,
                prospect_group_rec.remi_resource_id,
                prospect_group_rec.legacy_resource_id,
                group_id
            );

        END LOOP;

        FOR ungrouped_prospect_rec IN (
            SELECT prg.legacy_resource_id, prg.remi_resource_id
            FROM remi.padi_resource_groupings prg
            JOIN remi.resources r ON r.id = prg.remi_resource_id
            WHERE r.type = 'PROSPECT'
            AND r.id NOT IN (
                SELECT rgl.resource_id
                FROM remi.resource_group_links rgl
            )
            GROUP BY prg.legacy_resource_id, prg.remi_resource_id
        )
        LOOP

            INSERT INTO remi.resource_groups (id)
            VALUES (gen_random_uuid())
            RETURNING id INTO new_group_id;

            INSERT INTO remi.audit_revisions(
                updated_at,
                updated_by_user_id
            ) VALUES (
                now(),
                1
            ) RETURNING rev INTO new_group_audit_rev;

            INSERT INTO remi.resource_groups_aud (rev, revtype, id)
            VALUES (new_group_audit_rev, 0, new_group_id);

            -- Create group links
            INSERT INTO remi.resource_group_links (
                id,
                resource_group_id,
                resource_id,
                legacy_resource_id
            ) VALUES (
                gen_random_uuid(),
                new_group_id,
                ungrouped_prospect_rec.remi_resource_id,
                ungrouped_prospect_rec.legacy_resource_id
            ) RETURNING id INTO new_group_link_id;

            INSERT INTO remi.audit_revisions(
                updated_at,
                updated_by_user_id
            ) VALUES (
                now(),
                1
            ) RETURNING rev INTO new_group_link_audit_rev;

            INSERT INTO remi.resource_group_links_aud (
                rev,
                revtype,
                id,
                resource_id,
                legacy_resource_id,
                resource_group_id
            ) VALUES (
            new_group_link_audit_rev,
            0,
            new_group_link_id,
            ungrouped_prospect_rec.remi_resource_id,
            ungrouped_prospect_rec.legacy_resource_id,
            new_group_id
            );

        END LOOP;

END;
$$;