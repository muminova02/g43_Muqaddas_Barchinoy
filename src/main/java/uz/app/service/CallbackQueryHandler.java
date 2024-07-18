package uz.app.service;

//import delivery.Maps;
//import delivery.Steps;
//import model_repo.BasketRepo;
import lombok.RequiredArgsConstructor;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;

@RequiredArgsConstructor
public class CallbackQueryHandler {

   private static UserService userService = UserService.getInstance();

    public static void handle(CallbackQuery callbackQueryHandler, TelegramLongPollingBot bot) throws TelegramApiException {
        Long chatId1 = callbackQueryHandler.getMessage().getChatId();
        if (callbackQueryHandler.getData().startsWith("basket")) {
//            addBasket(callbackQueryHandler, bot, chatId1);
//            TextHandler.menuButtons(callbackQueryHandler.getMessage(), bot);
//            Maps.USER_STEPS.put(callbackQueryHandler.getMessage().getChatId(), Steps.MENU);
        }


    }



}
