import com.vdurmont.emoji.EmojiParser;
import data.UserCache;
import handlers.FillingInfo;
import handlers.MassageHandler;
import model.BotState;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import services.BotService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bot extends TelegramLongPollingBot {


    UserCache userCache = new UserCache();
    FillingInfo fillingInfo = new FillingInfo();
    MassageHandler massageHandler = new MassageHandler();

    public void onUpdateReceived(Update update) {


        int userId = update.getMessage().getFrom().getId();

        BotState state = userCache.getUsersBotState(userId);
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
        if(update.hasMessage() && update.getMessage().hasText()){
            if(update.getMessage().getText().equals("Назад")){
                userCache.setUsersBotState(userId,userCache.getPreviousBotState(userId));
            }
            state = userCache.getUsersBotState(userId);
            if(update.getMessage().getText().equals("Мои заказы")){

               List<List<String>> orders = BotService.getInstance().getOrdersByUserId(userId);

               List<String> names = new ArrayList<>();
                names.add("<b>Номер заказа:</b> ");
                names.add("<b>Имя:</b> ");
                names.add("<b>Номер:</b> ");
                names.add("<b>Адрес:</b> ");
                names.add("<b>Категория:</b> ");
                names.add("<b>Сервис:</b> ");
                names.add("<b>Опция:</b> ");
                message.setParseMode("HTML");
                StringBuilder text = new StringBuilder();
                if(orders!=null) {
                   for (List<String> order : orders) {
                       for (int i = 0; i < order.size(); i++) {
                           if (order.get(i).contains(":arrow_down:")) {
                               String str = order.get(i);
                               str = str.replace(":arrow_down:", "");
                               text.append(names.get(i)).append(str).append("\n");
                           } else {
                               text.append(names.get(i)).append(order.get(i)).append("\n");
                           }

                       }

                       message.setText(String.valueOf(text));
                       text.delete(0, text.capacity() - 1);
                       sendMessage(message);
                   }

                   userCache.setUsersBotState(userId, BotState.DELETE);
                   message.setText(BotService.getInstance().getTextByName("Отмена заказа"));
                   message.setReplyMarkup(setMenuKeyboard());
                   sendMessage(message);
               }else {
                   message.setText(BotService.getInstance().getTextByName("Нет заказов"));
                   message.setReplyMarkup(menuMarkup());
                   sendMessage(message);

               }
            }
            else if(update.getMessage().getText().equals("/start")){
                mainMenu(update);
            }else if(update.getMessage().getText().equals(EmojiParser.parseToUnicode(":house: Вернуться в меню"))){
                mainMenu(update);
            }
            else if(state == BotState.ORDER){
                message = massageHandler.handleUpdate(update,userCache);
                sendMessage(message);
            }else if(state == BotState.DELETE){
                long id = 0L;
                try {
                    id = Long.parseLong(update.getMessage().getText());
                } catch (Exception e) {
                    message.setText(BotService.getInstance().getTextByName("Некорректный номер заказа"));
                }
                String response = removeOrder(id,update);
                if(response.equals("Order does not exist") || response.equals("")){
                    message.setText(BotService.getInstance().getTextByName("Некорректный номер заказа"));
                }else{
                    message.setText(BotService.getInstance().getTextByName("Успешно отменен"));
                }
                sendMessage(message);
            }else if(state == BotState.VIDEO){
                message.setText(BotService.getInstance().getTextByName("Отправьте видео"));
                sendMessage(message);
            }
            else if(state == BotState.ADDITION){


                String message_text = update.getMessage().getText();

                if(message_text.equals("Продолжить")){
                    userCache.setUsersBotState(userId,BotState.FILLING_INFO);
                    message = fillingInfo.fillingHandler(update, userCache);
                    userCache.setUsersAddition(userId, "Не указана");
                    ReplyKeyboardMarkup keyboardMarkup = setMenuKeyboard();
                    message.setReplyMarkup(keyboardMarkup);
                    sendMessage(message);
                }else {
                    userCache.setUsersBotState(userId, BotState.FILLING_INFO);
                    userCache.setUsersAddition(userId, message_text);
                    message = fillingInfo.fillingHandler(update, userCache);
                    ReplyKeyboardMarkup keyboardMarkup = setMenuKeyboard();
                    message.setReplyMarkup(keyboardMarkup);
                    sendMessage(message);
                }


            }
            else{
                message = fillingInfo.fillingHandler(update,userCache);
                if(userCache.getUsersBotState(userId)==BotState.PROCESS_INFO){
                    processInfo(update);
                    sendOrder(update);
                }
                sendMessage(message);
            }
        }else if(update.getMessage().hasPhoto()){
            if(state == BotState.ADDITION) {
                int message_id = update.getMessage().getMessageId();
                String mId = String.valueOf(message_id);
                userCache.setUsersBotState(userId,BotState.FILLING_INFO);
                userCache.setUsersVideo(userId, mId);
                message = fillingInfo.fillingHandler(update, userCache);
                ReplyKeyboardMarkup keyboardMarkup = setMenuKeyboard();
                message.setReplyMarkup(keyboardMarkup);
                sendMessage(message);
            }
            else {
                message.setText(BotService.getInstance().getTextByName("Отправьте видео"));
                sendMessage(message);
            }
        }else if(update.getMessage().hasVideo()){
            if(state == BotState.VIDEO) {
                String video_id = update.getMessage().getVideo().getFileId();
                userCache.setUsersVideo(userId, video_id);
                userCache.setUsersBotState(userId, BotState.ADDITION);
                ReplyKeyboardMarkup keyboardMarkup = keyboardMarkupSettings();
                KeyboardRow keyboardRow = new KeyboardRow();
                keyboardRow.add(EmojiParser.parseToUnicode("Продолжить"));
                keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
                message.setText(BotService.getInstance().getTextByName("Дополнительно"));
                message.setReplyMarkup(keyboardMarkup);
                sendMessage(message);
            }

        }else if(update.getMessage().hasVoice()){
            if(state == BotState.ADDITION){
                String audio_id = update.getMessage().getVoice().getFileId();
                userCache.setUsersAddition(userId,audio_id);
                userCache.setUsersBotState(userId,BotState.FILLING_INFO);
                message = fillingInfo.fillingHandler(update,userCache);
                ReplyKeyboardMarkup keyboardMarkup = setMenuKeyboard();
                message.setReplyMarkup(keyboardMarkup);
                sendMessage(message);
            }
        } else if(update.getMessage().hasDocument()){
            message.setText(BotService.getInstance().getTextByName("Отправьте видео"));
            sendMessage(message);
        }
    }
    private void sendOrder(Update update) {

        HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead
        int userId = update.getMessage().getFrom().getId();
        try {

            HttpPost request = new HttpPost("https://fivesbot3.herokuapp.com/orders/add");
            //HttpPost request = new HttpPost("http://localhost:9966/orders/add");
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            String time = LocalDateTime.now().toString();
//            Date parsedDate = dateFormat.parse(time);
//            Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
//            String times = timestamp.toString();
            StringEntity params =new StringEntity("{\"name\":\"" + userCache.getUsersData(userId).getName() + "\"," +
                    "\"address\":\"" + userCache.getUsersData(userId).getAddress() + "\"," +
                    "\"phoneNumber\":\"" + userCache.getUsersData(userId).getNumber() + "\"," +
                    "\"userId\":\"" + userId + "\"," +
                    "\"categoryName\":\"" + userCache.getOrdersData(userId).getCategory() + "\"," +
                    "\"serviceName\":\"" + userCache.getOrdersData(userId).getService() + "\"," +
                    "\"optionName\":\"" + userCache.getOrdersData(userId).getOption() + "\"} ","UTF-8");
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);

            //handle response here...

        }catch (Exception ex) {

            //handle exception here

        } finally {
            //Deprecated
            //httpClient.getConnectionManager().shutdown();
        }
    }

    private ReplyKeyboardMarkup setMenuKeyboard(){
        ReplyKeyboardMarkup keyboardMarkup = keyboardMarkupSettings();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(EmojiParser.parseToUnicode(":house: Вернуться в меню"));
        keyboardMarkup.setKeyboard(Collections.singletonList(keyboardRow));
        return keyboardMarkup;
    }

    private void mainMenu(Update update){
        SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId());
        int userId = update.getMessage().getFrom().getId();
        message.setText(BotService.getInstance().getTextByName("Приветствие"));
        userCache.setUsersBotState(userId,BotState.ORDER);
        message.setReplyMarkup(menuMarkup());
        sendMessage(message);
    }

    private void processInfo(Update update) {
        List<Long> chats = BotService.getInstance().getChats();
        if(chats!=null) {
            for (long chat : chats) {
                int userId = update.getMessage().getFrom().getId();
                SendVideo message = new SendVideo().setChatId(chat);
                message.setVideo(userCache.getUsersData(userId).getVideo_id());
                SendVoice audio = new SendVoice().setChatId(chat).setVoice(userCache.getUsersData(userId).getAddition());
                //audio.setCaption(userCache.getOrdersData(userId).toString() + "\n" + userCache.getUsersData(userId).toString());
                message.setCaption(userCache.getOrdersData(userId).toString().replace(":arrow_down:", "") + "\n" + userCache.getUsersData(userId).toString());

                int errors = 0;
                try {
                    if (!userCache.getUsersData(userId).getAddition().equals("Продолжить")) {
                        execute(audio);
                    }
                } catch (Exception e) {
                    errors++;
                    e.printStackTrace();
                }
                try {
                    if (errors == 1) {
                        message.setCaption("Проблема: " + userCache.getUsersData(userId).getAddition() + "\n" + userCache.getOrdersData(userId).toString().replace(":arrow_down:", "") + "\n" + userCache.getUsersData(userId).toString()).setParseMode("HTML");
                        execute(message);
                    } else {
                        execute(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public ReplyKeyboardMarkup keyboardMarkupSettings() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    private ReplyKeyboardMarkup menuMarkup() {
        List<String> categories = BotService.getInstance().getCategories();
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        for(String category : categories){
            row.add(category);
            keyboard.add(row);
            row = new KeyboardRow();
        }
        row.add("Мои заказы");
        keyboard.add(row);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);
        keyboardMarkup.setSelective(true);
        return keyboardMarkup;
    }

    public void sendMessage(SendMessage message){
        try{
            execute(message);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public String getBotUsername() {
        return "fivesgroup_bot";
    }

    public String getBotToken() {
        return "1141031716:AAEhdy7Ft4u7c3YBE0KpBVDB4t5awHKpYBc";
    }

    private String removeOrder(long orderId,Update update) {
        int userId = update.getMessage().getFrom().getId();
        try {
            String url = "https://fivesbot3.herokuapp.com/orders/delete/userid/" + userId + "/id/" + orderId;
            //String url = "http://localhost:9966/orders/delete/userid/" + userId + "/id/" + orderId;

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("DELETE");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

             return response.toString();
        } catch (Exception e) {

        }
        return "";
    }

}
