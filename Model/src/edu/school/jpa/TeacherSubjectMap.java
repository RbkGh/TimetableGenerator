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
  @NamedQuery(name = "TeacherSubjectMap.findAll", query = "select o from TeacherSubjectMap o")
})
@Table(name = "\"teacher_subject_map\"")
@IdClass(TeacherSubjectMapPK.class)
public class TeacherSubjectMap extends AbstractJpaEntity implements Serializable {
  @Id
  @Column(name="subject_id", nullable = false, insertable = false, updatable = false)
  private Integer subject_id;
  @Id
  @Column(name="teacher_id", nullable = false, insertable = false, updatable = false)
  private Integer teacher_id;
  @ManyToOne
  @JoinColumn(name = "teacher_id")
  private Teacher teacher;
  @ManyToOne
  @JoinColumn(name = "subject_id")
  private Subject subject;

  public TeacherSubjectMap() {
  }

  public TeacherSubjectMap(Subject subject, Teacher teacher) {
    this.subject = subject;
    this.teacher = teacher;
  }

  public Integer getSubject_id() {
    return subject_id;
  }

  public void setSubject_id(Integer subject_id) {
    this.subject_id = subject_id;
  }

  public Integer getTeacher_id() {
    return teacher_id;
  }

  public void setTeacher_id(Integer teacher_id) {
    this.teacher_id = teacher_id;
  }

  public Teacher getTeacher() {
    return teacher;
  }

  public void setTeacher(Teacher teacher) {
    this.teacher = teacher;
    if (teacher != null) {
      this.teacher_id = teacher.getId();
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
    return subject_id;
  }
}
