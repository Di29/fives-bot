package data;

import model.BotState;
import model.OrderData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserCache {

    private Map<Integer, BotState> usersBotstate = new HashMap<>();
    private Map<Integer, UserData> usersData = new HashMap<>();
    private Map<Integer, OrderData> usersOrder = new HashMap<>();
    private Map<Integer, BotState> previousBotState = new HashMap<>();

    public void setUsersOrder(int userId, OrderData orderData){
        usersOrder.put(userId,orderData);
    }

    public OrderData getOrdersData(int userId){
        OrderData orderData = usersOrder.get(userId);
        if(orderData==null){
            orderData = new OrderData();
        }
        return orderData;
    }

    public void setUsersBotState(int userId, BotState state){
        usersBotstate.put(userId,state);
    }

    public void setPreviousBotState(int userId, BotState state){
        previousBotState.put(userId,state);
    }

    public BotState getUsersBotState(int userID){
        return usersBotstate.get(userID);
    }

    public BotState getPreviousBotState(int userID){ return previousBotState.get(userID);}

    public void setUsersOrderCat(int userId, String category){
        usersOrder.put(userId,new OrderData());
        usersOrder.get(userId).setCategory(category);


    }

    public void setUsersOrderService(int userId, String service){
        usersOrder.get(userId).setService(service);
    }

    public void setUsersOrderOption(int userId, String option){
        usersOrder.get(userId).setOption(option);
    }






    public void setUsersName(int userId, String name){

        usersData.get(userId).setName(name);
    }

    public void setUsersAddress(int userId, String address){
        usersData.get(userId).setAddress(address);
    }

    public void setUsersPhone(int userId, String number){
        usersData.get(userId).setNumber(number);
    }

    public void setUsersVideo(int userId, String id){
        usersData.put(userId,new UserData());
        usersData.get(userId).setVideo_id(id);
    }

    public void setUsersAddition(int userId, String id){
        usersData.get(userId).setAddition(id);
    }

    public void setUsersData(int userId, UserData userData) {
        usersData.put(userId,userData);
    }

    public UserData getUsersData(int userId){
        UserData userData = usersData.get(userId);
        if(userData==null){
            userData = new UserData();
        }
        return userData;
    }
}
