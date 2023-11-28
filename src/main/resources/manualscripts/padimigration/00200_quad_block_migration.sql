DO $$
DECLARE

    quad_block_rec RECORD;
    centroid_rec RECORD;

BEGIN

    FOR quad_block_rec IN (
        WITH block_split AS (
            SELECT
                gd.id gd_id,
                unnest(string_to_array(pqbm.block_polygon_match, ',')) quad_block,
                pqbm.padi_resource_id,
                pqbm.shore_type
            FROM remi.padi_quad_block_mapping pqbm
            JOIN remi.padi_res_data_header prdh ON prdh.resource_id = pqbm.padi_resource_id
            JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
            JOIN remi.general_details gd ON gd.submission_detail_id = sd.id
            WHERE pqbm.block_polygon_match IS NOT NULL
        )
        , grouped_quad_blocks AS (
            SELECT bs.gd_id, bs.quad_block, bs.padi_resource_id, bs.shore_type
            FROM block_split bs
            GROUP BY bs.gd_id, bs.quad_block, bs.padi_resource_id, bs.shore_type
        )
        SELECT
            gqb.gd_id,
            gqb.quad_block,
            CASE
                WHEN gqb.quad_block LIKE '%/%' THEN substring(gqb.quad_block FROM '^.*(?=(/))') -- if contains a slash then get the quadrant from before the slash
                ELSE substring(gqb.quad_block FROM '^[A-Z]+') -- otherwise get the quadrant from the letters in the quad/block
            END quadrant,
            CASE
                WHEN gqb.quad_block LIKE '%/%' THEN substring(gqb.quad_block FROM '[^/]*$') -- if contains slash then get the block from after the slash
                ELSE substring(gqb.quad_block FROM '[^A-Z]*$') -- otherwise get the block from any character which appear after the letters in the quad/block
            END block,
            gqb.shore_type
        FROM grouped_quad_blocks gqb
    )
    LOOP

        INSERT INTO remi.general_details_quadrant_blocks (
            id,
            general_details_id,
            quadrant_block_reference,
            quadrant,
            block,
            onshore_offshore
        ) VALUES (
            gen_random_uuid(),
            quad_block_rec.gd_id,
            quad_block_rec.quad_block,
            quad_block_rec.quadrant,
            quad_block_rec.block::INT,
            quad_block_rec.shore_type
        );

    END LOOP;

    FOR centroid_rec IN (
        WITH block_split AS (
            SELECT
                tp.id tp_id,
                pqbm.block_centroid_match centroid,
                pqbm.padi_resource_id
            FROM remi.padi_quad_block_mapping pqbm
            JOIN remi.padi_res_data_header prdh ON prdh.resource_id = pqbm.padi_resource_id
            JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
            JOIN remi.trap_properties tp ON tp.submission_detail_id = sd.id
            WHERE pqbm.block_centroid_match IS NOT NULL
            AND pqbm.block_centroid_match NOT LIKE '%,%' -- can't migrate onshore centroids which have multiple quad/blocks after the GIS to PEARS block mapping as we don't know which is the true centre of interest
        )
        , grouped_quad_blocks AS (
            SELECT bs.tp_id, bs.centroid, bs.padi_resource_id
            FROM block_split bs
            GROUP BY bs.tp_id, bs.centroid, bs.padi_resource_id
        )
        SELECT
            gqb.tp_id,
            gqb.centroid
        FROM grouped_quad_blocks gqb
    )
    LOOP

        UPDATE remi.trap_properties
        SET centre_of_interest_quadrant_block = centroid_rec.centroid
        WHERE id = centroid_rec.tp_id;

    END LOOP;

END;

$$;