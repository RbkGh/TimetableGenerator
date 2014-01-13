package edu.school.jpa;

import java.io.Serializable;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "TimeScheduleMaster.findAll", query = "select o from TimeScheduleMaster o where o.userId = :userId"),
  @NamedQuery(name = "TimeScheduleMaster.maxVersion", query = "select max(o.version) from TimeScheduleMaster o where o.userId = :userId")
})
@Table(name = "\"time_schedule_master\"")
public class TimeScheduleMaster extends UserIdJpaEntity implements Serializable {
  @Column(name="comment")
  private String comment;
  @Column(name="date_created")
  private Timestamp date_created;
  @Id
  @Column(name="eid", nullable = false)
  private Integer eid;
  @Column(name="organization_id", nullable = false)
  private Integer organization_id;
  @Column(name="user_id", nullable = false)
  private Integer userId;
  @Column(name="version", nullable = false)
  private Integer version;

  public TimeScheduleMaster() {
  }

  public TimeScheduleMaster(String comment, Timestamp date_created, Integer eid, Integer organization_id,
                            Integer user_id, Integer version) {
    this.comment = comment;
    this.date_created = date_created;
    this.eid = eid;
    this.organization_id = organization_id;
    this.userId = user_id;
    this.version = version;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Timestamp getDate_created() {
    return date_created;
  }

  public void setDate_created(Timestamp date_created) {
    this.date_created = date_created;
  }

  public Integer getEid() {
    return eid;
  }

  public Integer getEID() {
    return eid;
  }

  public void setEid(Integer eid) {
    this.eid = eid;
  }

  public Integer getOrganization_id() {
    return organization_id;
  }

  public void setOrganization_id(Integer organization_id) {
    this.organization_id = organization_id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }
}
