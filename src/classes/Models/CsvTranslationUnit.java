package classes.Models;

public class CsvTranslationUnit {

    private String id;
    private String description;
    private String baseTranslation;
    private String[] translations;

    public CsvTranslationUnit(String id, String description, String baseTranslation, String[] translations) {
        this.id = id;
        this.description = description;
        this.baseTranslation = baseTranslation;
        this.translations = translations;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getBaseTranslation() {
        return baseTranslation;
    }

    public String[] getTranslation() {
        return translations;
    }
}
