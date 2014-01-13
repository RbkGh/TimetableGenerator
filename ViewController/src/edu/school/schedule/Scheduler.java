/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.school.schedule;

import edu.school.jpa.ClassGroup;
import edu.school.jpa.ClassSubjectCount;
import edu.school.jpa.Classroom;
import edu.school.jpa.Lesson;
import edu.school.jpa.Subject;
import edu.school.jpa.Teacher;
import edu.school.view.ClassGroupController;
import edu.school.view.ClassroomController;
import edu.school.view.LessonController;
import edu.school.view.SubjectController;
import edu.school.view.TeacherController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Penguin
 */
public class Scheduler {

    private TimeSchedule schedule;
    protected List<Teacher> teachers;
    protected List<ClassGroup> classGroups;
    protected List<Subject> subjects;
    protected List<Classroom> classRooms;
    protected List<Lesson> lessons;
    
    protected HashMap<Integer,List<Teacher>> teachersForSubject;
    protected HashMap<Integer, SchedulingTeacher> schedTeachers = new HashMap<Integer, SchedulingTeacher>();
    private ArrayList<String> errors = new ArrayList<String>();
    
    public Scheduler() {
        super();
        teachersForSubject = new HashMap<Integer,List<Teacher>>();
        TeacherController tc = TeacherController.getInstance();
        teachers = tc.getFacade().getTeacherFindAll();
        ClassGroupController cg = ClassGroupController.getInstance();
        classGroups = cg.getFacade().findRange(new int[]{0,1000000},ClassGroup.class);
        ClassroomController cr = ClassroomController.getInstance();
        classRooms = cr.getFacade().getClassroomFindAll();
        LessonController lc = LessonController.getInstance();
        lessons = lc.getFacade().getLessonFindAll();
        SubjectController sc = SubjectController.getInstance();
        subjects = sc.getFacade().getSubjectFindAll();
        List<ClassSubjectCount> sbjCounts = cg.getFacade().getClassSubjectCountFindAll();
        for (ClassGroup group : classGroups) {
          for (ClassSubjectCount sbjCount : sbjCounts) {
            if (sbjCount.getClass_group_id().equals(group.getId())) {
              for (Subject sbj : group.getSubjectCollection()) {
                if (sbj.getId().equals(sbjCount.getSubject_id()))
                  sbj.setCount(sbjCount.getSubject_count());
              }
            }
          }
        }
    // organize teachers by subject
        for (Teacher t : teachers) {
            schedTeachers.put(t.getId(), new SchedulingTeacher(t));
            Collection<Subject> teacherSubjects = t.getSubjectCollection();
            if (teacherSubjects != null) {
                for (Subject s : teacherSubjects) {
                    List<Teacher> ts = teachersForSubject.get(s.getId());
                    if (ts == null) {
                        ts = new ArrayList<Teacher>();
                    }
                    teachersForSubject.put(s.getId(), ts);
                    ts.add(t);
                }
            }
        }
        for (Integer sid : teachersForSubject.keySet()) {
            List<Teacher> t = teachersForSubject.get(sid);
            Collections.sort(t, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return -1*(new Integer(((Teacher)o1).getSubjectCollection().size()).compareTo(((Teacher)o2).getSubjectCollection().size()));
                }
            });
        }
    }
    
    /**
     * returns and(or) initializes the schedule cube with initial random values.
     * @return 
     */
    public synchronized TimeSchedule getInitializedSchedule() {
        if (schedule == null) {
            schedule = new TimeSchedule(this);
        }
        return schedule;
    }
    
    public Teacher getRandomTeacherForSubject(Subject subject) {
        List<Teacher> ts = teachersForSubject.get(subject.getId());
        Teacher teacher = null;
        if (ts != null && ts.size() > 0) {
            int idx = Double.valueOf(Math.floor(ts.size()*Math.random())).intValue();
            teacher = ts.get(idx);
        }
        return teacher;
    }
    
    public void addError(String errorText) {
        errors.add(errorText);
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public List<ClassGroup> getClassGroups() {
        return classGroups;
    }
    
    public List<Lesson> getLessons() {
        return lessons;
    }
    
    public List<Classroom> getRooms() {
        return classRooms;
    }

    public Teacher allocateTeacherForSubject(Subject subject, int day, SchedulingLesson sLesson) {
        Teacher teacher = null;
        List<Teacher> teachersForSubj = teachersForSubject.get(subject.getId());
        HashMap<Teacher,Integer> allocatedTeachers = sLesson.getAllocatedTeachersForDay(day);
        int i = teachersForSubj != null ? teachersForSubj.size() : -1;
        while (i > 0) {
            teacher = teachersForSubj.get(--i);
            SchedulingTeacher sTeacher = schedTeachers.get(teacher.getId());
            if (allocatedTeachers.get(teacher) == null && sTeacher.isAvailableForLesson(day, sLesson.getIndex()))
                break;
            teacher = null;
        }
        if (teacher != null)
            allocatedTeachers.put(teacher, Integer.SIZE);
        return teacher;
    }
    
    public void undoAllocateTeacherForSubject(Teacher teacher, Subject subject, int day, SchedulingLesson sLesson) {
        HashMap<Teacher,Integer> allocatedTeachers = sLesson.getAllocatedTeachersForDay(day);
        allocatedTeachers.remove(teacher);
    }
    
    public List<SchedulingRoom> allocateRoomForClassGroup(ClassGroup group, Subject subject, int day, SchedulingLesson sLesson) {
        ArrayList<SchedulingRoom> groupRoom = new ArrayList<SchedulingRoom>(2);
        Classroom preferredRoom = subject.getClassroom();
        HashMap<Classroom,Integer> allocRoomsForDay = sLesson.getAllocatedRoomsForDay(day);
        if (preferredRoom != null && allocRoomsForDay.get(preferredRoom) == null) {
            for (int room = 0; room < classRooms.size(); room++) {
                if (classRooms.get(room).getId().equals(preferredRoom.getId())) {
                    allocRoomsForDay.put(preferredRoom, room);
                    groupRoom.add(new SchedulingRoom(preferredRoom, room));
                    break;
                }
            }
            // check if classgrp will fit to the subject preferred room
            if (!groupRoom.isEmpty()) {
                int unallocatedCapacity = group.getCapacity() - preferredRoom.getCapacity();
                boolean hasRoom = true;
                while (unallocatedCapacity > 0 && hasRoom) {
                    // see if there's more rooms for the subject to split the group into
                    hasRoom = false;
                    for (int room = 0; room < classRooms.size() && unallocatedCapacity > 0; room++) {
                        Classroom aRoom = classRooms.get(room);
                        if (aRoom.getPrefSubject().equals(preferredRoom.getPrefSubject()) &&
                            allocRoomsForDay.get(aRoom) == null) {
                            allocRoomsForDay.put(aRoom, room);
                            groupRoom.add(new SchedulingRoom(aRoom, room));
                            unallocatedCapacity -= aRoom.getCapacity();
                            hasRoom = true;
                            break;
                        }
                    }
                }
                if (unallocatedCapacity > 0) {
                    StringBuilder msg = new StringBuilder("Cannot fit the group to the preferred room for subject ["+subject.getName()+"]. Rooms [");
                    int roomCapacity = 0;
                    for (SchedulingRoom s : groupRoom) {
                        msg.append(s.getRoom().getName()).append(" ");
                        roomCapacity += s.getRoom().getCapacity();
                    }
                    msg.append("] can seat only ").append(Integer.toString(roomCapacity)).append("while class group capacity is: ").append(group.getCapacity());
                    msg.append(". Trying other room now.");
                    addError(msg.toString());
                    // remove preffered rooms for the class will not fit in
                    groupRoom.clear();
                }
            }
        }
        if (groupRoom.isEmpty()) {
            preferredRoom = group.getClassroom();
            if (preferredRoom != null) {
                for (int room = 0; room < classRooms.size(); room++) {
                    if (classRooms.get(room).getId().equals(preferredRoom.getId())
                        && allocRoomsForDay.get(preferredRoom) == null) {
                        allocRoomsForDay.put(preferredRoom, room);
                        groupRoom.add(new SchedulingRoom(preferredRoom, room));
                        break;
                    }
                }
            }
        }
        // check if preferred room was available, if not assign random room
        if (groupRoom.isEmpty()) {
            for (int room = 0; room < classRooms.size(); room++) {
                if (classRooms.get(room).getCapacity() >= group.getCapacity()
                    && allocRoomsForDay.get(classRooms.get(room)) == null) {
                    preferredRoom = classRooms.get(room);
                    groupRoom.add(new SchedulingRoom(preferredRoom, room));
                    allocRoomsForDay.put(preferredRoom, room);
                    break;
                }
            }
        }
        return groupRoom;
    }
    
    public void undoAllocateRoomForClassGroup(List<SchedulingRoom> groupRoom, int day, SchedulingLesson sLesson) {
        if (sLesson != null && groupRoom != null) {
            HashMap<Classroom,Integer> allocRoomsForDay = sLesson.getAllocatedRoomsForDay(day);
            for(SchedulingRoom room : groupRoom)
                allocRoomsForDay.remove(room.getRoom());
        }
    }

    public String toXML() {
      StringBuffer xml = new StringBuffer("<?xml version = '1.0' encoding = 'windows-1252'?>\n");
      xml.append("<definition>\n");
      xml.append("<classGroups>\n");
      for(ClassGroup g : classGroups) 
        xml.append(g.toXML());
      xml.append("</classGroups>\n");
      xml.append("<classRooms>\n");
      for(Classroom r : classRooms) 
        xml.append(r.toXML());
      xml.append("</classRooms>\n");
      xml.append("<lessons>\n");
      for(Lesson l : lessons) 
        xml.append(l.toXML());
      xml.append("</lessons>\n");
      xml.append("<subjects>\n");
      for(Subject s : subjects) 
        xml.append(s.toXML());
      xml.append("</subjects>\n");
      xml.append("<teachers>\n");
      for(Teacher t : teachers) 
        xml.append(t.toXML());
      xml.append("</teachers>\n");
      xml.append("</definition>");
      return xml.toString();
    }
}
