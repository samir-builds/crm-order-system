    package com.samir.crm_order_system.dto;

    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotNull;
    import jakarta.validation.constraints.Size;
    import lombok.Data;

    import java.util.List;

    @Data
    public class UserDTO {
        private Long id;

        @NotNull(message = "Username can not be null")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        private String username;

        @Email(message = "Email must be valid")
        private String email;

        @NotNull(message = "Password can not be null")
        @Size(min = 6 , message = "Password must be at least 6 characters")
        private String password;

        private List<String> roleNames;
    }
