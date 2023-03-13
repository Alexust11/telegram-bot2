package pro.sky.telegrambot.service;

import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationTaskService {
    private final   NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    public List<NotificationTask> findByDateTime(LocalDateTime time){
        return findByDateTimeBetween(time, time.plusMinutes(1));
    }

    public List<NotificationTask> findByDateTimeBetween (LocalDateTime from, LocalDateTime to){
        return notificationTaskRepository.findAllByScheduledTimeBetween(from, to);
    }

    public void addTask(long chatId, String dateTime, String taskDescription) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        addTask(new NotificationTask(chatId, taskDescription, localDateTime));
    }

    public void addTask (NotificationTask task){
        notificationTaskRepository.save(task);
    }

}
