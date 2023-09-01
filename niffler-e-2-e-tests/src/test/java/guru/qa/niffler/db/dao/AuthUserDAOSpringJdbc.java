package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.mapper.AuthorityEntityRowMapper;
import guru.qa.niffler.db.mapper.UserEntityRowMapper;
import guru.qa.niffler.db.model.auth.Authority;
import guru.qa.niffler.db.model.auth.AuthorityEntity;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.auth.AuthUserEntity;
import guru.qa.niffler.db.model.userdata.UserDataUserEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.UUID;

public class AuthUserDAOSpringJdbc implements AuthUserDAO, UserDataUserDAO {
    private final TransactionTemplate authTtpl;
    private final TransactionTemplate userdataTtpl;
    private final JdbcTemplate authJdbcTemplate;
    private final JdbcTemplate userDataJdbcTemplate;

    public AuthUserDAOSpringJdbc() {
        JdbcTransactionManager authTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH)
        );
        JdbcTransactionManager userdataTm = new JdbcTransactionManager(
                DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA)
        );

        this.authTtpl = new TransactionTemplate(authTm);
        this.userdataTtpl = new TransactionTemplate(userdataTm);
        this.authJdbcTemplate = new JdbcTemplate(authTm.getDataSource());
        this.userDataJdbcTemplate = new JdbcTemplate(userdataTm.getDataSource());
    }

    @Override
    @SuppressWarnings("unchecked")
    public UUID createUser(AuthUserEntity user) {
        return authTtpl.execute(status -> {
            var kh = new GeneratedKeyHolder();
            authJdbcTemplate.update(con -> {
                var ps = con.prepareStatement(
                        "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                                "VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, pe.encode(user.getPassword()));
                ps.setBoolean(3, user.getEnabled());
                ps.setBoolean(4, user.getAccountNonExpired());
                ps.setBoolean(5, user.getAccountNonLocked());
                ps.setBoolean(6, user.getCredentialsNonExpired());
                return ps;
            }, kh);
            UUID userId = (UUID) kh.getKeyList().get(0).get("id");
            authJdbcTemplate.batchUpdate("INSERT INTO authorities (user_id, authority) " +
                            "VALUES (?, ?)", new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setObject(1, userId);
                            ps.setObject(2, Authority.values()[i].name());
                        }

                        @Override
                        public int getBatchSize() {
                            return Authority.values().length;
                        }
                    }
            );
            return userId;
        });
    }

    @Override
    public void deleteUserById(AuthUserEntity user) {
        authJdbcTemplate.update("DELETE FROM authorities WHERE user_id = ? ", user.getId());
        authJdbcTemplate.update("DELETE FROM users WHERE id = ?", user.getId());
    }

    @Override
    public AuthUserEntity getUserById(UUID userId) {
        var user = authJdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                UserEntityRowMapper.instance,
                userId
        );
        if (user != null) {
            List<AuthorityEntity> authorities = authJdbcTemplate.query(
                    "SELECT * FROM authorities WHERE user_id = ? ",
                    AuthorityEntityRowMapper.instance,
                    user.getId()
            );
            user.setAuthorities(authorities);
        }
        return user;
    }

    @Override
    public AuthUserEntity updateUser(AuthUserEntity user) {
        authJdbcTemplate.update(
                "UPDATE users SET " +
                        "username = ?, " +
                        "password = ?, " +
                        "enabled = ?, " +
                        "account_non_expired = ?, " +
                        "account_non_locked = ? , " +
                        "credentials_non_expired = ? " +
                        "WHERE id = ? ",
                user.getUsername(),
                pe.encode(user.getPassword()),
                user.getEnabled(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                user.getId());
        return user;
    }

    @Override
    public int createUserInUserData(UserDataUserEntity user) {
        return userDataJdbcTemplate.update("INSERT INTO users (username, currency) " +
                        "VALUES (?, ?)",
                user.getUsername(),
                CurrencyValues.RUB.name());
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        userDataJdbcTemplate.update("DELETE FROM users WHERE username = ?", username);
    }

    @Override
    public void deleteUserFromUserData(UserDataUserEntity user) {

    }
}
