package com.mpl.GrowthTeacher.Bean;


public class TaskItem {
    private String id;               //任务ID
    private String type;             //类型， 1成就，2问卷 ,
    private String task_type;       //任务类型
    private String image;           //图片路径
    private String name;            //标题
    private String category_name;  // 分类名称
    private String label_name;     //标签名称 ,
    private String write_by_type; //填写人类型， 1各填一份，2共填一份
    private String write_by;        //填写人, 1学生，2家长。3老师 ,
    private String join_number;     // 参与人 ,
    private String complete_number; //完成人 ,
    private String start;             //开始时间 ,
    private String end;                 //结束时间 ,
    private String complete_rate;       //完成率 ,
    private int countdown;              //距离截止有x天， -1 表示已过期
    public boolean isCheck;             //选中

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getWrite_by_type() {
        return write_by_type;
    }

    public void setWrite_by_type(String write_by_type) {
        this.write_by_type = write_by_type;
    }

    public String getWrite_by() {
        return write_by;
    }

    public void setWrite_by(String write_by) {
        this.write_by = write_by;
    }

    public String getJoin_number() {
        return join_number;
    }

    public void setJoin_number(String join_number) {
        this.join_number = join_number;
    }

    public String getComplete_number() {
        return complete_number;
    }

    public void setComplete_number(String complete_number) {
        this.complete_number = complete_number;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
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


    public TaskItem(String id, String type, String task_type, String image, String name, String category_name, String label_name,
                    String write_by_type, String write_by, String join_number, String complete_number,
                    String start, String end, String complete_rate, int countdown) {
        this.id = id;
        this.type = type;
        this.task_type = task_type;
        this.image = image;
        this.name = name;
        this.category_name = category_name;
        this.label_name = label_name;
        this.write_by_type = write_by_type;
        this.write_by = write_by;
        this.join_number = join_number;
        this.complete_number = complete_number;
        this.start = start;
        this.end = end;
        this.complete_rate = complete_rate;
        this.countdown = countdown;
    }


}
