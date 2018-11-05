package ru.javawebinar.topjava;

import org.springframework.test.context.ActiveProfilesResolver;

//http://stackoverflow.com/questions/23871255/spring-profiles-simple-example-of-activeprofilesresolver
public class ActiveDbProfileResolver implements ActiveProfilesResolver {

    public final static String activeDb = Profiles.getActiveDbProfile();

    @Override
    public String[] resolve(Class<?> aClass) {
        return new String[]{Profiles.getActiveDbProfile()};
    }
}