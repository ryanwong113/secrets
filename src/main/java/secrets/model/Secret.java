package secrets.model;

import java.util.Date;

public class Secret {

    private String title;
    private String tag;
    private String message;
    private String createdBy;
    private Date createdAt;

    public Secret() {

    }

    public Secret(String title, String tag, String message, String createdBy, Date createdAt) {
        this.title = title;
        this.tag = tag;
        this.message = message;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

}
