package excluz.excluz.domain.ranking.storeRevenue.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import excluz.excluz.domain.ranking.storeRevenue.service.StoreRevenueService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/store-ranking")
@RequiredArgsConstructor
public class StoreRevenueController {
	private final StoreRevenueService storeRevenueService;
}
