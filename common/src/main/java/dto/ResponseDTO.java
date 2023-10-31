package dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ResponseDTO<E> {

    private List<E> orders;
    private int pageIndex;
    private int pageCount;
}
