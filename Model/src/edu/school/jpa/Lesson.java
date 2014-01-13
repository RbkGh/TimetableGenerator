package edu.school.jpa;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javax.xml.bind.annotation.XmlTransient;

@Entity
@NamedQueries({
  @NamedQuery(name = "Lesson.findAll", query = "select o from Lesson o WHERE o.userId = :userId")
})
@Table(name = "\"lesson\"")
public class Lesson extends UserIdJpaEntity implements Serializable {
  @Column(name="end", nullable = false)
  private String end;
  @Id
  @Column(name="id", nullable = false)
  private Integer id;
  @Column(name="start", nullable = false)
  private String start;
  @Column(name = "USER_ID")
  protected Integer userId;
  @OneToMany(mappedBy = "lesson")
  private List<Availability> availabilityList;

  public Lesson() {
  }

  public Lesson(String end, Integer id, String start, Integer userId) {
    this.end = end;
    this.id = id;
    this.start = start;
    this.userId = userId;
  }


  public String getEnd() {
    return end;
  }

  public void setEnd(String end) {
    this.end = end;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public List<Availability> getAvailabilityList() {
    return availabilityList;
  }

  public void setAvailabilityList(List<Availability> availabilityList) {
    this.availabilityList = availabilityList;
  }

  public Availability addAvailability(Availability availability) {
    getAvailabilityList().add(availability);
    availability.setLesson(this);
    return availability;
  }

  public Availability removeAvailability(Availability availability) {
    getAvailabilityList().remove(availability);
    availability.setLesson(null);
    return availability;
  }

  public Object getEID() {
    return id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return start+" - "+end;
  }

  @XmlTransient
  public String toXML() {
    StringBuffer xml = new StringBuffer("<?xml version = '1.0' encoding = 'windows-1252'?>\n");
    xml.append("  <lesson>\n");
    xml.append("  </lesson>\n");
    return xml.toString();
  }
}
