package Library.System.repository;

import Library.System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * 根據手機號碼查詢使用者
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * 檢查手機號碼是否已存在
     */
    boolean existsByPhoneNumber(String phoneNumber);
    
    /**
     * 根據手機號碼和密碼雜湊查詢使用者
     */
    @Query("SELECT u FROM User u WHERE u.phoneNumber = :phoneNumber AND u.passwordHash = :passwordHash")
    Optional<User> findByPhoneNumberAndPasswordHash(@Param("phoneNumber") String phoneNumber, 
                                                   @Param("passwordHash") String passwordHash);
} 