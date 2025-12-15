DO $$
DECLARE

    volumetric_parameters RECORD;
    padi_distribution_type TEXT;
    padi_other_distribution_type TEXT;
    N TEXT := 'N';
    L TEXT := 'L';
    B TEXT := 'B';
    T TEXT := 'T';
    R TEXT := 'R';
    C TEXT := 'C';
    Z TEXT := 'Z';
    NORMAL TEXT := 'NORMAL';
    LOGNORMAL TEXT := 'LOGNORMAL';
    BETA TEXT := 'BETA';
    TRIANGULAR TEXT := 'TRIANGULAR';
    RECTANGULAR TEXT := 'RECTANGULAR';
    CONSTANT TEXT := 'CONSTANT';
    NOT_CALCULATED TEXT := 'NOT_CALCULATED';

BEGIN

    FOR volumetric_parameters IN (
        SELECT prdp.*, LOWER(prdh.hc_case) hydrocarbon_case, sd.id sd_id, sd.submission_type, rsd.id rsd_id
        FROM remi.padi_res_data_param prdp
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdp.resdat_id
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
        WHERE r.type = 'PROSPECT'
    )
    LOOP

        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- depth to crest
        IF COALESCE(
            volumetric_parameters.depth_to_crest_p99,
            volumetric_parameters.depth_to_crest_p90,
            volumetric_parameters.depth_to_crest_mode,
            volumetric_parameters.depth_to_crest_p50,
            volumetric_parameters.depth_to_crest_mean,
            volumetric_parameters.depth_to_crest_p10,
            volumetric_parameters.depth_to_crest_p01
        ) IS NOT NULL THEN

            SELECT
                CASE volumetric_parameters.depth_to_crest_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    END
            INTO padi_distribution_type;

            IF padi_distribution_type IS NULL AND volumetric_parameters.depth_to_crest_dist != Z THEN
                padi_other_distribution_type := volumetric_parameters.depth_to_crest_dist;
            END IF;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                other_distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments
            ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'DEPTH_TO_CREST_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.depth_to_crest_mean) END, -- most values exist in the mean column, but two items exist in the P50 column
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_crest_mean) END,
                 volumetric_parameters.depth_to_crest_comment
             );

        END IF;


        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- depth to spill point
        IF COALESCE(
                volumetric_parameters.depth_to_spill_p99,
                volumetric_parameters.depth_to_spill_p90,
                volumetric_parameters.depth_to_spill_mode,
                volumetric_parameters.depth_to_spill_p50,
                volumetric_parameters.depth_to_spill_mean,
                volumetric_parameters.depth_to_spill_p10,
                volumetric_parameters.depth_to_spill_p01
        ) IS NOT NULL THEN

            SELECT
                CASE volumetric_parameters.depth_to_spill_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    END
            INTO padi_distribution_type;

            IF padi_distribution_type IS NULL AND volumetric_parameters.depth_to_spill_dist != Z THEN
                padi_other_distribution_type := volumetric_parameters.depth_to_spill_dist;
            END IF;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                other_distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments
            ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'DEPTH_TO_SPILL_POINT_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.depth_to_spill_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_spill_mean) END,
                 volumetric_parameters.depth_to_spill_comment
             );

        END IF;

        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- productive area
        IF COALESCE(
            volumetric_parameters.area_p99,
            volumetric_parameters.area_p90,
            volumetric_parameters.area_mode,
            volumetric_parameters.area_p50,
            volumetric_parameters.area_mean,
            volumetric_parameters.area_p10,
            volumetric_parameters.area_p01
        ) IS NOT NULL THEN

            SELECT
                CASE volumetric_parameters.area_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    END
            INTO padi_distribution_type;

            IF padi_distribution_type IS NULL AND volumetric_parameters.area_dist != Z THEN
                padi_other_distribution_type := volumetric_parameters.area_dist;
            END IF;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                other_distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments
            ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'PRODUCTIVE_AREA_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.area_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.area_mean) END,
                 volumetric_parameters.area_comment
             );

        END IF;

        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- RESERVOIR PROPERTIES
        -- Depth to hydrocarbon contact
        IF COALESCE(
            volumetric_parameters.depth_to_water_p99,
            volumetric_parameters.depth_to_water_p90,
            volumetric_parameters.depth_to_water_mode,
            volumetric_parameters.depth_to_water_p50,
            volumetric_parameters.depth_to_water_mean,
            volumetric_parameters.depth_to_water_p10,
            volumetric_parameters.depth_to_water_p01
        ) IS NOT NULL THEN

        SELECT
            CASE volumetric_parameters.depth_to_water_dist
                WHEN N THEN NORMAL
                WHEN L THEN LOGNORMAL
                WHEN B THEN BETA
                WHEN T THEN TRIANGULAR
                WHEN R THEN RECTANGULAR
                WHEN C THEN CONSTANT
                END
        INTO padi_distribution_type;

        IF padi_distribution_type IS NULL AND volumetric_parameters.depth_to_water_dist != Z THEN
            padi_other_distribution_type := volumetric_parameters.depth_to_water_dist;
        END IF;

        INSERT INTO remi.probability_distributions (
            id,
            submission_detail_id,
            distribution_type,
            other_distribution_type,
            instance,
            min_value,
            p99,
            p90,
            mode_value,
            p50,
            mean_value,
            p10,
            p1,
            max_value,
            constant_value,
            most_likely_value,
            comments,
            reservoir_submission_detail_id
        ) VALUES (
             gen_random_uuid(),
             volumetric_parameters.sd_id,
             padi_distribution_type,
             padi_other_distribution_type,
             'DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT',
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_p99) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_p99) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_p90) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_mode) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_p50) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_mean) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_p10) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_p01) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_p01) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.depth_to_water_mean) END,
             CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.depth_to_water_mean) END,
             volumetric_parameters.depth_to_water_comment,
             volumetric_parameters.rsd_id
         );

        END IF;

        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- Gross reservoir thickness
        IF COALESCE(
            volumetric_parameters.gross_thick_p99,
            volumetric_parameters.gross_thick_p90,
            volumetric_parameters.gross_thick_mode,
            volumetric_parameters.gross_thick_p50,
            volumetric_parameters.gross_thick_mean,
            volumetric_parameters.gross_thick_p10,
            volumetric_parameters.gross_thick_p01
        ) IS NOT NULL THEN

            SELECT
                CASE volumetric_parameters.gross_thick_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    END
            INTO padi_distribution_type;

            IF padi_distribution_type IS NULL AND volumetric_parameters.gross_thick_dist != Z THEN
                padi_other_distribution_type := volumetric_parameters.gross_thick_dist;
            END IF;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                other_distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'GROSS_RESERVOIR_THICKNESS_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.gross_thick_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gross_thick_mean) END,
                 volumetric_parameters.gross_thick_comment,
                 volumetric_parameters.rsd_id
             );

        END IF;

        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- Gross rock volume
        IF COALESCE(
            volumetric_parameters.grv_p99,
            volumetric_parameters.grv_p90,
            volumetric_parameters.grv_mode,
            volumetric_parameters.grv_p50,
            volumetric_parameters.grv_mean,
            volumetric_parameters.grv_p10,
            volumetric_parameters.grv_p01
        ) IS NOT NULL THEN

            SELECT
                CASE volumetric_parameters.grv_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    END
            INTO padi_distribution_type;

            IF padi_distribution_type IS NULL AND volumetric_parameters.grv_dist != Z THEN
                padi_other_distribution_type := volumetric_parameters.grv_dist;
            END IF;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                other_distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'GROSS_ROCK_VOLUME_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.grv_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.grv_mean) END,
                 volumetric_parameters.grv_comment,
                 volumetric_parameters.rsd_id
             );

        END IF;

        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- Net-to-gross
        IF COALESCE(
            volumetric_parameters.ntg_p99,
            volumetric_parameters.ntg_p90,
            volumetric_parameters.ntg_mode,
            volumetric_parameters.ntg_p50,
            volumetric_parameters.ntg_mean,
            volumetric_parameters.ntg_p10,
            volumetric_parameters.ntg_p01
        ) IS NOT NULL THEN

            SELECT
                CASE volumetric_parameters.ntg_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    END
            INTO padi_distribution_type;

            IF padi_distribution_type IS NULL AND volumetric_parameters.ntg_dist != Z THEN
                padi_other_distribution_type := volumetric_parameters.ntg_dist;
            END IF;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                other_distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'NET_TO_GROSS_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.ntg_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.ntg_mean) END,
                 volumetric_parameters.ntg_comment,
                 volumetric_parameters.rsd_id
             );

        END IF;

        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- Net porosity
        IF COALESCE(
            volumetric_parameters.porosity_p99,
            volumetric_parameters.porosity_p90,
            volumetric_parameters.porosity_mode,
            volumetric_parameters.porosity_p50,
            volumetric_parameters.porosity_mean,
            volumetric_parameters.porosity_p10,
            volumetric_parameters.porosity_p01
        ) IS NOT NULL THEN

            SELECT
                CASE volumetric_parameters.porosity_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    END
            INTO padi_distribution_type;

            IF padi_distribution_type IS NULL AND volumetric_parameters.porosity_dist != Z THEN
                padi_other_distribution_type := volumetric_parameters.porosity_dist;
            END IF;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                other_distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'NET_POROSITY_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.porosity_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.porosity_mean) END,
                 volumetric_parameters.porosity_comment,
                 volumetric_parameters.rsd_id
             );

        END IF;


        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        -- Average net pay thickness
        IF COALESCE(
            volumetric_parameters.net_pay_p99,
            volumetric_parameters.net_pay_p90,
            volumetric_parameters.net_pay_mode,
            volumetric_parameters.net_pay_p50,
            volumetric_parameters.net_pay_mean,
            volumetric_parameters.net_pay_p10,
            volumetric_parameters.net_pay_p01
        ) IS NOT NULL THEN

            SELECT
                CASE volumetric_parameters.net_pay_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    WHEN Z THEN NOT_CALCULATED
                    END
            INTO padi_distribution_type;

            IF padi_distribution_type IS NULL THEN
                padi_other_distribution_type := volumetric_parameters.net_pay_dist;
            END IF;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
                other_distribution_type,
                instance,
                min_value,
                p99,
                p90,
                mode_value,
                p50,
                mean_value,
                p10,
                p1,
                max_value,
                constant_value,
                most_likely_value,
                comments,
                reservoir_submission_detail_id
            ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 CASE
                     WHEN volumetric_parameters.submission_type = 'POST_DRILL_DRY_HOLE' THEN 'AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL'
                     ELSE 'AVERAGE_NET_PAY_THICKNESS_PROSPECT'
                     END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_p99) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_p99) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_p90) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_mode) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_p50) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_mean) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_p10) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_p01) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.net_pay_mean) END,
                 CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.net_pay_mean) END,
                 volumetric_parameters.net_pay_comment,
                 volumetric_parameters.rsd_id
             );

        END IF;

        IF volumetric_parameters.hydrocarbon_case IN ('gas','condensate','oil') THEN

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Hydrocarbon saturation
            IF COALESCE(
                    volumetric_parameters.hsc_p99,
                    volumetric_parameters.hsc_p90,
                    volumetric_parameters.hsc_mode,
                    volumetric_parameters.hsc_p50,
                    volumetric_parameters.hsc_mean,
                    volumetric_parameters.hsc_p10,
                    volumetric_parameters.hsc_p01
               ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.hsc_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.hsc_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.hsc_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 CASE
                     WHEN volumetric_parameters.hydrocarbon_case IN ('gas', 'condensate') THEN 'GAS_HYDROCARBON_SATURATION_PROSPECT'
                     WHEN volumetric_parameters.hydrocarbon_case = 'oil' THEN 'OIL_HYDROCARBON_SATURATION_PROSPECT'
                 END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.hsc_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mean) END,
                 volumetric_parameters.hsc_comment,
                 volumetric_parameters.rsd_id
             );

            END IF;

        END IF;

        IF volumetric_parameters.hydrocarbon_case = 'mixed oil and gas' THEN

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Hydrocarbon saturation
            IF COALESCE(
                    volumetric_parameters.hsc_p99,
                    volumetric_parameters.hsc_p90,
                    volumetric_parameters.hsc_mode,
                    volumetric_parameters.hsc_p50,
                    volumetric_parameters.hsc_mean,
                    volumetric_parameters.hsc_p10,
                    volumetric_parameters.hsc_p01
               ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.hsc_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.hsc_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.hsc_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'MIXED_OIL_HYDROCARBON_SATURATION_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.hsc_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mean) END,
                 volumetric_parameters.hsc_comment,
                 volumetric_parameters.rsd_id
             );

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                 gen_random_uuid(),
                 volumetric_parameters.sd_id,
                 padi_distribution_type,
                 padi_other_distribution_type,
                 'MIXED_GAS_HYDROCARBON_SATURATION_PROSPECT',
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p99) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p90) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mode) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p50) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p10) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_p01) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.hsc_mean) END,
                 CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.hsc_mean) END,
                 volumetric_parameters.hsc_comment,
                 volumetric_parameters.rsd_id
             );

            END IF;

        END IF;

        padi_distribution_type := NULL;
        padi_other_distribution_type := NULL;

        IF volumetric_parameters.submission_type = 'POST_DRILL_DRY_HOLE' OR volumetric_parameters.hydrocarbon_case = 'mixed oil and gas' THEN

            -- GRV percentage
            IF COALESCE(
                volumetric_parameters.grv_perc_oil_p99,
                volumetric_parameters.grv_perc_oil_p90,
                volumetric_parameters.grv_perc_oil_mode,
                volumetric_parameters.grv_perc_oil_p50,
                volumetric_parameters.grv_perc_oil_mean,
                volumetric_parameters.grv_perc_oil_p10,
                volumetric_parameters.grv_perc_oil_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.grv_perc_oil_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        WHEN Z THEN NOT_CALCULATED
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL THEN
                    padi_other_distribution_type := volumetric_parameters.grv_perc_oil_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'PERCENT_OF_GRV_OIL_BEARING_PROSPECT',
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_p90) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_mode) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_p50) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_p10) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_p01) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.grv_perc_oil_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.grv_perc_oil_mean) END,
                     volumetric_parameters.grv_perc_oil_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;


            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

        END IF;

        IF volumetric_parameters.submission_type != 'POST_DRILL_DRY_HOLE' AND volumetric_parameters.hydrocarbon_case IN ('oil', 'gas', 'condensate') THEN

            -- Gas expansion factor
            IF COALESCE(
                volumetric_parameters.gef_p99,
                volumetric_parameters.gef_p90,
                volumetric_parameters.gef_mode,
                volumetric_parameters.gef_p50,
                volumetric_parameters.gef_mean,
                volumetric_parameters.gef_p10,
                volumetric_parameters.gef_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.gef_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.gef_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.gef_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'GAS_EXPANSION_FACTOR',
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.gef_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_mean) END,
                     volumetric_parameters.gef_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Gas recovery factor
            IF COALESCE(
                volumetric_parameters.rf_gas_p99,
                volumetric_parameters.rf_gas_p90,
                volumetric_parameters.rf_gas_mode,
                volumetric_parameters.rf_gas_p50,
                volumetric_parameters.rf_gas_mean,
                volumetric_parameters.rf_gas_p10,
                volumetric_parameters.rf_gas_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.rf_gas_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        WHEN Z THEN NOT_CALCULATED
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL THEN
                    padi_other_distribution_type := volumetric_parameters.rf_gas_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'GAS_RECOVERY_FACTOR',
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_p90) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_mode) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_p50) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_p10) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_p01) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.rf_gas_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_gas_mean) END,
                     volumetric_parameters.rf_gas_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Condensate gas ratio
            IF COALESCE(
                volumetric_parameters.cgr_p99,
                volumetric_parameters.cgr_p90,
                volumetric_parameters.cgr_mode,
                volumetric_parameters.cgr_p50,
                volumetric_parameters.cgr_mean,
                volumetric_parameters.cgr_p10,
                volumetric_parameters.cgr_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.cgr_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.cgr_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.cgr_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.cgr_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.cgr_mean) END,
                     volumetric_parameters.cgr_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Formation volume factor
            IF COALESCE(
                volumetric_parameters.fvf_p99,
                volumetric_parameters.fvf_p90,
                volumetric_parameters.fvf_mode,
                volumetric_parameters.fvf_p50,
                volumetric_parameters.fvf_mean,
                volumetric_parameters.fvf_p10,
                volumetric_parameters.fvf_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.fvf_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.fvf_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.fvf_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     CASE
                         WHEN volumetric_parameters.hydrocarbon_case IN ('gas', 'condensate') THEN 'GAS_FORMATION_VOLUME_FACTOR_PROSPECT'
                         WHEN volumetric_parameters.hydrocarbon_case = 'oil' THEN 'OIL_FORMATION_VOLUME_FACTOR_PROSPECT'
                         END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.fvf_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mean) END,
                     volumetric_parameters.fvf_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Oil recovery factor
            IF COALESCE(
                volumetric_parameters.rf_oil_p99,
                volumetric_parameters.rf_oil_p90,
                volumetric_parameters.rf_oil_mode,
                volumetric_parameters.rf_oil_p50,
                volumetric_parameters.rf_oil_mean,
                volumetric_parameters.rf_oil_p10,
                volumetric_parameters.rf_oil_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.rf_oil_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        WHEN Z THEN NOT_CALCULATED
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL THEN
                    padi_other_distribution_type := volumetric_parameters.rf_oil_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'OIL_RECOVERY_FACTOR_PROSPECT',
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_p90) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_mode) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_p50) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_p10) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_p01) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.rf_oil_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.rf_oil_mean) END,
                     volumetric_parameters.rf_oil_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;


            -- Gas oil ratio
            IF COALESCE(
                volumetric_parameters.gor_p99,
                volumetric_parameters.gor_p90,
                volumetric_parameters.gor_mode,
                volumetric_parameters.gor_p50,
                volumetric_parameters.gor_mean,
                volumetric_parameters.gor_p10,
                volumetric_parameters.gor_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.gor_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.gor_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.gor_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.gor_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gor_mean) END,
                     volumetric_parameters.gor_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

        END IF;

        IF volumetric_parameters.submission_type != 'POST_DRILL_DRY_HOLE' AND volumetric_parameters.hydrocarbon_case = 'mixed oil and gas' THEN

            -- Formation volume factor
            IF COALESCE(
                    volumetric_parameters.fvf_p99,
                    volumetric_parameters.fvf_p90,
                    volumetric_parameters.fvf_mode,
                    volumetric_parameters.fvf_p50,
                    volumetric_parameters.fvf_mean,
                    volumetric_parameters.fvf_p10,
                    volumetric_parameters.fvf_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.fvf_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.fvf_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.fvf_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'MIXED_OIL_FORMATION_VOLUME_FACTOR_PROSPECT',
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.fvf_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mean) END,
                     volumetric_parameters.fvf_comment,
                     volumetric_parameters.rsd_id
                 );

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                    gen_random_uuid(),
                    volumetric_parameters.sd_id,
                    padi_distribution_type,
                    padi_other_distribution_type,
                    'MIXED_GAS_FORMATION_VOLUME_FACTOR_PROSPECT',
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p99) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p99) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p90) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mode) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p50) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mean) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p10) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p01) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_p01) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.fvf_mean) END,
                    CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.fvf_mean) END,
                    volumetric_parameters.fvf_comment,
                    volumetric_parameters.rsd_id
                );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Oil recovery factor
            IF COALESCE(
                volumetric_parameters.rf_oil_p99,
                volumetric_parameters.rf_oil_p90,
                volumetric_parameters.rf_oil_mode,
                volumetric_parameters.rf_oil_p50,
                volumetric_parameters.rf_oil_mean,
                volumetric_parameters.rf_oil_p10,
                volumetric_parameters.rf_oil_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.rf_oil_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.rf_oil_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.rf_oil_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'MIXED_OIL_RECOVERY_FACTOR_PROSPECT',
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.rf_oil_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_oil_mean) END,
                     volumetric_parameters.rf_oil_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Gas oil ratio
            IF COALESCE(
                volumetric_parameters.gor_p99,
                volumetric_parameters.gor_p90,
                volumetric_parameters.gor_mode,
                volumetric_parameters.gor_p50,
                volumetric_parameters.gor_mean,
                volumetric_parameters.gor_p10,
                volumetric_parameters.gor_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.gor_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        WHEN Z THEN NOT_CALCULATED
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL THEN
                    padi_other_distribution_type := volumetric_parameters.gor_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_p90) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_mode) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_p50) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_p10) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_p01) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.gor_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.gor_mean) END,
                     volumetric_parameters.gor_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Gas expansion factor
            IF COALESCE(
                volumetric_parameters.gef_p99,
                volumetric_parameters.gef_p90,
                volumetric_parameters.gef_mode,
                volumetric_parameters.gef_p50,
                volumetric_parameters.gef_mean,
                volumetric_parameters.gef_p10,
                volumetric_parameters.gef_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.gef_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.gef_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.gef_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'MIXED_GAS_EXPANSION_FACTOR',
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.gef_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.gef_mean) END,
                     volumetric_parameters.gef_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Gas recovery factor
            IF COALESCE(
                volumetric_parameters.rf_gas_p99,
                volumetric_parameters.rf_gas_p90,
                volumetric_parameters.rf_gas_mode,
                volumetric_parameters.rf_gas_p50,
                volumetric_parameters.rf_gas_mean,
                volumetric_parameters.rf_gas_p10,
                volumetric_parameters.rf_gas_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.rf_gas_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND volumetric_parameters.rf_gas_dist != Z THEN
                    padi_other_distribution_type := volumetric_parameters.rf_gas_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'MIXED_GAS_RECOVERY_FACTOR',
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.rf_gas_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(volumetric_parameters.rf_gas_mean) END,
                     volumetric_parameters.rf_gas_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Condensate gas ratio
            IF COALESCE(
                volumetric_parameters.cgr_p99,
                volumetric_parameters.cgr_p90,
                volumetric_parameters.cgr_mode,
                volumetric_parameters.cgr_p50,
                volumetric_parameters.cgr_mean,
                volumetric_parameters.cgr_p10,
                volumetric_parameters.cgr_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE volumetric_parameters.cgr_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        WHEN Z THEN NOT_CALCULATED
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL THEN
                    padi_other_distribution_type := volumetric_parameters.cgr_dist;
                END IF;

                INSERT INTO remi.probability_distributions (
                    id,
                    submission_detail_id,
                    distribution_type,
                    other_distribution_type,
                    instance,
                    min_value,
                    p99,
                    p90,
                    mode_value,
                    p50,
                    mean_value,
                    p10,
                    p1,
                    max_value,
                    constant_value,
                    most_likely_value,
                    comments,
                    reservoir_submission_detail_id
                ) VALUES (
                     gen_random_uuid(),
                     volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_p90) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_mode) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_p50) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_p10) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_p01) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_p01) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(volumetric_parameters.cgr_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(volumetric_parameters.cgr_mean) END,
                     volumetric_parameters.cgr_comment,
                     volumetric_parameters.rsd_id
                 );

            END IF;

        END IF;

    END LOOP;

END;
    
$$;