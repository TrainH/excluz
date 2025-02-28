package excluz.excluz.domain.store.item.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import excluz.excluz.common.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

	@Query("SELECT i FROM Item i " +
		"WHERE (i.itemName LIKE CONCAT('%', :itemName, '%') OR :itemName IS NULL) " +
		"AND (i.price >= :minPrice) " +
		"AND (i.price <= :maxPrice) " +
		"AND (i.isDeleted = FALSE ) " +
		"ORDER BY i.id DESC")
	Page<Item> findByPriceWithItemName(
		Pageable pageable,
		@Param("minPrice") Integer minPrice,
		@Param("maxPrice") Integer maxPrice,
		@Param("itemName") String itemName);

	@Query("SELECT i FROM Item i " +
		"WHERE (i.id = :itemsId)" +
		"AND (i.isDeleted = FALSE)")
	Optional<Item> findItemByIdAndNotDeleted(@Param("itemsId") Integer itemsId);

	@Query("SELECT i FROM Item i " +
	"WHERE i.store.id = :storeId " +
	"AND (i.isDeleted = FALSE ) " +
	"ORDER BY i.id DESC")
	Page<Item> findByStoreId(@Param("storeId") Integer storeId, Pageable pageable);
}
