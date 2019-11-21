package ca.gc.dfo.slims.dto.user;

import lombok.Data;

@Data
public class UserListItemDTO {
    private Long userId;

    private String ntPrincipal;

    private String fullname;

    private String buttons;

    private Boolean activeStatus;
}
