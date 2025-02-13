package excluz.excluz.domain.store.item.service;

import org.springframework.stereotype.Service;

import excluz.excluz.domain.store.item.repository.ItemRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
}
