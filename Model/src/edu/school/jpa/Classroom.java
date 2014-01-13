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
  @NamedQuery(name = "Classroom.findAll", query = "select o from Classroom o WHERE o.userId = :userId")
})
@Table(name = "\"classroom\"")
public class Classroom extends UserIdJpaEntity implements Serializable {
  private Integer capacity;
  private String comment;
  @Id
  @Column(nullable = false)
  private Integer id;
  @Column(nullable = false)
  private String name;
  @Column(name="PREF_SUBJECT")
  private Integer prefSubject;
  @Column(name = "USER_ID")
  protected Integer userId;
  @OneToMany(mappedBy = "classroom")
  private List<ClassGroup> classGroupList;
  @OneToMany(mappedBy = "classroom")
  private List<Subject> subjectList;

  public Classroom() {
  }

  public Classroom(Integer capacity, String comment, Integer id, String name, Integer prefSubject, Integer userId) {
    this.capacity = capacity;
    this.comment = comment;
    this.id = id;
    this.name = name;
    this.prefSubject = prefSubject;
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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPrefSubject() {
    return prefSubject;
  }

  public void setPrefSubject(Integer prefSubject) {
    this.prefSubject = prefSubject;
  }

  public List<ClassGroup> getClassGroupList() {
    return classGroupList;
  }

  public void setClassGroupList(List<ClassGroup> classGroupList) {
    this.classGroupList = classGroupList;
  }

  public ClassGroup addClassGroup(ClassGroup classGroup) {
    getClassGroupList().add(classGroup);
    classGroup.setClassroom(this);
    return classGroup;
  }

  public ClassGroup removeClassGroup(ClassGroup classGroup) {
    getClassGroupList().remove(classGroup);
    classGroup.setClassroom(null);
    return classGroup;
  }

  public List<Subject> getSubjectList() {
    return subjectList;
  }

  public void setSubjectList(List<Subject> subjectList) {
    this.subjectList = subjectList;
  }

  public Subject addSubject(Subject subject) {
    getSubjectList().add(subject);
    subject.setClassroom(this);
    return subject;
  }

  public Subject removeSubject(Subject subject) {
    getSubjectList().remove(subject);
    subject.setClassroom(null);
    return subject;
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
    xml.append("  <classroom>\n");
    xml.append("  </classroom>\n");
    return xml.toString();
  }

}
