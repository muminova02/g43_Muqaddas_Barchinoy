package uz.app.service;

import org.example.entity.Meal;
import org.example.entity.MenuType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class ReplyMarkupService {
    public  ReplyKeyboardMarkup keyboardMaker(String[][] buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (String[] button : buttons) {
            KeyboardRow row = new KeyboardRow();
            for (String s : button) {
                KeyboardButton keyboardButton = new KeyboardButton(s);
                row.add(keyboardButton);
            }
            keyboardRows.add(row);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup keyboardMaker(Set<MenuType> options) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        int count = 0;
        for (MenuType option : options) {
            if (count % 2 == 0) {
                row1.add(new KeyboardButton(option.getTitle()));
            } else {
                row2.add(new KeyboardButton(option.getTitle()));
            }
            count++;
        }

        // Adding "orqaga" button at the end
        row2.add(new KeyboardButton("orqaga"));

        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true); // Optional: makes the keyboard fit the screen size
        replyKeyboardMarkup.setOneTimeKeyboard(true); // Optional: hides the keyboard after a button press

        return replyKeyboardMarkup;
    }

    public ReplyKeyboardMarkup keyboardMaker(ArrayList<Meal> meals) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        int count = 0;
        for (Meal option : meals) {
            if (count % 2 == 0) {
                row1.add(new KeyboardButton(option.getTitle()));
            } else {
                row2.add(new KeyboardButton(option.getTitle()));
            }
            count++;
        }

        // Adding "orqaga" button at the end
        row2.add(new KeyboardButton("orqaga"));

        keyboard.add(row1);
        keyboard.add(row2);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true); // Optional: makes the keyboard fit the screen size
        replyKeyboardMarkup.setOneTimeKeyboard(true); // Optional: hides the keyboard after a button press

        return replyKeyboardMarkup;
    }
}
