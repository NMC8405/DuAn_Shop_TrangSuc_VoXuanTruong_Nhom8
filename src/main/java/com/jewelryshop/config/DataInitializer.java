package com.jewelryshop.config;

import com.jewelryshop.entity.User;
import com.jewelryshop.enums.Role;
import com.jewelryshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Khoi tao du lieu mac dinh khi ung dung chay lan dau.
 * Tu dong tao tai khoan Admin va User mau neu chua co trong database.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Tao tai khoan Admin neu chua co
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@jewelryshop.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Quản trị viên")
                    .phone("0901234567")
                    .role(Role.ADMIN)
                    .active(true)
                    .build();
            userRepository.save(admin);
            System.out.println("✅ Đã tạo tài khoản Admin: admin / admin123");
        }

        // Tao tai khoan User mau neu chua co
        if (userRepository.findByUsername("user1").isEmpty()) {
            User user1 = User.builder()
                    .username("user1")
                    .email("user1@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .fullName("Nguyễn Văn A")
                    .phone("0912345678")
                    .address("123 Nguyễn Huệ, Q1, TP.HCM")
                    .role(Role.USER)
                    .active(true)
                    .build();
            userRepository.save(user1);
            System.out.println("✅ Đã tạo tài khoản User: user1 / user123");
        }

        if (userRepository.findByUsername("user2").isEmpty()) {
            User user2 = User.builder()
                    .username("user2")
                    .email("user2@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .fullName("Trần Thị B")
                    .phone("0987654321")
                    .address("456 Lê Lợi, Q3, TP.HCM")
                    .role(Role.USER)
                    .active(true)
                    .build();
            userRepository.save(user2);
            System.out.println("✅ Đã tạo tài khoản User: user2 / user123");
        }
    }
}
