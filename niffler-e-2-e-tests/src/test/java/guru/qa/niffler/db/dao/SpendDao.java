package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.spend.CategoryEntity;
import guru.qa.niffler.db.model.spend.SpendEntity;

public interface SpendDao {
    int createCategory(CategoryEntity category);

    void deleteCategory(CategoryEntity category);

    int createSpend(SpendEntity spend);

    void deleteSpend(SpendEntity spend);

    SpendEntity getSpendByCategory(CategoryEntity category);
}
