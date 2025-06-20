package vn.vinaacademy.email.listener.message;

import lombok.Data;

import java.util.Map;

@Data
public class GenericEmailEvent {
    private EmailEventType type;
    private Map<String, Object> data;
}
