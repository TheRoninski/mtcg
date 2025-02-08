package at.fhtw.app.service;

import at.fhtw.app.database.UnitOfWork;
import at.fhtw.app.model.request.CreateUserRequest;
import at.fhtw.app.repository.UserRepository;

public class UserService {

    public void createUser(CreateUserRequest createUserRequest) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            userRepository.createUser(createUserRequest);
            unitOfWork.commitTransaction();
        } catch (Exception e) {
            unitOfWork.rollbackTransaction();
        }
    }
}
