DO $$
DECLARE

    common_parameters RECORD;
    prospect_parameters RECORD;
    discovery_dry_well_parameters RECORD;
    dry_well_or_mixed_case_parameters RECORD;
    gas_case_parameters RECORD;
    oil_case_parameters RECORD;
    mixed_case_parameters RECORD;
    distribution_type TEXT;
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

    -- Parameters which appear across all submission types
    -- TRAP PROPERTIES
    FOR common_parameters IN (
        SELECT prdp.*, sd.id sd_id
        FROM remi.padi_res_data_param prdp
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdp.resdat_id
    )
    LOOP

        -- depth to crest
        IF common_parameters.depth_to_crest_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE common_parameters.depth_to_crest_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                common_parameters.sd_id,
                distribution_type,
                'DEPTH_TO_CREST_PROSPECT_DISCOVERY',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_crest_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_crest_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_crest_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_crest_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_crest_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_crest_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_crest_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN common_parameters.depth_to_crest_mean END, -- most values exist in the mean column, but two items exist in the P50 column
                NULL -- does most likely exist in PaDI
             );

        END IF;

        -- depth to spill point
        IF common_parameters.depth_to_spill_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE common_parameters.depth_to_spill_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                common_parameters.sd_id,
                distribution_type,
                'DEPTH_TO_SPILL_POINT_PROSPECT_DISCOVERY',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_spill_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_spill_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_spill_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_spill_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_spill_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_spill_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.depth_to_spill_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN common_parameters.depth_to_spill_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- productive area
        IF common_parameters.area_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE common_parameters.area_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                common_parameters.sd_id,
                distribution_type,
                'PRODUCTIVE_AREA',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.area_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.area_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.area_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.area_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.area_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.area_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.area_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN common_parameters.area_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- RESERVOIR PROPERTIES
        -- Gross reservoir thickness
        IF common_parameters.gross_thick_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE common_parameters.gross_thick_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                common_parameters.sd_id,
                distribution_type,
                'GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.gross_thick_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.gross_thick_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.gross_thick_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.gross_thick_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.gross_thick_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.gross_thick_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.gross_thick_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN common_parameters.gross_thick_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Gross rock volume
        IF common_parameters.grv_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE common_parameters.grv_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                common_parameters.sd_id,
                distribution_type,
                'GROSS_ROCK_VOLUME',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.grv_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.grv_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.grv_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.grv_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.grv_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.grv_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.grv_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN common_parameters.grv_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Net-to-gross
        IF common_parameters.ntg_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE common_parameters.ntg_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                common_parameters.sd_id,
                distribution_type,
                'NET_TO_GROSS_PROSPECT_DISCOVERY',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.ntg_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.ntg_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.ntg_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.ntg_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.ntg_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.ntg_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.ntg_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN common_parameters.ntg_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Net porosity
        IF common_parameters.porosity_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE common_parameters.porosity_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                common_parameters.sd_id,
                distribution_type,
                'NET_POROSITY_PROSPECT_DISCOVERY',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.porosity_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.porosity_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.porosity_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.porosity_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.porosity_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.porosity_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE common_parameters.porosity_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN common_parameters.porosity_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

    END LOOP;

    -- Parameters which only appear for prospects
    -- RESERVOIR PROPERTIES
    FOR prospect_parameters IN (
        SELECT prdp.*, sd.id sd_id
        FROM remi.padi_res_data_param prdp
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdp.resdat_id
        WHERE sd.submission_type IN ('CREATE_PROSPECT','PROSPECT_UPDATE','RELINQUISHMENT_PROSPECT')
    )
    LOOP

        -- Average net pay thickness
        IF prospect_parameters.net_pay_dist IN (N,L,B,T,R,C,Z) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE prospect_parameters.net_pay_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    WHEN Z THEN NOT_CALCULATED
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                prospect_parameters.sd_id,
                distribution_type,
                'AVERAGE_NET_PAY_THICKNESS_PROSPECT',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE prospect_parameters.net_pay_p99 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE prospect_parameters.net_pay_p90 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE prospect_parameters.net_pay_mode END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE prospect_parameters.net_pay_p50 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE prospect_parameters.net_pay_mean END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE prospect_parameters.net_pay_p10 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE prospect_parameters.net_pay_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN prospect_parameters.net_pay_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

    END LOOP;

    -- Parameters which only appear for either dry wells or discoveries
    -- RESERVOIR PROPERTIES
    FOR discovery_dry_well_parameters IN (
        SELECT prdp.*, sd.id sd_id
        FROM remi.padi_res_data_param prdp
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdp.resdat_id
        WHERE sd.submission_type IN ('CREATE_DISCOVERY_FROM_PROSPECT','CREATE_DISCOVERY','UPDATE_DISCOVERY','RELINQUISHMENT_DISCOVERY','POST_DRILL_DRY_HOLE')
    )
    LOOP

        -- Average net pay thickness
        IF discovery_dry_well_parameters.net_pay_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE discovery_dry_well_parameters.net_pay_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                discovery_dry_well_parameters.sd_id,
                distribution_type,
                'AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE discovery_dry_well_parameters.net_pay_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE discovery_dry_well_parameters.net_pay_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE discovery_dry_well_parameters.net_pay_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE discovery_dry_well_parameters.net_pay_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE discovery_dry_well_parameters.net_pay_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE discovery_dry_well_parameters.net_pay_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE discovery_dry_well_parameters.net_pay_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN discovery_dry_well_parameters.net_pay_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

    END LOOP;

    -- Parameters which only appear for either dry wells or discoveries
    -- Percent of GRV which is oil bearing appears for both dry well cases, or if it is a mixed oil and gas case
    FOR dry_well_or_mixed_case_parameters IN (
        SELECT prdp.*, sd.id sd_id
        FROM remi.padi_res_data_param prdp
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdp.resdat_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type = 'POST_DRILL_DRY_HOLE' OR LOWER(prdh.hc_case) = 'mixed oil and gas'
    )
    LOOP

        IF dry_well_or_mixed_case_parameters.grv_perc_oil_dist IN (N,L,B,T,R,C,Z) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE discovery_dry_well_parameters.grv_perc_oil_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    WHEN Z THEN NOT_CALCULATED
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                dry_well_or_mixed_case_parameters.sd_id,
                distribution_type,
                'PERCENT_OF_GRV_OIL_BEARING',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE dry_well_or_mixed_case_parameters.grv_perc_oil_p99 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE dry_well_or_mixed_case_parameters.grv_perc_oil_p90 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE dry_well_or_mixed_case_parameters.grv_perc_oil_mode END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE dry_well_or_mixed_case_parameters.grv_perc_oil_p50 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE dry_well_or_mixed_case_parameters.grv_perc_oil_mean END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE dry_well_or_mixed_case_parameters.grv_perc_oil_p10 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE dry_well_or_mixed_case_parameters.grv_perc_oil_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN dry_well_or_mixed_case_parameters.grv_perc_oil_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

    END LOOP;

    -- Parameters for gas cases
    FOR gas_case_parameters IN (
        SELECT prdp.*, sd.id sd_id
        FROM remi.padi_res_data_param prdp
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdp.resdat_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type != 'POST_DRILL_DRY_HOLE'
        AND LOWER(prdh.hc_case) IN ('gas', 'condensate')
    )
    LOOP

        -- Gas expansion factor
        IF gas_case_parameters.gef_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE gas_case_parameters.gef_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                gas_case_parameters.sd_id,
                distribution_type,
                'GAS_EXPANSION_FACTOR',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gef_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gef_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gef_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gef_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gef_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gef_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gef_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN gas_case_parameters.gef_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Gas recovery factor
        IF gas_case_parameters.rf_gas_dist IN (N,L,B,T,R,C,Z) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE gas_case_parameters.rf_gas_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    WHEN Z THEN NOT_CALCULATED
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                gas_case_parameters.sd_id,
                distribution_type,
                'GAS_RECOVERY_FACTOR',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE gas_case_parameters.rf_gas_p99 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE gas_case_parameters.rf_gas_p90 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE gas_case_parameters.rf_gas_mode END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE gas_case_parameters.rf_gas_p50 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE gas_case_parameters.rf_gas_mean END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE gas_case_parameters.rf_gas_p10 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE gas_case_parameters.rf_gas_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN gas_case_parameters.rf_gas_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Condensate gas ratio
        IF gas_case_parameters.cgr_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE gas_case_parameters.cgr_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                gas_case_parameters.sd_id,
                distribution_type,
                'CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.cgr_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.cgr_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.cgr_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.cgr_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.cgr_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.cgr_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.cgr_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN gas_case_parameters.cgr_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

    END LOOP;

    -- Parameters for oil cases
    FOR oil_case_parameters IN (
        SELECT prdp.*, sd.id sd_id
        FROM remi.padi_res_data_param prdp
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdp.resdat_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type != 'POST_DRILL_DRY_HOLE'
        AND LOWER(prdh.hc_case) = 'oil'
    )
    LOOP

        -- Formation volume factor
        IF oil_case_parameters.fvf_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE oil_case_parameters.fvf_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                oil_case_parameters.sd_id,
                distribution_type,
                'FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE oil_case_parameters.fvf_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE oil_case_parameters.fvf_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE oil_case_parameters.fvf_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE oil_case_parameters.fvf_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE oil_case_parameters.fvf_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE oil_case_parameters.fvf_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE oil_case_parameters.fvf_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN oil_case_parameters.fvf_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Oil recovery factor
        IF oil_case_parameters.rf_oil_dist IN (N,L,B,T,R,C,Z) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE oil_case_parameters.rf_oil_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    WHEN Z THEN NOT_CALCULATED
                    END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                oil_case_parameters.sd_id,
                distribution_type,
                'OIL_RECOVERY_FACTOR',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE oil_case_parameters.rf_oil_p99 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE oil_case_parameters.rf_oil_p90 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE oil_case_parameters.rf_oil_mode END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE oil_case_parameters.rf_oil_p50 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE oil_case_parameters.rf_oil_mean END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE oil_case_parameters.rf_oil_p10 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE oil_case_parameters.rf_oil_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN oil_case_parameters.rf_oil_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Gas oil ratio
        IF gas_case_parameters.gor_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE gas_case_parameters.gor_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                gas_case_parameters.sd_id,
                distribution_type,
                'ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gor_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gor_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gor_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gor_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gor_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gor_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE gas_case_parameters.gor_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN gas_case_parameters.gor_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

    END LOOP;

    -- Parameters for mixed cases
    FOR mixed_case_parameters IN (
        SELECT prdp.*, sd.id sd_id
        FROM remi.padi_res_data_param prdp
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdp.resdat_id
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = sd.legacy_res_data_id
        WHERE sd.submission_type != 'POST_DRILL_DRY_HOLE'
        AND LOWER(prdh.hc_case) = 'mixed oil and gas'
    )
    LOOP

        -- Formation volume factor
        IF mixed_case_parameters.fvf_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE mixed_case_parameters.fvf_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                oil_case_parameters.sd_id,
                distribution_type,
                'MIXED_FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.fvf_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.fvf_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.fvf_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.fvf_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.fvf_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.fvf_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.fvf_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN mixed_case_parameters.fvf_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Oil recovery factor
        IF mixed_case_parameters.rf_oil_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE mixed_case_parameters.rf_oil_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                mixed_case_parameters.sd_id,
                distribution_type,
                'MIXED_OIL_RECOVERY_FACTOR',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_oil_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_oil_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_oil_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_oil_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_oil_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_oil_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_oil_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN mixed_case_parameters.rf_oil_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Gas oil ratio
        IF mixed_case_parameters.gor_dist IN (N,L,B,T,R,C,Z) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE mixed_case_parameters.gor_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    WHEN Z THEN NOT_CALCULATED
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                mixed_case_parameters.sd_id,
                distribution_type,
                'MIXED_ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.gor_p99 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.gor_p90 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.gor_mode END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.gor_p50 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.gor_mean END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.gor_p10 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.gor_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN mixed_case_parameters.gor_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Gas expansion factor
        IF mixed_case_parameters.gef_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE mixed_case_parameters.gef_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                mixed_case_parameters.sd_id,
                distribution_type,
                'MIXED_GAS_EXPANSION_FACTOR',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.gef_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.gef_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.gef_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.gef_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.gef_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.gef_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.gef_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN mixed_case_parameters.gef_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Gas recovery factor
        IF mixed_case_parameters.rf_gas_dist IN (N,L,B,T,R,C) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE mixed_case_parameters.rf_gas_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                mixed_case_parameters.sd_id,
                distribution_type,
                'MIXED_GAS_RECOVERY_FACTOR',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_gas_p99 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_gas_p90 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_gas_mode END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_gas_p50 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_gas_mean END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_gas_p10 END,
                CASE WHEN distribution_type = CONSTANT THEN NULL ELSE mixed_case_parameters.rf_gas_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN mixed_case_parameters.rf_gas_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

        -- Condensate gas ratio
        IF mixed_case_parameters.cgr_dist IN (N,L,B,T,R,C,Z) THEN -- need to verify with NSTA what to do with other values

            SELECT
                CASE mixed_case_parameters.cgr_dist
                    WHEN N THEN NORMAL
                    WHEN L THEN LOGNORMAL
                    WHEN B THEN BETA
                    WHEN T THEN TRIANGULAR
                    WHEN R THEN RECTANGULAR
                    WHEN C THEN CONSTANT
                    WHEN Z THEN NOT_CALCULATED
                END
            INTO distribution_type;

            INSERT INTO remi.probability_distributions (
                id,
                submission_detail_id,
                distribution_type,
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
                most_likely_value
            ) VALUES (
                gen_random_uuid(),
                mixed_case_parameters.sd_id,
                distribution_type,
                'MIXED_CONDENSATE_GAS_RATIO_LIFE_OF_FIELD',
                NULL, -- does min exist in PaDI
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.cgr_p99 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.cgr_p90 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.cgr_mode END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.cgr_p50 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.cgr_mean END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.cgr_p10 END,
                CASE WHEN distribution_type IN (CONSTANT,NOT_CALCULATED) THEN NULL ELSE mixed_case_parameters.cgr_p01 END,
                NULL, -- does max exist in PaDI
                CASE WHEN distribution_type = CONSTANT THEN mixed_case_parameters.cgr_mean END,
                NULL -- does most likely exist in PaDI
            );

        END IF;

    END LOOP;

END;
    
$$;