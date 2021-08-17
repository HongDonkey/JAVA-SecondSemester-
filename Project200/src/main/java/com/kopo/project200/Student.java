package com.kopo.project200;

public class Student {
	int idx;
	String name;
	int score;
	
	public Student() {
		// TODO Auto-generated constructor stub
	}
	
	Student(int idx, String name, int score) {
		this.idx = idx;
		this.name = name;
		this.score = score;
	}
	
	Student(String name, int score) {
		this.name = name;
		this.score = score;
	}
	
	public int getIdx() {
		return this.idx;
	}
	
	public String getName() {
		return this.name;
	}
	
	public int getScore() {
		return this.score;
	}
}
