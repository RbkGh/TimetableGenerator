/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.school.schedule;

import edu.school.jpa.Availability;
import edu.school.jpa.ClassGroup;
import edu.school.jpa.Subject;
import edu.school.view.AvailabilityController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
/**
 *
 * @author Penguin
 */
class SchedulingClassGroup {
    private ClassGroup          group;
    private ArrayList<Subject>  subjects;
    private List<Availability>  availability;
    private int                 lessonsPerWeek = 0;
    private int                 availabilityPerWeek = 0;
    private int                 availableDaysPerWeek = 0;
//    private int                 lessonsPerDay = 0;
//    private int                 lessonsPerDayFragment = 0;
    private HashMap<Subject,Integer> subjectCount = new HashMap<Subject,Integer>();
//    private boolean mon=false,tue=false,wed=false,thu=false,fri=false,sat=false,sun=false;
    private ArrayList<HashMap<Subject,Integer>> subjectCountPerDayMap = new ArrayList<HashMap<Subject,Integer>>(7);
    private ArrayList<Integer>  subjectCountPerDayList = new ArrayList<Integer>(7);
    private ArrayList<Subject>  optimizedSubjectList = new ArrayList<Subject>();
    private int[]               availabilityPerDay = new int[7];
    private int[]               lessonsPerDay = new int[7];
    
    public SchedulingClassGroup(ClassGroup classGroup) {
        group = classGroup;
        if (classGroup.getSubjectCollection() != null) {
            subjects = new ArrayList<Subject>(classGroup.getSubjectCollection().size());
            subjects.addAll(classGroup.getSubjectCollection());
            Collections.sort(subjects, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return -1*((Subject)o1).getWeight().compareTo(((Subject)o2).getWeight());
                }
            });
            boolean doLoop = true;
            while (doLoop) {
                doLoop = false;
                for (Subject subject : subjects) {
                    Integer sbjCount = subjectCount.get(subject);
                    if (sbjCount == null) {
                        sbjCount = subject.getCount();
                    }
                    if (sbjCount > 0) {
                        optimizedSubjectList.add(subject);
                        doLoop = true;
                    }
                    sbjCount--;
                    subjectCount.put(subject,sbjCount);
                }
            }
            subjectCount.clear();
        } else {
            subjects = new ArrayList<Subject>(1);
        }
        // get the actual availability matrix
        availability = AvailabilityController.getInstance().getAvailabilityByLesson(classGroup);
        // get total lesson count per week
        for (Subject subject : subjects) {
            lessonsPerWeek += subject.getCount();
            subjectCount.put(subject, subject.getCount());
        }
        // get total availability to study per week
        for (Availability a : availability) {
            availabilityPerWeek += a.getLessonCount();
            if (a.getMondayBool().booleanValue())
                availabilityPerDay[0]++;
            if (a.getTuesdayBool().booleanValue())
                availabilityPerDay[1]++;
            if (a.getWednesdayBool().booleanValue())
                availabilityPerDay[2]++;
            if (a.getThursdayBool().booleanValue())
                availabilityPerDay[3]++;
            if (a.getFridayBool().booleanValue())
                availabilityPerDay[4]++;
            if (a.getSaturdayBool().booleanValue())
                availabilityPerDay[5]++;
            if (a.getSundayBool().booleanValue())
                availabilityPerDay[6]++;
        }
        if (availabilityPerDay[0] > 0) availableDaysPerWeek++;
        if (availabilityPerDay[1] > 0) availableDaysPerWeek++;
        if (availabilityPerDay[2] > 0) availableDaysPerWeek++;
        if (availabilityPerDay[3] > 0) availableDaysPerWeek++;
        if (availabilityPerDay[4] > 0) availableDaysPerWeek++;
        if (availabilityPerDay[5] > 0) availableDaysPerWeek++;
        if (availabilityPerDay[6] > 0) availableDaysPerWeek++;

        // holds count of subject per day to ensure there's not too many same subjects per day
        
        for (int i = 0; i < TimeSchedule.Day.values().length; i++) {
            subjectCountPerDayMap.add(new HashMap<Subject,Integer>());
            subjectCountPerDayList.add(0);
        }
        // determine how many lessons (optimal number spread equally) there should be for the group for each week day
        // based on the configured availability
        int lessonsPerWeekCopy = lessonsPerWeek;
        double offset = 0d;
        while (lessonsPerWeekCopy > 0) {
            lessonsPerWeekCopy = lessonsPerWeek;
            for (int i = 0; i < TimeSchedule.Day.values().length; i++) {
                lessonsPerDay[i] = (int)Math.round(availabilityPerDay[i] / lessonsPerWeek + offset);
                if (lessonsPerDay[i] > lessonsPerWeekCopy) {
                    lessonsPerDay[i] = lessonsPerWeekCopy;
                }
                lessonsPerWeekCopy -= lessonsPerDay[i];
                if (lessonsPerWeekCopy < 0)
                    lessonsPerWeekCopy = 0;
            }
            // lessonsPerWeekCopy should be 0 if all lessons were allcoated, if > 0, then need to adjust 
            if (lessonsPerWeekCopy > 0) {
                offset += (double)lessonsPerWeekCopy / (double)lessonsPerWeek;
            }
        }
    }
    
    public ClassGroup getGroup() {
        return group;
    }
    
    public int getMaxSubjectCountPerDay(Subject subject, int day) {
        int subjCountPerDay = 1;
        if (subject.getCount() > availableDaysPerWeek) {
            int fragment = subject.getCount() % availableDaysPerWeek;
            double dFragment = (double)fragment;
            if ((availableDaysPerWeek - day)/dFragment > (availableDaysPerWeek - fragment)/fragment) {
                subjCountPerDay++;
            }
        }
        return subjCountPerDay;
    }
    
    /**
     * Allocates one subject from the optimized list for the given lesson.
     * tests for : the subjects allocated for the given date do not exceed optimum subject number for the day
     *             the class group is available to study for given day/lesson
     * @param lesson - 0 based index of lesson in given day
     * @param day    - 0 based index of day within a week
     * @param scheduler - scheduler reference
     * @param level  - for repeated allocation attempts increasing the level will result in allocating different subject.
     * @return allocated subject or NULL if the preconditions (see above) are not met
     */
    public Subject allocateSubject(int day, int lesson, int level) {
        Subject subject = null;
        int idxSelect = level >= 0 ? (level < optimizedSubjectList.size() ? level : optimizedSubjectList.size() - 1) : 0;
        if (isAvailableOnDay(day, lesson)) {
            Integer subjectsForThisDay = subjectCountPerDayList.get(day);
            if (subjectsForThisDay < getMaxLessonsPerDay(day) && idxSelect >= 0) {
                subject = optimizedSubjectList.remove(idxSelect);
                subjectCountPerDayList.set(day, (subjectsForThisDay+1));
                HashMap<Subject,Integer> countPerDay = subjectCountPerDayMap.get(day);
                Integer sbjCount = countPerDay.get(subject);
                if (sbjCount == null) {
                    countPerDay.put(subject, 1);
                } else {
                    countPerDay.put(subject, sbjCount+1);
                }
            }
        }
        return subject;
    }
    
    /**
     * reverts the allocateSubject action, returning subject to a list, decreasing subjects-per-day count.
     * @param subject
     * @param day
     * @param level 
     */
    public void undoAllocateSubject(Subject subject, int day, int level) {
        int idxSelect = level >= 0 ? ((level < optimizedSubjectList.size() || optimizedSubjectList.size() == 0) ? level : optimizedSubjectList.size() - 1) : 0;
        Integer subjectsForThisDay = subjectCountPerDayList.get(day);
        if (idxSelect > optimizedSubjectList.size())
          optimizedSubjectList.add(subject);
        else
          optimizedSubjectList.add(idxSelect, subject);
        if (subjectsForThisDay > 0)
            subjectCountPerDayList.set(day, (subjectsForThisDay-1));
        HashMap<Subject,Integer> countPerDay = subjectCountPerDayMap.get(day);
        Integer sbjCount = countPerDay.get(subject);
        if (sbjCount != null && sbjCount > 1) {
            countPerDay.put(subject, sbjCount-1);
        } else {
            countPerDay.remove(subject);
        }
    }

    /**
     * @return the lessonsPerWeek
     */
    public int getLessonsPerWeek() {
        return lessonsPerWeek;
    }

    /**
     * @return the availabilityPerWeek
     */
    public int getAvailabilityPerWeek() {
        return availabilityPerWeek;
    }
    
    public List<Subject> getSubjects() {
        return subjects;
    }
    
    public int getAvailableDaysPerWeek() {
        return availableDaysPerWeek;
    }
    /**
     * return proportion limit for lesson count this group should have on a given day
     * @param day
     * @return 
     */
    public int getMaxLessonsPerDay(int day) {
        return day >= 0 ? (day < lessonsPerDay.length ? lessonsPerDay[day] : 0) : 0;
    }

    /** 
     * Determines if class group is available for study on given day & lesson
     * @param day    0 based day of the week
     * @param lesson 0-based lesson index in a day
     * @return true if available.
     */
    public boolean isAvailableOnDay(int day, int lesson) {
        
        boolean isAvailable = day >= 0 && day < lessonsPerDay.length && lessonsPerDay[day] > 0 &&
                              lesson >= 0 && lesson < availability.size() && availability.get(lesson).isAvailableOnDay(day);
        return isAvailable;
    }
    
    /**
     * return the existing subject allocation for the day
     * @param day
     * @return 
     */
    public HashMap<Subject,Integer> getSubjectCountPerDayMap(int day) {
        return subjectCountPerDayMap.get(day < subjectCountPerDayMap.size() ? day : 0);
    }
    
    public boolean hasMaxSubjectsForDay(int day) {
        boolean hasMax = false;
        HashMap<Subject,Integer> subjsForDay = getSubjectCountPerDayMap(day);
        int subjCount = 0;
        for (Subject s : subjsForDay.keySet()) {
            subjCount += subjsForDay.get(s);
        }
        hasMax = subjCount >= lessonsPerDay[day];
        return hasMax;
    }
}
