/********************************************************************************************************
 * File:  SecurityUser.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * 
 */
package acmemedical.entity;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@SuppressWarnings("unused")

/**
 * User class used for (JSR-375) Jakarta EE Security authorization/authentication
 */

@Entity
@Table(name = "security_user")
@NamedQuery(name = "SecurityUser.userByName", query = "SELECT u FROM SecurityUser u WHERE u.username = :param1")
@NamedQuery(name = "SecurityUser.findByPhysician", query = "SELECT u FROM SecurityUser u WHERE u.physician = :param1")
public class SecurityUser implements Serializable, Principal {
    /** Explicit set serialVersionUID */
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "user_id", nullable = false)
    protected int id;
    
    @Basic(optional = false)
    @Column(name = "username", nullable = false, length = 50)
    protected String username;
    
    @Basic(optional = false)
    @Column(name = "pw_hash", nullable = false, length = 255)
    protected String pwHash;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "physician_id", referencedColumnName = "id")
    protected Physician physician;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_has_role",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    )
    @JsonIgnore
    protected Set<SecurityRole> roles = new HashSet<SecurityRole>();

    public SecurityUser() {
        super();
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwHash() {
        return pwHash;
    }
    
    public void setPwHash(String pwHash) {
        this.pwHash = pwHash;
    }

    public Set<SecurityRole> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<SecurityRole> roles) {
        this.roles = roles;
    }

    public Physician getPhysician() {
        return physician;
    }
    
    public void setPhysician(Physician physician) {
        this.physician = physician;
    }

    // Principal
    @Override
    public String getName() {
        return getUsername();
    }

    @JsonProperty("roles")
    public Set<String> getRoleNames() {
        Set<String> roleNames = new HashSet<>();
        for (SecurityRole role : roles) {
            roleNames.add(role.getRoleName());
        }
        return roleNames;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        // Only include member variables that really contribute to an object's identity
        // i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
        // they shouldn't be part of the hashCode calculation
        return prime * result + Objects.hash(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof SecurityUser otherSecurityUser) {
            // See comment (above) in hashCode():  Compare using only member variables that are
            // truly part of an object's identity
            return Objects.equals(this.getId(), otherSecurityUser.getId());
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SecurityUser [id = ").append(id).append(", username = ").append(username).append("]");
        return builder.toString();
    }
    
}
