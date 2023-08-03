package org.example.model.database.loginWork;

import org.example.data.entity.ELogin;
import org.example.model.database.IDBWork;

public interface IDBLoginWork extends IDBWork {
    ELogin login(String login, String Password) throws Exception;
}
