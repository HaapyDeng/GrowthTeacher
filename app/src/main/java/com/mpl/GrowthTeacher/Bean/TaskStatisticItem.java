package com.mpl.GrowthTeacher.Bean;

/*
	"id": "00153898425109300001000027140001",
			"type": "1",
			"task_type": "3",
			"image": "http:\/\/101.201.197.224\/getFile.php?f=5.png",
			"name": "视频录制1",
			"category_name": "全面素质",
			"label_name": "体育运动",
			"write_by_type": "1",
			"write_by": "student",
			"join_number": "1",
			"complete_number": "0",
			"start": "2019-05-03",
			"end": "2019-05-06",
			"complete_rate": "0%",
			"countdown": 206
 */
public class TaskStatisticItem {
    private String id;
    private String task_type;
    private String image;
    private String name;
    private String category_name;
    private String label_name;
    private String complete_rate;
    private int countdown;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask_type() {
        return task_type;
    }

    public void setTask_type(String task_type) {
        this.task_type = task_type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getComplete_rate() {
        return complete_rate;
    }

    public void setComplete_rate(String complete_rate) {
        this.complete_rate = complete_rate;
    }

    public int getCountdown() {
        return countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }


    public TaskStatisticItem(String id, String task_type, String image, String name, String category_name, String label_name, String complete_rate, int countdown) {
        this.id = id;
        this.task_type = task_type;
        this.image = image;
        this.name = name;
        this.category_name = category_name;
        this.label_name = label_name;
        this.complete_rate = complete_rate;
        this.countdown = countdown;
    }


}
