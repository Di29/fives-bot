package handlers;

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
        String message_text = update.getMessage().getText();
        ReplyKeyboardMarkup keyboardMarkup = keyboardMarkupSettings();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        message.setText(EmojiParser.parseToUnicode("Выберите одну из предоставленных выборов :arrow_down:"));


        long category_id = BotService.getInstance().getCatIdByName(message_text);
        List<String> services = BotService.getInstance().getServicesByCatId(category_id);
        if(category_id!=0) {

            if(services!=null) {
                for (String service : services) {
                    row.add(service);
                    keyboard.add(row);
                    row = new KeyboardRow();
                }
                row.add(EmojiParser.parseToUnicode(":house: Вернуться в меню"));
                keyboard.add(row);
//                for(int i = 0;i<services.size();i++){
//                    row.add(services.get(i));
//                    keyboard.add(row);
//                    if(i%2==0){
//                        row = new KeyboardRow();
//                    }
//                }
                userCache.setUsersOrderCat(userId,message_text);
                keyboardMarkup.setKeyboard(keyboard);
                message.setReplyMarkup(keyboardMarkup);

                return message;
            }
        }

        long service_id = BotService.getInstance().getServiceIdByName(message_text);
        List<String> options = BotService.getInstance().getOptionsByServiceId(service_id);
        if(service_id!=0){

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
            userCache.setUsersBotState(userId,BotState.VIDEO);
            return message;
        }


        message.setText("Ошибка");
        return message;

    }



    public ReplyKeyboardMarkup keyboardMarkupSettings() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

}
