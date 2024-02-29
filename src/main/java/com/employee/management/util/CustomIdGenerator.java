package com.employee.management.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomIdGenerator implements IdentifierGenerator {

    private static final String PREFIX = "S2C";

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        String query = "SELECT MAX(employeeID) FROM Employees";
        Connection connection = null;
        try {
            connection = session.getJdbcConnectionAccess().obtainConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String lastId = rs.getString(1);
                if (lastId != null) {
                    int number = Integer.parseInt(lastId.substring(PREFIX.length())) + 1;
                    return PREFIX + formatNumber(number);
                }
            }
        } catch (SQLException e) {
            throw new HibernateException("Unable to generate ID", e);
        }

        return PREFIX + "01"; // Initial value if no records are present
    }

    private String formatNumber(int number) {
        return String.format("%02d", number);
    }
}
