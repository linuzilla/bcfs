package ncu.cc.bcfs.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the silos database table.
 * 
 */
@Entity
@Table(name="silos")
@NamedQuery(name="Silo.findAll", query="SELECT s FROM Silo s")
public class Silo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false)
	private Integer id;

	@Column(nullable=false, length=81)
	private String bundle;

	@Column(name="remote_address", length=128)
	private String remoteAddress;

	@Column(nullable=false)
	private int size;

	@Column(name="updated_at", nullable=true)
	private Timestamp updatedAt;

	@Column(nullable=false, length=24)
	private String user;

	public Silo() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBundle() {
		return this.bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getRemoteAddress() {
		return this.remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Timestamp getUpdatedAt() {
		return this.updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}