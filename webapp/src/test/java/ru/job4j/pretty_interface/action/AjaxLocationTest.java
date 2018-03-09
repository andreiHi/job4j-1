package ru.job4j.pretty_interface.action;

import org.json.simple.JSONObject;
import org.junit.Test;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Test class.
 */
public class AjaxLocationTest {
    /**
     * json request.
     */
    private JSONObject json = new JSONObject();

    /**
     * @param expect expect json answer
     */
    private void test(String expect) {
        AjaxLocation location = new AjaxLocation();
        String actual = location.action(this.json, mock(HttpSession.class)).toJSON();
        assertEquals(expect, actual);
    }

    /**
     * Test method.
     */
    @Test
    public void whenRequestEmptyThenEmptyAnswer() {
        test("{\"data\":[]}");
    }

    /**
     * Test method.
     */
    @Test
    public void whenRequestCountryThenReturnAllCountries() {
        this.json.put("type", "country");
        test("{\"countries\":[\"-------\",\"Russia\",\"UK\",\"USA\"]}");
    }

    /**
     * Test method.
     */
    @Test
    public void whenRequestCitiesThenReturnFromRegion() {
        this.json.put("type", "city");
        this.json.put("country", "Russia");
        test("{\"country\":\"Russia\",\"cities\":[\"Moscow\",\"StPtb\"]}");
    }

    /**
     * Test method.
     */
    @Test
    public void whenRequestCitiesEmptyCountryThenReturnEmtyAnswer() {
        this.json.put("type", "city");
        test("{\"data\":[]}");
    }
}