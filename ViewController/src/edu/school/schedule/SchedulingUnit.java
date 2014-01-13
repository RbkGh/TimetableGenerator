/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.school.schedule;

import edu.school.jpa.ClassGroup;
import edu.school.jpa.Classroom;
import edu.school.jpa.Lesson;
import edu.school.jpa.Subject;
import edu.school.jpa.Teacher;
import edu.school.jpa.TimeScheduleUnit;
import static edu.school.schedule.TimeSchedule.Day.MONDAY;

/**
 *
 * @author Penguin
 */
public class SchedulingUnit {
    private Subject     subject;
    private Teacher     teacher;
    private ClassGroup  classGroup;
    private Lesson      lesson;
    private int         day;
    private Classroom   room;
    private boolean     locked;
    private TimeScheduleUnit timeScheduleUnit;
    
    public SchedulingUnit() {
        super();
        locked = false;
        timeScheduleUnit = new TimeScheduleUnit();
    }
    
    public SchedulingUnit(Subject subject, Teacher teacher, ClassGroup classGroup, int day, Lesson lesson, Classroom room) {
      this();
      this.subject    = subject;
      timeScheduleUnit.setSubject(subject);
      this.teacher    = teacher;
      timeScheduleUnit.setTeacher(teacher);
      this.classGroup = classGroup;
      timeScheduleUnit.setClassGroup(classGroup);
      this.lesson     = lesson;
      timeScheduleUnit.setLesson(lesson);
      if (day >= 0 && day < TimeSchedule.Day.values().length)
          this.day = day;
      else
          day = 0;
      timeScheduleUnit.setDay(this.day);
      this.room       = room;
      timeScheduleUnit.setClassroom(room);
    }
    
    public SchedulingUnit(TimeScheduleUnit timeScheduleUnit) {
      super();
      this.locked = false;
      this.timeScheduleUnit = timeScheduleUnit;
      this.subject = timeScheduleUnit.getSubject();
      this.teacher = timeScheduleUnit.getTeacher();
      this.classGroup = timeScheduleUnit.getClassGroup();
      this.lesson  = timeScheduleUnit.getLesson();
      this.day     = timeScheduleUnit.getDay();
      this.room    = timeScheduleUnit.getClassroom();
    }

    /**
     * @return the subject
     */
    public Subject getSubject() {
        return subject;
    }

    /**
     * @param subject the subject to set
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
      timeScheduleUnit.setSubject(subject);
    }

    /**
     * @return the teacher
     */
    public Teacher getTeacher() {
        return teacher;
    }

    /**
     * @param teacher the teacher to set
     */
    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
      timeScheduleUnit.setTeacher(teacher);
    }

    /**
     * @return the classGroup
     */
    public ClassGroup getClassGroup() {
        return classGroup;
    }

    /**
     * @param classGroup the classGroup to set
     */
    public void setClassGroup(ClassGroup classGroup) {
        this.classGroup = classGroup;
      timeScheduleUnit.setClassGroup(classGroup);
    }
    
    public String toString(int day, Lesson lesson, Classroom room) {
        TimeSchedule.Day eDay = TimeSchedule.Day.values()[day];
        String sDay = eDay.name;
        /*
        switch (eDay) {
            case MONDAY: sDay = "Monday";
                         break;
            case TUESDAY: sDay = "Tuesday";
                         break;
            case WEDNESDAY: sDay = "Wednesday";
                         break;
            case THURSDAY: sDay = "Thursday";
                         break;
            case FRIDAY: sDay = "Friday";
                         break;
            case SATURDAY: sDay = "Saturday";
                         break;
            case SUNDAY: sDay = "Sunday";
                         break;
        }
        */
        String sName = subject != null ? subject.getName() : "NULL";
        String tName = teacher != null ? teacher.getFirstName()+" "+teacher.getLastName() : "NULL";
        String gName = classGroup != null ? classGroup.getName() : "NULL";
        return sDay+" "+lesson.toString()+" in "+room.getName()+"("+room.getCapacity()+") : group["+gName+"] subj["+sName+"] teacher["+tName+"]";
    }

    public String toString() {
        return toString(day, lesson, room);
    }
    
    public String toXML() {
      String sName = subject != null ? subject.getName() : "NULL";
      String tName = teacher != null ? teacher.getFirstName()+" "+teacher.getLastName() : "NULL";
      String gName = classGroup != null ? classGroup.getName() : "NULL";
      return "<unit subject=\""+sName+"\" teacher=\""+tName+"\" classGroup=\""+gName+"\"/>\n";
    }
    
    public String getHtmlForRoom() {
        String sName = subject != null ? subject.getName() : "NULL";
        String tName = teacher != null ? teacher.getFirstName()+" "+teacher.getLastName() : "NULL";
        String gName = classGroup != null ? classGroup.getName() : "NULL";
        return "<div class=\"SUnit\">"+sName+"<br><span class=\"SUnitTchr\">["+tName+"]</span><br><span class=\"SUnitGrp\">["+gName+"]</span></div>";
    }
    
    public String getHtmlForGroup() {
        String sName = subject != null ? subject.getName() : "NULL";
        String tName = teacher != null ? teacher.getFirstName()+" "+teacher.getLastName() : "NULL";
        String rName = room != null ? room.getName() : "NULL";
        return "<div class=\"SUnit\">"+sName+"<br/><span class=\"SUnitTchr\">["+tName+"]</span><br/><span class=\"SRoom\">["+rName+"]</span></div>";
    }
    
    /**
     * @return the lesson
     */
    public Lesson getLesson() {
        return lesson;
    }

    /**
     * @param lesson the lesson to set
     */
    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
      timeScheduleUnit.setLesson(lesson);
    }

    /**
     * @return the day
     */
    public String getDay() {
        return TimeSchedule.Day.values()[day].toString();
    }

    /**
     * @param day the day to set
     */
    public void setDay(int day) {
      if (day >= 0 && day < TimeSchedule.Day.values().length) {
        this.day = day;
        timeScheduleUnit.setDay(day);
      }
    }

    /**
     * @return the room
     */
    public Classroom getRoom() {
        return room;
    }

    /**
     * @param room the room to set
     */
    public void setRoom(Classroom room) {
        this.room = room;
      timeScheduleUnit.setClassroom(room);
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    public void lock() {
        locked = true;
    }
    
    public void unlock() {
        locked = false;
    }
    
    public TimeScheduleUnit getEntity() {
      return timeScheduleUnit;
    }
}
