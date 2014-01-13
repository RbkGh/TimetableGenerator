package edu.school.jpa;

import java.io.Serializable;

public class ClassGroupSubjectMapPK implements Serializable {
  private Integer class_group_id;
  private Integer subject_id;

  public ClassGroupSubjectMapPK() {
  }

  public ClassGroupSubjectMapPK(Integer class_group_id, Integer subject_id) {
    this.class_group_id = class_group_id;
    this.subject_id = subject_id;
  }

  public boolean equals(Object other) {
    if (other instanceof ClassGroupSubjectMapPK) {
      final ClassGroupSubjectMapPK otherClassGroupSubjectMapPK = (ClassGroupSubjectMapPK) other;
      final boolean areEqual = (otherClassGroupSubjectMapPK.class_group_id.equals(class_group_id) && otherClassGroupSubjectMapPK.subject_id.equals(subject_id));
      return areEqual;
    }
    return false;
  }

  public int hashCode() {
    return super.hashCode();
  }

  Integer getClass_group_id() {
    return class_group_id;
  }

  void setClass_group_id(Integer class_group_id) {
    this.class_group_id = class_group_id;
  }

  Integer getSubject_id() {
    return subject_id;
  }

  void setSubject_id(Integer subject_id) {
    this.subject_id = subject_id;
  }
}
