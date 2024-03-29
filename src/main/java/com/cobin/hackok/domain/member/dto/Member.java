package com.cobin.hackok.domain.member.dto;

import com.cobin.hackok.domain.member.domain.MemberGrade;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "members")
public class Member {
    @Id
    private ObjectId id;            //아이디
    @NotBlank private String loginId;     //로그인 아이디
    private String name;        //이름
    @NotBlank private String password;    //비밀번호
    private String birthday;    //생년월일
    private String mobileNumber;    //휴대폰 번호
    private String gender;  // 성별
}
