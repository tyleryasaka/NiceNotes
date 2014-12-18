package me.tyleryasaka.apps.nicenotes;

public class Note {

    private long id;
    private String content;

    public Note(){}

    public Note(String content) {
        super();
        this.content = content;
    }

    //getters & setters

    @Override
    public String toString() {
        return "Note [id=" + id + ", content=" + content + "]";
    }

    public String getContent() {
        return content;
    }

    public long getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setId(long id){
        this.id = id;
    }
}