package com.employee.management.util;

import org.springframework.stereotype.Component;

@Component
public class EmailBodyBuilder {
    public String getBodyForAccountCreationMail(String name, String empId, String password) {
        return "Hi " + name + ",\n\n" +
                "Welcome Seabed2Crest Technologies Pvt Ltd" + "\n" +
                "Here are your login details:" + "\n" +
                "Employee ID: " + empId + "\n" +
                "Password: " + password + "\n\n" +
                "Please keep this information confidential." + "\n\n" +
                "If you have any questions, feel free to contact us." + "\n\n" +
                "Best regards,\nThe HR Team";
    }

    public String getBodyForHikeLetter(String name){
        return "Dear "+name+",\n" +
                "\n" +
                "Congratulations on your Salary Hike. We wanted to extend our heartfelt congratulations on your Salary Hike! Your dedication, perseverance, and adventurous spirit have not only taken you to new heights \n" +
                "in your personal journey but also in your professional career. Here's to celebrating your achievements and looking forward to even greater successes ahead! \n" +
                "\n\n" +
                "Thanks and Regards,\n" +
                "Seabed2crest Technologies Pvt Ltd.";
    }
}
