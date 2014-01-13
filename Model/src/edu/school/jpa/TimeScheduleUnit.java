package edu.school.jpa;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "TimeScheduleUnit.findAll", query = "select o from TimeScheduleUnit o where o.userId = :userId"),
  @NamedQuery(name = "TimeScheduleUnit.findByVersion", query = "select o from TimeScheduleUnit o where o.userId = :userId and o.version = :version")
})
@Table(name = "time_schedule_unit")
public class TimeScheduleUnit extends UserIdJpaEntity implements Serializable {
  @ManyToOne
  @JoinColumn(name = "class_group_id", referencedColumnName = "id")
  private ClassGroup classGroup;
  @ManyToOne
  @JoinColumn(name = "classroom_id", referencedColumnName = "ID")
  private Classroom classroom;
  @Column(name="day")
  private Integer day;
  @Id
  @Column(name="eid", nullable = false)
  private Integer eid;
  @ManyToOne
  @JoinColumn(name = "lesson_id", referencedColumnName = "id")
  private Lesson lesson;
  @Column(name="locked")
  private Integer locked;
  @ManyToOne
  @JoinColumn(name = "subject_id", referencedColumnName = "ID")
  private Subject subject;
  @ManyToOne
  @JoinColumn(name = "teacher_id", referencedColumnName = "ID")
  private Teacher teacher;
  @Column(name="version")
  private Integer version;
  @Column(name="user_id")
  private Integer userId;

  public TimeScheduleUnit() {
  }

  public TimeScheduleUnit(ClassGroup classGroup, Classroom classroom, Integer day, Integer eid, Lesson lesson,
                          Integer locked, Subject subject, Teacher teacher, Integer version) {
    this.setClassGroup(classGroup);
    this.setClassroom(classroom);
    this.day = day;
    this.eid = eid;
    this.setLesson(lesson);
    this.locked = locked;
    this.setSubject(subject);
    this.setTeacher(teacher);
    this.version = version;
  }

  public Integer getDay() {
    return day;
  }

  public void setDay(Integer day) {
    this.day = day;
  }

  public Integer getEid() {
    return eid;
  }

  public Integer getEID() {
    return eid;
  }

  public void setEid(Integer eid) {
    this.eid = eid;
  }

  public Integer getLocked() {
    return locked;
  }

  public void setLocked(Integer locked) {
    this.locked = locked;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public ClassGroup getClassGroup() {
    return classGroup;
  }

  public void setClassGroup(ClassGroup classGroup) {
    this.classGroup = classGroup;
  }

  public Classroom getClassroom() {
    return classroom;
  }

  public void setClassroom(Classroom classroom) {
    this.classroom = classroom;
  }

  public Lesson getLesson() {
    return lesson;
  }

  public void setLesson(Lesson lesson) {
    this.lesson = lesson;
  }

  public Subject getSubject() {
    return subject;
  }

  public void setSubject(Subject subject) {
    this.subject = subject;
  }

  public Teacher getTeacher() {
    return teacher;
  }

  public void setTeacher(Teacher teacher) {
    this.teacher = teacher;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
