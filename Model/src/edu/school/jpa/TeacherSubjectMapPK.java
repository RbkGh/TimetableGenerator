package edu.school.jpa;

import java.io.Serializable;

public class TeacherSubjectMapPK implements Serializable {
  private Integer subject_id;
  private Integer teacher_id;

  public TeacherSubjectMapPK() {
  }

  public TeacherSubjectMapPK(Integer subject_id, Integer teacher_id) {
    this.subject_id = subject_id;
    this.teacher_id = teacher_id;
  }

  public boolean equals(Object other) {
    if (other instanceof TeacherSubjectMapPK) {
      final TeacherSubjectMapPK otherTeacherSubjectMapPK = (TeacherSubjectMapPK) other;
      final boolean areEqual = (otherTeacherSubjectMapPK.subject_id.equals(subject_id) && otherTeacherSubjectMapPK.teacher_id.equals(teacher_id));
      return areEqual;
    }
    return false;
  }

  public int hashCode() {
    return super.hashCode();
  }

  Integer getSubject_id() {
    return subject_id;
  }

  void setSubject_id(Integer subject_id) {
    this.subject_id = subject_id;
  }

  Integer getTeacher_id() {
    return teacher_id;
  }

  void setTeacher_id(Integer teacher_id) {
    this.teacher_id = teacher_id;
  }
}
