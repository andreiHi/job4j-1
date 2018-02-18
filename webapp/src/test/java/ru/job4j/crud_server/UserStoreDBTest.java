package ru.job4j.crud_server;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Answers;
import ru.job4j.user_store.IUserStore;
import ru.job4j.user_store.Role;
import ru.job4j.user_store.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * junior.
 *
 * @author Igor Kovalkov
 * @version 0.1
 * @since 18.02.2018
 */
public class UserStoreDBTest {
    /**
     * Connection mock.
     */
    private static Connection connection = mock(Connection.class);

    /**
     * Test store object.
     */
    private IUserStore store;

    /**
     * PreparedStatement mock.
     */
    private PreparedStatement st = mock(PreparedStatement.class);

    /**
     * actual SQL query string.
     */
    private String query;

    /**
     * actual SQL query argument.
     */
    private final List<Object> arg = new ArrayList<>();

    /** before test class.
     * @throws SQLException SQLException
     */
    @BeforeClass
    public static void beforeClass() throws SQLException {
        org.postgresql.Driver.deregister();
        Driver driver = mock(Driver.class);
        when(driver.connect(anyString(), any())).thenReturn(connection);
        DriverManager.registerDriver(driver, () -> { });
    }

    /** before test method.
     * @throws SQLException SQLException
     */
    @Before
    public void before() throws SQLException {
        when(this.st.getParameterMetaData()).thenAnswer(Answers.RETURNS_MOCKS);
        when(connection.prepareStatement(anyString())).thenAnswer(i -> {
            this.query = i.getArgumentAt(0, String.class); return this.st;
        });
        try { //reset reflect injections.
            Field storeField = UserStoreDB.class.getDeclaredField("USER_STORE");
            storeField.setAccessible(true);
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(storeField, storeField.getModifiers() & ~Modifier.FINAL);
            Constructor constructor =  UserStoreDB.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            storeField.set(null, constructor.newInstance());
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        this.store = UserStoreDB.getUserStore();
        doAnswer(i -> { arg.add(i.getArgumentAt(1, Object.class)); return null; })
                .when(this.st).setObject(anyInt(), any(), anyInt());
    }

    /** Test method.
     * @throws SQLException SQLException
     */
    @Test
    public void whenDBInitThenInitQuery() throws SQLException {
        assertEquals(this.query, "CREATE TABLE if not exists Users_store (login VARCHAR(30) PRIMARY KEY UNIQUE, name VARCHAR(50), email VARCHAR(30), created TIMESTAMP)");
        verify(connection, atLeastOnce()).commit();
    }

    /** Test method. */
    @Test(expected = UnsupportedOperationException.class)
    public void whenGetUsersThenUnSupport() {
        this.store.getUsers();
    }

    /** Test method. */
    @Test(expected = UnsupportedOperationException.class)
    public void whenGetUserRoleThenUnSupport() {
        this.store.getUserRole("");
    }

    /** Test method. */
    @Test(expected = UnsupportedOperationException.class)
    public void whenSetUserRoleThenUnSupport() {
        this.store.setUserRole("", Role.DefaultUser);
    }

    /** Test method. */
    @Test(expected = UnsupportedOperationException.class)
    public void whenIteratorThenReturnIteratorUserStore() {
        this.store.iterator();
    }

    /** Test method. */
    @Test
    public void whenAddUserThenCorrectQuery() {
        this.store.addUser("log", "name", "email");
        assertEquals(this.query, "INSERT  INTO  users_store VALUES (?, ?, ?, ?)");
        this.arg.remove(3);
        assertEquals(new Object[] {"log", "name", "email"}, this.arg.toArray());
    }

    /** Test method. */
    @Test
    public void whenDeleteUserThenCorrectQuery() {
        this.store.deleteUser("DeleteLogin");
        assertEquals(this.query, "DELETE FROM users_store WHERE login = ?");
        assertEquals(new Object[] {"DeleteLogin"}, this.arg.toArray());
    }

    /** Test method. */
    @Test
    public void whenUpdateUserThenCorrectQuery() {
        this.store.updateUser("log", "name", "email");
        assertEquals(this.query, "UPDATE users_store SET name = ?, email =  ? WHERE login = ?");
        assertEquals(new Object[] {"log", "name", "email"}, this.arg.toArray());
    }

    /** Test method. */
    @Test
    public void whenGetUserThenReturnUser() {
        this.store.updateUser("log", "name", "email");
        assertEquals(this.query, "UPDATE users_store SET name = ?, email =  ? WHERE login = ?");
        assertEquals(new Object[] {"log", "name", "email"}, this.arg.toArray());
    }

    /** Test method.
     * @throws SQLException SQLException
     */
    @Test
    public void whenSQLExceptionThenCorrectMethod() throws SQLException {
        doThrow(new SQLException()).when(this.st).execute();
        this.store.deleteUser("");
        verify(connection).rollback();
        reset(connection);
        doThrow(new SQLException()).when(connection).prepareStatement(anyString());
        this.store.getUser("");
        this.store.deleteUser("");
        verify(connection, times(0)).rollback();
        verify(connection, times(0)).rollback();
        reset(connection);
    }

    /** Test method.
     * @throws SQLException SQLException
     */
    @Test
    public void whenGetUserThenCorrectMethod() throws SQLException {
        ResultSet rs = mock(ResultSet.class);
        when(this.st.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getTimestamp(eq("created"))).thenReturn(new Timestamp(347)); //time not compare
        when(rs.getString(eq("login"))).thenReturn("123");
        when(rs.getString(eq("name"))).thenReturn("234");
        when(rs.getString(eq("email"))).thenReturn("367");
        assertEquals(this.store.getUser(""), new User("123", "234", "367", 345));
    }

}