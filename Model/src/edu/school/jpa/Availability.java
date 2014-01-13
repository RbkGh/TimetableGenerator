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
import javax.persistence.Transient;

import javax.xml.bind.annotation.XmlTransient;

@Entity
@NamedQueries({
  @NamedQuery(name = "Availability.findAll", query = "SELECT a FROM Availability a"),
  @NamedQuery(name = "Availability.findById", query = "SELECT a FROM Availability a WHERE a.id = :id"),
  @NamedQuery(name = "Availability.findByOwner", query = "SELECT a FROM Availability a WHERE a.owner = :owner AND a.ownerType = :ownerType AND a.userId = :userId"),
  @NamedQuery(name = "Availability.maxOwner", query = "SELECT MAX(a.owner) FROM Availability a WHERE a.ownerType = :ownerType AND a.userId = :userId"),
  @NamedQuery(name = "Availability.findByOwnerType", query = "SELECT a FROM Availability a WHERE a.ownerType = :ownerType AND a.userId = :userId")
})
@Table(name = "\"availability\"")
public class Availability extends UserIdJpaEntity implements Serializable {
  private Byte friday;
  @Id
  @Column(nullable = false)
  private Integer id;
  private Byte monday;
  private Integer owner;
  @Column(name="OWNER_TYPE")
  private String ownerType;
  private Byte saturday;
  private Byte sunday;
  private Byte thursday;
  private Byte tuesday;
  private Byte wednesday;
  @Column(name = "USER_ID")
  protected Integer userId;
  @JoinColumn(name = "LESSON", referencedColumnName = "id", nullable = false)
  @ManyToOne(optional = false)
  private Lesson lesson;

  public Availability() {
  }
  
  public Availability(String ownerType) {
    this.ownerType = ownerType;
  }
  
  public Availability(Byte friday, Integer id, Lesson lesson, Byte monday, Integer owner, String ownerType,
                      Byte saturday, Byte sunday, Byte thursday, Byte tuesday, Byte wednesday, Integer userId) {
    this.friday = friday;
    this.id = id;
    this.lesson = lesson;
    this.monday = monday;
    this.owner = owner;
    this.ownerType = ownerType;
    this.saturday = saturday;
    this.sunday = sunday;
    this.thursday = thursday;
    this.tuesday = tuesday;
    this.wednesday = wednesday;
    this.userId = userId;
  }

  public Byte getFriday() {
    return friday;
  }

  public void setFriday(Byte friday) {
    this.friday = friday;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public Byte getMonday() {
    return monday;
  }

  public void setMonday(Byte monday) {
    this.monday = monday;
  }

  public Integer getOwner() {
    return owner;
  }

  public void setOwner(Integer owner) {
    this.owner = owner;
  }

  public String getOwnerType() {
    return ownerType;
  }

  public void setOwnerType(String ownerType) {
    this.ownerType = ownerType;
  }

  public Byte getSaturday() {
    return saturday;
  }

  public void setSaturday(Byte saturday) {
    this.saturday = saturday;
  }

  public Byte getSunday() {
    return sunday;
  }

  public void setSunday(Byte sunday) {
    this.sunday = sunday;
  }

  public Byte getThursday() {
    return thursday;
  }

  public void setThursday(Byte thursday) {
    this.thursday = thursday;
  }

  public Byte getTuesday() {
    return tuesday;
  }

  public void setTuesday(Byte tuesday) {
    this.tuesday = tuesday;
  }

  public Byte getWednesday() {
    return wednesday;
  }

  public void setWednesday(Byte wednesday) {
    this.wednesday = wednesday;
  }

  public Lesson getLesson() {
    return lesson;
  }

  public void setLesson(Lesson lesson) {
    this.lesson = lesson;
  }

  @XmlTransient
  public Boolean getMondayBool() {
      return monday != null && monday==1;
  }
  @XmlTransient
  public void setMondayBool(Boolean m) {
      this.monday = m.booleanValue() ? Byte.valueOf((byte)1) : Byte.valueOf((byte)0);
  }

  @XmlTransient
  public Boolean getTuesdayBool() {
      return tuesday != null && tuesday==1;
  }
  @XmlTransient
  public void setTuesdayBool(Boolean m) {
      this.tuesday = m.booleanValue() ? Byte.valueOf((byte)1) : Byte.valueOf((byte)0);
  }

  @XmlTransient
  public Boolean getWednesdayBool() {
      return wednesday != null && wednesday==1;
  }
  @XmlTransient
  public void setWednesdayBool(Boolean m) {
      this.wednesday = m.booleanValue() ? Byte.valueOf((byte)1) : Byte.valueOf((byte)0);
  }

  @XmlTransient
  public Boolean getThursdayBool() {
      return thursday != null && thursday==1;
  }
  @XmlTransient
  public void setThursdayBool(Boolean m) {
      this.thursday = m.booleanValue() ? Byte.valueOf((byte)1) : Byte.valueOf((byte)0);
  }

  @XmlTransient
  public Boolean getFridayBool() {
      return friday != null && friday==1;
  }
  @XmlTransient
  public void setFridayBool(Boolean m) {
      this.friday = m.booleanValue() ? Byte.valueOf((byte)1) : Byte.valueOf((byte)0);
  }

  @XmlTransient
  public Boolean getSaturdayBool() {
      return saturday != null && saturday==1;
  }
  @XmlTransient
  public void setSaturdayBool(Boolean m) {
      this.saturday = m.booleanValue() ? Byte.valueOf((byte)1) : Byte.valueOf((byte)0);
  }

  @XmlTransient
  public Boolean getSundayBool() {
      return sunday != null && sunday==1;
  }
  @XmlTransient
  public void setSundayBool(Boolean m) {
      this.sunday = m.booleanValue() ? Byte.valueOf((byte)1) : Byte.valueOf((byte)0);
  }

  @XmlTransient
  public boolean isAvailableOnDay(int day) {
      boolean available;
      switch (day) {
          case 0 : available = monday == 1;
              break;
          case 1 : available = tuesday == 1;
              break;
          case 2 : available = wednesday == 1;
              break;
          case 3 : available = thursday == 1;
              break;
          case 4 : available = friday == 1;
              break;
          case 5 : available = saturday == 1;
              break;
          case 6 : available = sunday == 1;
              break;
          default: available = false;
      }
      return available;
  }

  public int getLessonCount() {
    if (monday == null)
      monday = 1;
    if (tuesday == null)
      tuesday = 1;
    if (wednesday == null)
      wednesday = 1;
    if (thursday == null)
      thursday = 1;
    if (friday == null)
      friday = 1;
    if (saturday == null)
      saturday = 0;
    if (sunday == null)
      sunday = 0;
    return (int)(monday+tuesday+wednesday+thursday+friday+saturday+sunday);
  }

  public Object getEID() {
    return id;
  }
  
  @Override
  public String toString() {
    return "Mon:"+monday+" Tue:"+tuesday+" Wed:"+wednesday+" Thu:"+thursday+" Fri:"+friday+" Sat:"+saturday+" Sun:"+sunday;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
