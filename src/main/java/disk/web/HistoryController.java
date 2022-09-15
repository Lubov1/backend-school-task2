package disk.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import disk.Errors.NotFound;
import disk.Errors.NotValid;
import disk.ItemHistory;
import disk.db.ItemsHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping()
public class HistoryController {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ItemsHistoryRepository itemsHistoryRepository;

    public HistoryController(ItemsHistoryRepository itemsHistoryRepository) {
        this.itemsHistoryRepository = itemsHistoryRepository;
    }

    @GetMapping("/node/{id}/history")
    public ResponseEntity<Object> get(@PathVariable(name = "id") String id,
                                      @ModelAttribute(value = "dateStart") String startDate,
                                      @ModelAttribute(value = "dateEnd") String endDate) {
        try {
            Date start = disk.Utils.getDate(startDate);
            Date end = disk.Utils.getDate(endDate);
            if (end == null || start == null) {
                throw new Exception("Validation Failed1");
            }
            List<ItemHistory> history = itemsHistoryRepository.findAllById(id);
            ObjectNode response = JsonNodeFactory.instance.objectNode();
            ArrayNode items = JsonNodeFactory.instance.arrayNode();
            if (history.size() == 0)
                throw new NullPointerException();
            for (ItemHistory i : history) {
                if (!i.getData().before(start) && i.getData().before(end)) {
                    JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(i));
                    ((ObjectNode) node).remove("data");
                    ((ObjectNode) node).remove("idGen");
                    items.add(node);
                }
            }
            response.put("items", items);
            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.OK);

        } catch (NullPointerException e) {
            return new ResponseEntity<>(new NotFound(), HttpStatus.NOT_FOUND);
        } catch (Exception r) {
            return new ResponseEntity<>(new NotValid(), HttpStatus.BAD_REQUEST);
        }
    }
}
