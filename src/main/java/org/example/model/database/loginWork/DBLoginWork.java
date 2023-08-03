package org.example.model.database.loginWork;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.example.data.entity.ELogin;
import org.example.model.database.DBConstructor;

public class DBLoginWork extends DBConstructor implements IDBLoginWork {

    @Override
    public ELogin login(String login, String password) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from users where login = ? and password = ?", ELogin.class);
            query.setParameter(1, login)
                    .setParameter(2, password);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Неверный логин или пароль");
            }

            ELogin eLogin = (ELogin) query.getResultList().get(query.getFirstResult());
            transaction.commit();

            return eLogin;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }
}
