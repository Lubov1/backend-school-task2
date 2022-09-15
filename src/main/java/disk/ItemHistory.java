package disk;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class ItemHistory {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO, generator="my_entity_seq_gen")
    @SequenceGenerator(name="my_entity_seq_gen", sequenceName="MY_ENTITY_SEQ")
    private long idGen;
    String id;
    String url;
    String date;
    String parentId;
    String type;
    Integer size;
    Date data;
    public void upd(Item item){
        this.id = item.id;
        this.url = item.url;
        this.data = item.data;
        this.date = item.date;
        this.parentId = item.parentId;
        this.size = item.size;
        this.type = item.type;
    }

}
