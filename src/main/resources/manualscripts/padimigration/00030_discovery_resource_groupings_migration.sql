DO $$
DECLARE

    multi_reservoir_rec RECORD;
    discovery_rec RECORD;
    resource_rec RECORD;
    remi_res_id UUID;

BEGIN

    -- Set legacy resdat_id based on name, resource id, and reference as not provided in data extract for multi reservoirs
    FOR multi_reservoir_rec IN (
        SELECT prdh.resdat_id, prdh.resource_id, pmrd.resource_stage_name, pmrd.reference
        FROM remi.padi_multi_reservoir_discoveries pmrd
        JOIN remi.padi_res_data_header prdh ON prdh.name_alternative = pmrd.resource_stage_name AND pmrd.resource_id = prdh.resource_id AND prdh.reference = pmrd.reference
        GROUP BY prdh.resdat_id, prdh.resource_id, pmrd.resource_stage_name, pmrd.reference
    )
    LOOP

        UPDATE remi.padi_multi_reservoir_discoveries
        SET resdat_id = multi_reservoir_rec.resdat_id
        WHERE resource_id = multi_reservoir_rec.resource_id
        AND resource_stage_name = multi_reservoir_rec.resource_stage_name
        AND reference = multi_reservoir_rec.reference;

    END LOOP;

    -- Create discovery groupings based on the PaDI resource and the agreed organisation names
    -- Reliquishments will be created separately for discoveries
    FOR discovery_rec IN (
        SELECT
            prdh.resdat_id,
            prdh.resource_id,
            pom.agreed_match,
            COALESCE(pmrd.resource_stage_name, prdh.name_alternative) remi_resource_name,
            CASE WHEN pmrd.resdat_id IS NOT NULL THEN true ELSE false END multi_reservoir
        FROM remi.padi_res_data_header prdh
        LEFT JOIN remi.padi_operator_mapping pom ON LOWER(pom.padi_name) = LOWER(prdh.company)
        LEFT JOIN remi.padi_multi_reservoir_discoveries pmrd ON pmrd.resdat_id = prdh.resdat_id
        WHERE prdh.opportunity_type = 'Discovery'
        AND prdh.stage_name != 'Relinquishment'
    )
    LOOP

        INSERT INTO remi.padi_resource_groupings (
            id,
            remi_resource_id,
            legacy_resource_id,
            agreed_operator,
            legacy_res_data_id,
            name_alternative,
            resource_type,
            relinquishment,
            multi_reservoir
        ) VALUES (
            gen_random_uuid(),
            NULL,
            discovery_rec.resource_id,
            discovery_rec.agreed_match,
            discovery_rec.resdat_id,
            discovery_rec.remi_resource_name,
            'DISCOVERY',
            false,
            discovery_rec.multi_reservoir
        );

    END LOOP;

    -- Create ReMI discovery resources and link them to the PaDI resource groupings. The groupings will be used to create the submissions later
    FOR resource_rec IN (
        SELECT prg.legacy_resource_id padi_res_id, prg.agreed_operator padi_operator
        FROM remi.padi_resource_groupings prg
        WHERE prg.resource_type = 'DISCOVERY'
        AND prg.relinquishment IS FALSE
        GROUP BY prg.legacy_resource_id, prg.agreed_operator
    )
    LOOP

        INSERT INTO remi.resources (id, type, created_timestamp)
        VALUES (
            gen_random_uuid(),
            'DISCOVERY',
            now()
        ) RETURNING id INTO remi_res_id;

        UPDATE remi.padi_resource_groupings prg
        SET remi_resource_id = remi_res_id
        WHERE prg.legacy_resource_id = resource_rec.padi_res_id
        AND ((prg.agreed_operator = resource_rec.padi_operator) OR (prg.agreed_operator IS NULL AND resource_rec.padi_operator IS NULL))
        AND prg.resource_type = 'DISCOVERY'
        AND prg.relinquishment IS FALSE;

    END LOOP;

END;

$$;