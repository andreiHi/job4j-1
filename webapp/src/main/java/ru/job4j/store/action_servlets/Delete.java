package ru.job4j.store.action_servlets;

import ru.job4j.store.model.Role;

/**
 * junior.
 *
 * @author Igor Kovalkov
 * @version 0.1
 * @since 14.01.2018
 */
public class Delete extends AbstractActionServlets {

    @Override
    void doAction(String login, String name, String email, Role role) {
        USERS.deleteUser(login);
    }
}