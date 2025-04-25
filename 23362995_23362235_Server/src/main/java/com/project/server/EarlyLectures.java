package com.project.server;

import java.util.concurrent.RecursiveTask;
import java.util.ArrayList;
import java.util.List;

public class EarlyLectures extends RecursiveTask<Boolean> {
    private Lecture[][] schedule;
    private int col;

    public EarlyLectures(Lecture[][] sch, int col) {
        this.schedule = sch;
        this.col = col;
    }

    @Override
    protected Boolean compute() {
        // indicate whether any changes were made
        boolean changed = false;
        // make an array of free slots in the col
        ArrayList<Integer> freeSlots = new ArrayList<>();
        
        for (int row = 0; row < schedule.length; row++) {
            // if slot is free, add to list
            if (schedule[row][col] == null) {
                freeSlots.add(row);
            } else {
                // if slot is taken, check if there are free slots before
                if (!freeSlots.isEmpty()) {
                    // move to first free slot
                    int firstFreeSlot = freeSlots.remove(0);
                    synchronized (schedule) {
                        //update the time of the lecture
                        schedule[row][col].setLecTimeNum(firstFreeSlot);
                        // update the schedule array
                        schedule[firstFreeSlot][col] = schedule[row][col];
                        schedule[row][col] = null;
                        changed = true;
                    }
                    freeSlots.add(row); // add newly cleared slot to free slots
                }
            }
        }
        return changed;
    }

    public static boolean moveLectures(Lecture[][] array) {
        boolean changed = false;
        List<EarlyLectures> tasks = new ArrayList<>();
    
        for (int col = 0; col < array[0].length; col++) {
            EarlyLectures task = new EarlyLectures(array, col);
            task.fork(); // launch task in parallel
            tasks.add(task);
        }

        for (EarlyLectures task : tasks) {
            boolean result = task.join(); // wait for each task to complete

            if(result){
                changed = true; // if any task changed the schedule, set changed to true
            }
        }
        return changed;
    }
}