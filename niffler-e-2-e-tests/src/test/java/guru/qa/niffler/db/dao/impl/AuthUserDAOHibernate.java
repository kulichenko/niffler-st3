package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.AuthUserDAO;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.auth.AuthUserEntity;

import java.util.UUID;

public class AuthUserDAOHibernate extends JpaService implements AuthUserDAO {
    public AuthUserDAOHibernate() {
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
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.AUTH).createEntityManager());
    }

    @Override
    public UUID createUser(AuthUserEntity user) {
        user.setPassword(pe.encode(user.getPassword()));
        persist(user);
        return user.getId();
    }

    @Override
    public void deleteUserById(AuthUserEntity user) {
        remove(user);
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        return em.createQuery("SELECT u FROM AuthUserEntity u WHERE u.id=:id", AuthUserEntity.class)
                .setParameter("id", userId)
                .getSingleResult();
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        return merge(user);
    }
}
