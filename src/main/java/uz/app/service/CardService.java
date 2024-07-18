package uz.app.service;

import org.example.db.Db;
import org.example.entity.Meal;
import org.example.entity.MenuType;
import uz.app.enums.Card;
import uz.app.repository.CardRepositary;

import java.util.ArrayList;

public class CardService {
    CardRepositary cardRepositary = CardRepositary.getInstance();
    private final ReplyMarkupService replyMarkupService = new ReplyMarkupService();
    private final InlineMarkupService inlineMarkupService = new InlineMarkupService();


    public void addCard(Card card,User user) {
        db.getAllMeals().put(menuType,new ArrayList<>());
    }

    public void test(Card card){
        System.out.println("Card qani !!");
        System.out.println("CARD MANA");
        System.out.println("asdasd");
    }
    private static CardService cardService;

    public static CardService getInstance() {
        if (cardService == null) {
            cardService = new CardService();
        }
        return cardService;
    }

    userCard(){
        
    }

}
