package ca.gc.dfo.slims.service;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.entity.user.SlimsUser;
import ca.gc.dfo.slims.domain.entity.user.UserRole;
import ca.gc.dfo.slims.domain.repository.user.SlimsUserRepository;
import ca.gc.dfo.slims.domain.repository.user.UserRoleRepository;
import ca.gc.dfo.slims.dto.UserManagementDTO;
import ca.gc.dfo.slims.dto.user.UserListDTO;
import ca.gc.dfo.slims.dto.user.UserListItemDTO;
import ca.gc.dfo.slims.service.audit.UserAuditService;
import ca.gc.dfo.slims.utility.CommonUtils;
import ca.gc.dfo.spring_commons.commons_offline_wet.services.AbstractEAccessUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UserService extends AbstractEAccessUserService {
    private static final String SERVICE_NAME = "UserService";

    @Autowired
    private SlimsUserRepository slimsUserRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserAuditService userAuditService;

    @Transactional
    public SlimsUser saveUser(SlimsUser user) {
        CommonUtils.getLogger().debug("{}:saveUser with SlimsUser({}", SERVICE_NAME, user.getLastName());
        SlimsUser savedUser = null;
        user.setDateModified(new Date());
        try {
            savedUser = (SlimsUser) getUserRepository().saveAndFlush(user);
        } catch (Exception e) {
            CommonUtils.getLogger().error("{}:saveUser Failed to save SlimsUser({} due to {}",
                SERVICE_NAME, user.getFullname(), e.getMessage(), e);
        }
        return savedUser;
    }

    @Transactional
    public SlimsUser addUserForm(UserManagementDTO umDto) {
        CommonUtils.getLogger().debug("{}:addUserForm with UserManagementDTO({}", SERVICE_NAME, umDto.toString());
        SlimsUser user = convertFromForm(umDto);
        user.setDateCreated(new Date());
        user.setUserRole(userRoleRepository.getOne(umDto.getRole().longValue()));
        return saveUser(user);
    }

    @Transactional
    public void toggleUserActive(Long id) {
        CommonUtils.getLogger().debug("{}:toggleUserActive with id({}", SERVICE_NAME, id);
        SlimsUser user = getUserById(id);
        boolean active = user.getActivated() != null ? user.getActivated() : false;
        CommonUtils.getLogger().debug("{}:toggleUserActive set user activated to {}", SERVICE_NAME, !active);
        user.setActivated(!active);
        saveUser(user);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public SlimsUser editUserForm(UserManagementDTO umDto) {
        CommonUtils.getLogger().debug("{}:editUserForm with UserManagementDTO({}", SERVICE_NAME, umDto.toString());
        SlimsUser user = convertFromForm(umDto);
        SlimsUser editedUser = (SlimsUser)getUserRepository().getOne(umDto.getUserId());
        BeanUtils.copyProperties(user, editedUser, "userId", "role");
        editedUser.setDateModified(new Date());
        editedUser.setUserRole(userRoleRepository.getOne(umDto.getRole().longValue()));
        return saveUser(editedUser);
    }

    @SuppressWarnings("unchecked")
    public SlimsUser getUserById(Long id) {
        CommonUtils.getLogger().debug("{}:getUserById with id({}", SERVICE_NAME, id);
        return (SlimsUser) getUserRepository().getOne(id);
    }

    public SlimsUser findUserByUsername(String username) {
        CommonUtils.getLogger().debug("{}:findUserByUsername with username({}", SERVICE_NAME, username);
        return slimsUserRepository.findByNtPrincipalEquals(username);
    }

    public List<UserRole> getAllRoles() {
        CommonUtils.getLogger().debug("{}:getAllRoles", SERVICE_NAME);
        return userRoleRepository.findAll();
    }

    @SuppressWarnings("unchecked")
    private List<SlimsUser> getAllUsers() {
        CommonUtils.getLogger().debug("{}:getAllUsers", SERVICE_NAME);
        return (List<SlimsUser>) getUserRepository().findAll();
    }

    public UserListDTO getUserList() {
        CommonUtils.getLogger().debug("{}:getUserList", SERVICE_NAME);
        List<UserListItemDTO> userItemList = new ArrayList<>();
        List<SlimsUser> tempList = getAllUsers();

        for (SlimsUser user: tempList) {
            UserListItemDTO userListItem = new UserListItemDTO();
            userListItem.setUserId(user.getUserId());
            userListItem.setFullname(user.getFullname());
            userListItem.setNtPrincipal(user.getNtPrincipal());
            userListItem.setActiveStatus(user.getActivated());

            if (!user.getNtPrincipal().equals(SecurityHelper.getNtPrincipal())) {
                userItemList.add(userListItem);
            }
        }

        List<Long> allUserID = userAuditService.getAllUserID();
        CommonUtils.getLogger().debug("{}:getUserList got {} userIds", SERVICE_NAME, allUserID.size());
        return new UserListDTO(userItemList, allUserID);
    }

    private SlimsUser convertFromForm(UserManagementDTO umDto) {
        CommonUtils.getLogger().debug("{}:convertFromForm with UserManagementDTO({}", SERVICE_NAME, umDto.toString());
        SlimsUser user = new SlimsUser();
        user.setUserId(umDto.getUserId());
        user.setNtPrincipal(umDto.getNtPrincipal());
        user.setActivated(umDto.getActiveFlag() != null && umDto.getActiveFlag().equals(1));
        user.setFirstName(umDto.getFirstName());
        user.setLastName(umDto.getLastName());

        return user;
    }
}
