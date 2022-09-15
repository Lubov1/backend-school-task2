package disk.web;

import disk.Errors.NotFound;
import disk.Errors.NotValid;
import disk.Item;
import disk.ItemHistory;
import disk.Utils;
import disk.db.ItemsHistoryRepository;
import disk.db.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping()
public class DeleteController {
    @Autowired
    private ItemsHistoryRepository itemsHistoryRepository;
    @Autowired
    private ItemsRepository itemsRepository;

    public DeleteController(ItemsRepository itemsRepository,
                          ItemsHistoryRepository itemsHistoryRepository){
        this.itemsRepository = itemsRepository;
        this.itemsHistoryRepository =  itemsHistoryRepository;
    }

    public void resize(String id, int size, String date) {
        Item y = itemsRepository.findById(id).orElse(null);//error
        itemsRepository.deleteById(id);
        y.resize(size, date);
        ItemHistory itemHistory = new ItemHistory();
        itemHistory.upd(y);
        itemsHistoryRepository.save(itemHistory);
        itemsRepository.save(y);
        if (y.getParentId() != null)
            resize(y.getParentId(), size, date);
    }
    void deleteHistory(String id){
        for (ItemHistory i : itemsHistoryRepository.findAllById(id)) {
            itemsHistoryRepository.delete(i);
        }
    }
    void delete(Item item){
        for (Item child : itemsRepository.findAllByParentId(item.getId())) {
            delete(child);
            deleteHistory(child.getId());
            itemsRepository.delete(child);
        }
        deleteHistory(item.getId());
        itemsRepository.delete(item);
    }

    @RequestMapping(value = {"/delete/{id}"}, method = {RequestMethod.DELETE})
    public ResponseEntity<Object> deleteItem(@PathVariable String id, @ModelAttribute(value = "date") String stringDate){
        try {
            Date date;
            date = Utils.getDate(stringDate);
            if (date==null){
                throw new Exception("Validation Failed1");
            }
            Item item = itemsRepository.findById(id).orElseThrow(() -> new NullPointerException());
            if (item.getParentId() != null) {
                resize(item.getParentId(), -item.getSize(), stringDate);
            }
            delete(item);

            return new ResponseEntity<>( HttpStatus.OK);
        } catch (NullPointerException e) {
            return new ResponseEntity<>(new NotFound(), HttpStatus.NOT_FOUND);
        } catch (Exception r) {
            return new ResponseEntity<>(new NotValid(), HttpStatus.BAD_REQUEST);
        }
    }
}

