package services;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class BotService {

    private static final String catUrl = "https://fivesbot3.herokuapp.com/categories/";
    private static final String serviceUrl = "https://fivesbot3.herokuapp.com/services/";
    private static final String optionUrl = "https://fivesbot3.herokuapp.com/options/";

    private static volatile BotService instance;

    private BotService(){

    }

    public static BotService getInstance(){
        BotService currentInstance;
        if(instance == null){
            synchronized (BotService.class){
                if(instance == null){
                    instance = new BotService();
                }
                currentInstance = instance;
            }
        }
        else{
            currentInstance = instance;
        }
        return currentInstance;
    }


    public List<String> getCategories(){
        try{
            List<String> result = new ArrayList<>();
            String URL = catUrl;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String str = jObject.getString("categoryName");
                result.add(str);
            }
            return result;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public long getCatIdByName(String name){
        try{

            String URL = catUrl + "category/name?name=" + URLEncoder.encode(name, "UTF-8");
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            long str  = 0;
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                str = jObject.getLong("id");

            }
            return str;
        } catch (Exception e){
            return 0;
        }
    }

    public List<String> getServicesByCatId(long id){
        try{
            List<String> result = new ArrayList<>();
            String URL = serviceUrl + "service/category/id/" + id;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String str = jObject.getString("serviceName");
                result.add(str);
            }
            return result;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public String getServicesURLByName(String name){
        try{
            String URL = serviceUrl + "service/name?name=" + URLEncoder.encode(name, "UTF-8");
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            String str  = "";
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                str = jObject.getString("videoUrl");

            }
            return str;
        } catch (Exception e){
            return "";
        }
    }

    public long getServiceIdByName(String name){
        try{

            String URL = serviceUrl + "service/name?name=" + URLEncoder.encode(name, "UTF-8");
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            long str  = 0;
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                str = jObject.getLong("id");

            }
            return str;
        } catch (Exception e){
            return 0;
        }
    }

    public List<String> getOptionsByServiceId(long id){
        try{
            List<String> result = new ArrayList<>();
            String URL = optionUrl + "option/service/" + id;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String str = jObject.getString("optionName");
                result.add(str);
            }
            return result;

        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public long getOptionIdByName(String name){
        try{

            String URL = optionUrl + "option/name?name=" + URLEncoder.encode(name, "UTF-8");
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            long str  = 0;
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                str = jObject.getLong("id");

            }
            return str;
        } catch (Exception e){
            return 0;
        }
    }

    public List<List<String>> getOrdersByUserId(int userID){
        try{

            List<List<String>> result = new ArrayList<>();
            String URL = "https://fivesbot3.herokuapp.com/orders/user/" + userID;
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();

            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
//                str = jObject.getLong("id");
                List<String> row = new ArrayList<>();
                row.add(String.valueOf(jObject.getLong("id")));
                row.add(jObject.getString("fullName"));
                row.add(jObject.getString("phoneNumber"));
                row.add(jObject.getString("address"));
                row.add(jObject.getString("categoryName"));
                row.add(jObject.getString("serviceName"));
                row.add(jObject.getString("optionName"));
                result.add(row);

            }
            return result;
        } catch (Exception e){
            return null;
        }
    }

    public String getTextByName(String name){
        try{
            String URL = "https://fivesbot3.herokuapp.com/texts/text/name?name=" + URLEncoder.encode(name, "UTF-8");
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            String str  = "";
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                str = jObject.getString("text");

            }
            return str;
        } catch (Exception e){
            return "";
        }
    }

    public String getChats(){
        try{
            String URL = "https://fivesbot3.herokuapp.com/chats";
            CloseableHttpClient client = HttpClientBuilder.create().setSSLHostnameVerifier(
                    new NoopHostnameVerifier()).build();
            HttpGet request = new HttpGet(URL);
            CloseableHttpResponse response = client.execute(request);
            HttpEntity ht = response.getEntity();
            BufferedHttpEntity buf = new BufferedHttpEntity(ht);
            String responseString = EntityUtils.toString(buf,"UTF-8");
            JSONArray jArray = (JSONArray) new JSONTokener(responseString).nextValue();
            String str  = "";
            for(int i = 0;i<jArray.length();i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                str = jObject.getString("chatId");

            }
            return str;
        } catch (Exception e){
            return "";
        }
    }
}
