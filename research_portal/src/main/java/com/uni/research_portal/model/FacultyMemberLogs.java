package com.uni.research_portal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "faculty_member_logs")
public class FacultyMemberLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "author_id")
    private FacultyMember authorId;

    @Column(nullable = false)
    private String log;

    @Column
    private Date date;

    public FacultyMemberLogs(FacultyMember facultyMember, String log) {
        this.authorId = facultyMember;
        this.log = log;
        this.date = new Date();
    }
}
