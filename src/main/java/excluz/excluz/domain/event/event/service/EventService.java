package excluz.excluz.domain.event.event.service;


import excluz.excluz.common.entity.Event;
import excluz.excluz.common.entity.Store;
import excluz.excluz.domain.event.event.dto.EventRequestDto;
import excluz.excluz.domain.event.event.dto.EventResponseDto;
import excluz.excluz.domain.event.event.enums.ParticipantCondition;
import excluz.excluz.domain.event.event.enums.SelectionMethod;
import excluz.excluz.domain.event.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;


    public EventResponseDto createEvent(EventRequestDto eventRequestDto) {
        // 이벤트 생성 비즈니스 로직 구현
        // 예를 들어, Event 엔티티로 변환 후 저장하고, 저장된 엔티티를 EventResponseDto로 변환하여 반환

        // Store 조회 (예시)
        Store store = storeRepository.findById(eventRequestDto.getStreamerStoreId())
                .orElseThrow(() -> new IllegalArgumentException("해당 스토어를 찾을 수 없습니다."));

        // Event 엔티티 생성
        Event event = Event.builder()
                .store(store)
                .numberOfWinners(eventRequestDto.getNumberOfWinners())
                .participantCondition(ParticipantCondition.valueOf(eventRequestDto.getParticipantCondition()))
                .selectionMethod(SelectionMethod.valueOf(eventRequestDto.getSelectionMethod()))
                .startDatetime(eventRequestDto.getStartDatetime())
                .endDatetime(eventRequestDto.getEndDatetime())
                .isCompleted(false) // 초기값 설정
                .generatedCode(generateUniqueCode()) // 고유 코드 생성 로직 필요
                .build();

        // 이벤트 저장
        Event savedEvent = eventRepository.save(event);

        // EventResponseDto로 변환하여 반환
        return EventResponseDto.from(savedEvent, /* EventItems 리스트 */);
    }

    private String generateUniqueCode() {
        // 고유 코드 생성 로직 구현
        return "UNIQUE_CODE";
    }

}
