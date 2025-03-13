package excluz.excluz.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BulkInsertController {
	// RankingBulkInsertService 주입
	private final RankingBulkInsertService rankingBulkInsertService;

	@GetMapping("/bulk-insert-ranking")
	public String rankingBulk() {
		rankingBulkInsertService.insertBulkData();
		return "랭킹 더미 데이터 삽입 시작!";
	}
}