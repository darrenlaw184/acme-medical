/********************************************************************************************************
 * File:  Prescription.java Course Materials CST 8277
 *
 * @author Teddy Yap
 * 
 */
package acmemedical.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("unused")
/**
 * The persistent class for the prescription database table.
 */
@Entity
@Table(name = "prescription")
@Access(AccessType.FIELD)
@NamedQuery(name = "Prescription.findAll", query = "SELECT p FROM Prescription p")
@NamedQuery(name = "Prescription.findByPhysicianAndPatient", query = "SELECT p FROM Prescription p WHERE p.physician.id = :param1 AND p.patient.id = :param2")
public class Prescription extends PojoBaseCompositeKey<PrescriptionPK> implements Serializable {
	public static final String ALL_PRESCRIPTIONS_QUERY_NAME = "Prescription.findAll";
	public static final String FIND_BY_PHYSICIAN_PATIENT_QUERY_NAME = "Prescription.findByPhysicianAndPatient";
	private static final long serialVersionUID = 1L;

	// Hint - What annotation is used for a composite primary key type?
	@EmbeddedId
	private PrescriptionPK id;

	// @MapsId is used to map a part of composite key to an entity.
	@MapsId("physicianId")
    @ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "physician_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	private Physician physician;

	@MapsId("patientId")
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
	@JsonIgnore
	private Patient patient;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "medicine_id", referencedColumnName = "medicine_id")
	@JsonIgnore
	private Medicine medicine;

	@Column(name = "number_of_refills")
	private int numberOfRefills;

	@Column(length = 100, name = "prescription_information")
	private String prescriptionInformation;


	public Prescription() {
		id = new PrescriptionPK();
	}

	@Override
	public PrescriptionPK getId() {
		return id;
	}

	@Override
	public void setId(PrescriptionPK id) {
		this.id = id;
	}

	public Physician getPhysician() {
		return physician;
	}

	public void setPhysician(Physician physician) {
		id.setPhysicianId(physician.id);
		this.physician = physician;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		id.setPatientId(patient.id);
		this.patient = patient;
	}

	public Medicine getMedicine() {
		return medicine;
	}

	public void setMedicine(Medicine medicine) {
		this.medicine = medicine;
	}

	public int getNumberOfRefills() {
		return numberOfRefills;
	}
	
	public void setNumberOfRefills(int numberOfRefills) {
		this.numberOfRefills = numberOfRefills;
	}

	public String getPrescriptionInformation() {
		return prescriptionInformation;
	}

	public void setPrescriptionInformation(String prescriptionInformation) {
		this.prescriptionInformation = prescriptionInformation;
	}

	//Inherited hashCode/equals is sufficient for this entity class

}