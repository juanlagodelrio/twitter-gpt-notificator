package com.twitter.notification.twitter.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.objects.Update;

@Controller
public class DemoController {

    private final MiBot telegramBot;

    @Autowired
    public DemoController(MiBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping("/telegram-update")
    public void handleTelegramUpdate(@RequestBody Update update) {
        System.out.println("NOTIFICADO");
        if (update.getMessage() != null && update.getMessage().getText().contains("chat")) {
            System.out.println("Mensaje recibido: " + update.getMessage().getText());
        }
    }

}
