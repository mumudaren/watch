package cn.cvtt.nuoche.entity;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;

public class Entitys implements Serializable{
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}

