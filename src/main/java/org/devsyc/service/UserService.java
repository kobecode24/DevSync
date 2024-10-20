    package org.devsyc.service;

    import jakarta.enterprise.context.ApplicationScoped;
    import org.devsyc.domain.entities.User;
    import org.devsyc.repository.UserRepositoryHibernate;

    import java.util.List;

    @ApplicationScoped
    public class UserService {
        private UserRepositoryHibernate userRepository;

        public UserService() {
            userRepository = new UserRepositoryHibernate();
        }

        public UserService(UserRepositoryHibernate userRepository) {
            this.userRepository = userRepository;
        }

        public List<User> getAllUsers() {
            return userRepository.findAll();
        }

        public User getUserById(Long id) {
            return userRepository.findById(id);
        }

        public User createUser(User user) {
            userRepository.save(user);
            return user;
        }

        public void updateUser(User user) {
            userRepository.update(user);
        }

        public void deleteUser(Long id) {
            User user = getUserById(id);
            if (user != null) {
                userRepository.delete(user);
            }
        }

        public User getUserByEmail(String email) {
            return userRepository.findByEmail(email);
        }
    }