package com.uni.research_portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAuthorRequestDto {

        private int departmentId;

        private String authorName;

        private String openAlexId;

        private int semanticId;

        private String email;

        private String phone;

        private String address;

        private String photo;

        private String title;

}
