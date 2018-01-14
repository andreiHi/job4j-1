package ru.job4j.interface_servlet;

import ru.job4j.crud_server.User;

import java.util.List;

/**
 * junior.
 *
 * @author Igor Kovalkov
 * @version 0.1
 * @since 03.01.2018
 */
public class UI {
    /**
     * @param title title
     * @param body html - body
     * @return html page
     */
    public String getHTML(String title, String body) {
        final String html = "<!DOCTYPE html><html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\">"
                + "<title>%s</title></head>"
                + "<body>%s</body></html>";
        return String.format(html, title, body);
    }

    /**
     * @param create create
     * @param delete delete
     * @param update update
     * @param url url
     * @return table
     */
    public String getUserTable(String create, String delete, String update, String url) {
        List<User> list = UserStore.getInstance().getUsers();
        String del = String.format("<input type=\"submit\" formaction = \"%s/delete\" value=\"%s\">", url, delete);
        String upd = String.format("<input type=\"submit\" formaction = \"%s/update\" value=\"%s\">", url, update);
        StringBuffer table = new StringBuffer("<table border = 1px>");
        for (int i = 0; i <= list.size(); i++) {
            table.append("<tr><form method = \"POST\">");
            if (i == 0) {
                table.append("<td></td><td>login</td><td>name</td><td>email</td><td></td><td></td>");
            } else {
                User user = list.get(i - 1);
                table.append(String.format("<td>%s</td>"
                                + "<td>%s</td>"
                                + "<input type=\"hidden\" name=\"login\" value=\"%s\">"
                                + "<td><input type=\"text\" name=\"name\" value=\"%s\"></td>"
                                + "<td><input type=\"text\" name=\"email\" value=\"%s\"></td>"
                                + "<td>%s</td><td>%s</td>",
                        String.valueOf(i), user.getLogin(), user.getLogin(), user.getName(), user.getEmail(), upd, del));
            }
            table.append("</form></tr>");

        }
        table.append("</table>");
        table.append(getForm(create, url));
        return table.toString();
    }

    /**
     * @param create value button "create"
     * @param url target url
     * @return form
     */
    public String getForm(String create, String url) {
        return String.format(" <form method = \"POST\">"
                + "<input type=\"text\" name=\"login\" value=\"login\">"
                + "<input type=\"text\" name=\"name\" value=\"name\">"
                + "<input type=\"text\" name=\"email\" value=\"email\">"
                + "<p><input type=\"submit\" formaction = \"%s/create\" value=\"%s\"></p>\n"
                + "</form>", url, create);
    }
}