﻿package com.chuanonly.notes;

public enum EButtonTypes
{
	ENormal,
	ESwitch;

	public int getValue()
	{
		return this.ordinal();
	}

	public static EButtonTypes forValue(int value)
	{
		return values()[value];
	}
}