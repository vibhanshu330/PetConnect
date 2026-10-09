package com.petconnect.dao;

import com.petconnect.model.Pet;
import com.petconnect.util.DBConnection;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PetDAOUpdateStatusTest {

    @Test
    void updateStatusRequiresPendingStatusAndReturnsFalseWhenNoRowsChange() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(org.mockito.ArgumentMatchers.contains("status = 'PENDING'")))
                .thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(0);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
            db.when(DBConnection::getConnection).thenReturn(connection);

            boolean updated = new PetDAO().updateStatus(999999, Pet.Status.AVAILABLE);

            assertFalse(updated);
            verify(connection).prepareStatement(org.mockito.ArgumentMatchers.contains("status = 'PENDING'"));
            verify(statement).setString(1, "AVAILABLE");
            verify(statement).setInt(2, 999999);
            verify(statement).executeUpdate();
            verify(statement).close();
            verify(connection).close();
        }
    }

    @Test
    void updateStatusReturnsTrueWhenOnePendingRowChanges() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(org.mockito.ArgumentMatchers.contains("status = 'PENDING'")))
                .thenReturn(statement);
        when(statement.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBConnection> db = mockStatic(DBConnection.class)) {
            db.when(DBConnection::getConnection).thenReturn(connection);
            assertTrue(new PetDAO().updateStatus(42, Pet.Status.REJECTED));
        }
    }
}
