package org.techtown.moodots;

public class Diary {
    int _id;
    String title;
    String date;
    String mood;
    //String voice; 이 부분에 음성 녹음 파일 경로 설정

    public Diary(int _id, String title, String date, String mood) {
        this._id = _id;
        this.title = title;
        this.date = date;
        this.mood = mood;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

}
