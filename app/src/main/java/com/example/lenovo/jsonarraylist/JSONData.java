package com.example.lenovo.jsonarraylist;

/**
 * Created by Lenovo on 06-Jul-16.
 */
public class JSONData {
    private String title;
    public static class JSONSubData {
        String startTime,endTime,state;

        public JSONSubData(String startDate, String endDate, String state) {
            this.startTime = startDate;
            this.endTime = endDate;
            this.state = state;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public String getState() {
            return state;
        }
    }
    private JSONSubData jsonSubData;

    public JSONData(String title, JSONSubData jsonSubData) {
        this.title = title;
        this.jsonSubData = jsonSubData;
    }

    public String getTitle() {
        return title;
    }

    public JSONSubData getJsonSubData() {
        return jsonSubData;
    }
}
