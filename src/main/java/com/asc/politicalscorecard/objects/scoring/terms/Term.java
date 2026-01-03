package com.asc.politicalscorecard.objects.scoring.terms;

public class Term {
    // A term is a 'mathematical' term, in the context of a score, it is specifically a value between 0 and 1 that is used to
    // as a part of a set of terms in a single context to calculate scores.

    // This is the abstract form of the Term, which is used to compare different terms against each other.

    // Id of the term.
    private String id;

    // Name of the term.
    private String name;

    // Description of what this term represents.
    private String description;

    // Source of the term (URL, citation, or reference).
    private String source;

    // Optional category to group related terms (e.g., 'labor-quality', 'economic', etc.)
    private String category;

    // Logical Equivalence Terms to this context (for future use).
    private String[] logicalEquivalenceTerms;

    // Purpose Equivalence or Competitive Terms to this context (for future use).
    private String[] competitiveTerms;

    // Constructors
    public Term() {
    }

    public Term(String id, String name, String description, String source, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.source = source;
        this.category = category;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String[] getLogicalEquivalenceTerms() {
        return logicalEquivalenceTerms;
    }

    public void setLogicalEquivalenceTerms(String[] logicalEquivalenceTerms) {
        this.logicalEquivalenceTerms = logicalEquivalenceTerms;
    }

    public String[] getCompetitiveTerms() {
        return competitiveTerms;
    }

    public void setCompetitiveTerms(String[] competitiveTerms) {
        this.competitiveTerms = competitiveTerms;
    }

    @Override
    public String toString() {
        return "Term{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}