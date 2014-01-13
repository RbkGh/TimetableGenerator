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
  @NamedQuery(name = "Subject.findAll", query = "select o from Subject o WHERE o.userId = :userId")
})
@Table(name = "\"subject\"")
public class Subject extends UserIdJpaEntity implements Serializable {
  @Id
  @Column(nullable = false)
  private Integer id;
  @Column(nullable = false)
  private String name;
  @Column(name="SHORT_NAME", nullable = false)
  private String shortName;
  @Column(name = "USER_ID")
  protected Integer userId;
  private Integer weight;
  @OneToMany(mappedBy = "subject")
  private List<ClassGroupSubjectMap> classGroupSubjectMapList;
  @ManyToOne
  @JoinColumn(name = "PREF_CLASSROOM")
  private Classroom classroom;
  @OneToMany(mappedBy = "subject")
  private List<TeacherSubjectMap> teacherSubjectMapList;
  @Transient
  private int count = -1;
  

  public Subject() {
  }

  public Subject(Integer id, String name, Classroom classroom, String shortName, Integer userId, Integer weight) {
    this.id = id;
    this.name = name;
    this.classroom = classroom;
    this.shortName = shortName;
    this.userId = userId;
    this.weight = weight;
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


  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }


  public Integer getWeight() {
    if (weight == null)
      weight = 1;
    return weight;
  }

  public void setWeight(Integer weight) {
    this.weight = weight;
  }

  public List<ClassGroupSubjectMap> getClassGroupSubjectMapList() {
    return classGroupSubjectMapList;
  }

  public void setClassGroupSubjectMapList(List<ClassGroupSubjectMap> classGroupSubjectMapList) {
    this.classGroupSubjectMapList = classGroupSubjectMapList;
  }

  public ClassGroupSubjectMap addClassGroupSubjectMap(ClassGroupSubjectMap classGroupSubjectMap) {
    getClassGroupSubjectMapList().add(classGroupSubjectMap);
    classGroupSubjectMap.setSubject(this);
    return classGroupSubjectMap;
  }

  public ClassGroupSubjectMap removeClassGroupSubjectMap(ClassGroupSubjectMap classGroupSubjectMap) {
    getClassGroupSubjectMapList().remove(classGroupSubjectMap);
    classGroupSubjectMap.setSubject(null);
    return classGroupSubjectMap;
  }

  public Classroom getClassroom() {
    return classroom;
  }

  public void setClassroom(Classroom classroom) {
    this.classroom = classroom;
  }

  public List<TeacherSubjectMap> getTeacherSubjectMapList() {
    return teacherSubjectMapList;
  }

  public void setTeacherSubjectMapList(List<TeacherSubjectMap> teacherSubjectMapList) {
    this.teacherSubjectMapList = teacherSubjectMapList;
  }

  public TeacherSubjectMap addTeacherSubjectMap(TeacherSubjectMap teacherSubjectMap) {
    getTeacherSubjectMapList().add(teacherSubjectMap);
    teacherSubjectMap.setSubject(this);
    return teacherSubjectMap;
  }

  public TeacherSubjectMap removeTeacherSubjectMap(TeacherSubjectMap teacherSubjectMap) {
    getTeacherSubjectMapList().remove(teacherSubjectMap);
    teacherSubjectMap.setSubject(null);
    return teacherSubjectMap;
  }

  @XmlTransient
  public int getCount() {
      return count;
  }

  public void setCount(int c) {
      count = c;
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
    return name+" ("+shortName+")";
  }

  @XmlTransient
  public String toXML() {
    StringBuffer xml = new StringBuffer("<?xml version = '1.0' encoding = 'windows-1252'?>\n");
    xml.append("  <subject>\n");
    xml.append("  </subject>\n");
    return xml.toString();
  }
}
