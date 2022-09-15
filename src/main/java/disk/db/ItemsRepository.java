package disk.db;

import disk.Item;
import org.springframework.data.repository.CrudRepository;
import java.util.Date;
import java.util.List;

public interface ItemsRepository
        extends CrudRepository<Item, String> {
    List<Item> findAllByParentId(String parentId);
    List<Item> findByDataBetween(Date start, Date end);
}