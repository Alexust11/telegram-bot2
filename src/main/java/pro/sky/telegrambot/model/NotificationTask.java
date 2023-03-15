package pro.sky.telegrambot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
public class NotificationTask {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "chat_id", nullable = false)
    private long chatId;
    private String text;
    @Column(name = "date_time", nullable = false)
    private LocalDateTime scheduledTime;

    public NotificationTask() {
    }

    public NotificationTask(long chatId, String text, LocalDateTime scheduledTime) {
        this.chatId = chatId;
        this.text = text;
        this.scheduledTime = scheduledTime;

    }

//    public long getId() {
//        return id;
//    }
//
//      public long getChatId() {
//        return chatId;
//    }
//
//    public void setChatId(long chatId) {
//        this.chatId = chatId;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public LocalDateTime getScheduledTime() {
//        return scheduledTime;
//    }
//
//    public void setScheduledTime(LocalDateTime scheduledTime) {
//        this.scheduledTime = scheduledTime;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NotificationTask{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", text='" + text + '\'' +
                ", scheduledTime=" + scheduledTime +
                '}';
    }

    public String reminderInfo() {
        return this.getScheduledTime() + " - " + this.getText();
    }
}
