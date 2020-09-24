package dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author MOON
 * 2020-09-25
 */
@Data
@NoArgsConstructor
public class Student {
    private String studentId;

    private String name;

    private Boolean sex;

}
