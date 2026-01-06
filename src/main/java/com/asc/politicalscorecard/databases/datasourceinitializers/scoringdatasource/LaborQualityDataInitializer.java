package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes the Labor Quality Per State 2022 sample data.
 * This data comes from the frontend mock data and represents
 * a real-world scoring scenario across multiple US states.
 *
 * The Labor Quality Score includes:
 * - 3 positive terms: Union Participation (weight 5), LFPR (weight 2), Minimum Wage (weight 8)
 * - 1 negative term: Impoverished Workforce (weight 6)
 * - Total weight: 21
 * - States: Alabama, California, Washington DC, Idaho, Texas, Florida, Alaska, South Dakota, West Virginia, Ohio, New Jersey
 */
public class LaborQualityDataInitializer {

    private final JdbcClient scoringJdbcClient;

    // Term IDs
    private static final String TERM_UNION_PARTICIPATION = "term-union-participation";
    private static final String TERM_LFPR = "term-labor-force-participation";
    private static final String TERM_MIN_WAGE = "term-minimum-wage";
    private static final String TERM_IMPOVERISHED = "term-impoverished-workforce";

    // Value Metadata IDs
    private static final String VM_UNION_PARTICIPATION = "vm-union-participation";
    private static final String VM_LFPR = "vm-lfpr";
    private static final String VM_MIN_WAGE = "vm-minimum-wage";
    private static final String VM_IMPOVERISHED = "vm-impoverished-workforce";

    // Context IDs
    private static final String CONTEXT_TIMEFRAME_2022 = "context-timeframe-2022";

    public LaborQualityDataInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeData() {
        System.out.println("Initializing Labor Quality Per State 2022 data...");

        // Check if contextualized term data already exists
        Integer termCount = scoringJdbcClient.sql(
            "SELECT COUNT(*) FROM contextualized_term WHERE id = ?"
        ).param("ct-alabama-union-2022").query(Integer.class).single();

        if (termCount == 0) {
            // Initialize base data: Terms -> ValueMetadata -> Contexts -> ContextualizedTerms
            insertBaseTerms();
            insertValueMetadata();
            insertTimeframeContext();
            insertLocationContexts();
            insertContextualizedTerms();
            System.out.println("Labor Quality base data initialized successfully.");
        } else {
            System.out.println("Labor Quality base data already exists. Skipping base data initialization.");
        }

        // Check if election data already exists (separately from base data)
        Integer electionCount = scoringJdbcClient.sql(
            "SELECT COUNT(*) FROM worldview_election WHERE id = ?"
        ).param("election-labor-quality-2024").query(Integer.class).single();

        if (electionCount == 0) {
            // Initialize election-specific data: Election -> Ballot -> WeightedTerms
            insertLaborQualityElection();
            insertLaborQualityBallotTemplate();
            insertWeightedTerms();
            System.out.println("Labor Quality election data initialized successfully.");
        } else {
            System.out.println("Labor Quality election already exists. Skipping election initialization.");
        }
    }

    private void insertBaseTerms() {
        System.out.println("Inserting base terms...");

        // Union Participation
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO term (id, name, description, source, category) VALUES (?, ?, ?, ?, ?)"
        ).params(
            TERM_UNION_PARTICIPATION,
            "Union Participation Rate",
            "Percentage of workforce represented by labor unions",
            "Bureau of Labor Statistics",
            "Labor"
        ).update();

        // Labor Force Participation Rate
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO term (id, name, description, source, category) VALUES (?, ?, ?, ?, ?)"
        ).params(
            TERM_LFPR,
            "Labor Force Participation Rate",
            "Percentage of working-age population that is working or actively seeking work",
            "Bureau of Labor Statistics",
            "Labor"
        ).update();

        // Minimum Wage
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO term (id, name, description, source, category) VALUES (?, ?, ?, ?, ?)"
        ).params(
            TERM_MIN_WAGE,
            "Minimum Wage",
            "Minimum hourly wage (USD per hour)",
            "Department of Labor",
            "Labor"
        ).update();

        // Impoverished Workforce
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO term (id, name, description, source, category) VALUES (?, ?, ?, ?, ?)"
        ).params(
            TERM_IMPOVERISHED,
            "Impoverished Workforce Rate",
            "Percentage of workforce living below poverty line",
            "Census Bureau",
            "Labor"
        ).update();
    }

    private void insertValueMetadata() {
        System.out.println("Inserting value metadata...");

        // Union Participation metadata
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO value_metadata (id, value_type, unit, label, is_positive) VALUES (?, ?, ?, ?, ?)"
        ).params(
            VM_UNION_PARTICIPATION,
            "PERCENTAGE",
            "%",
            "Union Participation Rate",
            true
        ).update();

        // LFPR metadata
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO value_metadata (id, value_type, unit, label, is_positive) VALUES (?, ?, ?, ?, ?)"
        ).params(
            VM_LFPR,
            "PERCENTAGE",
            "%",
            "Labor Force Participation Rate",
            true
        ).update();

        // Minimum Wage metadata
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO value_metadata (id, value_type, unit, label, is_positive) VALUES (?, ?, ?, ?, ?)"
        ).params(
            VM_MIN_WAGE,
            "CURRENCY",
            "$",
            "Minimum Wage (per hour)",
            true
        ).update();

        // Impoverished Workforce metadata
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO value_metadata (id, value_type, unit, label, is_positive) VALUES (?, ?, ?, ?, ?)"
        ).params(
            VM_IMPOVERISHED,
            "PERCENTAGE",
            "%",
            "Impoverished Workforce Rate",
            false // This is negative - lower is better
        ).update();
    }

    private void insertTimeframeContext() {
        System.out.println("Inserting timeframe context...");

        scoringJdbcClient.sql(
            "INSERT IGNORE INTO term_context (id, type, label) VALUES (?, ?, ?)"
        ).params(
            CONTEXT_TIMEFRAME_2022,
            "TIMEFRAME",
            "2022"
        ).update();
    }

    private void insertLocationContexts() {
        System.out.println("Inserting location contexts...");

        String[] states = {
            "Alabama", "Alaska", "Arizona", "Arkansas", "California",
            "Colorado", "Connecticut", "Delaware", "Florida", "Georgia",
            "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa",
            "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland",
            "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri",
            "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey",
            "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio",
            "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina",
            "South Dakota", "Tennessee", "Texas", "Utah", "Vermont",
            "Virginia", "Washington", "Washington DC", "West Virginia", "Wisconsin",
            "Wyoming"
        };

        for (String state : states) {
            String contextId = "context-location-" + state.toLowerCase().replace(" ", "-");
            scoringJdbcClient.sql(
                "INSERT IGNORE INTO term_context (id, type, label) VALUES (?, ?, ?)"
            ).params(
                contextId,
                "LOCATION",
                state
            ).update();
        }
    }

    private void insertContextualizedTerms() {
        System.out.println("Inserting contextualized terms...");

        // All 50 US states + DC in alphabetical order
        // Format: stateName, stateSlug, unionPre, unionPost, lfprPre, lfprPost, minWagePre, minWagePost, impoverishedPre, impoverishedPost

        insertStateData(
            "Alabama", "alabama",
            6.9, 0.22,
            57.2, 0.12,
            7.25, 0.0,
            7.0, 0.377049180612201
        );
        insertStateData(
            "Alaska", "alaska",
            17.2, 0.69,
            66.3, 0.67,
            10.34, 0.35,
            9.3, 0.0
        );
        insertStateData(
            "Arizona", "arizona",
            6.7, 0.21,
            61.0, 0.35,
            12.80, 0.63,
            6.9, 0.393442623247514
        );
        insertStateData(
            "Arkansas", "arkansas",
            4.4, 0.11,
            56.8, 0.09,
            11.00, 0.42,
            7.3, 0.327868852706262
        );
        insertStateData(
            "California", "california",
            17.8, 0.71,
            62.0, 0.41,
            15.00, 0.88,
            5.7, 0.590163934871271
        );
        insertStateData(
            "Colorado", "colorado",
            7.5, 0.25,
            69.0, 0.83,
            12.56, 0.6,
            5.3, 0.655737705412524
        );
        insertStateData(
            "Connecticut", "connecticut",
            16.3, 0.65,
            64.7, 0.57,
            14.00, 0.76,
            4.3, 0.819672131765655
        );
        insertStateData(
            "Delaware", "delaware",
            10.2, 0.37,
            60.9, 0.34,
            10.50, 0.37,
            9.3, 0.0
        );
        insertStateData(
            "Florida", "florida",
            9.9, 0.36,
            71.8, 1.0,
            11.00, 0.42,
            6.2, 0.508196721694706
        );
        insertStateData(
            "Georgia", "georgia",
            6.1, 0.19,
            59.3, 0.24,
            7.25, 0.0,
            6.6, 0.442622951153453
        );
        insertStateData(
            "Hawaii", "hawaii",
            5.8, 0.17,
            62.1, 0.41,
            10.10, 0.32,
            4.0, 0.868852459671594
        );
        insertStateData(
            "Idaho", "idaho",
            24.1, 1.0,
            60.3, 0.3,
            7.25, 0.0,
            6.9, 0.393442623247514
        );
        insertStateData(
            "Illinois", "illinois",
            5.5, 0.16,
            63.1, 0.47,
            12.00, 0.54,
            5.5, 0.622950820141897
        );
        insertStateData(
            "Indiana", "indiana",
            15.2, 0.6,
            64.1, 0.53,
            7.25, 0.0,
            6.2, 0.508196721694706
        );
        insertStateData(
            "Iowa", "iowa",
            10.2, 0.37,
            63.2, 0.48,
            7.25, 0.0,
            6.1, 0.524590164330019
        );
        insertStateData(
            "Kansas", "kansas",
            8.3, 0.29,
            67.4, 0.73,
            7.25, 0.0,
            6.5, 0.459016393788766
        );
        insertStateData(
            "Kentucky", "kentucky",
            11.4, 0.43,
            66.3, 0.67,
            7.25, 0.0,
            7.3, 0.327868852706262
        );
        insertStateData(
            "Louisiana", "louisiana",
            9.8, 0.35,
            57.9, 0.16,
            7.25, 0.0,
            8.6, 0.114754098447192
        );
        insertStateData(
            "Maine", "maine",
            5.7, 0.17,
            58.7, 0.21,
            12.75, 0.62,
            5.1, 0.68852459068315
        );
        insertStateData(
            "Maryland", "maryland",
            14.7, 0.57,
            58.4, 0.19,
            12.50, 0.59,
            3.8, 0.90163934494222
        );
        insertStateData(
            "Massachusetts", "massachusetts",
            12.3, 0.47,
            65.3, 0.61,
            14.25, 0.79,
            3.9, 0.885245902306907
        );
        insertStateData(
            "Michigan", "michigan",
            13.6, 0.52,
            65.1, 0.59,
            9.87, 0.3,
            6.6, 0.442622951153453
        );
        insertStateData(
            "Minnesota", "minnesota",
            15.3, 0.6,
            60.1, 0.29,
            10.33, 0.35,
            4.8, 0.737704918589089
        );
        insertStateData(
            "Mississippi", "mississippi",
            17.1, 0.68,
            68.3, 0.79,
            7.25, 0.0,
            8.7, 0.0983606558118786
        );
        insertStateData(
            "Missouri", "missouri",
            6.9, 0.22,
            55.4, 0.01,
            11.15, 0.44,
            6.3, 0.491803279059393
        );
        insertStateData(
            "Montana", "montana",
            10.2, 0.37,
            62.8, 0.45,
            9.20, 0.22,
            7.7, 0.262295082165009
        );
        insertStateData(
            "Nebraska", "nebraska",
            12.9, 0.49,
            62.7, 0.45,
            9.00, 0.2,
            6.0, 0.540983606965332
        );
        insertStateData(
            "Nevada", "nevada",
            8.0, 0.27,
            70.2, 0.9,
            9.50, 0.25,
            5.9, 0.557377049600645
        );
        insertStateData(
            "New Hampshire", "new-hampshire",
            14.1, 0.55,
            60.6, 0.32,
            7.25, 0.0,
            3.2, 1.0000000007541
        );
        insertStateData(
            "New Jersey", "new-jersey",
            11.3, 0.42,
            65.1, 0.59,
            13.00, 0.65,
            4.1, 0.852459017036281
        );
        insertStateData(
            "New Mexico", "new-mexico",
            17.9, 0.72,
            62.7, 0.45,
            11.50, 0.48,
            9.3, 0.0
        );
        insertStateData(
            "New York", "new-york",
            9.1, 0.32,
            56.2, 0.05,
            13.20, 0.67,
            5.5, 0.622950820141897
        );
        insertStateData(
            "North Carolina", "north-carolina",
            24.1, 1.0,
            60.1, 0.29,
            7.25, 0.0,
            6.7, 0.42622950851814
        );
        insertStateData(
            "North Dakota", "north-dakota",
            3.4, 0.06,
            61.0, 0.35,
            7.25, 0.0,
            6.2, 0.508196721694706
        );
        insertStateData(
            "Ohio", "ohio",
            6.9, 0.22,
            69.2, 0.84,
            9.30, 0.23,
            6.3, 0.491803279059393
        );
        insertStateData(
            "Oklahoma", "oklahoma",
            13.0, 0.5,
            62.0, 0.41,
            7.25, 0.0,
            7.6, 0.278688524800322
        );
        insertStateData(
            "Oregon", "oregon",
            6.8, 0.22,
            60.9, 0.34,
            13.50, 0.71,
            6.5, 0.459016393788766
        );
        insertStateData(
            "Pennsylvania", "pennsylvania",
            18.8, 0.76,
            63.4, 0.49,
            7.25, 0.0,
            5.2, 0.672131148047837
        );
        insertStateData(
            "Rhode Island", "rhode-island",
            13.6, 0.52,
            62.0, 0.41,
            12.25, 0.56,
            4.5, 0.786885246495028
        );
        insertStateData(
            "South Carolina", "south-carolina",
            17.4, 0.7,
            63.0, 0.47,
            7.25, 0.0,
            6.9, 0.393442623247514
        );
        insertStateData(
            "South Dakota", "south-dakota",
            2.0, 0.0,
            57.7, 0.15,
            9.95, 0.31,
            6.4, 0.475409836424079
        );
        insertStateData(
            "Tennessee", "tennessee",
            5.0, 0.14,
            69.2, 0.84,
            7.25, 0.0,
            6.6, 0.442622951153453
        );
        insertStateData(
            "Texas", "texas",
            5.9, 0.18,
            61.2, 0.36,
            7.25, 0.0,
            6.9, 0.393442623247514
        );
        insertStateData(
            "Utah", "utah",
            4.7, 0.12,
            63.7, 0.51,
            7.25, 0.0,
            5.7, 0.590163934871271
        );
        insertStateData(
            "Vermont", "vermont",
            6.5, 0.2,
            68.1, 0.78,
            12.55, 0.6,
            5.3, 0.655737705412524
        );
        insertStateData(
            "Virginia", "virginia",
            14.2, 0.55,
            61.1, 0.35,
            11.00, 0.42,
            4.7, 0.754098361224402
        );
        insertStateData(
            "Washington", "washington",
            6.5, 0.2,
            63.7, 0.51,
            14.49, 0.82,
            4.8, 0.737704918589089
        );
        insertStateData(
            "Washington DC", "washington-dc",
            20.0, 0.81,
            64.9, 0.58,
            16.10, 1.0,
            4.6, 0.770491803859715
        );
        insertStateData(
            "West Virginia", "west-virginia",
            10.5, 0.38,
            55.3, 0.0,
            8.75, 0.17,
            7.5, 0.295081967435636
        );
        insertStateData(
            "Wisconsin", "wisconsin",
            9.3, 0.33,
            65.8, 0.64,
            7.25, 0.0,
            5.8, 0.573770492235958
        );
        insertStateData(
            "Wyoming", "wyoming",
            6.9, 0.22,
            63.1, 0.47,
            7.25, 0.0,
            6.3, 0.491803279059393
        );
    }

    private void insertStateData(String stateName, String stateSlug,
                                 double unionPre, double unionPost,
                                 double lfprPre, double lfprPost,
                                 double minWagePre, double minWagePost,
                                 double impoverishedPre, double impoverishedPost) {

        String locationContextId = "context-location-" + stateSlug;

        // Union Participation
        insertContextualizedTerm(
            "ct-" + stateSlug + "-union-2022",
            TERM_UNION_PARTICIPATION,
            VM_UNION_PARTICIPATION,
            locationContextId,
            unionPre,
            unionPost
        );

        // LFPR
        insertContextualizedTerm(
            "ct-" + stateSlug + "-lfpr-2022",
            TERM_LFPR,
            VM_LFPR,
            locationContextId,
            lfprPre,
            lfprPost
        );

        // Minimum Wage
        insertContextualizedTerm(
            "ct-" + stateSlug + "-minwage-2022",
            TERM_MIN_WAGE,
            VM_MIN_WAGE,
            locationContextId,
            minWagePre,
            minWagePost
        );

        // Impoverished Workforce
        insertContextualizedTerm(
            "ct-" + stateSlug + "-impoverished-2022",
            TERM_IMPOVERISHED,
            VM_IMPOVERISHED,
            locationContextId,
            impoverishedPre,
            impoverishedPost
        );
    }

    private void insertContextualizedTerm(String id, String termId, String valueMetadataId,
                                          String locationContextId, double preValue, double postValue) {
        // Insert the contextualized term
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO contextualized_term (id, term_id, value_metadata_id, pre_normalized_value, post_normalized_value) " +
            "VALUES (?, ?, ?, ?, ?)"
        ).params(
            id,
            termId,
            valueMetadataId,
            preValue,
            postValue
        ).update();

        // Link to timeframe context
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO contextualized_term_contexts (contextualized_term_id, context_id) VALUES (?, ?)"
        ).params(id, CONTEXT_TIMEFRAME_2022).update();

        // Link to location context
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO contextualized_term_contexts (contextualized_term_id, context_id) VALUES (?, ?)"
        ).params(id, locationContextId).update();
    }

    private void insertLaborQualityElection() {
        System.out.println("Inserting Labor Quality election...");

        // Create the Labor Quality Score 2024 election
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO worldview_election " +
            "(id, name, description, election_types, status, created_by, total_ballots) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)"
        ).params(
            "election-labor-quality-2024",
            "Labor Quality Score 2024",
            "Evaluates labor quality across US states using union participation, labor force participation, minimum wage, and poverty rates. Based on real-world data from all 50 states plus DC.",
            "[\"Economic Policy\", \"Labor Rights\"]", // JSON array
            "ACTIVE",
            "system",
            0
        ).update();
    }

    private void insertLaborQualityBallotTemplate() {
        System.out.println("Inserting Labor Quality ballot template...");

        // Create a template ballot that defines the Labor Quality scoring methodology
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO worldview_ballot " +
            "(id, election_id, voter_id, name, ballot_type) " +
            "VALUES (?, ?, ?, ?, ?)"
        ).params(
            "ballot-labor-quality-template",
            "election-labor-quality-2024",
            "system",
            "Labor Quality Score Template",
            "labor-quality"
        ).update();
    }

    private void insertWeightedTerms() {
        System.out.println("Inserting weighted worldview terms...");

        // Labor Quality weights (total = 21)
        // Union Participation: 5/21 ≈ 0.2381
        // Labor Force Participation Rate: 2/21 ≈ 0.0952
        // Minimum Wage: 8/21 ≈ 0.3810
        // Impoverished Workforce: 6/21 ≈ 0.2857 (negative - lower is better)

        String ballotId = "ballot-labor-quality-template";

        // Union Participation (positive, weight 5/21)
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO weighted_worldview_term " +
            "(id, term_id, weight, worldview_ballot_id, is_positive) " +
            "VALUES (?, ?, ?, ?, ?)"
        ).params(
            "wwt-labor-quality-union",
            TERM_UNION_PARTICIPATION,
            0.2381,
            ballotId,
            true
        ).update();

        // Labor Force Participation Rate (positive, weight 2/21)
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO weighted_worldview_term " +
            "(id, term_id, weight, worldview_ballot_id, is_positive) " +
            "VALUES (?, ?, ?, ?, ?)"
        ).params(
            "wwt-labor-quality-lfpr",
            TERM_LFPR,
            0.0952,
            ballotId,
            true
        ).update();

        // Minimum Wage (positive, weight 8/21)
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO weighted_worldview_term " +
            "(id, term_id, weight, worldview_ballot_id, is_positive) " +
            "VALUES (?, ?, ?, ?, ?)"
        ).params(
            "wwt-labor-quality-minwage",
            TERM_MIN_WAGE,
            0.3810,
            ballotId,
            true
        ).update();

        // Impoverished Workforce (negative - lower is better, weight 6/21)
        scoringJdbcClient.sql(
            "INSERT IGNORE INTO weighted_worldview_term " +
            "(id, term_id, weight, worldview_ballot_id, is_positive) " +
            "VALUES (?, ?, ?, ?, ?)"
        ).params(
            "wwt-labor-quality-impoverished",
            TERM_IMPOVERISHED,
            0.2857,
            ballotId,
            false
        ).update();
    }
}
