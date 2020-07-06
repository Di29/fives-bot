package handlers;

import com.vdurmont.emoji.Emoji;
import com.vdurmont.emoji.EmojiParser;
import data.UserCache;
import model.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import services.BotService;
// TODO: 27.06.2020 BotState чтобы запомнить гарант-платные услуги,сохранения выбранных услуг
// TODO: 27.06.2020 Добвить мэп для услуг и юзер айди
// TODO: 27.06.2020 ЗАКОНЧИТЬ БОТА
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MassageHandler {


    public SendMessage handleUpdate(Update update, UserCache userCache){

        int userId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage();
        BotState currentState = userCache.getUsersBotState(userId);
        if(currentState==BotState.ORDER) {
            message = handleMessage(update,userCache);
        }else{

        }
        return message;
    }

    private SendMessage handleMessage(Update update,UserCache userCache) {
        int userId = update.getMessage().getFrom().getId();
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
        String message_text = EmojiParser.parseToAliases(update.getMessage().getText());
        ReplyKeyboardMarkup keyboardMarkup = keyboardMarkupSettings();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        message.setText(EmojiParser.parseToUnicode(BotService.getInstance().getTextByName("Выбор")));


        long category_id = BotService.getInstance().getCatIdByName(message_text);

        if(category_id!=0) {
            if(category_id == 1) {
                message.setText(EmojiParser.parseToUnicode(BotService.getInstance().getTextByName("Выбор")) + "\n\n" + "<b>" + EmojiParser.parseToUnicode(BotService.getInstance().getTextByName("Устранить")) + "</b>").setParseMode("HTML");
            }

            List<String> subcategories = BotService.getInstance().getSubcatsByCatId(category_id);
            if(subcategories!=null) {
                for (String subcategory : subcategories) {
                    row.add(subcategory);
                    keyboard.add(row);
                    row = new KeyboardRow();
                }
                row.add(EmojiParser.parseToUnicode(":house: Вернуться в меню"));
                keyboard.add(row);

                userCache.setUsersOrderCat(userId,message_text);
                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);

                return message;
            }
        }


        long subcategory_id = BotService.getInstance().getSubCatIdByName(message_text);

        if(subcategory_id!=0) {
            List<String> services = BotService.getInstance().getServicesBySubCatId(subcategory_id);
            if(services!=null) {
                for (String service : services) {
                    row.add(EmojiParser.parseToUnicode(service));
                    keyboard.add(row);
                    row = new KeyboardRow();
                }
                row.add(EmojiParser.parseToUnicode(":arrow_left: Назад")); //
                userCache.setPreviousBotState(userId,BotState.BACK);
                keyboard.add(row);

                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);

                return message;
            }
        }

        category_id = BotService.getInstance().getCatIdByName(message_text);

        if(category_id!=0) {
            if(category_id == 1) {
                message.setText(EmojiParser.parseToUnicode(BotService.getInstance().getTextByName("Выбор")) + "\n\n" + "<b>" + EmojiParser.parseToUnicode(BotService.getInstance().getTextByName("Устранить")) + "</b>").setParseMode("HTML");            }
            List<String> services = BotService.getInstance().getServicesByCatId(category_id);
            if(services!=null) {
                for (String service : services) {
                    row.add(EmojiParser.parseToUnicode(service));
                    keyboard.add(row);
                    row = new KeyboardRow();
                }
                row.add(EmojiParser.parseToUnicode(":house: Вернуться в меню"));
                keyboard.add(row);
                userCache.setUsersOrderCat(userId,message_text);
                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);

                return message;
            }
        }

        long service_id = BotService.getInstance().getServiceIdByName(message_text);

        if(service_id!=0){
            List<String> options = BotService.getInstance().getOptionsByServiceId(service_id);
            if(!options.isEmpty()){
                for(String option: options){
                    row.add(option);
                    keyboard.add(row);
                    row = new KeyboardRow();
                }
                row.add(EmojiParser.parseToUnicode(":house: Вернуться в меню"));
                keyboard.add(row);

                userCache.setUsersOrderService(userId,message_text);
                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);
                return message;
            }

            String url = BotService.getInstance().getServicesURLByName(message_text);

            userCache.setUsersOrderOption(userId,"Отсутствует");
            userCache.setUsersOrderService(userId,message_text);
            if(url.length()>0) {
                message.setText(BotService.getInstance().getTextByName("Видео") + " " + url);
            }else{
                message.setText(BotService.getInstance().getTextByName("Без видео"));
            }
            message.setReplyMarkup(setMenuKeyboard());
            userCache.setUsersBotState(userId,BotState.VIDEO);
            return message;
        }

        long option_id = BotService.getInstance().getOptionIdByName(message_text);
        if(option_id!=0){
            String url = BotService.getInstance().getServicesURLByName(userCache.getOrdersData(userId).getService());
            userCache.setUsersOrderOption(userId,message_text);
            if(url.length()>0) {
                message.setText(BotService.getInstance().getTextByName("Видео") + " " +  url);
            }else{
                message.setText(BotService.getInstance().getTextByName("Без видео"));
            }
            message.setReplyMarkup(setMenuKeyboard());
            userCache.setUsersBotState(userId,BotState.VIDEO);
            return message;
        }

        message_text = EmojiParser.parseToUnicode(message_text);
        if(message_text.equals(EmojiParser.parseToUnicode(":arrow_left: Назад"))){
            category_id = BotService.getInstance().getCatIdByName(userCache.getOrdersData(userId).getCategory());

            if(category_id!=0) {
                List<String> subcategories = BotService.getInstance().getSubcatsByCatId(category_id);
                if(subcategories!=null) {
                    for (String subcategory : subcategories) {
                        row.add(subcategory);
                        keyboard.add(row);
                        row = new KeyboardRow();
                    }
                    row.add(EmojiParser.parseToUnicode(":house: Вернуться в меню"));
                    keyboard.add(row);

                    keyboardMarkup.setKeyboard(keyboard);
                    message.setReplyMarkup(keyboardMarkup);

                    return message;
                }
            }
        }

        message.setText(EmojiParser.parseToUnicode(BotService.getInstance().getTextByName("Выбор")));
        return message;

    }

    private ReplyKeyboardMarkup setMenuKeyboard(){
        ReplyKeyboardMarkup keyboardMarkup = keyboardMarkupSettings();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(EmojiParser.parseToUnicode(":house: Вернуться в меню"));
        keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
        return keyboardMarkup;
    }


    public ReplyKeyboardMarkup keyboardMarkupSettings() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

}
