package com.company;

import com.company.Models.Group;
import com.company.Models.User;


import java.sql.SQLException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        User user = new User(Helper.generateId(), "Alex", 20);
        User user2 = new User(Helper.generateId(), "Jack", 22);
        User user3 = new User(Helper.generateId(),"Xander",24);
        User user4 = new User(Helper.generateId(), "Daryl", 35);
        User user5 = new User(Helper.generateId(), "Clark", 40);
        User user6 = new User(Helper.generateId(),"Ane",30);
        User user7 = new User(Helper.generateId(),"Marya",32);
        Group group = new Group(Helper.generateId(),"JavaCore");
        Group group2 = new Group(Helper.generateId(),"JavaOOP");
        Group group1 = new Group(Helper.generateId(),"JavaMaven");
        group.addUser(user);
        group.addUser(user3);
        group.addUser(user5);
        group.addUser(user7);
        group1.addUser(user2);
        group1.addUser(user3);
        group1.addUser(user4);
        group1.addUser(user7);
        group2.addUser(user6);
        group2.addUser(user);
        group2.addUser(user3);
        group2.addUser(user5);

        try {
            UserDao userDao = new UserDao();
            userDao.clean();
            userDao.insetGroup(group);
            userDao.insetGroup(group1);
            userDao.insetGroup(group2);
            List<User> usersFromGroup = userDao.getUsersByGroupName("JavaOOP");
            System.out.println(usersFromGroup);
            List<Group> groupsByUserName = userDao.getGroupsByUserName("Alex");
            System.out.println(groupsByUserName);
        }catch (SQLException s){
            s.printStackTrace();
        }

    }
}
