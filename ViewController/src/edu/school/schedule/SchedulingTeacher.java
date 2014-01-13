/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.school.schedule;

import edu.school.jpa.Availability;
import edu.school.jpa.Lesson;
import edu.school.jpa.Subject;
import edu.school.jpa.Teacher;
import static edu.school.schedule.TimeSchedule.Day.*;
import edu.school.view.AvailabilityController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Penguin
 */
class SchedulingTeacher {
    
    private Teacher             teacher;
    private ArrayList<Subject>  subjects;
    private List<Availability>  availability;
    private int                 availabilityPerWeek = 0;
    
    public SchedulingTeacher(Teacher teacher) {
        this.teacher = teacher;
        if (teacher.getSubjectCollection() != null) {
            subjects = new ArrayList<Subject>(teacher.getSubjectCollection().size());
            subjects.addAll(teacher.getSubjectCollection());
        } else {
            subjects = new ArrayList<Subject>(1);
        }
        // get the actual availability matrix
        availability = AvailabilityController.getInstance().getAvailabilityByLesson(teacher);
        // get total availability to study per week
        for (Availability a : availability) {
            availabilityPerWeek += a.getLessonCount();
        }
    }

    /**
     * @return the teacher
     */
    public Teacher getTeacher() {
        return teacher;
    }
    
    /**
     * get random subject from list of subjects this teacher teaches.
     * @return 
     */
    public Subject getUniqueSubject() {
        Subject subject = null;
        if (subjects.size() > 0) {
            int idx = Double.valueOf(Math.floor(subjects.size()*Math.random())).intValue();
            subject = subjects.remove(idx);
        }
        return subject;
    }

    /**
     * @return the availabilityPerWeek
     */
    public int getAvailabilityPerWeek() {
        return availabilityPerWeek;
    }
    
    /**
     * requests to allocate certain number of teaching hours for this teacher.
     * It checks that teachers has available hours scheduled and how many.
     * If teacher teaches less hours then requested, it returns that number and records
     * reminder hours as 0 ( so that subsequent calls will return 0 - teacher is at full capacity)
     * @param howMany - how many hours to allocate
     * @return how many hours was able to allocate in reality given the teacher's current capacity
     */
    public int allocateTeachingLessons(int howMany) {
        int canTeach = 0;
        if (availabilityPerWeek > howMany) {
            canTeach = howMany;
            availabilityPerWeek -= howMany;
        } else if (availabilityPerWeek > 0) {
            canTeach = availabilityPerWeek;
            availabilityPerWeek = 0;
        }
        return canTeach;
    }
    
    /**
     * Test teachers availability for teaching this lesson on this day based on scheduled availability.
     * @param teachingLesson
     * @param day
     * @return 
     */
    public boolean isAvailableForLesson(int day, int teachingLesson) {
        boolean isAvailable = day >= 0 && day < TimeSchedule.Day.values().length &&
                              teachingLesson >= 0 && teachingLesson < availability.size() && 
                              availability.get(teachingLesson).isAvailableOnDay(day);
        return isAvailable;
    }
}
