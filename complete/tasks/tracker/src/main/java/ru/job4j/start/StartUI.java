package ru.job4j.start;

import ru.job4j.tracker.Tracker;

/**
 * junior.
 *
 * @author Igor Kovalkov
 * @version 0.1
 * @since 25.05.2017
 */
public class StartUI {
    /**
     * Ввод-вывод.
     */
    private Input input;

    /**
     * Трэкер заявок.
     */
    private Tracker tracker;

    /**
     * Меню.
     */
    private Menu menu;

    /**
     * @param tracker трэкер
     * @param input интерфейс пользователя
     */
    StartUI(Tracker tracker, Input input) {
        this.input = input;
        this.tracker = tracker;
        this.menu = new Menu();
    }

    /**
     * Application body.
     */
    void run() {
        TrackerAction action = null;
        do {
            this.input.println(this.menu.toString());
            try {
                action = this.menu.selectAction(input);
                action.execute(this.input, this.tracker);
            } catch (MenuOutException ex) {
                this.input.println(ex.getMessage());
            } catch (Tracker.ErrorValue errorValue) {
                this.input.println("Ошибка ввода данных");
            } catch (Tracker.NotFound notFound) {
                this.input.println("Заявка не найдена.");
            }
        } while (!(action instanceof Menu.Exit));
    }
}
