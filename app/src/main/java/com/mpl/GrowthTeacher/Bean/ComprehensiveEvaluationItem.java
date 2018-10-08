package com.mpl.GrowthTeacher.Bean;

public class ComprehensiveEvaluationItem {
    /*
    "type": "3",
			"write_by_type": "1",
			"role": "student",
			"point": "0",
			"classroom_id": "3",
			"name": "唐小雅",
			"gender": "2",
			"id": "00153699280762300001000071860001",
			"classroom_name": "二班",
			"star": "20",
			"task_star": "130",
			"task_point": "30",
			"grade": "小学一年级",
			"total_point": 60
     */
    private String name;
    private String gender;
    private String classroom_name;
    private String id;
    private String star;
    private String grade;
    private int total_point;

    public ComprehensiveEvaluationItem(String name, String gender, String classroom_name, String id, String star, String grade, int total_point) {
        this.name = name;
        this.gender = gender;
        this.classroom_name = classroom_name;
        this.id = id;
        this.star = star;
        this.grade = grade;
        this.total_point = total_point;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClassroom_name() {
        return classroom_name;
    }

    public void setClassroom_name(String classroom_name) {
        this.classroom_name = classroom_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getTotal_point() {
        return total_point;
    }

    public void setTotal_point(int total_point) {
        this.total_point = total_point;
    }


}
