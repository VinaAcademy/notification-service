package vn.vinaacademy.notification.external.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Long id;
    private String name;
    private String code;
}
