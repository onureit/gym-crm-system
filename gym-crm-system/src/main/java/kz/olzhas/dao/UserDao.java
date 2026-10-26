package kz.olzhas.dao;

public interface UserDao {
    boolean existsByUsername(String username);
}
