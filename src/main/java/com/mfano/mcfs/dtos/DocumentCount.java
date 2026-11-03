package com.mfano.mcfs.dtos;

import lombok.Getter;

@Getter
public class DocumentCount {
	public DocumentCount(String name, long count, double percentage, String color) {
        this.name = name;
        this.count = count;
        this.percentage = percentage;
        this.color = color;
    }

    private String name;
    private long count;
    private double percentage;
    private String color;

}