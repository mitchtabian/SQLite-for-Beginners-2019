package com.codingwithmitch.notes.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    private static final String TAG = "Utility";

    public static final String[] monthNumbers = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    public static final String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    public static final String DATE_FORMAT = "MM-yyyy";
    public static final String DATE_FORMAT_EXCEPTION = "Couldn't format the date into MM-yyyy";

    public static String getCurrentTimeStamp() throws Exception{
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT); //MUST USE LOWERCASE 'y'. API 23- can't use uppercase
            return dateFormat.format(new Date()); // Find todays date
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(DATE_FORMAT_EXCEPTION);
        }
    }

    public static String getMonthFromNumber(String monthNumber){
        switch(monthNumber){
            case "01":{
                return months[0];
            }
            case "02":{
                return months[1];
            }
            case "03":{
                return months[2];
            }
            case "04":{
                return months[3];
            }
            case "05":{
                return months[4];
            }
            case "06":{
                return months[5];
            }
            case "07":{
                return months[6];
            }
            case "08":{
                return months[7];
            }
            case "09":{
                return months[8];
            }
            case "10":{
                return months[9];
            }
            case "11":{
                return months[10];
            }
            case "12":{
                return months[11];
            }

            default:{
                return "Error";
            }
        }
    }

}
