package ca.gc.dfo.slims.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserManagementDTO {

    private Long userId;

    private String ntPrincipal;

    private Integer role;

    private String firstName;

    private String lastName;

    private Integer activeFlag;

    public void setNtPrincipal(String ntPrincipal) {
        this.ntPrincipal = ntPrincipal != null ? ntPrincipal.replaceAll("\\s", "") : "";
    }

    public String toString() {
        return String.format("UserId(%s), FirstName(%s), LastName(%s) Role(%d) active(%d) NtPrincipal(%s)",
            getUserId(), getFirstName(), getLastName(), getRole(), getActiveFlag(), getNtPrincipal());
    }
}
