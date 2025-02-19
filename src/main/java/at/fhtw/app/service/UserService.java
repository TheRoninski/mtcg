package at.fhtw.app.service;

import at.fhtw.app.database.UnitOfWork;
import at.fhtw.app.model.request.CreateUserRequest;
import at.fhtw.app.repository.UserRepository;

public class UserService {

    public boolean createUser(CreateUserRequest createUserRequest) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try (unitOfWork) {
            UserRepository userRepository = new UserRepository(unitOfWork);
            userRepository.createUser(createUserRequest);
            unitOfWork.commitTransaction();
            return true;
        } catch (Exception e) {
            unitOfWork.rollbackTransaction();
            return false;
        }
    }
}
