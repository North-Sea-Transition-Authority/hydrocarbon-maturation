DO $$
DECLARE

    source_and_seal_rec RECORD;
    prospect_reservoir_rec RECORD;
    discovery_reservoir_rec RECORD;
    source_formation TEXT;
    source_manual_formation TEXT;
    source_age TEXT;
    source_manual_age TEXT;
    source_lithology_option TEXT;
    source_lithology_other_text TEXT;
    top_seal_formation TEXT;
    top_seal_manual_formation TEXT;
    top_seal_age TEXT;
    top_seal_manual_age TEXT;
    top_seal_lithology_option TEXT;
    top_seal_lithology_other_text TEXT;
    reservoir_formation TEXT;
    reservoir_manual_formation TEXT;
    reservoir_age TEXT;
    reservoir_manual_age TEXT;
    reservoir_lithology_option TEXT;
    reservoir_lithology_other_text TEXT;

BEGIN

    FOR source_and_seal_rec IN (
        SELECT prdc.*, sd.id sd_id
        FROM remi.padi_res_data_cos prdc
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdc.resdat_id
    )
    LOOP

        source_formation := NULL;
        source_manual_formation := NULL;
        source_age := NULL;
        source_manual_age := NULL;
        source_lithology_option := NULL;
        source_lithology_other_text := NULL;
        top_seal_formation := NULL;
        top_seal_manual_formation := NULL;
        top_seal_age := NULL;
        top_seal_manual_age := NULL;
        top_seal_lithology_option := NULL;
        top_seal_lithology_other_text := NULL;
        reservoir_formation := NULL;
        reservoir_manual_formation := NULL;
        reservoir_age := NULL;
        reservoir_manual_age := NULL;
        reservoir_lithology_option := NULL;
        reservoir_lithology_other_text := NULL;

        SELECT
            CASE
                WHEN REPLACE(REPLACE(UPPER(source_and_seal_rec.play_source_formation), ' ', '_'), '''', '')
                IN ('ASGARD',
                    'AUK',
                    'BALDER',
                    'BEATRICE',
                    'BEAULY',
                    'BERYL',
                    'BOSIES_BANK',
                    'BRAEMORE_MUDSTONE',
                    'BROOM',
                    'BRORA_COAL',
                    'BRYNE',
                    'BUCHAN',
                    'BUNTER_SHALE',
                    'CAISTER',
                    'CAITHNESS_FLAGSTONE',
                    'CEMENTSTONE',
                    'CLEAVER',
                    'COMMODORE',
                    'CORMORANT',
                    'CRUISER',
                    'DORNOCH',
                    'DUNNET_HEAD_SANDSTONE',
                    'EKOFISK',
                    'ERIKSSON',
                    'ETIVE',
                    'FARSUND',
                    'FELL',
                    'FINDHORN',
                    'FLETT',
                    'FOULA',
                    'FRASERBURGH',
                    'FULMAR',
                    'GOLSPIE',
                    'HALIBUT_CARBONATE',
                    'HEATHER',
                    'HERRING',
                    'HIDRA',
                    'HOD',
                    'HORDA',
                    'HOY_SANDSTONE',
                    'HUTTON',
                    'JORSALFARE',
                    'KIMMERIDGE_CLAY',
                    'KINNESSWOOD',
                    'KYRRE',
                    'LADYS_WALK',
                    'LAMBA',
                    'LARK',
                    'LEMAN',
                    'LINNHE',
                    'LISTA',
                    'LOSSIEHEAD',
                    'LOWER_CLAIR',
                    'MACBETH',
                    'MACKEREL',
                    'MAINS',
                    'MANDAL',
                    'MAUREEN',
                    'MIDDLE_CLAIR',
                    'MILLSTONE_GRIT',
                    'MORRISON',
                    'MOUSA',
                    'NANSEN',
                    'NAUST',
                    'NESS',
                    'ORCADIA',
                    'ORRIN',
                    'OTTER_BANK',
                    'OUSDALE_ARKOSE',
                    'PENTLAND',
                    'PIPER',
                    'RANNOCH',
                    'RODBY',
                    'RONA',
                    'ROYAL_SOVEREIGN',
                    'SAUDA',
                    'SCREMERSTON',
                    'SELE',
                    'SHEARWATER_SALT',
                    'SILVERPIT',
                    'SKADE',
                    'SKAGERRAK',
                    'SMITH_BANK',
                    'SOLA',
                    'SOLAN_BANK',
                    'STATFJORD',
                    'STOTFIELD_CALCRETE',
                    'STRATH_RORY',
                    'STRUIE',
                    'SULLOM',
                    'SVARTE',
                    'TARBERT',
                    'TAU',
                    'TAYPORT',
                    'TEITH',
                    'TOR',
                    'TURBOT_ANDYDRITE',
                    'ULA',
                    'UPPER_CLAIR',
                    'UPPER_GREENSAND',
                    'UTSIRA',
                    'VAILA',
                    'VALHALL',
                    'VICTORY',
                    'WESTOE',
                    'WICK',
                    'YESNABY_SANDSTONE',
                    'YOREDALE')
                THEN REPLACE(REPLACE(UPPER(source_and_seal_rec.play_source_formation), ' ', '_'), '''', '')
            END
        INTO source_formation;

        IF source_formation IS NULL THEN
            source_manual_formation := source_and_seal_rec.play_source_formation;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(REPLACE(UPPER(source_and_seal_rec.play_source_age), ' ', '_'), '&', 'AND')
                IN (
                    'PLEISTOCENE',
                    'PLIOCENE',
                    'UPPER_MIOCENE',
                    'MIDDLE_MIOCENE',
                    'LOWER_MIOCENE',
                    'UPPER_OLIGOCENE',
                    'LOWER_OLIGOCENE',
                    'UPPER_EOCENE',
                    'MIDDLE_EOCENE',
                    'LOWER_EOCENE',
                    'UPPER_PALEOCENE',
                    'LOWER_PALEOCENE',
                    'UPPER_CRETACEOUS',
                    'LOWER_CRETACEOUS',
                    'UPPER_JURASSIC',
                    'MIDDLE_JURASSIC',
                    'LOWER_JURASSIC',
                    'UPPER_TRIASSIC',
                    'MIDDLE_TRIASSIC',
                    'LOWER_TRIASSIC',
                    'UPPER_PERMIAN',
                    'LR_AND_MIDDLE_PERMIAN',
                    'UPPER_CARBONIFEROUS',
                    'LOWER_CARBONIFEROUS',
                    'UPPER_DEVONIAN',
                    'MIDDLE_DEVONIAN',
                    'LOWER_DEVONIAN'
                    )
                THEN REPLACE(REPLACE(UPPER(source_and_seal_rec.play_source_age), ' ', '_'), '&', 'AND')
            END
        INTO source_age;

        IF source_age IS NULL THEN
            source_manual_age := source_and_seal_rec.play_source_age;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(UPPER(source_and_seal_rec.play_source_lithology), ' ', '_')
                    IN (
                        'SANDSTONE',
                        'SILTSTONE',
                        'MUDSTONE',
                        'LIMESTONE',
                        'DOLOMITE',
                        'SALT',
                        'NON_SALT_EVAPORITE',
                        'COAL',
                        'FRACTURED_BASEMENT',
                        'VOLCANICS'
                    )
                    THEN REPLACE(UPPER(source_and_seal_rec.play_source_lithology), ' ', '_')
                WHEN source_and_seal_rec.play_source_lithology IS NOT NULL THEN 'OTHER'
            END
        INTO source_lithology_option;

        IF source_lithology_option = 'OTHER' THEN
            source_lithology_other_text := source_and_seal_rec.play_source_lithology;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(REPLACE(UPPER(source_and_seal_rec.play_seal_formation), ' ', '_'), '''', '')
                    IN ('ASGARD',
                        'AUK',
                        'BALDER',
                        'BEATRICE',
                        'BEAULY',
                        'BERYL',
                        'BOSIES_BANK',
                        'BRAEMORE_MUDSTONE',
                        'BROOM',
                        'BRORA_COAL',
                        'BRYNE',
                        'BUCHAN',
                        'BUNTER_SHALE',
                        'CAISTER',
                        'CAITHNESS_FLAGSTONE',
                        'CEMENTSTONE',
                        'CLEAVER',
                        'COMMODORE',
                        'CORMORANT',
                        'CRUISER',
                        'DORNOCH',
                        'DUNNET_HEAD_SANDSTONE',
                        'EKOFISK',
                        'ERIKSSON',
                        'ETIVE',
                        'FARSUND',
                        'FELL',
                        'FINDHORN',
                        'FLETT',
                        'FOULA',
                        'FRASERBURGH',
                        'FULMAR',
                        'GOLSPIE',
                        'HALIBUT_CARBONATE',
                        'HEATHER',
                        'HERRING',
                        'HIDRA',
                        'HOD',
                        'HORDA',
                        'HOY_SANDSTONE',
                        'HUTTON',
                        'JORSALFARE',
                        'KIMMERIDGE_CLAY',
                        'KINNESSWOOD',
                        'KYRRE',
                        'LADYS_WALK',
                        'LAMBA',
                        'LARK',
                        'LEMAN',
                        'LINNHE',
                        'LISTA',
                        'LOSSIEHEAD',
                        'LOWER_CLAIR',
                        'MACBETH',
                        'MACKEREL',
                        'MAINS',
                        'MANDAL',
                        'MAUREEN',
                        'MIDDLE_CLAIR',
                        'MILLSTONE_GRIT',
                        'MORRISON',
                        'MOUSA',
                        'NANSEN',
                        'NAUST',
                        'NESS',
                        'ORCADIA',
                        'ORRIN',
                        'OTTER_BANK',
                        'OUSDALE_ARKOSE',
                        'PENTLAND',
                        'PIPER',
                        'RANNOCH',
                        'RODBY',
                        'RONA',
                        'ROYAL_SOVEREIGN',
                        'SAUDA',
                        'SCREMERSTON',
                        'SELE',
                        'SHEARWATER_SALT',
                        'SILVERPIT',
                        'SKADE',
                        'SKAGERRAK',
                        'SMITH_BANK',
                        'SOLA',
                        'SOLAN_BANK',
                        'STATFJORD',
                        'STOTFIELD_CALCRETE',
                        'STRATH_RORY',
                        'STRUIE',
                        'SULLOM',
                        'SVARTE',
                        'TARBERT',
                        'TAU',
                        'TAYPORT',
                        'TEITH',
                        'TOR',
                        'TURBOT_ANDYDRITE',
                        'ULA',
                        'UPPER_CLAIR',
                        'UPPER_GREENSAND',
                        'UTSIRA',
                        'VAILA',
                        'VALHALL',
                        'VICTORY',
                        'WESTOE',
                        'WICK',
                        'YESNABY_SANDSTONE',
                        'YOREDALE')
                    THEN REPLACE(REPLACE(UPPER(source_and_seal_rec.play_seal_formation), ' ', '_'), '''', '')
                END
        INTO top_seal_formation;

        IF top_seal_formation IS NULL THEN
            top_seal_manual_formation := source_and_seal_rec.play_seal_formation;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(REPLACE(UPPER(source_and_seal_rec.play_seal_age), ' ', '_'), '&', 'AND')
                IN (
                    'PLEISTOCENE',
                    'PLIOCENE',
                    'UPPER_MIOCENE',
                    'MIDDLE_MIOCENE',
                    'LOWER_MIOCENE',
                    'UPPER_OLIGOCENE',
                    'LOWER_OLIGOCENE',
                    'UPPER_EOCENE',
                    'MIDDLE_EOCENE',
                    'LOWER_EOCENE',
                    'UPPER_PALEOCENE',
                    'LOWER_PALEOCENE',
                    'UPPER_CRETACEOUS',
                    'LOWER_CRETACEOUS',
                    'UPPER_JURASSIC',
                    'MIDDLE_JURASSIC',
                    'LOWER_JURASSIC',
                    'UPPER_TRIASSIC',
                    'MIDDLE_TRIASSIC',
                    'LOWER_TRIASSIC',
                    'UPPER_PERMIAN',
                    'LR_AND_MIDDLE_PERMIAN',
                    'UPPER_CARBONIFEROUS',
                    'LOWER_CARBONIFEROUS',
                    'UPPER_DEVONIAN',
                    'MIDDLE_DEVONIAN',
                    'LOWER_DEVONIAN'
                     )
                THEN REPLACE(REPLACE(UPPER(source_and_seal_rec.play_seal_age), ' ', '_'), '&', 'AND')
            END
        INTO top_seal_age;

        IF top_seal_age IS NULL THEN
            top_seal_manual_age := source_and_seal_rec.play_seal_age;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(UPPER(source_and_seal_rec.play_seal_lithology), ' ', '_')
                IN (
                    'SANDSTONE',
                    'SILTSTONE',
                    'MUDSTONE',
                    'LIMESTONE',
                    'DOLOMITE',
                    'SALT',
                    'NON_SALT_EVAPORITE',
                    'COAL',
                    'FRACTURED_BASEMENT',
                    'VOLCANICS'
                )
                THEN REPLACE(UPPER(source_and_seal_rec.play_seal_lithology), ' ', '_')
                WHEN source_and_seal_rec.play_seal_lithology IS NOT NULL THEN 'OTHER'
            END
        INTO top_seal_lithology_option;

        IF top_seal_lithology_option = 'OTHER' THEN
            top_seal_lithology_other_text := source_and_seal_rec.play_seal_lithology;
        END IF;

        IF COALESCE(
            source_formation,
            source_manual_formation,
            source_age,
            source_manual_age,
            source_lithology_option,
            source_lithology_other_text,
            top_seal_formation,
            top_seal_manual_formation,
            top_seal_age,
            top_seal_manual_age,
            top_seal_lithology_option,
            top_seal_lithology_other_text
        ) IS NOT NULL THEN

            INSERT INTO remi.source_and_seal_details (
                id,
                submission_detail_id,
                source_formation_name,
                source_formation_name_manual,
                source_age_stage,
                source_age_stage_manual,
                source_lithology,
                source_lithology_other,
                top_seal_formation_name,
                top_seal_formation_name_manual,
                top_seal_age_stage,
                top_seal_age_stage_manual,
                top_seal_lithology,
                top_seal_lithology_other
            ) VALUES (
                gen_random_uuid(),
                source_and_seal_rec.sd_id,
                source_formation,
                source_manual_formation,
                source_age,
                source_manual_age,
                source_lithology_option,
                source_lithology_other_text,
                top_seal_formation,
                top_seal_manual_formation,
                top_seal_age,
                top_seal_manual_age,
                top_seal_lithology_option,
                top_seal_lithology_other_text
            );

        END IF;

    END LOOP;

    FOR prospect_reservoir_rec IN (
        SELECT prdc.*, sd.id sd_id, rsd.id rsd_id, rp.id rp_id
        FROM remi.padi_res_data_cos prdc
        JOIN remi.submission_details sd ON sd.legacy_res_data_id = prdc.resdat_id
        JOIN remi.submissions s ON s.id = sd.submission_id
        JOIN remi.resources r ON r.id = s.resource_id
        JOIN remi.reservoir_submission_details rsd ON rsd.submission_detail_id = sd.id
        LEFT JOIN remi.reservoir_properties rp ON rp.reservoir_submission_detail_id = rsd.id
        WHERE r.type = 'PROSPECT'
    )
    LOOP

        SELECT
            CASE
                WHEN REPLACE(REPLACE(UPPER(prospect_reservoir_rec.play_reservoir_formation), ' ', '_'), '''', '')
                IN ('ASGARD',
                    'AUK',
                    'BALDER',
                    'BEATRICE',
                    'BEAULY',
                    'BERYL',
                    'BOSIES_BANK',
                    'BRAEMORE_MUDSTONE',
                    'BROOM',
                    'BRORA_COAL',
                    'BRYNE',
                    'BUCHAN',
                    'BUNTER_SHALE',
                    'CAISTER',
                    'CAITHNESS_FLAGSTONE',
                    'CEMENTSTONE',
                    'CLEAVER',
                    'COMMODORE',
                    'CORMORANT',
                    'CRUISER',
                    'DORNOCH',
                    'DUNNET_HEAD_SANDSTONE',
                    'EKOFISK',
                    'ERIKSSON',
                    'ETIVE',
                    'FARSUND',
                    'FELL',
                    'FINDHORN',
                    'FLETT',
                    'FOULA',
                    'FRASERBURGH',
                    'FULMAR',
                    'GOLSPIE',
                    'HALIBUT_CARBONATE',
                    'HEATHER',
                    'HERRING',
                    'HIDRA',
                    'HOD',
                    'HORDA',
                    'HOY_SANDSTONE',
                    'HUTTON',
                    'JORSALFARE',
                    'KIMMERIDGE_CLAY',
                    'KINNESSWOOD',
                    'KYRRE',
                    'LADYS_WALK',
                    'LAMBA',
                    'LARK',
                    'LEMAN',
                    'LINNHE',
                    'LISTA',
                    'LOSSIEHEAD',
                    'LOWER_CLAIR',
                    'MACBETH',
                    'MACKEREL',
                    'MAINS',
                    'MANDAL',
                    'MAUREEN',
                    'MIDDLE_CLAIR',
                    'MILLSTONE_GRIT',
                    'MORRISON',
                    'MOUSA',
                    'NANSEN',
                    'NAUST',
                    'NESS',
                    'ORCADIA',
                    'ORRIN',
                    'OTTER_BANK',
                    'OUSDALE_ARKOSE',
                    'PENTLAND',
                    'PIPER',
                    'RANNOCH',
                    'RODBY',
                    'RONA',
                    'ROYAL_SOVEREIGN',
                    'SAUDA',
                    'SCREMERSTON',
                    'SELE',
                    'SHEARWATER_SALT',
                    'SILVERPIT',
                    'SKADE',
                    'SKAGERRAK',
                    'SMITH_BANK',
                    'SOLA',
                    'SOLAN_BANK',
                    'STATFJORD',
                    'STOTFIELD_CALCRETE',
                    'STRATH_RORY',
                    'STRUIE',
                    'SULLOM',
                    'SVARTE',
                    'TARBERT',
                    'TAU',
                    'TAYPORT',
                    'TEITH',
                    'TOR',
                    'TURBOT_ANDYDRITE',
                    'ULA',
                    'UPPER_CLAIR',
                    'UPPER_GREENSAND',
                    'UTSIRA',
                    'VAILA',
                    'VALHALL',
                    'VICTORY',
                    'WESTOE',
                    'WICK',
                    'YESNABY_SANDSTONE',
                    'YOREDALE')
                THEN REPLACE(REPLACE(UPPER(prospect_reservoir_rec.play_reservoir_formation), ' ', '_'), '''', '')
            END
        INTO reservoir_formation;

        IF reservoir_formation IS NULL THEN
            reservoir_manual_formation := prospect_reservoir_rec.play_reservoir_formation;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(REPLACE(UPPER(prospect_reservoir_rec.play_reservoir_age), ' ', '_'), '&', 'AND')
                IN (
                    'PLEISTOCENE',
                    'PLIOCENE',
                    'UPPER_MIOCENE',
                    'MIDDLE_MIOCENE',
                    'LOWER_MIOCENE',
                    'UPPER_OLIGOCENE',
                    'LOWER_OLIGOCENE',
                    'UPPER_EOCENE',
                    'MIDDLE_EOCENE',
                    'LOWER_EOCENE',
                    'UPPER_PALEOCENE',
                    'LOWER_PALEOCENE',
                    'UPPER_CRETACEOUS',
                    'LOWER_CRETACEOUS',
                    'UPPER_JURASSIC',
                    'MIDDLE_JURASSIC',
                    'LOWER_JURASSIC',
                    'UPPER_TRIASSIC',
                    'MIDDLE_TRIASSIC',
                    'LOWER_TRIASSIC',
                    'UPPER_PERMIAN',
                    'LR_AND_MIDDLE_PERMIAN',
                    'UPPER_CARBONIFEROUS',
                    'LOWER_CARBONIFEROUS',
                    'UPPER_DEVONIAN',
                    'MIDDLE_DEVONIAN',
                    'LOWER_DEVONIAN'
                )
                THEN REPLACE(REPLACE(UPPER(prospect_reservoir_rec.play_reservoir_age), ' ', '_'), '&', 'AND')
            END
        INTO reservoir_age;

        IF reservoir_age IS NULL THEN
            reservoir_manual_age := prospect_reservoir_rec.play_reservoir_age;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(UPPER(prospect_reservoir_rec.play_reservoir_lithology), ' ', '_')
                IN (
                    'SANDSTONE',
                    'SILTSTONE',
                    'MUDSTONE',
                    'LIMESTONE',
                    'DOLOMITE',
                    'SALT',
                    'NON_SALT_EVAPORITE',
                    'COAL',
                    'FRACTURED_BASEMENT',
                    'VOLCANICS'
                )
                THEN REPLACE(UPPER(prospect_reservoir_rec.play_reservoir_lithology), ' ', '_')
                WHEN prospect_reservoir_rec.play_reservoir_lithology IS NOT NULL THEN 'OTHER'
                END
        INTO reservoir_lithology_option;

        IF reservoir_lithology_option = 'OTHER' THEN
            reservoir_lithology_other_text := prospect_reservoir_rec.play_reservoir_lithology;
        END IF;

        IF COALESCE(
            reservoir_formation,
            reservoir_manual_formation,
            reservoir_age,
            reservoir_manual_age,
            reservoir_lithology_option,
            reservoir_lithology_other_text
        ) IS NOT NULL THEN

            IF prospect_reservoir_rec.rp_id IS NOT NULL THEN

                UPDATE remi.reservoir_properties
                SET
                    formation_name = reservoir_formation,
                    formation_name_manual = reservoir_manual_formation,
                    age_stage = reservoir_age,
                    age_stage_manual = reservoir_manual_age,
                    lithology = reservoir_lithology_option,
                    lithology_other = reservoir_lithology_other_text
                WHERE id = prospect_reservoir_rec.rp_id;

            ELSE

                INSERT INTO remi.reservoir_properties (
                    id,
                    reservoir_submission_detail_id,
                    age_stage,
                    age_stage_manual,
                    formation_name,
                    formation_name_manual,
                    lithology,
                    lithology_other
                ) VALUES (
                    gen_random_uuid(),
                    prospect_reservoir_rec.rsd_id,
                    reservoir_age,
                    reservoir_manual_age,
                    reservoir_formation,
                    reservoir_manual_formation,
                    reservoir_lithology_option,
                    reservoir_lithology_other_text
                );

            END IF;

        END IF;

    END LOOP;

    FOR discovery_reservoir_rec IN (
        SELECT prdc.*, rsd.id rsd_id, rp.id rp_id
        FROM remi.padi_res_data_cos prdc
        JOIN remi.padi_res_data_header prdh ON prdh.resdat_id = prdc.resdat_id
        JOIN remi.padi_reservoirs pr ON pr.legacy_res_data_id = prdh.resdat_id
        JOIN remi.reservoir_submission_details rsd ON rsd.reservoir = pr.remi_reservoir_id
        LEFT JOIN remi.reservoir_properties rp ON rp.reservoir_submission_detail_id = rsd.id
        WHERE prdh.opportunity_type = 'Discovery'
    )
    LOOP

        SELECT
            CASE
                WHEN REPLACE(REPLACE(UPPER(discovery_reservoir_rec.play_reservoir_formation), ' ', '_'), '''', '')
                    IN ('ASGARD',
                        'AUK',
                        'BALDER',
                        'BEATRICE',
                        'BEAULY',
                        'BERYL',
                        'BOSIES_BANK',
                        'BRAEMORE_MUDSTONE',
                        'BROOM',
                        'BRORA_COAL',
                        'BRYNE',
                        'BUCHAN',
                        'BUNTER_SHALE',
                        'CAISTER',
                        'CAITHNESS_FLAGSTONE',
                        'CEMENTSTONE',
                        'CLEAVER',
                        'COMMODORE',
                        'CORMORANT',
                        'CRUISER',
                        'DORNOCH',
                        'DUNNET_HEAD_SANDSTONE',
                        'EKOFISK',
                        'ERIKSSON',
                        'ETIVE',
                        'FARSUND',
                        'FELL',
                        'FINDHORN',
                        'FLETT',
                        'FOULA',
                        'FRASERBURGH',
                        'FULMAR',
                        'GOLSPIE',
                        'HALIBUT_CARBONATE',
                        'HEATHER',
                        'HERRING',
                        'HIDRA',
                        'HOD',
                        'HORDA',
                        'HOY_SANDSTONE',
                        'HUTTON',
                        'JORSALFARE',
                        'KIMMERIDGE_CLAY',
                        'KINNESSWOOD',
                        'KYRRE',
                        'LADYS_WALK',
                        'LAMBA',
                        'LARK',
                        'LEMAN',
                        'LINNHE',
                        'LISTA',
                        'LOSSIEHEAD',
                        'LOWER_CLAIR',
                        'MACBETH',
                        'MACKEREL',
                        'MAINS',
                        'MANDAL',
                        'MAUREEN',
                        'MIDDLE_CLAIR',
                        'MILLSTONE_GRIT',
                        'MORRISON',
                        'MOUSA',
                        'NANSEN',
                        'NAUST',
                        'NESS',
                        'ORCADIA',
                        'ORRIN',
                        'OTTER_BANK',
                        'OUSDALE_ARKOSE',
                        'PENTLAND',
                        'PIPER',
                        'RANNOCH',
                        'RODBY',
                        'RONA',
                        'ROYAL_SOVEREIGN',
                        'SAUDA',
                        'SCREMERSTON',
                        'SELE',
                        'SHEARWATER_SALT',
                        'SILVERPIT',
                        'SKADE',
                        'SKAGERRAK',
                        'SMITH_BANK',
                        'SOLA',
                        'SOLAN_BANK',
                        'STATFJORD',
                        'STOTFIELD_CALCRETE',
                        'STRATH_RORY',
                        'STRUIE',
                        'SULLOM',
                        'SVARTE',
                        'TARBERT',
                        'TAU',
                        'TAYPORT',
                        'TEITH',
                        'TOR',
                        'TURBOT_ANDYDRITE',
                        'ULA',
                        'UPPER_CLAIR',
                        'UPPER_GREENSAND',
                        'UTSIRA',
                        'VAILA',
                        'VALHALL',
                        'VICTORY',
                        'WESTOE',
                        'WICK',
                        'YESNABY_SANDSTONE',
                        'YOREDALE')
                    THEN REPLACE(REPLACE(UPPER(discovery_reservoir_rec.play_reservoir_formation), ' ', '_'), '''', '')
                END
        INTO reservoir_formation;

        IF reservoir_formation IS NULL THEN
            reservoir_manual_formation := discovery_reservoir_rec.play_reservoir_formation;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(REPLACE(UPPER(discovery_reservoir_rec.play_reservoir_age), ' ', '_'), '&', 'AND')
                    IN (
                        'PLEISTOCENE',
                        'PLIOCENE',
                        'UPPER_MIOCENE',
                        'MIDDLE_MIOCENE',
                        'LOWER_MIOCENE',
                        'UPPER_OLIGOCENE',
                        'LOWER_OLIGOCENE',
                        'UPPER_EOCENE',
                        'MIDDLE_EOCENE',
                        'LOWER_EOCENE',
                        'UPPER_PALEOCENE',
                        'LOWER_PALEOCENE',
                        'UPPER_CRETACEOUS',
                        'LOWER_CRETACEOUS',
                        'UPPER_JURASSIC',
                        'MIDDLE_JURASSIC',
                        'LOWER_JURASSIC',
                        'UPPER_TRIASSIC',
                        'MIDDLE_TRIASSIC',
                        'LOWER_TRIASSIC',
                        'UPPER_PERMIAN',
                        'LR_AND_MIDDLE_PERMIAN',
                        'UPPER_CARBONIFEROUS',
                        'LOWER_CARBONIFEROUS',
                        'UPPER_DEVONIAN',
                        'MIDDLE_DEVONIAN',
                        'LOWER_DEVONIAN'
                         )
                    THEN REPLACE(REPLACE(UPPER(discovery_reservoir_rec.play_reservoir_age), ' ', '_'), '&', 'AND')
                END
        INTO reservoir_age;

        IF reservoir_age IS NULL THEN
            reservoir_manual_age := discovery_reservoir_rec.play_reservoir_age;
        END IF;

        SELECT
            CASE
                WHEN REPLACE(UPPER(discovery_reservoir_rec.play_reservoir_lithology), ' ', '_')
                    IN (
                        'SANDSTONE',
                        'SILTSTONE',
                        'MUDSTONE',
                        'LIMESTONE',
                        'DOLOMITE',
                        'SALT',
                        'NON_SALT_EVAPORITE',
                        'COAL',
                        'FRACTURED_BASEMENT',
                        'VOLCANICS'
                         )
                    THEN REPLACE(UPPER(discovery_reservoir_rec.play_reservoir_lithology), ' ', '_')
                WHEN discovery_reservoir_rec.play_reservoir_lithology IS NOT NULL THEN 'OTHER'
                END
        INTO reservoir_lithology_option;

        IF reservoir_lithology_option = 'OTHER' THEN
            reservoir_lithology_other_text := discovery_reservoir_rec.play_reservoir_lithology;
        END IF;

        IF COALESCE(
            reservoir_formation,
            reservoir_manual_formation,
            reservoir_age,
            reservoir_manual_age,
            reservoir_lithology_option,
            reservoir_lithology_other_text
        ) IS NOT NULL THEN

            IF discovery_reservoir_rec.rp_id IS NOT NULL THEN

                UPDATE remi.reservoir_properties
                SET
                    formation_name = reservoir_formation,
                    formation_name_manual = reservoir_manual_formation,
                    age_stage = reservoir_age,
                    age_stage_manual = reservoir_manual_age,
                    lithology = reservoir_lithology_option,
                    lithology_other = reservoir_lithology_other_text
                WHERE id = discovery_reservoir_rec.rp_id;

            ELSE

                INSERT INTO remi.reservoir_properties (
                    id,
                    reservoir_submission_detail_id,
                    age_stage,
                    age_stage_manual,
                    formation_name,
                    formation_name_manual,
                    lithology,
                    lithology_other
                ) VALUES (
                    gen_random_uuid(),
                    discovery_reservoir_rec.rsd_id,
                    reservoir_age,
                    reservoir_manual_age,
                    reservoir_formation,
                    reservoir_manual_formation,
                    reservoir_lithology_option,
                    reservoir_lithology_other_text
                );

            END IF;

        END IF;

    END LOOP;

END;

$$;