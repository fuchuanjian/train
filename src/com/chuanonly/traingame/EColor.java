﻿package com.chuanonly.traingame;

public enum EColor
{
	EColorRed,
	EColorYellow,
	EColorBlue;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EColor forValue(int value)
	{
		return values()[value];
	}
}