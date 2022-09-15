package disk.Errors;

import lombok.Data;

@Data
public class NotValid {
    public String message = "Validation Failed";
    public int code = 400;
}