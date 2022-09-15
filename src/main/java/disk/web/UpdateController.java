package disk.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import disk.Errors.NotValid;
import disk.Item;
import disk.Utils;
import disk.db.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping()
public class UpdateController {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ItemsRepository itemsRepository;
//    public ItemsRepository getItemsRepository() {
//        return itemsRepository;
//    }
    public UpdateController(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @Autowired
    public void setItemsRepository(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    @GetMapping("/updates")
    public ResponseEntity<Object> get(@ModelAttribute(value = "date") String stringDate){
        try {
            Date date;

            date = Utils.getDate(stringDate);
            if (date == null) {
                throw new Exception("Validation Failed1");
            }

            List<Item> upd = itemsRepository.findByDataBetween(new DateTime(date).minusDays(1).toDate(), date);
            ObjectNode response =  JsonNodeFactory.instance.objectNode();
            ArrayNode items =  JsonNodeFactory.instance.arrayNode();


            for (Item i: upd) {
                JsonNode node = objectMapper.readTree(objectMapper.writeValueAsString(i));
                System.out.println("/upd4");
                ((ObjectNode)node).remove("data");
                System.out.println("/upd5");
                items.add(node);
            }
            response.put("items", items);
            return new ResponseEntity<>(objectMapper.writeValueAsString(response), HttpStatus.OK);
        }
        catch (Exception e){
            return new ResponseEntity<>(new NotValid(), HttpStatus.BAD_REQUEST);
        }

    }
}
