package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.Profiles;

@Repository
@Profile(Profiles.POSTGRES_DB)
public class PostgresJdbcMealRepositoryImpl extends AbstractJdbcMealRepositoryImpl {

    @Override
    protected <T> Object getDbSpecificDate(T datetime) {
        return datetime;
    }
}
