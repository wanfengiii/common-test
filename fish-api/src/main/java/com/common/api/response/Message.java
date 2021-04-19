package com.common.api.response;

import lombok.Data;

import java.text.MessageFormat;

@Data
public class Message {

    private String code;

    private String message;

    private Message(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static Message of(String code, String message, Object... args) {
        if (args != null) {
            message = MessageFormat.format(message, args);
        }
        return new Message(code, message);
    }

}