package ca.gc.dfo.slims.dto.user;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class UserListDTO {

    private List<UserListItemDTO> userList;

    public UserListDTO() {
        // do nothing
    }

    public UserListDTO(List<UserListItemDTO> userList,List<Long> allUserIds) {
        String auditHrefUrl = "";
        for (UserListItemDTO userListItem: userList) {

            if(!CollectionUtils.isEmpty(allUserIds) && allUserIds.contains(userListItem.getUserId())){
                auditHrefUrl ="<a href='list/report/single?userID=" + userListItem.getUserId() +"' class='btn btn-link cell-btn'><span class='glyphicon glyphicon-record text-green'></span></a>";
            }else {
                auditHrefUrl = "";
            }
            String buttons = "<a href='javascript: userManagement.goToEdit("
                + userListItem.getUserId()
                + ")' title='Edit' class='btn btn-link cell-btn'>"
                + "<span class='glyphicon glyphicon-edit'></span></a>"
                + "<a href='javascript: userManagement.activateUser("
                + userListItem.getUserId()
                + ",\""
                + userListItem.getNtPrincipal()
                + "\","
                + userListItem.getActiveStatus()
                + ")' title='Disable' id='activate_"
                + userListItem.getUserId()
                + "' class='btn btn-link cell-btn'>";
            if (userListItem.getActiveStatus()) {
                buttons += "<span class='glyphicon glyphicon-ok-circle text-green'></span></a>" +auditHrefUrl;
            } else {
                buttons += "<span class='glyphicon glyphicon-ban-circle text-red'></span></a>"+auditHrefUrl;
            }
            userListItem.setButtons(buttons);
        }

        this.setUserList(userList);
    }
}
