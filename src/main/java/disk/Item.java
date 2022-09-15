package disk;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
public class Item {
    @Id
    String id;
    String url;
    String date;
    String parentId;
    String type;
    Integer size;
    String children;
    @Column(name="data")
    Date data;
    public void resize(int ch, String date){
        this.size = this.size+ch;
        this.date = date;
        this.data = Utils.getDate(date);
    }
    public boolean check(){
        return this.type.equals("FILE") || this.type.equals("FOLDER");
    }
    public boolean Folder(){
        return this.type.equals("FOLDER");
    }

}
