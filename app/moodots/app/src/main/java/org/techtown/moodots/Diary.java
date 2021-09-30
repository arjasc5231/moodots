package org.techtown.moodots;

public class Diary {
    int _id;
    String title;
    int mood;
    String contents;
    String date;
    //String voice; 이 부분에 음성 녹음 파일 경로 설정


    public Diary(int _id, String title, Integer mood, String contents, String date) {
        this._id = _id;
        this.title = title;
        this.mood = mood;
        this.contents = contents;
        this.date = date;
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

    public Integer getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
