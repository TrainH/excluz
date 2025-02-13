package excluz.excluz;

import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.eventApplicant.enums.ApplicantStatus;
import excluz.excluz.common.entity.*;
import excluz.excluz.domain.event.event.repository.EventRepository;
import excluz.excluz.domain.event.eventApplicant.repository.EventApplicantRepository;
import excluz.excluz.domain.event.eventItem.repository.EventItemRepository;
import excluz.excluz.domain.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false) // DB에 잘 저장되는지 확인위해 롤백하지 않음
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventApplicantRepository eventApplicantRepository;

    @Autowired
    private EventItemRepository eventItemRepository;

    @PersistenceContext
    private EntityManager em; // EntityManager를 주입받습니다.

    @Test
    public void testEventCreation() {
        // 1. Streamer 생성 및 저장
        Streamer streamer = Streamer.builder()
                .name("스트리머 수민")
                .nickName("Suminy")
                .phoneNumber("01087654321")
                .email("streamer@example.com")
                .password("password")
                .build();
        em.persist(streamer); // EntityManager를 사용하여 저장

        // 2. Store 생성 및 저장
        Store store = Store.builder()
                .streamer(streamer)
                .address("서울시 서초구")
                .storeName("스트리머샵")
                .registrationNumber("123-45-67890")
                .build();
        em.persist(store);

        // 3. Item 생성 및 저장
        Item item = Item.builder()
                .store(store)
                .itemName("굿즈 상품")
                .explanation("스트리머 김철수의 굿즈")
                .price(50000)
                .remainingQuantity(100)
                .build();
        em.persist(item);

        // 4. Event 생성 및 저장
        Event event = Event.builder()
                .store(store)
                .numberOfWinners(10)
                .generatedCode("EVENT1234")
                .participantCondition(ParticipantCondition.ALL_USERS)
                .selectionMethod(SelectionMethod.FIRST_COME_FIRST_SERVED)
                .startDatetime(LocalDateTime.now())
                .endDatetime(LocalDateTime.now().plusDays(7))
                .isCompleted(false)
                .build();
        eventRepository.save(event); // EventRepository를 사용하여 저장

        // 5. EventItem 생성 및 저장
        EventItem eventItem = new EventItem(event, item, 10);
        eventItemRepository.save(eventItem);

        // 6. EventApplicant 생성 및 저장
        EventApplicant eventApplicant = EventApplicant.builder()
                .event(event)
                .applicantName("이영희")
                .email("applicant@example.com")
                .applicantPassword("password")
                .applicantStatus(ApplicantStatus.WAITING)
                .deliveryAddress("서울시 송파구")
                .build();
        eventApplicantRepository.save(eventApplicant);

        // 7. 저장된 Event 조회 및 검증
        Optional<Event> fetchedEventOpt = eventRepository.findById(event.getId());
        assertTrue(fetchedEventOpt.isPresent());
        Event fetchedEvent = fetchedEventOpt.get();
        assertEquals(event.getGeneratedCode(), fetchedEvent.getGeneratedCode());
        assertEquals(event.getStore().getStoreName(), fetchedEvent.getStore().getStoreName());

        // 8. 저장된 EventItem 조회 및 검증
        Optional<EventItem> fetchedEventItemOpt = eventItemRepository.findById(eventItem.getId());
        assertTrue(fetchedEventItemOpt.isPresent());
        EventItem fetchedEventItem = fetchedEventItemOpt.get();
        assertEquals(eventItem.getQuantity(), fetchedEventItem.getQuantity());
        assertEquals(eventItem.getItem().getItemName(), fetchedEventItem.getItem().getItemName());

        // 9. 저장된 EventApplicant 조회 및 검증
        Optional<EventApplicant> fetchedApplicantOpt = eventApplicantRepository.findById(eventApplicant.getId());
        assertTrue(fetchedApplicantOpt.isPresent());
        EventApplicant fetchedApplicant = fetchedApplicantOpt.get();
        assertEquals(eventApplicant.getApplicantName(), fetchedApplicant.getApplicantName());
        assertEquals(eventApplicant.getEmail(), fetchedApplicant.getEmail());
    }
}