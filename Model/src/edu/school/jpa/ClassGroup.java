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

import javax.xml.bind.annotation.XmlTransient;

@Entity
@NamedQueries({
  @NamedQuery(name = "ClassGroup.findAll", query = "select o from ClassGroup o WHERE o.userId = :userId")
})
@Table(name = "\"class_group\"")
public class ClassGroup extends UserIdJpaEntity implements Serializable {
  private Integer capacity;
  private String comment;
  @Column(nullable = false)
  private String name;
  @Id
  @Column(name="id", nullable = false)
  private Integer id;
  @ManyToOne
  @JoinColumn(name = "PREF_CLASSROOM", referencedColumnName = "ID")
  private Classroom classroom;
  @Column(name = "USER_ID")
  protected Integer userId;
//  @ManyToOne
//  @JoinColumn(name = "AVAILABILITY_ID")
  @Column(name = "AVAILABILITY_ID")
  private Integer availabilityId;
  @OneToMany(mappedBy = "classGroup")
  private List<ClassGroupSubjectMap> classGroupSubjectMapList;
  @JoinTable(name = "class_group_subject_map", joinColumns = {
      @JoinColumn(name = "class_group_id", referencedColumnName = "id", nullable = false)}, inverseJoinColumns = {
      @JoinColumn(name = "subject_id", referencedColumnName = "ID", nullable = false)})
  @ManyToMany(fetch=FetchType.EAGER)
  private Collection<Subject> subjectCollection;

  public ClassGroup() {
  }

  public ClassGroup(Integer availabilityId, Integer capacity, String comment, Integer id, String name,
                    Classroom classroom, Integer userId) {
    this.availabilityId = availabilityId;
    this.capacity = capacity;
    this.comment = comment;
    this.id = id;
    this.name = name;
    this.classroom = classroom;
    this.userId = userId;
  }


  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Classroom getClassroom() {
    return classroom;
  }

  public void setClassroom(Classroom classroom) {
    this.classroom = classroom;
  }

  public Integer getAvailabilityId() {
    return availabilityId;
  }

  public void setAvailabilityId(Integer availability) {
    this.availabilityId = availability;
  }

  public List<ClassGroupSubjectMap> getClassGroupSubjectMapList() {
    return classGroupSubjectMapList;
  }

  public void setClassGroupSubjectMapList(List<ClassGroupSubjectMap> classGroupSubjectMapList) {
    this.classGroupSubjectMapList = classGroupSubjectMapList;
  }

  public ClassGroupSubjectMap addClassGroupSubjectMap(ClassGroupSubjectMap classGroupSubjectMap) {
    getClassGroupSubjectMapList().add(classGroupSubjectMap);
    classGroupSubjectMap.setClassGroup(this);
    return classGroupSubjectMap;
  }

  public ClassGroupSubjectMap removeClassGroupSubjectMap(ClassGroupSubjectMap classGroupSubjectMap) {
    getClassGroupSubjectMapList().remove(classGroupSubjectMap);
    classGroupSubjectMap.setClassGroup(null);
    return classGroupSubjectMap;
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

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
  
  @Override
  public String toString() {
    return name;
  }
  
  @XmlTransient
  public String toXML() {
    StringBuffer xml = new StringBuffer("<?xml version = '1.0' encoding = 'windows-1252'?>\n");
    xml.append("  <classGroup>\n");
    xml.append("  </classGroup>\n");
    return xml.toString();
  }

}
