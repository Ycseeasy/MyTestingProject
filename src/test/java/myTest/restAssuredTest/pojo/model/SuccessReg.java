package myTest.restAssuredTest.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
public class SuccessReg {
    private Integer id;
    private String token;
}
