package ru.javawebinar.topjava.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ru.javawebinar.topjava.service.datajpa.DataJpaMealServiceTest;
import ru.javawebinar.topjava.service.datajpa.DataJpaUserServiceTest;
import ru.javawebinar.topjava.service.jdbc.JdbcMealServiceTest;
import ru.javawebinar.topjava.service.jdbc.JdbcUserServiceTest;
import ru.javawebinar.topjava.service.jpa.JpaMealServiceTest;
import ru.javawebinar.topjava.service.jpa.JpaUserServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        DataJpaMealServiceTest.class,
        DataJpaUserServiceTest.class,
        JdbcMealServiceTest.class,
        JdbcUserServiceTest.class,
        JpaMealServiceTest.class,
        JpaUserServiceTest.class
})
public class AllTests {
}
