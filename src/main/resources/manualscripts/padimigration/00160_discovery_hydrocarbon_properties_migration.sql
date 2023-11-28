DO $$
DECLARE

    discovery_rec RECORD;
    has_oil_properties BOOLEAN;
    has_gas_properties BOOLEAN;
    has_condensate_properties BOOLEAN;
    has_general_properties BOOLEAN;

BEGIN

    FOR discovery_rec IN (
        SELECT rsd.id rsd_id, prdd.*
        FROM remi.padi_res_data_disc prdd
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdd.resdat_id
        JOIN remi.padi_reservoirs pr ON pr.legacy_res_data_id = prdh.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.reservoir = pr.remi_reservoir_id
        WHERE prdh.opportunity_type = 'Discovery'
    )
    LOOP

        -- oil properties
        has_oil_properties := FALSE;
        SELECT
            COALESCE(
                discovery_rec.oil_gravity::text,
                discovery_rec.oil_viscosity::text,
                discovery_rec.oil_sulphur::text,
                discovery_rec.oil_wax::text,
                discovery_rec.oil_other::text
            ) IS NOT NULL
        INTO has_oil_properties;

        IF has_oil_properties THEN

            INSERT INTO remi.oil_fluid_properties(
                id,
                reservoir_submission_detail_id,
                oil_gravity,
                oil_viscosity,
                oil_sulphur,
                oil_wax,
                oil_other_wax_appearance_temperature
            ) VALUES (
                gen_random_uuid(),
                discovery_rec.rsd_id,
                CASE
                    WHEN discovery_rec.oil_gravity ~ '^[0-9\.]+$' -- checks to see if the data is a number, doesn't enter text values, e.g. "N/A", "Unknown", etc
                    THEN ROUND(discovery_rec.oil_gravity::NUMERIC)
                END,
                CASE
                    WHEN discovery_rec.oil_viscosity ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.oil_viscosity::NUMERIC, 2)
                END,
                CASE
                    WHEN discovery_rec.oil_sulphur ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.oil_sulphur::NUMERIC)
                END,
                CASE
                    WHEN discovery_rec.oil_wax ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.oil_wax::NUMERIC, 2)
                END,
                CASE
                    WHEN discovery_rec.oil_other ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.oil_other::NUMERIC)
                END
            );

        END IF;

        -- gas / condensate properties
        has_gas_properties := FALSE;
        SELECT
            COALESCE(
                discovery_rec.gas_co2::text,
                discovery_rec.gas_gravity::text,
                discovery_rec.gas_h2s::text,
                discovery_rec.gas_n2::text,
                discovery_rec.gas_other::text
            ) IS NOT NULL
        INTO has_gas_properties;

        has_condensate_properties := FALSE;
        SELECT
            COALESCE(
                discovery_rec.cond_gravity::text,
                discovery_rec.cond_viscosity::text,
                discovery_rec.cond_sulphur::text,
                discovery_rec.cond_wax::text,
                discovery_rec.cond_other::text
            ) IS NOT NULL
        INTO has_condensate_properties;

        IF has_gas_properties OR has_condensate_properties THEN

            INSERT INTO remi.gas_fluid_properties (
                id,
                reservoir_submission_detail_id,
                gas_specific_gravity,
                gas_h2s,
                gas_co2,
                gas_n2,
                condensate_present,
                condensate_gravity,
                condensate_viscosity,
                condensate_sulphur,
                condensate_wax,
                condensate_other_incl_wax,
                advise_other_gases_concentration
            ) VALUES (
                gen_random_uuid(),
                discovery_rec.rsd_id,
                CASE
                    WHEN discovery_rec.gas_gravity ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.gas_gravity::NUMERIC, 2)
                END,
                CASE
                    WHEN discovery_rec.gas_h2s ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.gas_h2s::NUMERIC)
                END,
                CASE
                    WHEN discovery_rec.gas_co2 ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.gas_co2::NUMERIC, 2)
                END,
                CASE
                    WHEN discovery_rec.gas_n2 ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.gas_co2::NUMERIC, 2)
                END,
                has_condensate_properties,
                CASE
                    WHEN discovery_rec.cond_gravity ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.cond_gravity::NUMERIC)
                END,
                CASE
                    WHEN discovery_rec.cond_viscosity ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.cond_viscosity::NUMERIC, 2)
                END,
                CASE
                    WHEN discovery_rec.cond_sulphur ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.cond_sulphur::NUMERIC)
                END,
                CASE
                    WHEN discovery_rec.cond_wax ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.cond_wax::NUMERIC, 2)
                END,
                CASE
                    WHEN discovery_rec.cond_other ~ '^[0-9\.]+$'
                    THEN ROUND(discovery_rec.cond_other::NUMERIC)
                END,
                discovery_rec.gas_other
            );

        END IF;

        -- general properties
        has_general_properties := FALSE;
        SELECT
            COALESCE(
                discovery_rec.datum_depth::text,
                discovery_rec.datum_temp::text,
                discovery_rec.datum_pressure::text,
                discovery_rec.datum_saturation_pressure::text
            ) IS NOT NULL
        INTO has_general_properties;

        IF has_general_properties THEN

            INSERT INTO remi.general_fluid_properties(
                id,
                reservoir_submission_detail_id,
                datum_depth,
                temperature_at_datum_depth,
                pressure_at_datum_depth,
                saturation_pressure_at_datum_depth,
                water_salinity
            ) VALUES (
                gen_random_uuid(),
                discovery_rec.rsd_id,
                CASE
                    WHEN discovery_rec.datum_units IN ('ft tvdss', 'feet', 'ft MD')
                        THEN discovery_rec.datum_depth * 0.3048
                    ELSE discovery_rec.datum_depth
                END,
                (discovery_rec.datum_temp - 32) * 1.8,
                discovery_rec.datum_pressure,
                discovery_rec.datum_saturation_pressure,
                NULL -- does this exist in PaDI?
            );

        END IF;

    END LOOP;

    -- clean up entries which only had text values instead of numeric which would have been inserted as NULL values
    DELETE FROM remi.oil_fluid_properties ofp
    WHERE COALESCE(
        ofp.oil_gravity::TEXT,
        ofp.oil_viscosity::TEXT,
        ofp.oil_sulphur::TEXT,
        ofp.oil_wax::TEXT,
        ofp.oil_other_wax_appearance_temperature::TEXT
    ) IS NULL;

    DELETE FROM remi.gas_fluid_properties gfp
    WHERE COALESCE(
        gfp.gas_specific_gravity::TEXT,
        gfp.gas_h2s::TEXT,
        gfp.gas_co2::TEXT,
        gfp.gas_n2::TEXT,
        gfp.condensate_gravity::TEXT,
        gfp.condensate_viscosity::TEXT,
        gfp.condensate_sulphur::TEXT,
        gfp.condensate_wax::TEXT,
        gfp.condensate_other_incl_wax::TEXT,
        gfp.advise_other_gases_concentration::TEXT
    ) IS NULL;

END;

$$;