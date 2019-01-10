package classes.Models;

public class TransUnit {

    private String id;
    private String source;
    private String target;
    private String note;

    public TransUnit(String id, String string, String note) {
        this.id = id;
        this.source = string;
        this.note = note;
    }

    public TransUnit(String id, String source, String target, String note) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getNote() {
        return note;
    }

    public String getTarget() { return target; }

}
