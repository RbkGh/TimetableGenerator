/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.school.schedule;

import edu.school.jpa.Classroom;

/**
 *
 * @author Penguin
 */
public class SchedulingRoom {
    private Classroom room;
    private int index;
    
    /**
     * Creates the object, it stores the index which is part of identifying the associated scheduling unit
     * @param room
     * @param index
     */
    public SchedulingRoom(Classroom room, int index) {
        this.room = room;
        this.index = index;
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
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(int index) {
        this.index = index;
    }
    
}
