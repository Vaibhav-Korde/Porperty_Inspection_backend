package com.propertyinspection.dto;
public class ProblemDto {
 private String id,area,problemName,description,severity; private Integer photoCount;
 public String getId(){return id;} public void setId(String v){id=v;}
 public String getArea(){return area;} public void setArea(String v){area=v;}
 public String getProblemName(){return problemName;} public void setProblemName(String v){problemName=v;}
 public String getDescription(){return description;} public void setDescription(String v){description=v;}
 public String getSeverity(){return severity;} public void setSeverity(String v){severity=v;}
 public Integer getPhotoCount(){return photoCount;} public void setPhotoCount(Integer v){photoCount=v;}
}