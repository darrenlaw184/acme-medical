/********************************************************************************************************
 * File:  MedicalCertificate.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package acmemedical.entity;

import java.io.Serializable;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@SuppressWarnings("unused")

/**
 * The persistent class for the medical_certificate database table.
 */
@Entity
@Table(name = "medical_certificate")
@AttributeOverride(name = "id", column = @Column(name = "certificate_id"))
@NamedQuery(name = "MedicalCertificate.findById", query = "SELECT mc FROM MedicalCertificate mc WHERE mc.id = :param1")
public class MedicalCertificate extends PojoBase implements Serializable {
	public static final String ID_CARD_QUERY_NAME = "MedicalCertificate.findById";
	private static final long serialVersionUID = 1L;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medical_training_id", referencedColumnName = "training_id")
	private MedicalTraining medicalTraining;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", referencedColumnName = "id")
	private Physician owner;

	@Basic(optional = false)
	@Column(name = "signed", nullable = false)
	private byte signed;

	public MedicalCertificate() {
		super();
	}
	
	public MedicalCertificate(MedicalTraining medicalTraining, Physician owner, byte signed) {
		this();
		this.medicalTraining = medicalTraining;
		this.owner = owner;
		this.signed = signed;
	}

	public MedicalTraining getMedicalTraining() {
		return medicalTraining;
	}

	public void setMedicalTraining(MedicalTraining medicalTraining) {
		this.medicalTraining = medicalTraining;
	}

	public Physician getOwner() {
		return owner;
	}

	public void setOwner(Physician owner) {
		this.owner = owner;
	}

	public byte getSigned() {
		return signed;
	}

	public void setSigned(byte signed) {
		this.signed = signed;
	}

	public void setSigned(boolean signed) {
		this.signed = (byte) (signed ? 0b0001 : 0b0000);
	}
	
	//Inherited hashCode/equals is sufficient for this entity class

}