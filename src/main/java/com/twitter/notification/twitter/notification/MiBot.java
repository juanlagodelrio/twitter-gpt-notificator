package com.twitter.notification.twitter.notification;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
@Component
public class MiBot extends TelegramLongPollingBot {
    @Override
    public String getBotUsername() {
        return "tw_11_bot";
    }

    @Override
    public String getBotToken() {
        return "6201303459:AAFEAmLGpU5OZ34j6qg2EZrgBi9hFwKJkY8";
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("NOTIFICADO");
//        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().contains("chat")) {
//            try {
//                test(update.getMessage().getText().replaceAll("chat", ""));
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    public void enviarMensaje(String texto) {
        SendMessage mensaje = new SendMessage("13510653", texto);
        try {
            execute(mensaje);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void test(String text) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://api.openai.com/v1/completions");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer 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");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        StringEntity entity = new StringEntity("{\n" +
                "  \"model\": \"text-davinci-003\",\n" +
                "  \"prompt\": \""+text+"\",\n" +
                "  \"max_tokens\": 1024,\n" +
                "  \"temperature\": 0,\n" +
                "  \"top_p\": 1,\n" +
                "  \"n\": 1,\n" +
                "  \"stream\": false\n" +
                "}");
        httpPost.setEntity(entity);
        CloseableHttpResponse response = httpClient.execute(httpPost);
        String responseString = EntityUtils.toString(response.getEntity());
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(responseString.toString(), JsonElement.class);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonArray jsonArrayChoices = jsonObject.getAsJsonArray("choices");
        for (JsonElement element:jsonArrayChoices){
            String respuesta = element.getAsJsonObject().get("text").getAsString();
            System.out.println(respuesta);
            enviarMensaje(respuesta);
        }
        response.close();
        httpClient.close();

    }
}
