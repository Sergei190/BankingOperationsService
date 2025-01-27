package ru.effectivemobile.boperations.boundary;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.effectivemobile.boperations.boundary.request.AppCreateUserRequest;
import ru.effectivemobile.boperations.domain.core.boundary.response.CreateUserResponse;
import ru.effectivemobile.boperations.entity.AppAccount;
import ru.effectivemobile.boperations.entity.AppProfile;
import ru.effectivemobile.boperations.entity.AppUser;
import ru.effectivemobile.boperations.repository.AppAccountJpaRepository;
import ru.effectivemobile.boperations.repository.AppAccountOperationJpaRepository;
import ru.effectivemobile.boperations.repository.AppProfileJpaRepository;
import ru.effectivemobile.boperations.repository.AppUserJpaRepository;
import ru.effectivemobile.boperations.support.DataJpaTestDockerized;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTestDockerized
class AppCreateUserInteractorTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AppUserJpaRepository userDbRepository;

    @Autowired
    AppProfileJpaRepository profileDbRepository;

    @Autowired
    AppAccountJpaRepository bankAccountDbRepository;

    @Autowired
    AppAccountOperationJpaRepository operationDbRepository;

    @PersistenceContext
    EntityManager entityManager;

    AppCreateUserInteractor sut;

    @BeforeEach
    void setUp() {
        sut = AppCreateUserInteractor.builder()
                .bankAccountDbRepository(bankAccountDbRepository)
                .operationDbRepository(operationDbRepository)
                .passwordEncoder(passwordEncoder)
                .profileDbRepository(profileDbRepository)
                .userDbRepository(userDbRepository)
                .build();
    }

    @Test
    void givenGoodRequest_whenCreate_thenSuccessCreatedUserAndProfileAndAccount() {
        AppCreateUserRequest request = new AppCreateUserRequest("testuser", "testpassword", "Test User", "79999999999",
                "test@example.com", Instant.now(), BigDecimal.valueOf(10.10));

        CreateUserResponse result = sut.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getUser().getId()).isNotNull();
        UUID userId = result.getUser().getId();
        AppUser appUser = userDbRepository.findById(userId).orElse(null);
        assertThat(appUser).isNotNull();
        assertThat(appUser.getUsername()).isEqualTo(request.getUsername());
        assertThat(appUser.getPassword()).isNotEmpty().isNotEqualTo(request.getPassword());
        AppProfile appProfile = profileDbRepository.findById(userId).orElse(null);
        assertThat(appProfile).isNotNull();
        assertThat(appProfile.getBirthday().getValue()).isEqualTo(request.getBirthday());
        assertThat(appProfile.getName().getValue()).isEqualTo(request.getName());
        assertThat(appProfile.getEmails()).hasSize(1);
        assertThat(appProfile.getEmails().stream()
                .filter(email -> email.getValue().equals(request.getEmail()))
                .count()).isEqualTo(1);
        assertThat(appProfile.getPhones()).hasSize(1);
        assertThat(appProfile.getPhones().stream()
                .filter(phone -> phone.getValue().equals(request.getPhone()))
                .count()).isEqualTo(1);
        AppAccount appAccount = bankAccountDbRepository.findFirstByUser_Id(userId).orElse(null);
        entityManager.refresh(appAccount);
        assertThat(appAccount).isNotNull();
        assertThat(appAccount.getAccountBalance()).isNotNull();
        assertThat(appAccount.getBalance())
                .usingComparator(BigDecimal::compareTo).isEqualTo(request.getStartBalance());
        assertThat(appAccount.getFirstTopup()).isNotNull();
        assertThat(appAccount.getFirstTopup().getAmount())
                .usingComparator(BigDecimal::compareTo).isEqualTo(request.getStartBalance());
    }

}