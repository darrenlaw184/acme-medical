/********************************************************************************************************
 * File:  MedicalSchool.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package acmemedical.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * The persistent class for the medical_school database table.
 */
@Entity
@Table(name = "medical_school")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "is_public", discriminatorType = DiscriminatorType.INTEGER)
@NamedQuery(name = "MedicalSchool.findAll", query = "SELECT ms FROM MedicalSchool ms")
@NamedQuery(name = "MedicalSchool.findById", query = "SELECT ms FROM MedicalSchool ms WHERE ms.id = :param1")
@NamedQuery(name = "MedicalSchool.isDuplicate", query = "SELECT COUNT(ms) FROM MedicalSchool ms WHERE ms.name = :param1")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = PublicSchool.class, name = "PublicSchool"),
    @JsonSubTypes.Type(value = PrivateSchool.class, name = "PrivateSchool")
})
@AttributeOverride(name = "id", column = @Column(name = "school_id"))
public abstract class MedicalSchool extends PojoBase implements Serializable {
	public static final String ALL_MEDICAL_SCHOOLS_QUERY_NAME = "MedicalSchool.findAll";
	public static final String SPECIFIC_MEDICAL_SCHOOL_QUERY_NAME = "MedicalSchool.findById";
	public static final String IS_DUPLICATE_QUERY_NAME = "MedicalSchool.isDuplicate";
	private static final long serialVersionUID = 1L;
	
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@OneToMany(mappedBy = "school", cascade = jakarta.persistence.CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<MedicalTraining> medicalTrainings = new HashSet<>();

	@Basic(optional = false)
	@Column(name = "is_public", nullable = false, insertable = false, updatable = false)
	private boolean isPublic;

	public MedicalSchool() {
		super();
	}

    public MedicalSchool(boolean isPublic) {
        this();
        this.isPublic = isPublic;
    }

	public Set<MedicalTraining> getMedicalTrainings() {
		return medicalTrainings;
	}

	public void setMedicalTrainings(Set<MedicalTraining> medicalTrainings) {
		this.medicalTrainings = medicalTrainings;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	//Inherited hashCode/equals is NOT sufficient for this entity class
	
	/**
	 * Very important:  Use getter's for member variables because JPA sometimes needs to intercept those calls<br/>
	 * and go to the database to retrieve the value
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// Only include member variables that really contribute to an object's identity
		// i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
		// they shouldn't be part of the hashCode calculation
		
		// The database schema for the MEDICAL_SCHOOL table has a UNIQUE constraint for the NAME column,
		// so we should include that in the hash/equals calculations
		
		return prime * result + Objects.hash(getId(), getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof MedicalSchool otherMedicalSchool) {
			// See comment (above) in hashCode():  Compare using only member variables that are
			// truly part of an object's identity
			return Objects.equals(this.getId(), otherMedicalSchool.getId()) &&
				Objects.equals(this.getName(), otherMedicalSchool.getName());
		}
		return false;
	}
}
