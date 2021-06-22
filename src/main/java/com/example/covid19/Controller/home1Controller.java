package com.example.covid19.Controller;

import com.example.covid19.Model.Appointment;
import com.example.covid19.Model.DateAndTime;
import com.example.covid19.Model.TimeSlots;
import com.example.covid19.Model.User;
import com.example.covid19.Service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
@Controller
public class home1Controller {
    String currentAppointment;

    @Autowired
    ServiceInterface serviceInterface;

    @GetMapping("/DirectAppointment")
    public String makeDirectAppointment(@ModelAttribute DateAndTime dt, Model model) {

        String cpr = dt.getCpr();
        Date dateM = dt.getMydate();
        String timeM = dt.getTime();
        int tcID = dt.getTcID();
        model.addAttribute("dateM", dateM);
        model.addAttribute("timeM", timeM);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(dateM);
        String day = strDate.substring(0, 2);
        String month = strDate.substring(3, 5);
        String year = strDate.substring(6);
        String hour = timeM.substring(0, 2);
        String minute = timeM.substring(3);
        String receivedDate = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":00";
        String str = (year + "-" + month + "-" + day + " " + hour + ":" + minute);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(str, dtf);
        List<Appointment> appList = serviceInterface.fetchAllAppointments();
        model.addAttribute("appList", appList);
        String cprError = "You have already an appointment on";
        String timeMatch = "This time has already been taken by someone else please take another time. ";

        for (int i = 0; i < appList.size(); i++) {
            if (appList.get(i).getCpr().equals(cpr)) {
                model.addAttribute("cprError", cprError);
                currentAppointment = appList.get(i).getLocalDateTime().toString();
                //currentAppointment = appList.get(i).getDay()+"-"+appList.get(i).getMonth()+"-"+appList.get(i).getYear()+" at "+appList.get(i).getHour()+":"+ appList.get(i).getMinute();
                model.addAttribute("bookedAppointment", currentAppointment);
                return "secretary/makeDirectAppointment";

            } else if (appList.get(i).getLocalDateTime().toString().equals(receivedDate) && appList.get(i).getTcID() == tcID) {
                model.addAttribute("timeMatch", timeMatch);
                return "secretary/makeDirectAppointment";
            } else {
                continue;
            }
        }
        System.out.println("appointment created.....");
        serviceInterface.addAppointment(cpr, tcID, dateTime);
        return "secretary/secretaryDash";
    }
    @GetMapping("/makeDirectAppointment")
    public String showDirectAppointment(Model model) {
        List<TimeSlots> mytimeSlots = serviceInterface.fetchAllTimeSlots();
        model.addAttribute("timeSlots", mytimeSlots);
        return "secretary/makeDirectAppointment";
    }





}
