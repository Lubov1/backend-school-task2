package disk.db;

import disk.Item;
import disk.ItemHistory;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface ItemsHistoryRepository
        extends CrudRepository<ItemHistory, Long> {
    List<ItemHistory> findAllById(String stringId);
    List<ItemHistory> findByDataBetween(Date start, Date end);
}