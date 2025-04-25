package com.project.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Schedule {
    private final Lecture[][] schedule; 
    private final ArrayList<String> modules;
    private int moduleCount;
    private final ArrayList<ClientHandler> clients;
    private final List<ScheduleUpdateListener> listeners = new ArrayList<>();

    public Schedule() {
        this.schedule = new Lecture[Lecture.times.size()][Lecture.days.size()];
        this.modules = new ArrayList<>(5);
        this.moduleCount = 0;
        this.clients = new ArrayList<>();

        for (Lecture[] row : schedule) {
            Arrays.fill(row, null); 
        }
    }

    public synchronized void addClient(ClientHandler client) {
        clients.add(client);
    }

    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public synchronized ArrayList<ClientHandler> getClients() {
        return new ArrayList<>(clients);
    }

    public synchronized String addLecture(Lecture lec) throws IncorrectActionException {
        String response;

        if (!modules.contains(lec.getModuleCode())) {
            if (moduleCount < 5) {
                if (schedule[lec.getLecTimeNum()][lec.getDayNum()] == null) {
                    schedule[lec.getLecTimeNum()][lec.getDayNum()] = lec;
                    modules.add(lec.getModuleCode());
                    moduleCount++;
                    response = "Success: Lecture added successfully.";
                } else {
                    throw new IncorrectActionException("Time slot taken. Cannot add this lecture to the schedule.");
                }
            } else {
                throw new IncorrectActionException("Max 5 modules per course, cannot add another module.");
            }
        } else {
            if (schedule[lec.getLecTimeNum()][lec.getDayNum()] == null) {
                schedule[lec.getLecTimeNum()][lec.getDayNum()] = lec;
                response = "Success: Lecture added successfully.";
            } else {
                throw new IncorrectActionException("Time slot taken. Cannot add this lecture to the schedule.");
            }
        }
        notifyListeners();

        return response;
        

    }

    public synchronized String removeLecture(Lecture lec) throws IncorrectActionException {
        String response;

        if (schedule[lec.getLecTimeNum()][lec.getDayNum()] != null &&
                schedule[lec.getLecTimeNum()][lec.getDayNum()].getModuleCode().equals(lec.getModuleCode()) &&
                schedule[lec.getLecTimeNum()][lec.getDayNum()].getRoomNum().equals(lec.getRoomNum())) {

            schedule[lec.getLecTimeNum()][lec.getDayNum()] = null;

            boolean moreLecs = false;
            outerloop:
            for (Lecture[] i : schedule) {
                for (Lecture j : i) {
                    if (j != null && lec.getModuleCode().equals(j.getModuleCode())) {
                        moreLecs = true;
                        break outerloop;
                    }
                }
            }

            if (!moreLecs) {
                modules.remove(lec.getModuleCode());
                moduleCount--;
            }

            response = String.format("Success: Lecture removed successfully.\nDay: %s, Time: %s, Room: %s",
                    lec.getDay(), lec.getLecTime(), lec.getRoomNum());

        } else {
            throw new IncorrectActionException("This lecture does not exist. Cannot remove from schedule.");
        }
        notifyListeners();

        return response;
    }

    public synchronized String clearSchedule() {
        modules.clear();
        moduleCount = 0;

        for (Lecture[] row : schedule) {
            Arrays.fill(row, null); 
        }

        return "Success: Schedule cleared successfully.";
    }

    public synchronized String earlyLectures() throws IncorrectActionException {
        if (moduleCount == 0) {
            throw new IncorrectActionException("Schedule empty. Cannot reschedule lectures.");
        }

        boolean result = EarlyLectures.moveLectures(schedule);
        notifyListeners();

        if (result) {
            return "Success: Lectures rescheduled where applicable.";
        } else {
            throw new IncorrectActionException("No changes available.");
        }

    }

    
    public synchronized String getFormattedSchedule() {
        if (moduleCount == 0) {
            return "";
        }

        StringBuilder response = new StringBuilder();

        for (Lecture[] i : schedule) {
            for (Lecture j : i) {
                if (j != null) {
                    response.append(j.getModuleCode()).append(",")
                            .append(j.getDay()).append(",")
                            .append(j.getLecTime()).append(",")
                            .append(j.getRoomNum()).append(";");
                }
            }
        }

        if (response.length() > 0) {
            response.setLength(response.length() - 1);
        }

        return response.toString();
    }

   
    public synchronized String displaySchedule() {
        String formatted = getFormattedSchedule();
        return formatted.isEmpty() ? "Schedule empty." : formatted;
    }
    

public void addListener(ScheduleUpdateListener listener) {
    listeners.add(listener);
}

private void notifyListeners() {
    for (ScheduleUpdateListener listener : listeners) {
        listener.onScheduleUpdated();
    }
}

}