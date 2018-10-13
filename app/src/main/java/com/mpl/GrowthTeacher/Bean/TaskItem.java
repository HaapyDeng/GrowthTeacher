package com.mpl.GrowthTeacher.Bean;

/*
"id": "00153700053614900001000084330002",
			"name": "我的运动会",
			"type": "1",
			"write_by_type": "1",
			"image": "http:\/\/101.201.197.224\/getFile.php?f=6.png",
			"category_name": "体育运动",
			"label_name": "体育运动",
			"status": "4",
			"role": "student",
			"updated_at": "1539246919",
			"username": "邓吉州",
			"classroom_id": "3",
			"classroom_name": "二班",
			"task_relation_id": "00153699652798000001000083750001",
			"grade": "小学一年级"
 */

public class TaskItem {
    private String id;               //任务ID
    private String name;
    private String type;
    private String write_by_type;
    private String image;
    private String category_name;
    private String label_name;
    private String status;
    private String role;
    private String updated_at;
    private String username;
    private String classroom_id;
    private String classroom_name;
    private String task_relation_id;
    private String grade;

    //记录是否选中要删除
    public boolean choosed = false;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWrite_by_type() {
        return write_by_type;
    }

    public void setWrite_by_type(String write_by_type) {
        this.write_by_type = write_by_type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClassroom_id() {
        return classroom_id;
    }

    public void setClassroom_id(String classroom_id) {
        this.classroom_id = classroom_id;
    }

    public String getClassroom_name() {
        return classroom_name;
    }

    public void setClassroom_name(String classroom_name) {
        this.classroom_name = classroom_name;
    }

    public String getTask_relation_id() {
        return task_relation_id;
    }

    public void setTask_relation_id(String task_relation_id) {
        this.task_relation_id = task_relation_id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


    public TaskItem(String id, String name, String type, String write_by_type, String image, String category_name, String label_name, String status,
                    String role, String updated_at, String username, String classroom_id, String classroom_name, String task_relation_id, String grade) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.write_by_type = write_by_type;
        this.image = image;
        this.category_name = category_name;
        this.label_name = label_name;
        this.status = status;
        this.role = role;
        this.updated_at = updated_at;
        this.username = username;
        this.classroom_id = classroom_id;
        this.classroom_name = classroom_name;
        this.task_relation_id = task_relation_id;
        this.grade = grade;
    }


}
