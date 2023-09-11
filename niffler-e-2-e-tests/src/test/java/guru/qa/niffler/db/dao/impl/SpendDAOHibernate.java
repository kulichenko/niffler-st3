package guru.qa.niffler.db.dao.impl;

import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.dao.SpendDao;
import guru.qa.niffler.db.jpa.EntityManagerFactoryProvider;
import guru.qa.niffler.db.jpa.JpaService;
import guru.qa.niffler.db.model.spend.CategoryEntity;
import guru.qa.niffler.db.model.spend.SpendEntity;

public class SpendDAOHibernate extends JpaService implements SpendDao {
    public SpendDAOHibernate() {
        super(EntityManagerFactoryProvider.INSTANCE.getDataSource(ServiceDB.SPEND).createEntityManager());
    }

    @Override
    public int createCategory(CategoryEntity category) {
        persist(category);
        return 0;
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        remove(category);
    }

    @Override
    public int createSpend(SpendEntity spend) {
        persist(spend);
        return 0;
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        remove(spend);
    }

    @Override
    public SpendEntity getSpendByCategory(CategoryEntity category) {
        SpendEntity spend = null;
        try {
            return em.createQuery("SELECT s FROM SpendEntity s WHERE s.category=:category", SpendEntity.class)
                    .setParameter("category", category)
                    .getSingleResult();
        } catch (Exception e) {
            return spend;
        }
    }
}
