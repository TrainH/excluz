package excluz.excluz.domain.store.item.controller;

import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.store.item.service.ItemService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ItemV1Controller {

	private final ItemService itemService;
}
