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
import uz.app.enums.Card;
import uz.app.enums.UserState;
import uz.app.repository.UserRepositary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class CallbackQueryHandler {

   private static UserService userService = UserService.getInstance();
   private static UserRepositary userRepositary = UserRepositary.getInstance();
   public static String depositCardNumber = null;

    public static void handle(CallbackQuery callbackQueryHandler, TelegramLongPollingBot bot) throws TelegramApiException {
        Long chatId1 = callbackQueryHandler.getMessage().getChatId();
        UserState state = userService.getState(chatId1);
        if (state.equals(UserState.MAY_USER_SEE_HIS_CARDS)) {
            List<Card> cards = userService.showCards(chatId1);
            for (Card card : cards) {
                if (card.getNumber().equals(callbackQueryHandler.getData())){
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Your Card inform: \n");
                    stringBuilder.append("Card Number: ");
                    stringBuilder.append(card.getNumber());
                    stringBuilder.append("\nCard Balance ");
                    stringBuilder.append(card.getBalance());
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId1);
                    sendMessage.setText(stringBuilder.toString());
                    bot.execute(sendMessage);
                    return;
                }
            }
        } else if (state.equals(UserState.TRANSFER_CARD_1)) {
            userRepositary.createTransferAndSetCard1(chatId1, callbackQueryHandler.getData());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId1);
            sendMessage.setText("Write second card: ");
            sendMessage.setReplyMarkup(null);
            userService.updateState(chatId1, UserState.TRANSFER_CARD_2);
            bot.execute(sendMessage);
        } else if (state.equals(UserState.DEPOSIT_CARD)) {
            setCardNumber(callbackQueryHandler.getData());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId1);
            sendMessage.setText("Write amount for card: ");
            sendMessage.setReplyMarkup(null);
            userService.updateState(chatId1, UserState.DEPOSIT_AMOUNT);
            bot.execute(sendMessage);
        }
    }
    private static void setCardNumber(String cardNumber) {
        depositCardNumber = cardNumber;
    }


}
