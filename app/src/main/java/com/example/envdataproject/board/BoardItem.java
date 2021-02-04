package com.example.envdataproject.board;

public class BoardItem {
    private String title, person, date, boardName;
    private Integer idx;

    public BoardItem(String title, String person, String date,Integer idx, String boardName) {
        this.title = title;
        this.person = person;
        this.date = date;
        this.idx = idx;
        this.boardName = boardName;
    }

    String getDate() {
        return date;
    }

    String getTitle() {
        return title;
    }

    String getPerson() {
        return person;
    }

    public Integer getIdx() {
        return idx;
    }

    public String getBoardName() {
        return boardName;
    }
}
