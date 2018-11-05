package ru.javawebinar.topjava.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

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
