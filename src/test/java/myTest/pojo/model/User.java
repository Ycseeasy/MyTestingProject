package myTest.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class User {

    private Integer id;
    private String email;
    private String first_name;
    private String last_name;
    private String avatar;
}
