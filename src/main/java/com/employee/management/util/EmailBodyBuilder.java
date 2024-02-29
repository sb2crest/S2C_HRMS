package com.employee.management.util;

import org.springframework.stereotype.Component;

@Component
public class EmailBodyBuilder {
    public String getBodyForAccountCreationMail(String name, String empId, String password) {
        StringBuilder body = new StringBuilder();
        body.append("Hi ").append(name).append(",\n\n");
        body.append("Welcome Seabed2Crest Technologies Pvt Ltd").append("\n");
        body.append("Here are your login details:").append("\n");
        body.append("Employee ID: ").append(empId).append("\n");
        body.append("Password: ").append(password).append("\n\n");
        body.append("Please keep this information confidential.").append("\n\n");
        body.append("If you have any questions, feel free to contact us.").append("\n\n");
        body.append("Best regards,\nThe HR Team");

        return body.toString();
    }
}
