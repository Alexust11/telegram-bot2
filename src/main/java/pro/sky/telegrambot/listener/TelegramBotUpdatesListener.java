package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotificationTaskService notificationTaskService;
    private final Logger logger;
    private final Pattern pattern = Pattern.compile("([0-9.:\\s]{16})(\\s)([\\W+]+)");
    private final DateTimeFormatter formatterPattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
        this.logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);


            if (update.message().text() != null){
                String message = update.message().text();
                long chatId = update.message().chat().id();
                if (message.equals("/start")) {
                    String username = update.message().chat().firstName();
                    sendMessage(chatId, "Привет, " + username + "!");
                } else if (message.matches("([\\d.:\s]{16})(\s)([\\W\\d+]+)")){
                    createNewTask(update);

                } else {
                    sendMessage(chatId, "Команда не распознана ");
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void createNewTask(Update update) {
        String inputMessage = update.message().text();
        Matcher matcher = pattern.matcher(inputMessage);

        if (matcher.matches()) {
            long chatId = update.message().chat().id();
            String date = matcher.group(1);
            LocalDateTime formattedDate = LocalDateTime.parse(date, formatterPattern);
            String textTask = matcher.group(3);

            try {
                NotificationTask newTask = new NotificationTask(chatId, textTask, formattedDate);
                notificationTaskService.addTask(newTask);
                sendMessage(chatId, "Задача добавлена");
            } catch (DateTimeParseException e)
             { e.printStackTrace();
                 sendMessage(chatId, "Некорректный форматы даты/времени. Дата должна быть в формате:\n 01.01.2022 20:00");
            }


        }

    }

       private void sendMessage(long chatId, String s) {
        telegramBot.execute(new SendMessage(chatId, s));
    }



    @Scheduled(cron = "0 0/1 * * * *")
    public void sendActualNotifications(){
        List<NotificationTask> tasks = notificationTaskService.findByDateTime(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
        );
        tasks.forEach(t-> sendMessage(t.getChatId(), "Напоминание: " + t.reminderInfo()));
    }

}
