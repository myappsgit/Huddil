package myapps.solutions.huddil.model;
// Generated Nov 17, 2017 4:19:00 PM by Hibernate Tools 5.2.1.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.beans.Transient;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Events generated by hbm2java
 */
@Entity
@Table(name = "events", catalog = "huddil")

@SqlResultSetMapping(name = "events", classes = {
		@ConstructorResult(targetClass = Events.class, columns = { @ColumnResult(name = "id"),
				@ColumnResult(name = "comments"), @ColumnResult(name = "status"), @ColumnResult(name = "dateTime") }) })
public class Events implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4401903697619602214L;
	private Integer id;
	private UserPref userPref;
	private String comments;
	private boolean status;
	private Date dateTime;
	private int count;

	public Events() {
	}

	public Events(UserPref userPref, String comments, boolean status, Date dateTime) {
		this.userPref = userPref;
		this.comments = comments;
		this.status = status;
		this.dateTime = dateTime;
	}

	public Events(int id) {
		this.id = id;
	}

	public Events(int id, String comments, boolean status, Date dateTime){
		this.id = id;
		this.comments = comments;
		this.status = status;
		this.dateTime = dateTime;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "forUserId", nullable = false)
	@JsonIgnore
	public UserPref getUserPref() {
		return this.userPref;
	}

	public void setUserPref(UserPref userPref) {
		this.userPref = userPref;
	}

	@Column(name = "comments", nullable = false, length = 128)
	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name = "status", nullable = false)
	public boolean isStatus() {
		return this.status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+5:30")
	@Column(name = "dateTime", nullable = false, length = 19)
	public Date getDateTime() {
		return this.dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	@Transient
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
