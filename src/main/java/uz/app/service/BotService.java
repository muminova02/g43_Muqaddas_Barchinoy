package uz.app.service;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class BotService extends TelegramLongPollingBot {
    private static final BotLogicService logicService = BotLogicService.getInstance();


    private SendMessage sendMessage = new SendMessage();
    private SendMessage sendMessageToAdmin = new SendMessage();

    private final ReplyMarkupService replyService = new ReplyMarkupService();
    private final InlineMarkupService inlineService = new InlineMarkupService();
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQueryHandler.handle(update.getCallbackQuery(),botService);
        } else if (update.hasMessage()) {
            logicService.messageHandler(update);
        }



    }


    @Override
    public String getBotUsername() {
        return "https://t.me/restaurant_M_bot";
    }

    @Override
    public String getBotToken() {
        return "7454034418:AAFQZctNIWuqMFsi-of5OVI4cl4oQ04j3vU";
    }


    public void executeMessages(SendMessage... messages) {
        for (SendMessage message : messages) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                System.out.println(e);
            }
        }
    }

    @SneakyThrows
    public Message executeMessages(SendMessage messages) {
        return execute(messages);

    }

    private static BotService botService;

    public static BotService getInstance() {
        if (botService == null) {
            botService = new BotService();
        }
        return botService;
    }

    @SneakyThrows
    public void executeMessages(ForwardMessage forwardMessage) {
        execute(forwardMessage);
    }

    @SneakyThrows
    public void executeMessages(SendPhoto sendPhoto) {
        execute(sendPhoto);
    }

    @SneakyThrows
    public void executeMessages(DeleteMessage deleteMessage) {
        execute(deleteMessage);
    }
}
