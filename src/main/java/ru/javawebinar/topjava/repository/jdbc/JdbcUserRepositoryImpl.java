package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiFunction;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepositoryImpl implements UserRepository {

    private static final RowMapper<User> ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            rs.getInt("calories_per_day"),
            rs.getBoolean("enabled"),
            rs.getDate("registered"),
            Collections.singleton(Role.valueOf(rs.getString("role"))));

    private static final BiFunction<User, User, User> USER_MERGER = (u1, u2) -> {
        Set<Role> roles = EnumSet.copyOf(u1.getRoles());
        roles.addAll(u2.getRoles());
        return new User(u1.getId(),
                u1.getName(),
                u1.getEmail(),
                u1.getPassword(),
                u1.getCaloriesPerDay(),
                u1.isEnabled(),
                u1.getRegistered(),
                roles);
    };

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else {
            if (namedParameterJdbcTemplate.update(
                    "UPDATE users SET name=:name, email=:email, password=:password, " +
                            "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id", parameterSource) == 0) {
                return null;
            } else {
                jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
            }
        }
        final List<Role> roles = new ArrayList<>(user.getRoles());
        jdbcTemplate.batchUpdate("INSERT INTO user_roles(user_id, role) VALUES(?,?)", new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, user.getId());
                ps.setString(2, roles.get(i).toString());
            }

            @Override
            public int getBatchSize() {
                return roles.size();
            }
        });
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("" +
                "SELECT * " +
                "FROM users LEFT JOIN user_roles on users.id = user_roles.user_id " +
                "WHERE id=?", ROW_MAPPER, id);
        return DataAccessUtils.singleResult(combineRoles(users));
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER_USER, email);
        List<User> users = jdbcTemplate.query("" +
                "SELECT * " +
                "FROM users LEFT JOIN user_roles ON users.id = user_roles.user_id " +
                "WHERE email=?", ROW_MAPPER, email);
        return DataAccessUtils.singleResult(combineRoles(users));
    }

    @Override
    public List<User> getAll() {
        List<User> users = jdbcTemplate.query("" +
                "SELECT * " +
                "FROM users left join user_roles ON users.id = user_roles.user_id " +
                "ORDER BY name, email", ROW_MAPPER);
        return new ArrayList<>(combineRoles(users));
    }

    private Collection<User> combineRoles(List<User> users) {
        return users.stream().collect(LinkedHashMap<Integer, User>::new,
                (map, u) -> map.merge(u.getId(), u, USER_MERGER),
                (m1, m2) -> m2.forEach((userId, u3) -> m1.merge(userId, u3, USER_MERGER)))
                .values();
    }
}
