package com.company;

import com.company.Models.Group;
import com.company.Models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private Connection connection;
    private int count = 1;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public UserDao() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/JEDZ22", "postgres", "12345");
        maybeCreateGroupsTable();
        maybeCreateUsersTable();
        maybeCreateUsersGroupsTable();
    }

    private void maybeCreateUsersTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS groups (\n" +
                    "_id uuid PRIMARY KEY,\n" +
                    "name varchar(100)\n" +
                    ");");
        }
    }

    private void maybeCreateUsersGroupsTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS groups_users (\n" +
                    "count int PRIMARY KEY,\n" +
                    "user_id uuid ,\n" +
                    "group_id uuid \n" +
                    ");");
        }
    }

    private void maybeCreateGroupsTable() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS users (\n" +
                    "_id uuid PRIMARY KEY,\n" +
                    "name varchar(100),\n" +
                    "age int\n" +
                    ");");
        }
    }

    public void clean() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            int count = statement.executeUpdate("DELETE FROM groups;");
            System.out.println("Deleted " + count + " rows from table groups");
        }

        try (Statement statement = connection.createStatement()) {
            int count = statement.executeUpdate("DELETE FROM users;");
            System.out.println("Deleted " + count + " rows from table users");
        }
        try (Statement statement = connection.createStatement()) {
            int count = statement.executeUpdate("DELETE FROM groups_users;");
            System.out.println("Deleted " + count + " rows from table groups_users");
        }
    }

    public void insetGroup(Group group) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String request = String.format("INSERT INTO groups VALUES ('%s', '%s');", group.getId(), group.getName());
            statement.execute(request);
            for (User user : group.getUsers()) {
                insertUser(user);
                insertUserGroups(user, group.getId());
            }
        }
    }

    private void insertUserGroups(User user, String groupId) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String request = String.format("INSERT INTO groups_users VALUES ('%s', '%s', '%s');",count, user.getId(), groupId);
            statement.execute(request);
            count++;
        }
    }

    private void insertUser(User user) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String request = String.format("SELECT * FROM users WHERE _id = '%s';", user.getId());
            ResultSet resultSet = statement.executeQuery(request);
            if (!resultSet.next()) {
                String request1 = String.format("INSERT INTO users VALUES ('%s', '%s', '%d');", user.getId(), user.getName(), user.getAge());
                statement.execute(request1);
            }
        }
    }
    public List<User> getUsersByGroupName(String groupName) throws SQLException{
        List<User> users=new ArrayList<>();
        try (Statement statement=connection.createStatement()){
            String request = String.format("SELECT users._id, users.name,users.age " +
                    "FROM users, groups, groups_users WHERE groups.name = '%s' " +
                    "AND groups._id = groups_users.group_id " +
                    "AND groups_users.user_id = users._id;", groupName);
            ResultSet resultSet = statement.executeQuery(request);
            while (resultSet.next()) {
                String id = resultSet.getString("_id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                users.add(new User(id, name, age));
            }
        }
        return users;
    }
    public List<Group> getGroupsByUserName(String userName) throws SQLException{
        List<Group> groups=new ArrayList<>();
        try (Statement statement=connection.createStatement()){
            String request = String.format("SELECT groups._id, groups.name " +
                    "FROM users, groups, groups_users WHERE users.name = '%s' " +
                    "AND users._id = groups_users.user_id " +
                    "AND groups_users.group_id = groups._id;", userName);
            ResultSet resultSet = statement.executeQuery(request);
            while (resultSet.next()) {
                String id = resultSet.getString("_id");
                String name = resultSet.getString("name");
                groups.add(new Group(id,name));
            }
        }
        return groups;
    }
}
