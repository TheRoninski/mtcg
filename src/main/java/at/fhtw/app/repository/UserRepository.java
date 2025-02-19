package at.fhtw.app.repository;

import at.fhtw.app.database.DataAccessException;
import at.fhtw.app.database.UnitOfWork;
import at.fhtw.app.model.request.CreateUserRequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class UserRepository {

    private final UnitOfWork unitOfWork;

    public UserRepository(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    public void createUser(CreateUserRequest createUserRequest) {
        String sql = "INSERT INTO users (username, password) VALUES (?,?)";

        try (PreparedStatement preparedStatement = this.unitOfWork.prepareStatement(sql)) {
            preparedStatement.setString(1, createUserRequest.getUsername());
            preparedStatement.setString(2, createUserRequest.getPassword());
            int rowCount = preparedStatement.executeUpdate();
            if (rowCount == 0) {
                throw new SQLException("User creation failed");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Insert user not successful", e);
        }
    }

    public boolean validateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement userCheck = this.unitOfWork.prepareStatement(sql)){
            userCheck.setString(1, username);
            ResultSet resultSet = userCheck.executeQuery();
            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                return storedPassword.equals(password);
            }
        } catch (SQLException e){
            throw new DataAccessException("User validation failed", e);
        }
        return false;
    }
}


