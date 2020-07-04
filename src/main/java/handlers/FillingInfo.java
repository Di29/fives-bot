package handlers;

import com.vdurmont.emoji.EmojiParser;
import data.UserCache;
import model.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import services.BotService;

import java.util.Collections;

public class FillingInfo {
    public SendMessage fillingHandler(Update update, UserCache userCache){
        int userId = update.getMessage().getFrom().getId();
        BotState currentState = userCache.getUsersBotState(userId);
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
        if(currentState == BotState.FILLING_INFO){
            String text = BotService.getInstance().getTextByName("Имя");
            message.setText(text);
            userCache.setUsersBotState(userId,BotState.ASK_NAME);
            userCache.setPreviousBotState(userId,BotState.VIDEO);
            ReplyKeyboardMarkup keyboardMarkup = setBack();
            message.setReplyMarkup(keyboardMarkup);
        }else if(currentState == BotState.ASK_NAME){
            if(!update.getMessage().getText().equals("Назад")) {
                userCache.setUsersName(userId, update.getMessage().getText());
            }
            message.setText(BotService.getInstance().getTextByName("Телефон"));
            userCache.setUsersBotState(userId,BotState.ASK_NUMBER);
            userCache.setPreviousBotState(userId,BotState.FILLING_INFO);
            ReplyKeyboardMarkup keyboardMarkup = setBack();
            message.setReplyMarkup(keyboardMarkup);
        }else if(currentState == BotState.ASK_NUMBER){
            if(!update.getMessage().getText().equals("Назад")) {
                userCache.setUsersPhone(userId,update.getMessage().getText());
            }

            message.setText(BotService.getInstance().getTextByName("Адрес"));
            userCache.setUsersBotState(userId,BotState.ASK_ADDRESS);
            userCache.setPreviousBotState(userId,BotState.ASK_NAME);
            ReplyKeyboardMarkup keyboardMarkup = setBack();
            message.setReplyMarkup(keyboardMarkup);

        }else if(currentState == BotState.ASK_ADDRESS){
            if(!update.getMessage().getText().equals("Назад")) {
                userCache.setUsersAddress(userId, update.getMessage().getText());
            }
            message.setText(BotService.getInstance().getTextByName("Свяжемся"));
            userCache.setUsersBotState(userId,BotState.PROCESS_INFO);
            userCache.setPreviousBotState(userId,BotState.ASK_NUMBER);
            ReplyKeyboardMarkup keyboardMarkup = keyboardMarkupSettings();
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.add(EmojiParser.parseToUnicode(":house: Вернуться в меню"));
            keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
            message.setReplyMarkup(keyboardMarkup);
        }
        return message;
    }

    public ReplyKeyboardMarkup keyboardMarkupSettings() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    public ReplyKeyboardMarkup setBack(){
        ReplyKeyboardMarkup keyboardMarkup = keyboardMarkupSettings();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("Назад");
        keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
        return keyboardMarkup;
    }
}
