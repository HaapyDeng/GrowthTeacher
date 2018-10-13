package com.mpl.GrowthTeacher.Bean;
/*	"id": "00153698255860500001000085530001",
			"username": "小学",
			"classroom_id": "1",
			"classroom_name": "1班",
			"status": "5",
			"updated_at": "1539151904",
			"gender": "2",
			"grade": "小学一年级"
			*/

public class StudentTaskItem {
    private String username;
    private String classroom_name;
    private String status;
    private String updated_at;
    private String gender;
    private String grade;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClassroom_name() {
        return classroom_name;
    }

    public void setClassroom_name(String classroom_name) {
        this.classroom_name = classroom_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


    public StudentTaskItem(String username, String classroom_name, String status, String updated_at, String gender, String grade) {
        this.username = username;
        this.classroom_name = classroom_name;
        this.status = status;
        this.updated_at = updated_at;
        this.gender = gender;
        this.grade = grade;
    }


}
