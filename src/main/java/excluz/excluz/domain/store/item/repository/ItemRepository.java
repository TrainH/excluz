package excluz.excluz.domain.store.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import excluz.excluz.common.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {
}
