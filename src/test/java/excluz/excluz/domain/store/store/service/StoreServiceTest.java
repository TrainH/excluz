package excluz.excluz.domain.store.store.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import excluz.excluz.domain.store.store.repository.StoreRepository;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

	@InjectMocks
	StoreService storeService;

	@Mock
	StoreRepository storeRepository;


}
