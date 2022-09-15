package disk.Errors;

import lombok.Data;
@Data
public class NotFound {
    public String message = "Item not found";
    public int code = 404;
}