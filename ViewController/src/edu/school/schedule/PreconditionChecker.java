/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.school.schedule;

import edu.school.jpa.ClassGroup;
import edu.school.jpa.Subject;
import edu.school.jpa.Teacher;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Penguin
 */
public class PreconditionChecker {
    
    private Scheduler scheduler;
    
    public PreconditionChecker() {
        scheduler = new Scheduler();
    }
    
    public PreconditionChecker(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    /**
     * Checks that the preconditions for the schedule are satisfied and that 
     * the schedule can be physically generated.
     * @return 
     */
    public boolean test() {
        boolean result = true;
        //
        // 1. is the school capacity enough (is there enough rooms for the groups & their lessons)?
        // 2. is each classGroup not having excessive amount of subjects?
        //
        int totalTaughtLessons = 0;
        HashMap<String, Integer> totalLessonsBySubject = new HashMap<String, Integer>();
        for (ClassGroup cg : scheduler.classGroups) {
            SchedulingClassGroup scg = new SchedulingClassGroup(cg);
            totalTaughtLessons += scg.getLessonsPerWeek();
            if (scg.getAvailabilityPerWeek() < scg.getLessonsPerWeek()) {
                scheduler.addError("ClassGroup "+cg.toString()+" has excessive amount of lessons: "+scg.getLessonsPerWeek()+" but attends only for: "+scg.getAvailabilityPerWeek());
                result = false;
            }
            for (Subject subject : cg.getSubjectCollection()) {
                Integer subjectLessonsCount = totalLessonsBySubject.get(subject.getName());
                if (subjectLessonsCount == null) {
                    subjectLessonsCount = 0;
                }
                totalLessonsBySubject.put(subject.getName(),(subjectLessonsCount+subject.getCount()));
            }
        }
        int schoolLessonCapacity = scheduler.lessons.size() * scheduler.classRooms.size() * 7; // 7 week days
        int teacherCapacity = 0;
        for (Teacher t : scheduler.teachers) {
            teacherCapacity += scheduler.schedTeachers.get(t.getId()).getAvailabilityPerWeek();
        }
        if (schoolLessonCapacity < totalTaughtLessons) {
            scheduler.addError("Too many scheduled lessons. Class Groups have: "+totalTaughtLessons+" scheduled while schedule allows only: "+schoolLessonCapacity+" to be taught per week in "+scheduler.classRooms.size()+" class rooms.");
            result = false;
        }
        if (teacherCapacity < totalTaughtLessons) {
            scheduler.addError("Too many scheduled lessons. The teaching staff can teach : ["+teacherCapacity+"] lessons per week while the schedule requires: "+totalTaughtLessons+" to be taught per week in "+scheduler.classRooms.size()+" class rooms.");
            result = false;
        }
        scheduler.addError(printReportA(totalTaughtLessons, teacherCapacity, totalLessonsBySubject));
        //
        // 3. Is there enough teachers?
        //
        HashMap<String, Integer> totalTeachersBySubject = new HashMap<String, Integer>();
        for (Subject subject : scheduler.subjects) {
            List<Teacher> teachers = scheduler.teachersForSubject.get(subject.getId());
            Integer requiredLessons = totalLessonsBySubject.get(subject.getName());
            int i = 0;
            int teachersForSubject = 0;
            if (teachers != null && teachers.size() > 0) {
                int quotaPerTeacher = requiredLessons / teachers.size();
                if (requiredLessons % teachers.size() >= teachers.size() / 2) {
                    quotaPerTeacher++;
                }
                for (Teacher t : teachers) {
                    SchedulingTeacher sTeacher = scheduler.schedTeachers.get(t.getId());
                    teachersForSubject += sTeacher.allocateTeachingLessons(
                                            (requiredLessons-teachersForSubject)>=quotaPerTeacher ?
                                                quotaPerTeacher : (requiredLessons-teachersForSubject));
                  i++;
                  int ut = teachers.size() - i;
                  if (ut > 0) {
                    quotaPerTeacher = requiredLessons / ut;
                    if (requiredLessons % ut >= ut / 2) {
                        quotaPerTeacher++;
                    }
                  }
                }
                if (teachersForSubject < requiredLessons) {
                    scheduler.addError("There is not enough teachers available to teach ["+subject.getName()+
                                        "], schedule requires ["+requiredLessons+"] lessons to be taught, but you have ["+
                                        teachers.size()+"] teachers qualified, who are available only for ["+teachersForSubject+"] hours.");
                    result = false;
                }
            } else {
                scheduler.addError("There are no teachers for subject: ["+subject.getName()+"].");
                result = false;
            }
            totalTeachersBySubject.put(subject.getName(),teachersForSubject);
        }
        return result;
    }
    
    public Scheduler getScheduler() {
        return scheduler;
    }
    
    private String printReportA(int totalTaughtLessons, int teacherCapacity, HashMap<String, Integer> totalLessonsBySubject) {
      StringBuilder rpt = new StringBuilder();
        rpt.append("There's ["+totalTaughtLessons+"] taught lessons pere week. School capacity is: "+(scheduler.lessons.size() * scheduler.classRooms.size() * 7)).append("\n");
        rpt.append("Number of lessons per subject:\n============================\n");
        for (String subjName : totalLessonsBySubject.keySet()) {
            rpt.append(subjName+ " : "+totalLessonsBySubject.get(subjName)).append("\n");
        }
        rpt.append("Teaching staff can teach ["+teacherCapacity+"] lessons per week.\n");
      System.out.println(rpt.toString());
      return rpt.toString();
    }
}
