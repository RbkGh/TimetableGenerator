package edu.school.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "ClassGroupSubjectMap.findAll", query = "select o from ClassGroupSubjectMap o")
})
@Table(name = "\"class_group_subject_map\"")
@IdClass(ClassGroupSubjectMapPK.class)
public class ClassGroupSubjectMap extends AbstractJpaEntity implements Serializable {
  @Id
  @Column(name="class_group_id", nullable = false, insertable = false, updatable = false)
  private Integer class_group_id;
  @Id
  @Column(name="subject_id", nullable = false, insertable = false, updatable = false)
  private Integer subject_id;
  @ManyToOne
  @JoinColumn(name = "class_group_id")
  private ClassGroup classGroup;
  @ManyToOne
  @JoinColumn(name = "subject_id")
  private Subject subject;

  public ClassGroupSubjectMap() {
  }

  public ClassGroupSubjectMap(ClassGroup classGroup, Subject subject) {
    this.classGroup = classGroup;
    this.subject = subject;
  }

  public Integer getClass_group_id() {
    return class_group_id;
  }

  public void setClass_group_id(Integer class_group_id) {
    this.class_group_id = class_group_id;
  }

  public Integer getSubject_id() {
    return subject_id;
  }

  public void setSubject_id(Integer subject_id) {
    this.subject_id = subject_id;
  }

  public ClassGroup getClassGroup() {
    return classGroup;
  }

  public void setClassGroup(ClassGroup classGroup) {
    this.classGroup = classGroup;
    if (classGroup != null) {
      this.class_group_id = classGroup.getId();
    }
  }

  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    this.subject = subject;
    if (subject != null) {
      this.subject_id = subject.getId();
    }
  }

  public Object getEID() {
    return class_group_id;
  }
}
