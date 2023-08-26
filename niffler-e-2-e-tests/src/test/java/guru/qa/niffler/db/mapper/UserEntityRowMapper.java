package guru.qa.niffler.db.mapper;

import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

public class UserEntityRowMapper implements RowMapper<UserEntity> {
    public static final UserEntityRowMapper instance = new UserEntityRowMapper();

    @Override
    public UserEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        var authorities = new ArrayList<AuthorityEntity>();
        var user = new UserEntity();
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
        return user;
    }
}
