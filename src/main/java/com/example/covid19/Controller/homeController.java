package com.example.covid19.Controller;

import com.example.covid19.Model.*;
import com.example.covid19.Repository.RepoInterface;
import com.example.covid19.Service.ServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class homeController {
    String TestCenterName;
    String UserName;
    int tcID = 0;
    String cpr1 = "598358948935";
    User currentUser;
    String testStatus;
    String currentAppointment;
    String userAppointment = "";
    int recentTestStatusID = 0;
    int recentVaccinStatusID = 0;
    Date testStatusDate;
    Date vaccinStatusDate;
    int currentTcID = 0;
    String currentTestCenterName = "";
    String appointmentTestCenterName = "";
    String testCenterMessage = "";
    int testStatusID;
    int vaccinNameID;
    int vaccinStatusID;
    String vaccinStatus;


    @Autowired
    ServiceInterface serviceInterface;
    // default page handler

    @GetMapping("/")
    public String showDashboard() throws IOException {
        currentTestCenterName = "";
        currentAppointment = "";
        userAppointment = "";
        String appointmentTestCenterName = "";


        List<Appointment> allAppointments = serviceInterface.fetchAllAppointments();
        Date myDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(myDate);
        String month = strDate.substring(3, 5);
        String day = strDate.substring(0, 2);

        saveLogs(myDate.toString() + "\n" + getIpAddress());

        for (int i = 0; i < allAppointments.size(); i++) {
            String day1 = allAppointments.get(i).getLocalDateTime().toString().substring(8, 10);
            String month1 = allAppointments.get(i).getLocalDateTime().toString().substring(5, 7);


            if ((Integer.parseInt(day1) < Integer.parseInt(day)) && (Integer.parseInt(month1) <= Integer.parseInt(month))) {
                long cpr = Long.parseLong(allAppointments.get(i).getCpr());
                serviceInterface.deleteAppointment(cpr);


            }
        }

        return "home/dashboard";
    }

    @GetMapping("/userByrole")
    public String showUserByrole() {
        return "home/userByrole";
    }

    @GetMapping("/chooseTestCenter")
    public String showChooseTestCenter() {
        return "home/chooseTestCenter";
    }

    @GetMapping("/makeAppointment")
    public String showMakeAppointment(Model model) {
        List<TimeSlots> mytimeSlots = serviceInterface.fetchAllTimeSlots();
        List<TestCenter> myCenters = serviceInterface.fetchTestCenter();
        List<Appointment> allAppointments = serviceInterface.fetchAllAppointments();
        String appointmentDetails = "";


        for (int i = 0; i < allAppointments.size(); i++) {
            if (allAppointments.get(i).getCpr().equals(currentUser.getCpr())) {
                appointmentDetails = "You have an appointment on  " + allAppointments.get(i).getLocalDateTime().toString();
            }
        }

        model.addAttribute("appointmentTestCenterName", appointmentTestCenterName);
        model.addAttribute("allAppointments", allAppointments);
        model.addAttribute("appointmentDetails", appointmentDetails);
        model.addAttribute("timeSlots", mytimeSlots);
        model.addAttribute("myCenters", myCenters);
        model.addAttribute("mytestcenter", TestCenterName);
        return "home/makeAppointment";
    }

    // corona pass handler
    @GetMapping("/getCoronaPass")
    public String showCoronaPass(Model model) {
        Date currentDate = null;
        List<User> userList = serviceInterface.fetchAllUser();
        List<TestStatusDate> testStatusDates = serviceInterface.fetchAllTestStatusDate();
        for (int i = 0; i < testStatusDates.size(); i++) {
            if (currentUser.getCpr().equals(testStatusDates.get(i).getCpr())) {
                currentDate = testStatusDates.get(i).getTestStatusDate();
            }
        }


        if (currentUser.getTsID() == 2) {

            Date today = currentDate;

            Long milsec = today.getTime();

            Date afterTomorrow = new Date(milsec + 86400 * 1000 * 2);
            model.addAttribute("today", currentDate);
            model.addAttribute("afterTomorrow", afterTomorrow);
            model.addAttribute("userList", userList);
            model.addAttribute("myUser", currentUser);
            model.addAttribute("testStatus", testStatus);
            return "home/getCoronaPass";
        } else if (currentUser.getTsID() == 1) {
            model.addAttribute("haMessage", "Message from health authorities: \nYour status is positive please get isolated and contact your own doctor.");
            return "home/coronaAlert";
        } else {
            model.addAttribute("haMessage", "Message from health authorities: \nYou are not tested yet please make a test to get corona pass");
            return "home/coronaAlert";
        }

    }


    @GetMapping("/signUp")
    public String showSignUp() {
        return "home/signUp";
    }

    @GetMapping("/gdpr")
    public String showGdpr() {
        return "home/gdpr";
    }



    @GetMapping("/login")
    public String showlogin() {
        return "home/login";
    }

    // log in
    @PostMapping("/user")
    public ModelAndView login(@ModelAttribute LoginCheck loginCheck, Model model) {

        if ((loginCheck.getUserName().equals("admin")) && (loginCheck.getPassword().equals("admin123"))) {
            ModelAndView modelAndView = new ModelAndView("administrator/administratorDash");

            return modelAndView;

        } else if ((loginCheck.getUserName().equals("secretary")) && (loginCheck.getPassword().equals("secretary123"))) {
            ModelAndView modelAndView = new ModelAndView("secretary/secretaryDash");

            return modelAndView;
        } else if ((loginCheck.getUserName().equals("secretaryI")) && (loginCheck.getPassword().equals("si123"))) {
            ModelAndView modelAndView = new ModelAndView("infectionCenter/secretaryIdash");

            return modelAndView;
        } else if (logIncheck1(loginCheck.getUserName(), (loginCheck.getPassword()))) {
            //System.out.println(logIncheck1(loginCheck.getUserName(), (loginCheck.getPassword())));
            ModelAndView modelAndView = new ModelAndView("home/chooseTestCenter");
            UserName = loginCheck.getUserName();
            String vennueName = TestCenterName;


            model.addAttribute("UserName", UserName);
            model.addAttribute("vennueName", vennueName);
            model.addAttribute("currentAppointment", currentAppointment);
            return modelAndView;
        } else if ((loginCheck.getUserName().equals("adminI")) && (loginCheck.getPassword().equals("ai123"))) {
            ModelAndView modelAndView = new ModelAndView("infectionCenter/secretaryIdash");
            return modelAndView;
        } else if ((loginCheck.getUserName().equals("secretaryV")) && (loginCheck.getPassword().equals("sv123"))) {
            ModelAndView modelAndView = new ModelAndView("vaccinCenter/secretaryVdash");
            return modelAndView;
        } else if ((loginCheck.getUserName().equals("adminV")) && (loginCheck.getPassword().equals("av123"))) {
            ModelAndView modelAndView = new ModelAndView("vaccinCenter/secretaryVdash");
            return modelAndView;
        } else {
            ModelAndView modelAndView1 = new ModelAndView("home/dashboard");

            return modelAndView1;
        }
    }

    // creates a user
    @PostMapping("/createList")
    public String create(@ModelAttribute User user, Model model) {
        String error = "";

        List<User> userList = serviceInterface.fetchAllUser();
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getCpr().equals(user.getCpr())) {
                error = "This cpr is already in use, please veryfi cpr number if it is correct you have already signedUp kindly go to login page.";
                model.addAttribute("error", error);
                return "home/signup";
            }

            if (userList.get(i).getName().equals(user.getName())) {
                error = "This name is already in use, please add any charecter after your name";
                model.addAttribute("error", error);
                return "home/signup";
            }
            if (userList.get(i).getPassword().equals(user.getPassword())) {
                error = "This password  is not approved by our system, please provide diffrent password";
                model.addAttribute("error", error);
                return "home/signup";
            }
            if (user.getCpr().length() != 10) {
                error = "please provide valid cpr number without '-' 0101804949 (not valid -->010180-4949)";
                model.addAttribute("error", error);
                return "home/signup";
            }
            if (userList.get(i).getUserName().equals((user.getUserName()))) {
                error = "This username is  already taken by someone else, please enter different username";
                model.addAttribute("error", error);
                return "home/signup";
            }
            if (user.getUserName().length() > 30) {
                error = "you have choosen a very long name please give a shorter name arround 30 character";
            }
        }
        this.serviceInterface.addUser(user);
        Date date = new Date();
        this.serviceInterface.addDates(user.getCpr(), user.getTsID(), date, user.getVsID(), date);
        this.serviceInterface.addVmessage(user.getCpr(),"You do not have any message from vaccin center yet");
        this.serviceInterface.addImessage(user.getCpr(),"You do not have any message from Infection detection center yet");
        return "home/dashboard";
    }






    // validates and records testcenter name
    @PostMapping("/testCenterName")
    public String testme(@ModelAttribute TestCenter testCenter, Model model) {
        TestCenterName = testCenter.getCname();

        if (TestCenterName.equals("CPH-Center")) {
            tcID = 1;
            serviceInterface.updateTestCenterId(currentUser.getCpr(), tcID);
        } else if (TestCenterName.equals("CPH-North")) {
            tcID = 2;
            serviceInterface.updateTestCenterId(currentUser.getCpr(), tcID);
        } else if (TestCenterName.equals("CPH-South")) {
            tcID = 3;
            serviceInterface.updateTestCenterId(currentUser.getCpr(), tcID);
        } else if (TestCenterName.equals("CPH-East")) {
            tcID = 4;
            serviceInterface.updateTestCenterId(currentUser.getCpr(), tcID);
        } else {
            tcID = 5;
            serviceInterface.updateTestCenterId(currentUser.getCpr(), tcID);
        }

        List<User> allUsers = serviceInterface.fetchAllUser();
        List<Appointment> allAppointments = serviceInterface.fetchAllAppointments();
        List<Imessage> imessages = serviceInterface.fetchAllImessage();
        List<Vmessage> vmessages = serviceInterface.fetchAllVmessage();

        int aTcID = 0;
        String recentCpr = "";
        String vaccinCenterMessage = "";
        String idcMessage = "";
        String vMessage = "";
        for (int i = 0; i < allAppointments.size(); i++) {
            if (allAppointments.get(i).getCpr().equals(currentUser.getCpr())) {

                aTcID = allAppointments.get(i).getTcID();
                switch (aTcID) {
                    case 1 -> appointmentTestCenterName = "CPH-Center";
                    case 2 -> appointmentTestCenterName = "CPH-North";
                    case 3 -> appointmentTestCenterName = "CPH-South";
                    case 4 -> appointmentTestCenterName = "CPH-East";
                    case 5 -> appointmentTestCenterName = "CPH-West";
                    default -> appointmentTestCenterName = "not available please contact secretary office";
                }

                userAppointment = allAppointments.get(i).getLocalDateTime().toString() + "Test center";
            }
        }

        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getCpr().equals(cpr1)) {
                testStatusID = allUsers.get(i).getTsID();
                vaccinStatusID = allUsers.get(i).getVsID();
                vaccinNameID = allUsers.get(i).getVacNameID();
                if(testStatusID ==0){
                    testCenterMessage = "--->   Not tested";

                } else if(testStatusID ==1){
                    testCenterMessage = "--->   Positive.";

                }else{
                    testCenterMessage = "--->   Negative ";

                }

                if(vaccinStatusID ==0){
                    vaccinStatus = "--->    Not vaccinated";

                } else if(vaccinStatusID ==1){
                    vaccinStatus = " --->   Vaccinated first dose";

                }else if (vaccinStatusID == 2){
                    vaccinStatus = "--->    Vaccinated both doses.";

                }
                else{
                    vaccinStatus = "--->    Not vaccinated";
                }


            }
        }




            vaccinCenterMessage = switch (vaccinNameID) {
                case 0 -> "You are not called for vaccination";
                case 1 -> "you are vaccinated 1st time";
                default -> "Your vaccination is completed";
            };

            for (Imessage ims : imessages) {
                if (ims.getCpr().equals(cpr1)) {
                    idcMessage = ims.getMessageI();

                    break;
                } else {
                    continue;
                }
            }
            for (Vmessage vms : vmessages) {
                if (vms.getCpr().equals(cpr1)) {
                    vMessage = vms.getMessageV();

                    break;
                } else {
                    continue;
                }
            }

        model.addAttribute("appointmentTestCenterName", appointmentTestCenterName);
        model.addAttribute("testCenterMessage", testCenterMessage);
        model.addAttribute("vaccinCenterMessage", vMessage);
        model.addAttribute("userAppointment", userAppointment);
        model.addAttribute("TestCenterName", TestCenterName);
        model.addAttribute("currentTestCenterName", currentTestCenterName);
        model.addAttribute("currentUser", currentUser.getUserName());
        model.addAttribute("idcMessage", idcMessage);
        model.addAttribute("vaccinStatus", vaccinStatus);

        return "home/index";
    }

    // make an appointment
    @GetMapping("/takeDate")
    public String getDateTime(@ModelAttribute DateAndTime dt, Model model) {
        System.out.println(dt.toString());

        String cpr = dt.getCpr();
        Date dateM = dt.getMydate();
        String timeM = dt.getTime();


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
            if (appList.get(i).getCpr().equals(cpr1)) {
                model.addAttribute("cprError", cprError);

                currentAppointment = " " + appList.get(i).getLocalDateTime().toString() + " ";
                model.addAttribute("bookedAppointment", currentAppointment);

                model.addAttribute("appointmentTestCenterName", appointmentTestCenterName);
                return "home/makeAppointment";

            } else if (appList.get(i).getLocalDateTime().toString().equals(receivedDate) && appList.get(i).getTcID() == tcID) {
                model.addAttribute("timeMatch", timeMatch);
                return "home/makeAppointment";
            } else {
                continue;
            }
        }

        serviceInterface.addAppointment(cpr1, tcID, dateTime);
        return "home/chooseTestCenter";
    }

    // appointment by admin or secretary


    // Login check
    public boolean logIncheck1(String userName, String password) {
        boolean correct = false;
        List<User> userList = serviceInterface.fetchAllUser();
        for (int i = 0; i < userList.size(); i++) {
            if ((userList.get(i).getUserName().equals(userName)) && (userList.get(i).getPassword().equals(password))) {
                correct = true;
                cpr1 = userList.get(i).getCpr();
                currentUser = userList.get(i);
                if (currentUser.getTsID() == 0) {
                    testStatus = "Not tested";
                }
                if (currentUser.getTsID() == 1) {
                    testStatus = "Positive";
                }
                if (currentUser.getTsID() == 2) {
                    testStatus = "Negative";
                }

                break;
            } else {
                correct = false;
            }
        }
        return correct;
    }



    @GetMapping("/delete/{id}")
    public String deleteMe(@PathVariable(value = "id") int id) {
        List<Appointment> appList = serviceInterface.fetchAllAppointments();
        List<Imessage> imessageList = serviceInterface.fetchAllImessage();
        List<Vmessage> vmessageList = serviceInterface.fetchAllVmessage();
        List<TestStatusDate> testStatusDateList = serviceInterface.fetchAllTestStatusDate();
        User user = serviceInterface.fetchSingleUser(id);
        long cprA = Long.parseLong(user.getCpr());

        for (int i = 0; i < appList.size(); i++) {

            if (appList.get(i).getCpr().equals(user.getCpr())) {

                serviceInterface.deleteAppointment(cprA);
            }
        }
        for (int i = 0; i < imessageList.size(); i++) {

            if (imessageList.get(i).getCpr().equals(user.getCpr())) {

                serviceInterface.deleteMessage(user.getCpr());
            }
        }
        for (int i = 0; i < vmessageList.size(); i++) {

            if (vmessageList.get(i).getCpr().equals(user.getCpr())) {

                serviceInterface.deleteVmessage(user.getCpr());
            }
        }
        for (int i = 0; i < testStatusDateList.size(); i++) {

            if (testStatusDateList.get(i).getCpr().equals(user.getCpr())) {

                serviceInterface.deleteTestStatusDate(user.getCpr());
            }
        }


        serviceInterface.deleteUser(id);

        return ("redirect:/deleteUser");
    }


    @GetMapping("/deleteUser")
    public String showDeleteUser(Model model) {
        List<User> userList = serviceInterface.fetchAllUser();
        model.addAttribute("userList", userList);
        return "administrator/deleteUser";
    }

    @GetMapping("/UpdateUserHome")
    public String showUserUpdateHome(Model model) {
        List<User> userList = serviceInterface.fetchAllUser();
        model.addAttribute("userList", userList);
        return "secretary/UpdateUserHome";
    }


    @GetMapping("/UpdateUserHome1")
    public String showUserUpdateHome1(Model model) {
        List<User> userList = serviceInterface.fetchAllUser();
        model.addAttribute("userList", userList);
        return "administrator/updateUserAdmin";
    }

    @GetMapping("/updateUserAdmin")
    public String showUpdateUserAdmin(Model model) {
        List<User> userList = serviceInterface.fetchAllUser();
        model.addAttribute("userList", userList);
        return "administrator/updateUserAdmin";
    }


    @GetMapping("/updateHome/{id}")
    public String showFormForUpdate(@PathVariable(value = "id") int id, Model md) {
        User user = serviceInterface.fetchSingleUser(id);
        recentTestStatusID = user.getTsID();
        recentVaccinStatusID = user.getVsID();
        md.addAttribute("user", user);
        return "secretary/userUpdatePage";
    }

    @GetMapping("/updateHome1/{id}")
    public String showFormForUpdate1(@PathVariable(value = "id") int id, Model md) {
        User user = serviceInterface.fetchSingleUser(id);
        recentTestStatusID = user.getTsID();
        recentVaccinStatusID = user.getVsID();
        md.addAttribute("user", user);
        return "administrator/updateHomeAdmin";


    }


    @PostMapping("/updateHome1")
    public String saveUser1(@ModelAttribute("user") User user) {
        if (user.getTsID() != recentTestStatusID) {
            testStatusDate = new Date();
            this.serviceInterface.updateTestStatusDate(user.getCpr(), user.getTsID(), testStatusDate);
        }
        if (user.getVsID() != recentVaccinStatusID) {
            vaccinStatusDate = new Date();
            this.serviceInterface.updateVaccinStatusDate(user.getCpr(), user.getVsID(), vaccinStatusDate);
        }
        if(user.getTsID()==0){
            testCenterMessage = "You are not tested yet";
        }
        if(user.getTsID()==1){
            testCenterMessage = "Your status is positive please be isolated";
        }
        if(user.getTsID()==0){
            testCenterMessage = "Your status is negative";
        }
        this.serviceInterface.updateUser(user);

        return "redirect:/updateUserAdmin";
    }


    @PostMapping("/updateHome")
    public String saveUser(@ModelAttribute("user") User user) {
        if (user.getTsID() != recentTestStatusID) {
            testStatusDate = new Date();
            this.serviceInterface.updateTestStatusDate(user.getCpr(), user.getTsID(), testStatusDate);
        }
        if (user.getVsID() != recentVaccinStatusID) {
            vaccinStatusDate = new Date();
            this.serviceInterface.updateVaccinStatusDate(user.getCpr(), user.getVsID(), vaccinStatusDate);
        }
        this.serviceInterface.updateUser(user);

        return "redirect:/UpdateUserHome";
    }


    @GetMapping("/infectionDash")
    public String showInfectionDash() {
        return "infectionCenter/infectionDash";
    }

    // This method is responsible for user search by name
    @GetMapping("/searchByName")
    public String searchByName(@ModelAttribute User val, Model model) {
        String name = val.getName();
        List<User> searchList = serviceInterface.searchByName(name);
        model.addAttribute("searchList", searchList);
        return "secretary/searchByName";
    }

    // This method is responsible for user search by positive
    @GetMapping("/searchByPositive")
    public String searchByPositive(@ModelAttribute User val, Model model) {
        List<User> positiveList = serviceInterface.fetchAllPositive();
        model.addAttribute("positiveList", positiveList);
        return "secretary/searchByName";
    }

    // This method is responsible for user search by negative
    @GetMapping("/searchByNegative")
    public String searchByNegative(@ModelAttribute User val, Model model) {
        List<User> negativeList = serviceInterface.fetchAllNegative();
        model.addAttribute("negativeList", negativeList);
        return "secretary/searchByName";
    }







    @GetMapping("/searchByNameA")
    public String searchByNameA(@ModelAttribute User val, Model model) {
        String name = val.getName();
        List<User> searchList = serviceInterface.searchByName(name);
        model.addAttribute("searchList", searchList);
        return "administrator/searchByNameA";
    }

    // This method is responsible for user search by positive
    @GetMapping("/searchByPositiveA")
    public String searchByPositiveA(@ModelAttribute User val, Model model) {
        List<User> positiveList = serviceInterface.fetchAllPositive();
        model.addAttribute("positiveList", positiveList);
        return "administrator/searchByNameA";
    }

    // This method is responsible for user search by negative
    @GetMapping("/searchByNegativeA")
    public String searchByNegativeA(@ModelAttribute User val, Model model) {
        List<User> negativeList = serviceInterface.fetchAllNegative();
        model.addAttribute("negativeList", negativeList);
        return "administrator/searchByNameA";
    }
    @GetMapping("/searchByCprA")
    public String searchByCprA(@ModelAttribute User val, Model model) {
        long cprL = Long.parseLong(val.getCpr());
        List<User> searchListCpr = serviceInterface.fetchAllByCpr(cprL);
        model.addAttribute("searchListCpr", searchListCpr);
        return "administrator/searchByNameA";
    }



    @GetMapping("/secretaryDash")
    public String showSecretaryDash() {

        return "secretary/secretaryDash";
    }
    @GetMapping("/administratorDash")
    public String showAdministratorDash() {

        return "administrator/administratorDash";
    }


    @GetMapping("/searchByCpr")
    public String searchByCpr(@ModelAttribute User val, Model model) {
        long cprL = Long.parseLong(val.getCpr());
        List<User> searchListCpr = serviceInterface.fetchAllByCpr(cprL);
        model.addAttribute("searchListCpr", searchListCpr);
        return "secretary/searchByName";
    }

    // write log(date,time and ip of the local device ) into log.txt file
    public void saveLogs(String myLogs) throws IOException {
        try {
            File file = new File("log.txt");
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(myLogs);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/seeAppointments")
    public String showSeeAppointment(Model model) {

        List<Appointment> allMyAppointments = serviceInterface.fetchAllAppointments();
        model.addAttribute("appointmentsList",allMyAppointments);


        return "administrator/seeAppointments";
    }

    @GetMapping("/secretaryVdash")
    public String showSecretaryVdash() {
        return "vaccinCenter/secretaryVdash";
    }

    @GetMapping("/messageToUser")
    public String ShowMessage() {
        return "infectionCenter/messageToUser";
    }

    @GetMapping("/vmessageToUser")
    public String ShowVMessage() {
        return "vaccinCenter/cMessageToUser";
    }

    @GetMapping("/vaccinCenterDash")
    public String ShowCDash() {
        return "vaccinCenter/vaccinCenterDash";
    }

    //this method records the message from Infection detection  center to specific user
    @PostMapping("/messageI")
    public String showMessageToUser(@ModelAttribute Imessage imessage) {
        List<Imessage> imessages = serviceInterface.fetchAllImessage();
        for (Imessage im : imessages) {
            if (imessage.getCpr().equals(im.getCpr())) {
                serviceInterface.deleteMessage(im.getCpr());
                break;
            }
        }
        serviceInterface.addImessage(imessage.getCpr(), imessage.getMessageI());
        return "infectionCenter/messageToUser";
    }

    //this method records the message from vaccin center to specific user
    @PostMapping("/messageV")
    public String showVmessageToUser(@ModelAttribute Vmessage vmessage) {
        List<Vmessage> vmessages = serviceInterface.fetchAllVmessage();
        for (Vmessage vm : vmessages) {
            if (vmessage.getCpr().equals(vm.getCpr())) {
                serviceInterface.deleteVmessage(vm.getCpr());
                break;
            }
        }
        serviceInterface.addVmessage(vmessage.getCpr(), vmessage.getMessageV());
        return "vaccinCenter/cMessageToUser";
    }


    // source of inspiration https://stackoverflow.com/questions/9481865/getting-the-ip-address-of-the-current-machine-using-java

    public String getIpAddress() throws UnknownHostException {
        InetAddress IP = InetAddress.getLocalHost();
        String ip = "Local ip address of user machine := " + IP.getHostAddress();
        return ip;
    }


}
