package com.project.server;

import java.util.List;

public class Lecture {
    public static final List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
    public static final List<String> times = List.of("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00");
    private int dayNum;
    private String day;
    private int lecTimeNum;
    private String lecTime;
    private String moduleCode;
    private String roomNum;

    public Lecture(String moduleCode, String day, String time, String roomNum){
        this.moduleCode = moduleCode.trim();
        this.day = day.trim();
        this.dayNum = days.indexOf(day);
        this.lecTime = time.trim();
        this.lecTimeNum = times.indexOf(time);
        this.roomNum = roomNum.trim();
    }

    @Override
    public String toString() {
        return getModuleCode() + "," + getDay() + "," + getLecTime() + "," + getRoomNum() + ";";
    }

    public String getModuleCode(){
        return moduleCode;
    }

    public String getDay(){
        return day;
    }

    public int getDayNum(){
        return dayNum;
    }

    public String getLecTime(){
        return lecTime;
    }

    public int getLecTimeNum(){
        return lecTimeNum;
    }

    public void setLecTimeNum(int lecTimeNum) {
        this.lecTimeNum = lecTimeNum;
        this.lecTime = times.get(lecTimeNum);
    }

    public String getRoomNum(){
        return roomNum;
    }
}