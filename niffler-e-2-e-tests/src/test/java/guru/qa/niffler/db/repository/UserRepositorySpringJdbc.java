package guru.qa.niffler.db.repository;

import guru.qa.niffler.db.dao.impl.UserDAOSpringJdbc;

public class UserRepositorySpringJdbc extends AbstractUserRepository {
    public UserRepositorySpringJdbc() {
        super(new UserDAOSpringJdbc(), new UserDAOSpringJdbc());
    }
}
