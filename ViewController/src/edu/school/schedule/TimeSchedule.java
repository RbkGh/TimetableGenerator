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
import edu.school.jpa.User;
import edu.school.view.ClassroomController;
import edu.school.view.LessonController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Penguin
 */
public class TimeSchedule {

  protected enum Day {
    MONDAY("Monday",0),
    TUESDAY("Tuesday",1),
    WEDNESDAY("Wednesday",2),
    THURSDAY("Thursday",3),
    FRIDAY("Friday",4),
    SATURDAY("Staurday",5),
    SUNDAY("Sunday",6);
    protected String name;
    protected int    index;
    Day(String aName, int aIndex) { name = aName; index = aIndex; }
    public boolean is (String aName) { return aName != null ? aName.toLowerCase().matches(this.name.toLowerCase()) : false; }
  };
  protected List<Lesson>    lessons;
  protected List<Classroom> rooms;
  private SchedulingUnit[][][] cube= null;
  private   Scheduler       scheduler;
    /**
     * initialize rooms & lessons
     */
    public TimeSchedule(Scheduler scheduler) {
        this.scheduler = scheduler;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        LessonController lc = (LessonController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "lessonController");
        lessons = lc.getFacade().getLessonFindAll();
        ClassroomController cc = (ClassroomController) facesContext.getApplication().getELResolver().
                getValue(facesContext.getELContext(), null, "classroomController");
        rooms = cc.getFacade().getClassroomFindAll();
        //  x - day,  y - lesson (timeslot)  z - class room
        cube  = new SchedulingUnit[Day.values().length][lessons.size()][rooms.size()];
    }
    
    /**
     * @return the cube
     */
    public SchedulingUnit[][][] getCube() {
        return cube;
    }

    public void intializeCube() {
        // cache the work class for class group, as the classgroup 
        HashMap<ClassGroup, SchedulingClassGroup> schedulingClassGroups = new HashMap<ClassGroup, SchedulingClassGroup>();
        for (ClassGroup group : scheduler.classGroups) {
            schedulingClassGroups.put(group, new SchedulingClassGroup(group));
        }
        for (int day = 0; day < Day.values().length; day++) {
            for (int lesson = 0; lesson < lessons.size(); lesson++) {
                // sLesson is list of available lessons for the day
                SchedulingLesson sLesson = new SchedulingLesson(lesson, lessons.get(lesson), scheduler.classGroups);
           
                // z-dimension is classRoom, however I iterate over the class groups, in case the 
                // class group needs to occupy 2 rooms for this lesson
                for (ClassGroup group : scheduler.classGroups) {
                    // get cached value
                    SchedulingClassGroup sGroup = schedulingClassGroups.get(group);
                    if (!sGroup.isAvailableOnDay(day,lesson) || sGroup.hasMaxSubjectsForDay(day))
                        continue; // reached max lessons per day limit
                    // place the class to room(s) based on subject, get teacher(s)
                    int unwindLevel = 0;
                    SchedulingUnit unit = null;
                    while (unwindLevel < 8 && unit == null) {
                        unit = fillSchedulingUnit(day, unwindLevel++, scheduler, sLesson, sGroup);
                    }
                    if (unit == null) {
                        scheduler.addError("Cannot schedule group ["+group.getName()+"] for lesson "+sLesson.getLesson().getStart()+", will postpone the class for the next lesson.");
System.out.println("Cannot schedule group ["+group.getName()+"] for lesson "+sLesson.getLesson().getStart());
                    }
                }
            }
        }
        printCube();
    }
    
    public SchedulingUnit fillSchedulingUnit(int day, int level,
                                            Scheduler scheduler,
                                            SchedulingLesson sLesson, 
                                            SchedulingClassGroup sGroup) {
        SchedulingUnit unit = null;
        Subject subject = sGroup.allocateSubject(day, sLesson.getIndex(), level);
        if (subject == null) {
          String err = "No available subject for classgrp ["+sGroup.getGroup().getName()+"] for lesson: "+sLesson.getLesson().getStart()+" on "+Day.values()[day].toString();
          scheduler.addError(err);
System.out.println(err);
            return null; // cannot allocate subject is fatal problem
        }
        // get room based on preferences
        List<SchedulingRoom> groupRooms = scheduler.allocateRoomForClassGroup(sGroup.getGroup(), subject, day, sLesson);
        if (groupRooms == null || groupRooms.isEmpty()) {
            sGroup.undoAllocateSubject(subject, day, level);
          String err = "No available Room for classgrp ["+sGroup.getGroup().getName()+"] for lesson: "+sLesson.getLesson().getStart()+" for subject: "+subject.getName()+" on "+Day.values()[day].toString()+". Trying to schedule the classgroup for next lesson.";
          scheduler.addError(err);
System.out.println(err);
            return null;
        }
        // get teacher(s) - one per room
        ArrayList<Teacher> groupTeachers = new ArrayList<Teacher>(groupRooms.size());
        for (int i = 0; i < groupRooms.size(); i++) {
            Teacher teacher = scheduler.allocateTeacherForSubject(subject, day, sLesson);
            if (teacher != null)
                groupTeachers.add(teacher);
        }
        // test if got enough teachers per room for the subject
        if (groupTeachers.isEmpty() || groupTeachers.size() < groupRooms.size()) {
            sGroup.undoAllocateSubject(subject, day, level);
            scheduler.undoAllocateRoomForClassGroup(groupRooms, day, sLesson);
            for (Teacher teacher : groupTeachers) 
                scheduler.undoAllocateTeacherForSubject(teacher, subject, day, sLesson);
            String err = "There is no teacher for classgrp ["+sGroup.getGroup().getName()+"] for lesson: "+sLesson.getLesson().getStart()+" for subject: "+subject.getName()+" on "+Day.values()[day].toString()+". Trying another subject now.";
            scheduler.addError(err);
System.out.println(err);
            return null;
        }
        for (int u = 0; u < groupRooms.size(); u++) {
            unit = placeSchedulingUnit(subject,groupTeachers.get(u),sGroup.getGroup(),day,sLesson,groupRooms.get(u));
System.out.println(unit.toString(day, sLesson.getLesson(), groupRooms.get(u).getRoom()));
          scheduler.addError("Scheduled OK: "+ unit.toString(day, sLesson.getLesson(), groupRooms.get(u).getRoom()));
        }
        return unit;
    }
    
    public SchedulingUnit placeSchedulingUnit(Subject subject, Teacher teacher, ClassGroup classGroup, int day, SchedulingLesson lesson, SchedulingRoom room) {
        SchedulingUnit unit = cube[day][lesson.getIndex()][room.getIndex()];
        if (unit != null && unit.isLocked()) {
            return unit;  // don't fill locked unit
        }
        if (unit == null) {
            unit = new SchedulingUnit(subject, teacher, classGroup, day, lesson.getLesson(), room.getRoom());
        } else {
            unit.setSubject(subject);
            unit.setTeacher(teacher);
            unit.setClassGroup(classGroup);
            unit.setDay(day);
            unit.setLesson(lesson.getLesson());
            unit.setRoom(room.getRoom());
        }
        cube[day][lesson.getIndex()][room.getIndex()] = unit;
        return unit;
    }
    
    public final void printCube() {
        for (int x = 0; x < cube.length; x++) 
            for (int y = 0; y < cube[0].length; y++)
                for (int z = 0; z < cube[0][0].length; z++)
                    if (cube[x][y][z] != null)
                        System.out.println(cube[x][y][z].toString(x,lessons.get(y),rooms.get(z)));
//                    else 
//                        System.out.println("x="+x+" y="+y+" z="+z+"  is empty.");
    }
    
    public void persist(int version) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      LessonController lc = (LessonController) facesContext.getApplication().getELResolver().
              getValue(facesContext.getELContext(), null, "lessonController");
      for (int x = 0; x < cube.length; x++) 
          for (int y = 0; y < cube[0].length; y++)
              for (int z = 0; z < cube[0][0].length; z++)
                if (cube[x][y][z] != null) {
                  TimeScheduleUnit unit = cube[x][y][z].getEntity(); 
                  boolean isNew = true;
                  if (unit.getEid() != null) {
                    if (unit.getVersion() != null && version == unit.getVersion())
                      isNew = false;
                    else
                      unit.setEid(null); // when version does not match, create new entity, thus I reset the iD
                  }
                  if (isNew) {
                    unit.setVersion(version);
                    lc.getEjbFacade()._persistEntity(unit);
                  } else {
                    lc.getEjbFacade()._mergeEntity(unit);
                  }
                }
      
    }
    
    public void load(int version) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      LessonController lc = (LessonController) facesContext.getApplication().getELResolver().
              getValue(facesContext.getELContext(), null, "lessonController");
      List<TimeScheduleUnit> list = lc.getEjbFacade().getTimeScheduleUnits(version);
      for (TimeScheduleUnit unit : list) {
        int x = unit.getDay();
        int y = getLessonIndex(unit.getLesson());
        int z = getRoomIndex(unit.getClassroom());
        cube[x][y][z] = new SchedulingUnit(unit); 
      }
      scheduler.addError("Successfully loaded schedule version ["+version+"] from database.");
    }
    
    private int getLessonIndex(Lesson lesson) {
      int i = -1;
      for (int j = 0; j < lessons.size(); j++) {
        if (lessons.get(j).getId().equals(lesson.getId())) {
          i = j;
          break;
        }
      }
      return i;
    }
    
  private int getRoomIndex(Classroom room) {
    int i = -1;
    for (int j = 0; j < rooms.size(); j++) {
      if (rooms.get(j).getId().equals(room.getId())) {
        i = j;
        break;
      }
    }
    return i;
  }
      
    public String toXML() {
      StringBuilder xml = new StringBuilder("<?xml version = '1.0' encoding = 'windows-1252'?>\n");
      Map<String,Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
      User currentUser = (User)session.get("currentUser");

      xml.append("<timeSchedule user=\"").append(currentUser != null ? currentUser.getFirstName()+" "+currentUser.getSurname() : "unknown").append("\">\n");
      for (int x = 0; x < cube.length; x++) {
        xml.append("  <day name=\"").append(Day.values()[x].name).append("\">\n");
        for (int y = 0; y < cube[0].length; y++) {
          xml.append("    <lesson start=\"").append(lessons.get(y).getStart()).append("\" end=\"").append(lessons.get(y).getEnd()).append("\">\n");
          for (int z = 0; z < cube[0][0].length; z++) {
            xml.append("      <room name=\"").append(rooms.get(z).getName()).append("\">\n");
            if (cube[x][y][z] != null) {
              xml.append("        ").append(cube[x][y][z].toXML());
            } else {
              xml.append("        <unit/>\n");
            }
            xml.append("      </room>\n");
          }
          xml.append("    </lesson>\n");
        }
        xml.append("  </day>\n");
      }
      xml.append("</timeSchedule>\n");
      return xml.toString();
    }
}
