/**
 * 
 */
package ca.gc.dfo.slims.domain.entity.audit;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import ca.gc.dfo.slims.audit.listener.Slims_RevisionListener;


@Entity
@Table(name = "SLIMS_REVINFO")
@RevisionEntity(Slims_RevisionListener.class)
@SequenceGenerator(name = "PK", sequenceName = "SLIMS_REVINFO_SQ", allocationSize = 1)
public class Slims_RevisionEntity implements Serializable {

    private static final long serialVersionUID = 3775550420286576001L;
    private String userFirstName;
    private String userLastName;
    private String userAccount;
    private Long userId;

    @RevisionTimestamp
    private Date createdDate;


    @Id
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PK")
    @Column(name = "ID")
    private int id;


 

   

    public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    @ElementCollection
    @JoinTable(name = "SLIMS_REVCHANGES", joinColumns = {@JoinColumn(name = "REV")})
    @Column(name = "ENTITYNAME")
    @ModifiedEntityNames
    private Set<String> modifiedEntityNames;
    
 

    public Set<String> getModifiedEntityNames() {
        return modifiedEntityNames;
    }

    public void setModifiedEntityNames(Set<String> modifiedEntityNames) {
        this.modifiedEntityNames = modifiedEntityNames;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}



