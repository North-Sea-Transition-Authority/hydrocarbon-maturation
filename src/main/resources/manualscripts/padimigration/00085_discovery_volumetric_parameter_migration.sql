DO $$
    DECLARE

        submission_reservoir_volumetric_parameters RECORD;
        reservoir_volumetric_parameters RECORD;
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

        FOR submission_reservoir_volumetric_parameters IN (
            SELECT
                prdp.*,
                sd.id sd_id,
                sd.legacy_res_data_id common_res_data_id
            FROM remi.padi_res_data_param prdp
            JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdp.resdat_id
            JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdh.resdat_id
            JOIN remi.submissions s ON s.id = sd.submission_id
            JOIN remi.resources r ON r.id = s.resource_id
            WHERE r.type = 'DISCOVERY'
        )
        LOOP

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- depth to crest
            IF COALESCE(
                submission_reservoir_volumetric_parameters.depth_to_crest_p99,
                submission_reservoir_volumetric_parameters.depth_to_crest_p90,
                submission_reservoir_volumetric_parameters.depth_to_crest_mode,
                submission_reservoir_volumetric_parameters.depth_to_crest_p50,
                submission_reservoir_volumetric_parameters.depth_to_crest_mean,
                submission_reservoir_volumetric_parameters.depth_to_crest_p10,
                submission_reservoir_volumetric_parameters.depth_to_crest_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE submission_reservoir_volumetric_parameters.depth_to_crest_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND submission_reservoir_volumetric_parameters.depth_to_crest_dist != Z THEN
                    padi_other_distribution_type := submission_reservoir_volumetric_parameters.depth_to_crest_dist;
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
                     submission_reservoir_volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'DEPTH_TO_CREST_DISCOVERY',
                     NULL, -- does min exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_crest_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_crest_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_crest_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_crest_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_crest_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_crest_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_crest_p01) END,
                     NULL, -- does max exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(submission_reservoir_volumetric_parameters.depth_to_crest_mean) END, -- most values exist in the mean column, but two items exist in the P50 column
                     NULL, -- does most likely exist in PaDI,
                     submission_reservoir_volumetric_parameters.depth_to_crest_comment
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- depth to spill point
            IF COALESCE(
                submission_reservoir_volumetric_parameters.depth_to_spill_p99,
                submission_reservoir_volumetric_parameters.depth_to_spill_p90,
                submission_reservoir_volumetric_parameters.depth_to_spill_mode,
                submission_reservoir_volumetric_parameters.depth_to_spill_p50,
                submission_reservoir_volumetric_parameters.depth_to_spill_mean,
                submission_reservoir_volumetric_parameters.depth_to_spill_p10,
                submission_reservoir_volumetric_parameters.depth_to_spill_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE submission_reservoir_volumetric_parameters.depth_to_spill_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND submission_reservoir_volumetric_parameters.depth_to_spill_dist != Z THEN
                    padi_other_distribution_type := submission_reservoir_volumetric_parameters.depth_to_spill_dist;
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
                     submission_reservoir_volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'DEPTH_TO_SPILL_POINT_DISCOVERY',
                     NULL, -- does min exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_spill_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_spill_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_spill_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_spill_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_spill_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_spill_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.depth_to_spill_p01) END,
                     NULL, -- does max exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(submission_reservoir_volumetric_parameters.depth_to_spill_mean) END,
                     NULL, -- does most likely exist in PaDI
                     submission_reservoir_volumetric_parameters.depth_to_spill_comment
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- productive area
            IF COALESCE(
                submission_reservoir_volumetric_parameters.area_p99,
                submission_reservoir_volumetric_parameters.area_p90,
                submission_reservoir_volumetric_parameters.area_mode,
                submission_reservoir_volumetric_parameters.area_p50,
                submission_reservoir_volumetric_parameters.area_mean,
                submission_reservoir_volumetric_parameters.area_p10,
                submission_reservoir_volumetric_parameters.area_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE submission_reservoir_volumetric_parameters.area_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND submission_reservoir_volumetric_parameters.area_dist != Z THEN
                    padi_other_distribution_type := submission_reservoir_volumetric_parameters.area_dist;
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
                     submission_reservoir_volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'PRODUCTIVE_AREA_DISCOVERY',
                     NULL, -- does min exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.area_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.area_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.area_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.area_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.area_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.area_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(submission_reservoir_volumetric_parameters.area_p01) END,
                     NULL, -- does max exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(submission_reservoir_volumetric_parameters.area_mean) END,
                     NULL, -- does most likely exist in PaDI
                     submission_reservoir_volumetric_parameters.area_comment
                 );
                
            END IF;

        END LOOP;

        FOR reservoir_volumetric_parameters IN (
            SELECT
                prdp.*,
                LOWER(prdh.hc_case) hydrocarbon_case,
                rsd.submission_detail_id sd_id,
                rsd.id rsd_id
            FROM remi.padi_res_data_param prdp
            JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdp.resdat_id
            JOIN remi.padi_reservoirs pr ON pr.legacy_res_data_id = prdh.resdat_id
            JOIN remi.reservoir_submission_details rsd ON rsd.reservoir = pr.remi_reservoir_id
            WHERE prdh.opportunity_type = 'Discovery'
        )
        LOOP

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- RESERVOIR PROPERTIES
            -- Gross reservoir thickness
            IF COALESCE(
                reservoir_volumetric_parameters.gross_thick_p99,
                reservoir_volumetric_parameters.gross_thick_p90,
                reservoir_volumetric_parameters.gross_thick_mode,
                reservoir_volumetric_parameters.gross_thick_p50,
                reservoir_volumetric_parameters.gross_thick_mean,
                reservoir_volumetric_parameters.gross_thick_p10,
                reservoir_volumetric_parameters.gross_thick_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE reservoir_volumetric_parameters.gross_thick_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.gross_thick_dist != Z THEN
                    padi_other_distribution_type := reservoir_volumetric_parameters.gross_thick_dist;
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
                     reservoir_volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'GROSS_RESERVOIR_THICKNESS_DISCOVERY',
                     NULL, -- does min exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gross_thick_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gross_thick_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gross_thick_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gross_thick_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gross_thick_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gross_thick_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gross_thick_p01) END,
                     NULL, -- does max exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.gross_thick_mean) END,
                     NULL, -- does most likely exist in PaDI
                     reservoir_volumetric_parameters.gross_thick_comment,
                     reservoir_volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Gross rock volume
            IF COALESCE(
                reservoir_volumetric_parameters.grv_p99,
                reservoir_volumetric_parameters.grv_p90,
                reservoir_volumetric_parameters.grv_mode,
                reservoir_volumetric_parameters.grv_p50,
                reservoir_volumetric_parameters.grv_mean,
                reservoir_volumetric_parameters.grv_p10,
                reservoir_volumetric_parameters.grv_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE reservoir_volumetric_parameters.grv_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.grv_dist != Z THEN
                    padi_other_distribution_type := reservoir_volumetric_parameters.grv_dist;
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
                     reservoir_volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'GROSS_ROCK_VOLUME_DISCOVERY',
                     NULL, -- does min exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_p01) END,
                     NULL, -- does max exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.grv_mean) END,
                     NULL, -- does most likely exist in PaDI
                     reservoir_volumetric_parameters.grv_comment,
                     reservoir_volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Net-to-gross
            IF COALESCE(
                reservoir_volumetric_parameters.ntg_p99,
                reservoir_volumetric_parameters.ntg_p90,
                reservoir_volumetric_parameters.ntg_mode,
                reservoir_volumetric_parameters.ntg_p50,
                reservoir_volumetric_parameters.ntg_mean,
                reservoir_volumetric_parameters.ntg_p10,
                reservoir_volumetric_parameters.ntg_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE reservoir_volumetric_parameters.ntg_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.ntg_dist != Z THEN
                    padi_other_distribution_type := reservoir_volumetric_parameters.ntg_dist;
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
                     reservoir_volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'NET_TO_GROSS_DISCOVERY',
                     NULL, -- does min exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.ntg_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.ntg_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.ntg_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.ntg_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.ntg_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.ntg_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.ntg_p01) END,
                     NULL, -- does max exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.ntg_mean) END,
                     NULL, -- does most likely exist in PaDI
                     reservoir_volumetric_parameters.ntg_comment,
                     reservoir_volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Net porosity
            IF COALESCE(
                reservoir_volumetric_parameters.porosity_p99,
                reservoir_volumetric_parameters.porosity_p90,
                reservoir_volumetric_parameters.porosity_mode,
                reservoir_volumetric_parameters.porosity_p50,
                reservoir_volumetric_parameters.porosity_mean,
                reservoir_volumetric_parameters.porosity_p10,
                reservoir_volumetric_parameters.porosity_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE reservoir_volumetric_parameters.porosity_dist
                        WHEN N THEN NORMAL
                        WHEN L THEN LOGNORMAL
                        WHEN B THEN BETA
                        WHEN T THEN TRIANGULAR
                        WHEN R THEN RECTANGULAR
                        WHEN C THEN CONSTANT
                        END
                INTO padi_distribution_type;

                IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.porosity_dist != Z THEN
                    padi_other_distribution_type := reservoir_volumetric_parameters.porosity_dist;
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
                     reservoir_volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'NET_POROSITY_DISCOVERY',
                     NULL, -- does min exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.porosity_p99) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.porosity_p90) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.porosity_mode) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.porosity_p50) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.porosity_mean) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.porosity_p10) END,
                     CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.porosity_p01) END,
                     NULL, -- does max exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.porosity_mean) END,
                     NULL, -- does most likely exist in PaDI
                     reservoir_volumetric_parameters.porosity_comment,
                     reservoir_volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            -- Average net pay thickness
            IF COALESCE(
                reservoir_volumetric_parameters.net_pay_p99,
                reservoir_volumetric_parameters.net_pay_p90,
                reservoir_volumetric_parameters.net_pay_mode,
                reservoir_volumetric_parameters.net_pay_p50,
                reservoir_volumetric_parameters.net_pay_mean,
                reservoir_volumetric_parameters.net_pay_p10,
                reservoir_volumetric_parameters.net_pay_p01
            ) IS NOT NULL THEN

                SELECT
                    CASE reservoir_volumetric_parameters.net_pay_dist
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
                    padi_other_distribution_type := reservoir_volumetric_parameters.net_pay_dist;
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
                     reservoir_volumetric_parameters.sd_id,
                     padi_distribution_type,
                     padi_other_distribution_type,
                     'AVERAGE_NET_PAY_THICKNESS_DISCOVERY',
                     NULL, -- does min exist in PaDI
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.net_pay_p99) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.net_pay_p90) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.net_pay_mode) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.net_pay_p50) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.net_pay_mean) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.net_pay_p10) END,
                     CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.net_pay_p01) END,
                     NULL, -- does max exist in PaDI
                     CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.net_pay_mean) END,
                     NULL, -- does most likely exist in PaDI
                     reservoir_volumetric_parameters.net_pay_comment,
                     reservoir_volumetric_parameters.rsd_id
                 );

            END IF;

            padi_distribution_type := NULL;
            padi_other_distribution_type := NULL;

            IF reservoir_volumetric_parameters.hydrocarbon_case = 'mixed oil and gas' THEN

                -- GRV percentage
                IF COALESCE(
                    reservoir_volumetric_parameters.grv_perc_oil_p99,
                    reservoir_volumetric_parameters.grv_perc_oil_p90,
                    reservoir_volumetric_parameters.grv_perc_oil_mode,
                    reservoir_volumetric_parameters.grv_perc_oil_p50,
                    reservoir_volumetric_parameters.grv_perc_oil_mean,
                    reservoir_volumetric_parameters.grv_perc_oil_p10,
                    reservoir_volumetric_parameters.grv_perc_oil_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.grv_perc_oil_dist
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
                        padi_other_distribution_type := reservoir_volumetric_parameters.grv_perc_oil_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'PERCENT_OF_GRV_OIL_BEARING_DISCOVERY',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_perc_oil_p99) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_perc_oil_p90) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_perc_oil_mode) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_perc_oil_p50) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_perc_oil_mean) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_perc_oil_p10) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.grv_perc_oil_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.grv_perc_oil_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.grv_perc_oil_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

            END IF;

            IF reservoir_volumetric_parameters.hydrocarbon_case IN ('oil', 'gas', 'condensate') THEN

                -- Gas expansion factor
                IF COALESCE(
                    reservoir_volumetric_parameters.gef_p99,
                    reservoir_volumetric_parameters.gef_p90,
                    reservoir_volumetric_parameters.gef_mode,
                    reservoir_volumetric_parameters.gef_p50,
                    reservoir_volumetric_parameters.gef_mean,
                    reservoir_volumetric_parameters.gef_p10,
                    reservoir_volumetric_parameters.gef_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.gef_dist
                            WHEN N THEN NORMAL
                            WHEN L THEN LOGNORMAL
                            WHEN B THEN BETA
                            WHEN T THEN TRIANGULAR
                            WHEN R THEN RECTANGULAR
                            WHEN C THEN CONSTANT
                            END
                    INTO padi_distribution_type;

                    IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.gef_dist != Z THEN
                        padi_other_distribution_type := reservoir_volumetric_parameters.gef_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'GAS_EXPANSION_FACTOR',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.gef_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.gef_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Gas recovery factor
                IF COALESCE(
                    reservoir_volumetric_parameters.rf_gas_p99,
                    reservoir_volumetric_parameters.rf_gas_p90,
                    reservoir_volumetric_parameters.rf_gas_mode,
                    reservoir_volumetric_parameters.rf_gas_p50,
                    reservoir_volumetric_parameters.rf_gas_mean,
                    reservoir_volumetric_parameters.rf_gas_p10,
                    reservoir_volumetric_parameters.rf_gas_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.rf_gas_dist
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
                        padi_other_distribution_type := reservoir_volumetric_parameters.rf_gas_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'GAS_RECOVERY_FACTOR',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p99) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p90) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_mode) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p50) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_mean) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p10) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.rf_gas_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.rf_gas_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Condensate gas ratio
                IF COALESCE(
                    reservoir_volumetric_parameters.cgr_p99,
                    reservoir_volumetric_parameters.cgr_p90,
                    reservoir_volumetric_parameters.cgr_mode,
                    reservoir_volumetric_parameters.cgr_p50,
                    reservoir_volumetric_parameters.cgr_mean,
                    reservoir_volumetric_parameters.cgr_p10,
                    reservoir_volumetric_parameters.cgr_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.cgr_dist
                            WHEN N THEN NORMAL
                            WHEN L THEN LOGNORMAL
                            WHEN B THEN BETA
                            WHEN T THEN TRIANGULAR
                            WHEN R THEN RECTANGULAR
                            WHEN C THEN CONSTANT
                            END
                    INTO padi_distribution_type;

                    IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.cgr_dist != Z THEN
                        padi_other_distribution_type := reservoir_volumetric_parameters.cgr_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.cgr_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.cgr_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Formation volume factor
                IF COALESCE(
                    reservoir_volumetric_parameters.fvf_p99,
                    reservoir_volumetric_parameters.fvf_p90,
                    reservoir_volumetric_parameters.fvf_mode,
                    reservoir_volumetric_parameters.fvf_p50,
                    reservoir_volumetric_parameters.fvf_mean,
                    reservoir_volumetric_parameters.fvf_p10,
                    reservoir_volumetric_parameters.fvf_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.fvf_dist
                            WHEN N THEN NORMAL
                            WHEN L THEN LOGNORMAL
                            WHEN B THEN BETA
                            WHEN T THEN TRIANGULAR
                            WHEN R THEN RECTANGULAR
                            WHEN C THEN CONSTANT
                            END
                    INTO padi_distribution_type;

                    IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.fvf_dist != Z THEN
                        padi_other_distribution_type := reservoir_volumetric_parameters.fvf_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         CASE
                             WHEN reservoir_volumetric_parameters.hydrocarbon_case IN ('gas', 'condensate')
                                 THEN 'GAS_FORMATION_VOLUME_FACTOR_DISCOVERY'
                             WHEN reservoir_volumetric_parameters.hydrocarbon_case = 'oil'
                                 THEN 'OIL_FORMATION_VOLUME_FACTOR_DISCOVERY'
                             END,
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.fvf_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.fvf_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Oil recovery factor
                IF COALESCE(
                    reservoir_volumetric_parameters.rf_oil_p99,
                    reservoir_volumetric_parameters.rf_oil_p90,
                    reservoir_volumetric_parameters.rf_oil_mode,
                    reservoir_volumetric_parameters.rf_oil_p50,
                    reservoir_volumetric_parameters.rf_oil_mean,
                    reservoir_volumetric_parameters.rf_oil_p10,
                    reservoir_volumetric_parameters.rf_oil_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.rf_oil_dist
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
                        padi_other_distribution_type := reservoir_volumetric_parameters.rf_oil_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'OIL_RECOVERY_FACTOR_DISCOVERY',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p99) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p90) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_mode) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p50) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_mean) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p10) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.rf_oil_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.rf_oil_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );
                    
                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Gas oil ratio
                IF COALESCE(
                    reservoir_volumetric_parameters.gor_p99,
                    reservoir_volumetric_parameters.gor_p90,
                    reservoir_volumetric_parameters.gor_mode,
                    reservoir_volumetric_parameters.gor_p50,
                    reservoir_volumetric_parameters.gor_mean,
                    reservoir_volumetric_parameters.gor_p10,
                    reservoir_volumetric_parameters.gor_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.gor_dist
                            WHEN N THEN NORMAL
                            WHEN L THEN LOGNORMAL
                            WHEN B THEN BETA
                            WHEN T THEN TRIANGULAR
                            WHEN R THEN RECTANGULAR
                            WHEN C THEN CONSTANT
                            END
                    INTO padi_distribution_type;

                    IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.gor_dist != Z THEN
                        padi_other_distribution_type := reservoir_volumetric_parameters.gor_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.gor_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.gor_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

            END IF;

            IF reservoir_volumetric_parameters.hydrocarbon_case = 'mixed oil and gas' THEN

                -- Formation volume factor
                IF COALESCE(
                    reservoir_volumetric_parameters.fvf_p99,
                    reservoir_volumetric_parameters.fvf_p90,
                    reservoir_volumetric_parameters.fvf_mode,
                    reservoir_volumetric_parameters.fvf_p50,
                    reservoir_volumetric_parameters.fvf_mean,
                    reservoir_volumetric_parameters.fvf_p10,
                    reservoir_volumetric_parameters.fvf_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.fvf_dist
                            WHEN N THEN NORMAL
                            WHEN L THEN LOGNORMAL
                            WHEN B THEN BETA
                            WHEN T THEN TRIANGULAR
                            WHEN R THEN RECTANGULAR
                            WHEN C THEN CONSTANT
                            END
                    INTO padi_distribution_type;

                    IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.fvf_dist != Z THEN
                        padi_other_distribution_type := reservoir_volumetric_parameters.fvf_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'MIXED_OIL_FORMATION_VOLUME_FACTOR_DISCOVERY',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.fvf_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.fvf_comment,
                         reservoir_volumetric_parameters.rsd_id
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'MIXED_GAS_FORMATION_VOLUME_FACTOR_DISCOVERY',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.fvf_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.fvf_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.fvf_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                    padi_distribution_type := NULL;
                    padi_other_distribution_type := NULL;

                END IF;

                -- Oil recovery factor
                IF COALESCE(
                    reservoir_volumetric_parameters.rf_oil_p99,
                    reservoir_volumetric_parameters.rf_oil_p90,
                    reservoir_volumetric_parameters.rf_oil_mode,
                    reservoir_volumetric_parameters.rf_oil_p50,
                    reservoir_volumetric_parameters.rf_oil_mean,
                    reservoir_volumetric_parameters.rf_oil_p10,
                    reservoir_volumetric_parameters.rf_oil_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.rf_oil_dist
                            WHEN N THEN NORMAL
                            WHEN L THEN LOGNORMAL
                            WHEN B THEN BETA
                            WHEN T THEN TRIANGULAR
                            WHEN R THEN RECTANGULAR
                            WHEN C THEN CONSTANT
                            END
                    INTO padi_distribution_type;

                    IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.rf_oil_dist != Z THEN
                        padi_other_distribution_type := reservoir_volumetric_parameters.rf_oil_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'MIXED_OIL_RECOVERY_FACTOR_DISCOVERY',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_oil_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.rf_oil_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.rf_oil_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Gas oil ratio
                IF COALESCE(
                    reservoir_volumetric_parameters.gor_p99,
                    reservoir_volumetric_parameters.gor_p90,
                    reservoir_volumetric_parameters.gor_mode,
                    reservoir_volumetric_parameters.gor_p50,
                    reservoir_volumetric_parameters.gor_mean,
                    reservoir_volumetric_parameters.gor_p10,
                    reservoir_volumetric_parameters.gor_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.gor_dist
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
                        padi_other_distribution_type := reservoir_volumetric_parameters.gor_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p99) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p90) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_mode) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p50) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_mean) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p10) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.gor_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.gor_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.gor_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Gas expansion factor
                IF COALESCE(
                    reservoir_volumetric_parameters.gef_p99,
                    reservoir_volumetric_parameters.gef_p90,
                    reservoir_volumetric_parameters.gef_mode,
                    reservoir_volumetric_parameters.gef_p50,
                    reservoir_volumetric_parameters.gef_mean,
                    reservoir_volumetric_parameters.gef_p10,
                    reservoir_volumetric_parameters.gef_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.gef_dist
                            WHEN N THEN NORMAL
                            WHEN L THEN LOGNORMAL
                            WHEN B THEN BETA
                            WHEN T THEN TRIANGULAR
                            WHEN R THEN RECTANGULAR
                            WHEN C THEN CONSTANT
                            END
                    INTO padi_distribution_type;

                    IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.gef_dist != Z THEN
                        padi_other_distribution_type := reservoir_volumetric_parameters.gef_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'MIXED_GAS_EXPANSION_FACTOR',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.gef_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.gef_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.gef_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );

                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Gas recovery factor
                IF COALESCE(
                    reservoir_volumetric_parameters.rf_gas_p99,
                    reservoir_volumetric_parameters.rf_gas_p90,
                    reservoir_volumetric_parameters.rf_gas_mode,
                    reservoir_volumetric_parameters.rf_gas_p50,
                    reservoir_volumetric_parameters.rf_gas_mean,
                    reservoir_volumetric_parameters.rf_gas_p10,
                    reservoir_volumetric_parameters.rf_gas_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.rf_gas_dist
                            WHEN N THEN NORMAL
                            WHEN L THEN LOGNORMAL
                            WHEN B THEN BETA
                            WHEN T THEN TRIANGULAR
                            WHEN R THEN RECTANGULAR
                            WHEN C THEN CONSTANT
                            END
                    INTO padi_distribution_type;

                    IF padi_distribution_type IS NULL AND reservoir_volumetric_parameters.rf_gas_dist != Z THEN
                        padi_other_distribution_type := reservoir_volumetric_parameters.rf_gas_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'MIXED_GAS_RECOVERY_FACTOR',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p99) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p90) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_mode) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p50) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_mean) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p10) END,
                         CASE WHEN padi_distribution_type = CONSTANT THEN NULL ELSE abs(reservoir_volumetric_parameters.rf_gas_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.rf_gas_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.rf_gas_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );
                    
                END IF;

                padi_distribution_type := NULL;
                padi_other_distribution_type := NULL;

                -- Condensate gas ratio
                IF COALESCE(
                    reservoir_volumetric_parameters.cgr_p99,
                    reservoir_volumetric_parameters.cgr_p90,
                    reservoir_volumetric_parameters.cgr_mode,
                    reservoir_volumetric_parameters.cgr_p50,
                    reservoir_volumetric_parameters.cgr_mean,
                    reservoir_volumetric_parameters.cgr_p10,
                    reservoir_volumetric_parameters.cgr_p01
                ) IS NOT NULL THEN

                    SELECT
                        CASE reservoir_volumetric_parameters.cgr_dist
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
                        padi_other_distribution_type := reservoir_volumetric_parameters.cgr_dist;
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
                         reservoir_volumetric_parameters.sd_id,
                         padi_distribution_type,
                         padi_other_distribution_type,
                         'MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                         NULL, -- does min exist in PaDI
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p99) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p90) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_mode) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p50) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_mean) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p10) END,
                         CASE WHEN padi_distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE abs(reservoir_volumetric_parameters.cgr_p01) END,
                         NULL, -- does max exist in PaDI
                         CASE WHEN padi_distribution_type = CONSTANT THEN abs(reservoir_volumetric_parameters.cgr_mean) END,
                         NULL, -- does most likely exist in PaDI
                         reservoir_volumetric_parameters.cgr_comment,
                         reservoir_volumetric_parameters.rsd_id
                     );
                    
                END IF;

            END IF;

        END LOOP;

    END;

$$;