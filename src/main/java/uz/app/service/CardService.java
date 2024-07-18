package uz.app.service;


import uz.app.enums.Card;
import uz.app.enums.User;
import uz.app.repository.CardRepositary;

import java.util.ArrayList;

public class CardService {
    CardRepositary cardRepositary = CardRepositary.getInstance();
    private final ReplyMarkupService replyMarkupService = new ReplyMarkupService();
    private final InlineMarkupService inlineMarkupService = new InlineMarkupService();


//    public void addCard(Card card, User user) {
//
//    }



    private static CardService cardService;

    public static CardService getInstance() {
        if (cardService == null) {
            cardService = new CardService();
        }
        return cardService;
    }


}
