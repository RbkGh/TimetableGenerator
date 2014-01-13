package edu.school.jpa;

import java.io.Serializable;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import javax.xml.bind.annotation.XmlTransient;

@Entity
@NamedQueries({
  @NamedQuery(name = "Teacher.findAll", query = "select o from Teacher o WHERE o.userId = :userId")
})
@Table(name = "\"teacher\"")
public class Teacher extends UserIdJpaEntity implements Serializable {
  private String address;
  @Column(name="AVAILABILITY_ID")
  private Integer availabilityId;
  @Column(name="CAN_SUBSTITUTE")
  private Byte canSubstitute;
  @Column(name="FIRST_NAME", nullable = false)
  private String firstName;
  @Id
  @Column(nullable = false)
  private Integer id;
  @Column(nullable = false)
  private String initials;
  @Column(name="LAST_NAME", nullable = false)
  private String lastName;
  private String phone;
  @Column(name = "USER_ID")
  protected Integer userId;
  @OneToMany(mappedBy = "teacher")
  private List<TeacherSubjectMap> teacherSubjectMapList;
  @JoinTable(name = "teacher_subject_map", joinColumns = {
      @JoinColumn(name = "teacher_id", referencedColumnName = "ID", nullable = false)}, inverseJoinColumns = {
      @JoinColumn(name = "subject_id", referencedColumnName = "ID", nullable = false)})
  @ManyToMany(fetch=FetchType.EAGER)
  private Collection<Subject> subjectCollection;
  @Transient
  private Boolean canSubstituteBool;


  public Teacher() {
  }

  public Teacher(String address, Integer availabilityId, Byte canSubstitute, String firstName, Integer id,
                 String initials, String lastName, String phone, Integer userId) {
    this.address = address;
    this.availabilityId = availabilityId;
    this.canSubstitute = canSubstitute;
    this.firstName = firstName;
    this.id = id;
    this.initials = initials;
    this.lastName = lastName;
    this.phone = phone;
    this.userId = userId;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getAvailabilityId() {
    return availabilityId;
  }

  public void setAvailabilityId(Integer availabilityId) {
    this.availabilityId = availabilityId;
  }

  public Byte getCanSubstitute() {
    return canSubstitute;
  }

  public void setCanSubstitute(Byte canSubstitute) {
    this.canSubstitute = canSubstitute;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getInitials() {
    return initials;
  }

  public void setInitials(String initials) {
    this.initials = initials;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }


  public List<TeacherSubjectMap> getTeacherSubjectMapList() {
    return teacherSubjectMapList;
  }

  public void setTeacherSubjectMapList(List<TeacherSubjectMap> teacherSubjectMapList) {
    this.teacherSubjectMapList = teacherSubjectMapList;
  }

  public TeacherSubjectMap addTeacherSubjectMap(TeacherSubjectMap teacherSubjectMap) {
    getTeacherSubjectMapList().add(teacherSubjectMap);
    teacherSubjectMap.setTeacher(this);
    return teacherSubjectMap;
  }

  public TeacherSubjectMap removeTeacherSubjectMap(TeacherSubjectMap teacherSubjectMap) {
    getTeacherSubjectMapList().remove(teacherSubjectMap);
    teacherSubjectMap.setTeacher(null);
    return teacherSubjectMap;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  /**
   * @return the canSubstituteBool
   */
  public Boolean getCanSubstituteBool() {
      this.canSubstituteBool = canSubstitute!=null && canSubstitute==1;
      return canSubstituteBool;
  }

  /**
   * @param canSubstituteBool the canSubstituteBool to set
   */
  public void setCanSubstituteBool(Boolean canSubstituteBool) {
      this.canSubstituteBool = canSubstituteBool;
      this.canSubstitute = canSubstituteBool?(byte)1:(byte)0;
  }
  
  @XmlTransient
  public Collection<Subject> getSubjectCollection() {
      return subjectCollection;
  }

  public void setSubjectCollection(Collection<Subject> subjectCollection) {
      this.subjectCollection = subjectCollection;
  }

  public Object getEID() {
    return id;
  }
 
  @Override
  public String toString() {
    return firstName+" "+lastName+" ("+initials+")";
  }

  @XmlTransient
  public String toXML() {
    StringBuffer xml = new StringBuffer("<?xml version = '1.0' encoding = 'windows-1252'?>\n");
    xml.append("  <teacher>\n");
    xml.append("  </teacher>\n");
    return xml.toString();
  }
}
