package myTest.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class Resource {

    private Integer id;
    private String name;
    private Integer year;
    private String color;
    private String pantone_value;
}
