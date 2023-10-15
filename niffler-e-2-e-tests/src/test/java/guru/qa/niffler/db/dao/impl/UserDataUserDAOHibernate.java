package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.UserDataUserDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;

public class UserDataUserDAOHibernate extends JpaService implements UserDataUserDAO {
    public UserDataUserDAOHibernate() {
        /*
            из лекции 5
            Обратились к синглтону (EntityManagerFactoryProvider). Вызвали у него getDataSource(ServiceDB.AUTH) который
            нам вернул наш декоратор
                    new ThreadLocalEntityManagerFactory(
                            Persistence.createEntityManagerFactory("niffler-st3", props)
                    );
                    и у этого декоратора вызвали метод createEntityManagerFactory() который внутри положит
                    переменную ThreadLocal и таким образом мы уверены, что сколько бы у нас не было потоков
                    в которых будет AuthUserDAOHibernate у каждого из этих потоков будет свой EntityManager,
                    потому что это обеспечивается методом createEntityManager() нашего декоратора
         */
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.USERDATA).createEntityManager());
    }

    @Override
    public int createUserInUserData(UserDataUserEntity user) {
        persist(user);
        return 0;
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        UserDataUserEntity
                user = em.createQuery("SELECT u FROM UserDataUserEntity u WHERE u.username=:username", UserDataUserEntity.class)
                .setParameter("username", username)
                .getSingleResult();
        deleteUserFromUserData(user);
    }

    @Override
    public void deleteUserFromUserData(UserDataUserEntity user) {
        remove(user);
    }

    @Override
    public UserDataUserEntity getUserInUserDataByUsername(String username) {
        return null;
    }
}
