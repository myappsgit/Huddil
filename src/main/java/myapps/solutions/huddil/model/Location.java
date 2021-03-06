package myapps.solutions.huddil.model;
// Generated Nov 17, 2017 4:19:00 PM by Hibernate Tools 5.2.1.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

/**
 * Location generated by hbm2java
 */
@SqlResultSetMapping(name = "locationDetails", classes = {
		@ConstructorResult(targetClass = LocationDetails.class, columns = { @ColumnResult(name = "id"),
				@ColumnResult(name = "city"), @ColumnResult(name = "locality"), @ColumnResult(name = "locationName"),
				@ColumnResult(name = "address"), @ColumnResult(name = "landmark"), @ColumnResult(name = "nearBy"),
				@ColumnResult(name = "description") }) })
@Entity
@Table(name = "location", catalog = "huddil")
public class Location implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2955860189330127033L;
	private Integer id;
	private City city;
	private Locality locality;
	private MultiTenant multiTenant;
	private Integer userPref;
	private String name;
	private String address;
	private String landmark;
	private String nearBy;
	private List<Facility> facilities = new ArrayList<Facility>(0);
	private Set<FacilityTermsConditions> facilityTermsConditionses = new HashSet<FacilityTermsConditions>(0);

	public Location() {
	}

	public Location(int userPref, String name, String address) {
		this.userPref = userPref;
		this.name = name;
		this.address = address;
	}

	public Location(City city, Locality locality, MultiTenant multiTenant, int userPref, String name, String address,
			String landmark, String nearBy, List<Facility> facilities) {
		this.city = city;
		this.locality = locality;
		this.multiTenant = multiTenant;
		this.userPref = userPref;
		this.name = name;
		this.address = address;
		this.landmark = landmark;
		this.nearBy = nearBy;
		this.facilities = facilities;
	}

	public Location(int i) {
		this.id = i;
	}

	public Location(String name) {
		this.name = name;
	}

	public Location(String string, String address) {
		this.address = address;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id", unique = true, nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "cityId")
	@JsonProperty(access = Access.WRITE_ONLY)
	public City getCity() {
		return this.city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "localityId")
	@JsonProperty(access = Access.WRITE_ONLY)
	public Locality getLocality() {
		return this.locality;
	}

	public void setLocality(Locality locality) {
		this.locality = locality;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JsonProperty(access = Access.WRITE_ONLY)
	@JoinColumn(name = "multiTenantId")
	public MultiTenant getMultiTenant() {
		return this.multiTenant;
	}

	public void setMultiTenant(MultiTenant multiTenant) {
		this.multiTenant = multiTenant;
	}

	// @ManyToOne(fetch = FetchType.LAZY)
	@Column(name = "userId", nullable = false)
	public Integer getUserPref() {
		return this.userPref;
	}

	public void setUserPref(Integer userPref) {
		this.userPref = userPref;
	}

	@Column(name = "name", nullable = false, length = 128)
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "address", nullable = false, length = 20000)
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "landmark", length = 128)
	public String getLandmark() {
		return this.landmark;
	}

	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}

	@Column(name = "nearBy", length = 128)
	public String getNearBy() {
		return this.nearBy;
	}

	public void setNearBy(String nearBy) {
		this.nearBy = nearBy;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "location")
	@JsonIgnore
	public List<Facility> getFacilities() {
		return this.facilities;
	}

	public void setFacilities(List<Facility> facilities) {
		this.facilities = facilities;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "location", cascade = CascadeType.ALL)
	public Set<FacilityTermsConditions> getFacilityTermsConditionses() {
		return this.facilityTermsConditionses;
	}

	public void setFacilityTermsConditionses(Set<FacilityTermsConditions> facilityTermsConditionses) {
		this.facilityTermsConditionses = facilityTermsConditionses;
	}

}