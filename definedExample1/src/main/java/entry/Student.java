package entry;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Student {

    private Integer id;
    private Boolean sex;
    private Integer classId;
    private Integer age;
    private String name;

}
