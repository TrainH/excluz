package excluz.excluz.common.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import excluz.excluz.domain.store.storeRevenue.enums.RevenuePeriod;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class) // @LastModifiedDate는 Spring Data JPA의 AuditingEntityListener를 활성화해야 적용됨
@Table(name="store_rankings")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreRanking {

	// StoreRanking 식별자
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	// store 식별자(외래키)
	@ManyToOne(
		fetch = FetchType.LAZY,
		optional = false // store가 항상 필수 필드인 것을 명시
	)
	@JoinColumn(
		name = "store_id",
		nullable = false
	)
	private Store store;

	// 매출 타입 (DAY, MONTH, YEAR)
	@Comment("매출 타입")
	@Enumerated(EnumType.STRING)
	private RevenuePeriod rankingPeriod;

	// 랭킹 날짜 (랭킹 기준 시작 날짜)
	@LastModifiedDate
	@Column(name = "rank_date", nullable = false)
	private LocalDateTime rankDate;

	// 랭킹 순위
	@Column(name = "rank_position", nullable = false)
	private Integer rankPosition;

	// 매출
	@Column(name = "revenue", nullable = false)
	private Long revenue;

	// 생성자: 매개변수 4개 이상은 빌더 패턴 사용
	@Builder
	public StoreRanking(Store store, RevenuePeriod rankingPeriod, Integer rankPosition, Long revenue) {
		this.store = store;
		this.rankingPeriod = rankingPeriod;
		this.rankDate = LocalDateTime.now();
		this.rankPosition = rankPosition;
		this.revenue = revenue;
	}

	// 기존 랭킹 업데이트 메서드 추가
	public void updateRank(Integer rankPosition, Long revenue) {
		this.rankPosition = rankPosition;
		this.revenue = revenue;
		this.rankDate = LocalDateTime.now();
	}
}
