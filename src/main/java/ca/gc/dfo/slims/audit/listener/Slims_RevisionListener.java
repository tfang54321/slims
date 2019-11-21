package ca.gc.dfo.slims.audit.listener;

import java.io.Serializable;

import ca.gc.dfo.slims.domain.entity.audit.Slims_RevisionEntity;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionListener;
import org.hibernate.envers.RevisionType;

import ca.gc.dfo.slims.config.SecurityHelper;
import ca.gc.dfo.slims.domain.entity.user.SlimsUser;


/**
 *
 */
public class Slims_RevisionListener implements RevisionListener, EntityTrackingRevisionListener {
    @Override
    public void newRevision(Object revisionEntity) {
        Slims_RevisionEntity revision = (Slims_RevisionEntity) revisionEntity;
        SlimsUser userDetails = (SlimsUser) SecurityHelper.getUserDetails();
        if (userDetails != null) {
            revision.setUserAccount(userDetails.getFullname());
            revision.setUserFirstName(userDetails.getFirstName());
            revision.setUserLastName(userDetails.getLastName());
            revision.setUserId(userDetails.getUserId());

        } else {
            revision.setUserAccount("SYSTEM");
            revision.setUserFirstName("SYSTEM");
            revision.setUserLastName("SYSTEM");
        }
    }

    @Override
    public void entityChanged(Class entityClass, String entityName,
                              Serializable entityId, RevisionType revisionType,
                              Object revisionEntity) {
        // no need do anything .
    }
}
