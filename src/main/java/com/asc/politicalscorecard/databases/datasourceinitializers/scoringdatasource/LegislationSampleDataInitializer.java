package com.asc.politicalscorecard.databases.datasourceinitializers.scoringdatasource;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Initializes sample legislation data for development and testing.
 * Inserts a mock legislation document in APPROVED status so that
 * list, detail, and annotation views can be exercised immediately.
 */
public class LegislationSampleDataInitializer {

    private final JdbcClient scoringJdbcClient;

    private static final String SAMPLE_LEGISLATION_ID = "legislation-clean-energy-act-2024";

    public LegislationSampleDataInitializer(@Qualifier("scoringJdbcClient") JdbcClient scoringJdbcClient) {
        this.scoringJdbcClient = scoringJdbcClient;
    }

    public void initializeData() {
        System.out.println("Initializing sample legislation data...");

        Integer count = scoringJdbcClient.sql(
            "SELECT COUNT(*) FROM legislation WHERE id = ?"
        ).param(SAMPLE_LEGISLATION_ID).query(Integer.class).single();

        if (count == 0) {
            insertSampleLegislation();
            System.out.println("Sample legislation data initialized successfully.");
        } else {
            System.out.println("Sample legislation data already exists. Skipping.");
        }
    }

    private void insertSampleLegislation() {
        System.out.println("Inserting sample legislation: Clean Energy Transition Act of 2024...");

        String legislationText =
            "<h1>Clean Energy Transition Act of 2024</h1>" +
            "<h2>AN ACT</h2>" +
            "<p>To accelerate the transition to clean energy sources, reduce greenhouse gas emissions, " +
            "and promote economic growth through investment in renewable energy infrastructure across the United States.</p>" +
            "<p><em>Be it enacted by the Senate and House of Representatives of the United States of America in Congress assembled,</em></p>" +

            "<h2>SECTION 1. SHORT TITLE</h2>" +
            "<p>This Act may be cited as the <strong>\"Clean Energy Transition Act of 2024\"</strong>.</p>" +

            "<h2>SECTION 2. FINDINGS</h2>" +
            "<p>Congress finds the following:</p>" +
            "<ol>" +
            "<li>The United States energy sector accounts for approximately 25 percent of total greenhouse gas emissions.</li>" +
            "<li>Renewable energy technologies, including solar, wind, and geothermal, have decreased in cost by over 70 percent in the last decade.</li>" +
            "<li>Investment in clean energy infrastructure has the potential to create over 500,000 new jobs by 2030.</li>" +
            "<li>Energy independence strengthens national security and reduces reliance on volatile foreign energy markets.</li>" +
            "<li>Rural communities stand to benefit disproportionately from distributed renewable energy development.</li>" +
            "</ol>" +

            "<h2>SECTION 3. DEFINITIONS</h2>" +
            "<p>In this Act:</p>" +
            "<ol>" +
            "<li><strong>\"Clean energy source\"</strong> means any energy source that produces zero direct greenhouse gas emissions during operation, " +
            "including solar photovoltaic, wind, geothermal, hydroelectric, and advanced nuclear energy.</li>" +
            "<li><strong>\"Covered entity\"</strong> means any electric utility, energy cooperative, or power generation facility serving more than 10,000 customers.</li>" +
            "<li><strong>\"Renewable portfolio standard\"</strong> means the minimum percentage of electricity that a covered entity must generate or procure from clean energy sources.</li>" +
            "<li><strong>\"Community energy project\"</strong> means a clean energy installation with a generating capacity of 5 megawatts or less that is owned, in whole or in part, by a community organization, municipality, or cooperative.</li>" +
            "</ol>" +

            "<h2>SECTION 4. RENEWABLE PORTFOLIO STANDARDS</h2>" +
            "<h3>(a) Federal Minimum Standards</h3>" +
            "<p>Not later than January 1, 2026, each covered entity shall generate or procure from clean energy sources not less than the following percentages of total electricity sold to retail customers:</p>" +
            "<ol>" +
            "<li>40 percent by calendar year 2028;</li>" +
            "<li>60 percent by calendar year 2032;</li>" +
            "<li>80 percent by calendar year 2036; and</li>" +
            "<li>100 percent by calendar year 2040.</li>" +
            "</ol>" +
            "<h3>(b) State Preemption</h3>" +
            "<p>Nothing in this section shall preempt any State law or regulation that establishes a renewable portfolio standard that is more stringent than the standards established under subsection (a).</p>" +
            "<h3>(c) Compliance Flexibility</h3>" +
            "<p>A covered entity may meet the requirements of subsection (a) through any combination of:</p>" +
            "<ol>" +
            "<li>direct generation from owned clean energy facilities;</li>" +
            "<li>power purchase agreements with clean energy generators;</li>" +
            "<li>purchase of renewable energy certificates; or</li>" +
            "<li>investment in community energy projects within the entity's service territory.</li>" +
            "</ol>" +

            "<h2>SECTION 5. COMMUNITY ENERGY INVESTMENT FUND</h2>" +
            "<h3>(a) Establishment</h3>" +
            "<p>There is established in the Treasury of the United States a fund to be known as the \"Community Energy Investment Fund\" (referred to in this section as the \"Fund\").</p>" +
            "<h3>(b) Authorization of Appropriations</h3>" +
            "<p>There are authorized to be appropriated to the Fund $15,000,000,000 for fiscal years 2025 through 2034.</p>" +
            "<h3>(c) Use of Funds</h3>" +
            "<p>Amounts in the Fund shall be available to the Secretary of Energy to provide grants, loans, and technical assistance for:</p>" +
            "<ol>" +
            "<li>community energy projects in underserved communities;</li>" +
            "<li>workforce development programs in clean energy installation and maintenance;</li>" +
            "<li>grid modernization projects that facilitate distributed energy resources; and</li>" +
            "<li>energy storage systems co-located with community energy projects.</li>" +
            "</ol>" +
            "<h3>(d) Priority</h3>" +
            "<p>In awarding funds under this section, the Secretary shall give priority to projects that:</p>" +
            "<ol>" +
            "<li>are located in communities with historically high energy cost burdens;</li>" +
            "<li>demonstrate meaningful community ownership or governance participation;</li>" +
            "<li>include workforce development commitments for local residents; and</li>" +
            "<li>incorporate energy storage or demand response capabilities.</li>" +
            "</ol>" +

            "<h2>SECTION 6. WORKFORCE DEVELOPMENT</h2>" +
            "<h3>(a) Clean Energy Apprenticeship Program</h3>" +
            "<p>The Secretary of Labor, in coordination with the Secretary of Energy, shall establish a national clean energy apprenticeship program to train workers in:</p>" +
            "<ol>" +
            "<li>solar photovoltaic installation and maintenance;</li>" +
            "<li>wind turbine technology and repair;</li>" +
            "<li>energy storage system installation;</li>" +
            "<li>electric vehicle charging infrastructure; and</li>" +
            "<li>building energy efficiency retrofitting.</li>" +
            "</ol>" +
            "<h3>(b) Authorization of Appropriations</h3>" +
            "<p>There are authorized to be appropriated $2,000,000,000 for fiscal years 2025 through 2030 to carry out this section.</p>" +

            "<h2>SECTION 7. GRID MODERNIZATION</h2>" +
            "<p>The Secretary of Energy shall establish a program to provide matching grants to States and utilities for grid modernization projects that:</p>" +
            "<ol>" +
            "<li>increase the capacity of the electric grid to integrate variable renewable energy sources;</li>" +
            "<li>deploy advanced metering infrastructure and smart grid technologies;</li>" +
            "<li>improve grid resilience against extreme weather events; and</li>" +
            "<li>facilitate interstate transmission of renewable energy.</li>" +
            "</ol>" +

            "<h2>SECTION 8. REPORTING AND ACCOUNTABILITY</h2>" +
            "<h3>(a) Annual Reports</h3>" +
            "<p>Not later than March 1 of each year, the Secretary of Energy shall submit to Congress a report on the implementation of this Act, including:</p>" +
            "<ol>" +
            "<li>progress toward the renewable portfolio standards established under Section 4;</li>" +
            "<li>amounts disbursed from the Community Energy Investment Fund;</li>" +
            "<li>number of apprenticeships created under Section 6; and</li>" +
            "<li>estimated greenhouse gas emission reductions attributable to this Act.</li>" +
            "</ol>" +
            "<h3>(b) Public Dashboard</h3>" +
            "<p>The Secretary of Energy shall maintain a publicly accessible online dashboard displaying real-time progress toward the goals established by this Act.</p>" +

            "<h2>SECTION 9. EFFECTIVE DATE</h2>" +
            "<p>This Act shall take effect on January 1, 2025.</p>";

        scoringJdbcClient.sql(
            "INSERT IGNORE INTO legislation " +
            "(id, title, description, legislative_body_id, valid_from_date, valid_to_date, legislation_text, url, status, created_by) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
        ).params(
            SAMPLE_LEGISLATION_ID,
            "Clean Energy Transition Act of 2024",
            "A comprehensive bill to accelerate the transition to clean energy sources, " +
            "reduce greenhouse gas emissions, and promote economic growth through investment " +
            "in renewable energy infrastructure. Establishes federal renewable portfolio standards, " +
            "creates a $15 billion Community Energy Investment Fund, and launches a national " +
            "clean energy apprenticeship program.",
            "us-congress",
            "2025-01-01",
            "",
            legislationText,
            "https://www.congress.gov/bill/118th-congress/house-bill/0000",
            "APPROVED",
            "system"
        ).update();
    }
}
