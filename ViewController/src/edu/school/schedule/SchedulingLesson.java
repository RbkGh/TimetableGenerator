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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Penguin
 */
public class SchedulingLesson {
    private int                            index;
    private ArrayList<ClassGroup>          classGroups       = new ArrayList<ClassGroup>();
    private Lesson                         lesson;
    private ArrayList<HashMap<Teacher,Integer>>       allocatedTeachersForDay = new ArrayList<HashMap<Teacher,Integer>>(7);
    private ArrayList<HashMap<Classroom,Integer>>     allocatedRoomsForDay = new ArrayList<HashMap<Classroom,Integer>>(7);
    
    public SchedulingLesson(int index, Lesson lesson, 
                            List<ClassGroup> classGroups) {
        this.index = index;
        this.lesson = lesson;
        // copy content of classgroups
        this.classGroups.addAll(classGroups);
        for (int i = 0; i < TimeSchedule.Day.values().length; i++) {
            allocatedTeachersForDay.add(new HashMap<Teacher,Integer>());
            allocatedRoomsForDay.add(new HashMap<Classroom,Integer>());
        }
    }
    
    public Lesson getLesson() {
        return lesson;
    }
    
    public int getIndex() {
        return index;
    }
    
    public HashMap<Teacher,Integer> getAllocatedTeachersForDay(int day) {
        HashMap<Teacher,Integer> allocTeachersForDay = null;
        if (day < allocatedTeachersForDay.size())
            allocTeachersForDay = allocatedTeachersForDay.get(day);
        return allocTeachersForDay;
    }
    
    public void undoAllocateTeacherForDay(Teacher teacher, int day) {
        if (day < allocatedTeachersForDay.size())
            allocatedTeachersForDay.get(day).put(teacher, 1);
    }
    
    
    public HashMap<Classroom,Integer> getAllocatedRoomsForDay(int day) {
        HashMap<Classroom,Integer> allocRoomsForDay = null;
        if (day < allocatedRoomsForDay.size())
            allocRoomsForDay = allocatedRoomsForDay.get(day);
        return allocRoomsForDay;
    }
    
    public void undoAllocateRoomForDay(Classroom room, int day) {
        if (day < allocatedRoomsForDay.size())
            allocatedRoomsForDay.get(day).put(room, 1);
    }
    
    
}
