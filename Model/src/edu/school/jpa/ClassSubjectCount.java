package edu.school.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "ClassSubjectCount.findAll", query = "select o from ClassSubjectCount o")
})
@Table(name = "\"class_subject_count\"")
@IdClass(ClassSubjectCountPK.class)
public class ClassSubjectCount extends AbstractJpaEntity implements Serializable {
  @Id
  @Column(name="class_group_id", nullable = false)
  private Integer class_group_id;
  @Column(name="subject_count", nullable = false)
  private Integer subject_count;
  @Id
  @Column(name="subject_id", nullable = false)
  private Integer subject_id;

  public ClassSubjectCount() {
  }

  public ClassSubjectCount(Integer class_group_id, Integer subject_count, Integer subject_id) {
    this.class_group_id = class_group_id;
    this.subject_count = subject_count;
    this.subject_id = subject_id;
  }

  public Integer getClass_group_id() {
    return class_group_id;
  }

  public void setClass_group_id(Integer class_group_id) {
    this.class_group_id = class_group_id;
  }

  public Integer getSubject_count() {
    return subject_count;
  }

  public void setSubject_count(Integer subject_count) {
    this.subject_count = subject_count;
  }

  public Integer getSubject_id() {
    return subject_id;
  }

  public void setSubject_id(Integer subject_id) {
    this.subject_id = subject_id;
  }

  public Object getEID() {
    return class_group_id;
  }
}
