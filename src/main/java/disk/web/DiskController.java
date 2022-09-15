package disk.web;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import disk.Errors.NotFound;
import disk.Errors.NotValid;
import disk.Item;
import disk.ItemHistory;
import disk.Utils;
import disk.db.ItemsHistoryRepository;
import disk.db.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
public class DiskController {
    @Autowired
    private ItemsHistoryRepository itemsHistoryRepository;
    @Autowired
    private ItemsRepository itemsRepository;
    ObjectMapper objectMapper = new ObjectMapper();


    public DiskController(ItemsRepository itemsRepository,
                          ItemsHistoryRepository itemsHistoryRepository){
        this.itemsRepository = itemsRepository;
        this.itemsHistoryRepository =  itemsHistoryRepository;
    }

    public void resize(String id, int size, String date){
        Item y = itemsRepository.findById(id).orElse(null);
        itemsRepository.deleteById(id);
        y.resize(size, date);
        ItemHistory itemHistory = new ItemHistory();
        itemHistory.upd(y);
        itemsHistoryRepository.save(itemHistory);
        itemsRepository.save(y);
        if(y.getParentId()!=null)
            resize(y.getParentId(), size, date);
    }

    @PostMapping("/imports")
    public ResponseEntity<?> post(@RequestBody JsonNode jsonNode){
        try {
            Set<String> ids = new HashSet<String>();
            ObjectMapper objectMapper = new ObjectMapper();
            Date date;
            String stringDate = jsonNode.get("updateDate").asText();

            // process date
            date = Utils.getDate(stringDate);

            // process items
            for (JsonNode s : jsonNode.get("items")) {
                Item item = objectMapper.treeToValue(s, Item.class);
                ItemHistory itemHistory = objectMapper.treeToValue(s, ItemHistory.class);
                item.setDate(stringDate);
                item.setData(date);
                itemHistory.setDate(stringDate);
                itemHistory.setData(date);

                // valid check
                if (item.getData() == null)
                    throw new Exception();
                if (!ids.contains(item.getId()))
                    ids.add(item.getId());
                else
                    throw new Exception();
                if (item.getId() == null || !item.check()) throw new Exception();
                if (item.getParentId() != null)
                    if (!itemsRepository.findById(item.getParentId()).orElse(null).Folder())
                        throw new Exception();
                if (item.Folder()) {
                    if (item.getUrl() != null || item.getSize() != null) {
                        throw new Exception();
                    }
                    item.setSize(0);
                    itemHistory.setSize(0);
                } else {
                    if (item.getUrl() != null)
                        if (item.getUrl().length() > 255)
                            throw new Exception();
                    if (item.getSize() <= 0 || item.getSize() == null){
                        throw new Exception();
                    }
                }

                // update item
                if (itemsRepository.existsById(s.get("id").asText())) {
                    Item y = itemsRepository.findById(s.get("id").asText()).orElse(null);
                    itemsRepository.deleteById(y.getId());
                    if (!y.getType().equals(item.getType())) {throw new Exception();}
                    if (item.getParentId() != null) {
                        resize(item.getParentId(), item.getSize() - y.getSize(),
                                item.getDate());
                    }
                    itemsRepository.save(y);
                // new item
                } else {
                    if (item.getParentId() != null && !(item.Folder())) {
                        resize(item.getParentId(), item.getSize(), item.getDate());
                    }
                    itemsRepository.save(item);
                }
                itemsHistoryRepository.save(itemHistory);

            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception r) {
            return ResponseEntity.status(400).body(new NotValid());
        }
    }



    JsonNode getChildrens(String Id) throws Exception {
        try {
            ObjectNode json = JsonNodeFactory.instance.objectNode();
            ArrayNode list = json.putArray("children");
            for (Item i : itemsRepository.findAllByParentId(Id)) {
                if (itemsRepository.findAllByParentId(i.getId()).size() != 0) {
                    JsonNode j = objectMapper.readTree(objectMapper.writeValueAsString(i));
                    ((ObjectNode) j).remove("data");
                    ((ObjectNode) j).put("children", getChildrens(i.getId()).get("children"));
                    list.add(j);
                } else{
                    JsonNode j = objectMapper.readTree(objectMapper.writeValueAsString(i));
                    ((ObjectNode) j).remove("data");
                    list.add(j);
                }
            }
            return json;
        }
        catch (Exception u){
            throw new Exception();
        }
    }



    @GetMapping("/nodes/{id}")
    public ResponseEntity<Object> get(@PathVariable String id){
        try {
            Item item = itemsRepository.findById(id).orElseThrow(() -> new NullPointerException());
            String o = objectMapper.writeValueAsString(item);
            JsonNode j = objectMapper.readTree(o);
            ((ObjectNode)j).remove("data");
            if (itemsRepository.findAllByParentId(id).size() != 0) {
                JsonNode k = getChildrens(id);
                ((ObjectNode) j).put("children", k.get("children"));
            }
            else
                ((ObjectNode) j).put("children", (String) null);
            return new ResponseEntity<>(objectMapper.writeValueAsString(j), HttpStatus.OK);

        } catch (NullPointerException e){
            return new ResponseEntity<>(new NotFound(), HttpStatus.NOT_FOUND);
        }
        catch (Exception r) {
            return new ResponseEntity<>(new NotValid(), HttpStatus.BAD_REQUEST);
        }
    }
}




