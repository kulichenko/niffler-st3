package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.DataSourceProvider;
import guru.qa.niffler.db.ServiceDB;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.CurrencyValues;
import guru.qa.niffler.db.model.UserEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class AuthUserDAOJdbc implements AuthUserDAO, UserDataUserDAO {

    private static DataSource authDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.AUTH);
    private static DataSource userdataDs = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.USERDATA);

    @Override
    public int createUser(UserEntity user) {
        int createdRows = 0;
        try (Connection conn = authDs.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);

                 PreparedStatement authorityPs = conn.prepareStatement(
                         "INSERT INTO authorities (user_id, authority) " +
                                 "VALUES (?, ?)")) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, pe.encode(user.getPassword()));
                usersPs.setBoolean(3, user.getEnabled());
                usersPs.setBoolean(4, user.getAccountNonExpired());
                usersPs.setBoolean(5, user.getAccountNonLocked());
                usersPs.setBoolean(6, user.getCredentialsNonExpired());

                createdRows = usersPs.executeUpdate();
                UUID generatedUserId;

                try (ResultSet generatedKeys = usersPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedUserId = UUID.fromString(generatedKeys.getString("id"));
                    } else {
                        throw new IllegalStateException("Can`t obtain id from given ResultSet");
                    }
                }

                for (Authority authority : Authority.values()) {
                    authorityPs.setObject(1, generatedUserId);
                    authorityPs.setString(2, authority.name());
                    authorityPs.addBatch();
                    authorityPs.clearParameters(); //чтобы каждый раз были новые authorities в каждом батче
                }

                authorityPs.executeBatch();
                user.setId(generatedUserId);
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }

    @Override
    public void deleteUserById(UUID userId) {
        try (Connection conn = authDs.getConnection()) {
            conn.setAutoCommit(false);
            try (
                    PreparedStatement authorityPs = conn.prepareStatement(
                            "DELETE FROM authorities WHERE user_id=?");
                    PreparedStatement usersPs = conn.prepareStatement(
                            "DELETE FROM users WHERE id=?")
            ) {

                usersPs.setObject(1, userId);
                authorityPs.setObject(1, userId);
                authorityPs.executeUpdate();
                usersPs.executeUpdate();
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int createUserInUserData(UserEntity user) {
        int createdRows = 0;
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "INSERT INTO users (username, currency) " +
                            "VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

                usersPs.setString(1, user.getUsername());
                usersPs.setString(2, CurrencyValues.RUB.name());

                createdRows = usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return createdRows;
    }

    @Override
    public void deleteUserByUsernameInUserData(String username) {
        try (Connection conn = userdataDs.getConnection()) {
            try (PreparedStatement usersPs = conn.prepareStatement(
                    "DELETE FROM users WHERE username = ?")) {

                usersPs.setString(1, username);
                usersPs.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity getUserFromDBById(UUID userId) {
        var user = new UserEntity();
        try (Connection conn = authDs.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM users AS ut " +
                            "JOIN authorities as ata " +
                            "ON ut.id = ata.user_id WHERE ut.id = ?"
            )) {
                ps.setObject(1, userId);
                ps.execute();
                var resultSet = ps.getResultSet();
                if (resultSet.next()) {
                    var authorities = new ArrayList<AuthorityEntity>();
                    user.setId((UUID) resultSet.getObject("id"));
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
                    user.setEnabled(resultSet.getBoolean("enabled"));
                    user.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                    var authority = new AuthorityEntity();
                    authority.setAuthority(Authority.valueOf(resultSet.getString("authority")));
                    authorities.add(authority);
                    while (resultSet.next()) {
                        var a = new AuthorityEntity();
                        a.setAuthority(Authority.valueOf(resultSet.getString("authority")));
                        authorities.add(a);
                    }
                    user.setAuthorities(authorities);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return user;
    }


    @Override
    public int updateUser(UserEntity user) {
        try (Connection conn = authDs.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE users SET " +
                             "password = ?, " +
                             "enabled = ?, " +
                             "account_non_expired = ?, " +
                             "account_non_locked = ? , " +
                             "credentials_non_expired = ? " +
                             "WHERE id = ? ")) {

            ps.setString(1, pe.encode(user.getPassword()));
            ps.setBoolean(2, user.getEnabled());
            ps.setBoolean(3, user.getAccountNonExpired());
            ps.setBoolean(4, user.getAccountNonLocked());
            ps.setBoolean(5, user.getCredentialsNonExpired());
            ps.setObject(6, user.getId());
            ps.executeUpdate();
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
