package com.example.demo;

public class ResponseText {

	public ResponseText() {
		
	}
	
	public ResponseText(String text) {
		this.text = text;
	}
	
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
