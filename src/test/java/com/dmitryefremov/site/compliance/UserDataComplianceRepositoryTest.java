package com.dmitryefremov.site.compliance;

import com.dmitryefremov.site.compliance.domain.DirectMessage;
import com.dmitryefremov.site.compliance.domain.ServiceOrder;
import com.dmitryefremov.site.compliance.repository.DirectMessageRepository;
import com.dmitryefremov.site.compliance.repository.ServiceOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserDataComplianceRepositoryTest {

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private DirectMessageRepository directMessageRepository;

    @Test
    @DisplayName("findByUserId should return only service orders belonging to the specified user")
    void testFindByUserIdOrders() {
        OffsetDateTime now = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        serviceOrderRepository.save(new ServiceOrder("user1", "MVP", "Build MVP", "PENDING", now));
        serviceOrderRepository.save(new ServiceOrder("user1", "AUDIT", "Audit system", "PENDING", now));
        serviceOrderRepository.save(new ServiceOrder("user2", "AUTOMATION", "Automate workflow", "PENDING", now));

        List<ServiceOrder> user1Orders = serviceOrderRepository.findByUserId("user1");
        assertThat(user1Orders).hasSize(2);
        assertThat(user1Orders).extracting(ServiceOrder::getServiceType).containsExactlyInAnyOrder("MVP", "AUDIT");
    }

    @Test
    @DisplayName("findByUserId should return only direct messages belonging to the specified user")
    void testFindByUserIdMessages() {
        OffsetDateTime now = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        directMessageRepository.save(new DirectMessage("user1", "Hello author", now));
        directMessageRepository.save(new DirectMessage("user2", "Other message", now));

        List<DirectMessage> user1Messages = directMessageRepository.findByUserId("user1");
        assertThat(user1Messages).hasSize(1);
        assertThat(user1Messages.get(0).getContent()).isEqualTo("Hello author");
    }

    @Test
    @DisplayName("deleteByUserId should permanently delete all user data for the specified user")
    void testDeleteByUserId() {
        OffsetDateTime now = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        serviceOrderRepository.save(new ServiceOrder("user-erase", "MVP", "MVP details", "PENDING", now));
        directMessageRepository.save(new DirectMessage("user-erase", "Message to delete", now));

        long ordersDeleted = serviceOrderRepository.deleteByUserId("user-erase");
        long messagesDeleted = directMessageRepository.deleteByUserId("user-erase");

        assertThat(ordersDeleted).isEqualTo(1);
        assertThat(messagesDeleted).isEqualTo(1);
        assertThat(serviceOrderRepository.findByUserId("user-erase")).isEmpty();
        assertThat(directMessageRepository.findByUserId("user-erase")).isEmpty();
    }

    @Test
    @DisplayName("updateStatusAtomically should update status only when current status matches expected status")
    void testUpdateStatusAtomically() {
        OffsetDateTime now = OffsetDateTime.of(2026, 10, 12, 10, 0, 0, 0, ZoneOffset.UTC);
        ServiceOrder saved = serviceOrderRepository.save(new ServiceOrder("user-atomic", "MVP", "Details", "PENDING", now));

        int updatedCount = serviceOrderRepository.updateStatusAtomically(saved.getId(), "PENDING", "CONFIRMED");
        assertThat(updatedCount).isEqualTo(1);

        ServiceOrder reloaded = serviceOrderRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo("CONFIRMED");

        int failedUpdateCount = serviceOrderRepository.updateStatusAtomically(saved.getId(), "PENDING", "CANCELLED");
        assertThat(failedUpdateCount).isEqualTo(0);
    }
}
